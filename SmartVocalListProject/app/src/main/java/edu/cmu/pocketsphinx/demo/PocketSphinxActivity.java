/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import DataObjects.ChecklistItem;
import DataObjects.Checklist;
import Dialogs.DialogFlow;
import Model.Model;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class PocketSphinxActivity extends Activity implements
        RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    //private static final String DIGITS_SEARCH = "digits";
    private static final String KEY_WORDS_SEARCH = "KEY_WORDS_SEARCH";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private HashMap<String, String> textToSpeechMap;
    private TextToSpeech textToSpeech;
    private Checklist chk = null;
    private DialogFlow<ChecklistItem> dlg = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        dlg = new DialogFlow<>();
        Model mdl = new Model();
        chk = mdl.getChecklists();
        dlg.items=chk.checklistItems;

        dlg.execute = ()-> {
            String text =  this.dlg.items.get(this.dlg.step).getName();
            String caption = "Step Number " + dlg.step + ". " + text;
            ((TextView) findViewById(R.id.caption_text)).setText(caption);
            playTextToSpeechWhenDoneSpeaking(caption);
            listenToKeyWords();
        };

        dlg.set = value -> {
            PocketSphinxActivity.this.playTextToSpeechWhenDoneSpeaking(value);
            return true;
        };

        dlg.sof  = ()->{
            String caption ="This is the first item";
            playTextToSpeechWhenDoneSpeaking(caption);
            makeText(getApplicationContext(), caption, Toast.LENGTH_SHORT).show();
            listenToKeyWords();
        };

        dlg.eof  = ()->{
            String caption = "This is the last item";
            playTextToSpeechWhenDoneSpeaking(caption);
            makeText(getApplicationContext(), caption, Toast.LENGTH_SHORT).show();
            listenToKeyWords();
        };

        dlg.readItem = ()->{
            this.dlg.execute.execute();
        };

        textToSpeechMap = new HashMap<String, String>();
        textToSpeechMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text)).setText("Preparing the recognizer");

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        SetupTask tsk= new SetupTask(this);
        tsk.dlg = this.dlg;
        tsk.execute();
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        public DialogFlow<ChecklistItem> dlg=null;
        WeakReference<PocketSphinxActivity> activityReference;

        SetupTask(PocketSphinxActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupTextToSpeech();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                ((TextView) activityReference.get().findViewById(R.id.caption_text))
                        .setText("Failed to init recognizer " + result);
            } else {
                activityReference.get().dlg.execute.execute();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();

        if(dlg.keywords.contains(text)) {
            playTextToSpeechNow(text);
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            dlg.setCommand(text, "");
        }
        else if(!dlg.items.get(dlg.step).toKeywords().contains(text)) {
            playTextToSpeechNow(text + " is not an answer");
            listenToKeyWords();
        }
        else {
            playTextToSpeechNow(text);
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            dlg.next();
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");

        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            //
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
    }

    private void listenToKeyWords(){
        if(!textToSpeech.isSpeaking()){
            recognizer.stop();
            recognizer.startListening(KEY_WORDS_SEARCH);
        }else{
            new listenToKeyWordsWaiter().execute();
        }
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create multiple keyword-activation search
        File keywords = new File(assetsDir, "keywords.lst");
        dlg.createKeyWordsFile(keywords);
        recognizer.addKeywordSearch(KEY_WORDS_SEARCH, keywords);

        // Create grammar-based search for digit recognition
  //      File digitsGrammar = new File(assetsDir, "digits.gram");
  //      recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
    }

    private void setupTextToSpeech(){
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
//                textToSpeech.setOnUtteranceCompletedListener(utteranceId ->
//                    recognizer.startListening(KEY_WORDS_SEARCH)
//                );
//                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                    @Override
//                    public void onDone(String utteranceId) {
//                        recognizer.startListening(KEY_WORDS_SEARCH);
//                    }
//
//                    @Override
//                    public void onError(String utteranceId) {
//                    }
//
//                    @Override
//                    public void onStart(String utteranceId) {
//                    }
//                });
            }
        });
    }

    private void playTextToSpeechNow(String text){
        if(textToSpeech == null)
            return;

        recognizer.stop();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, textToSpeechMap);
    }

    private void playTextToSpeechWhenDoneSpeaking(String text){
        if(textToSpeech == null)
            return;

        recognizer.stop();
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, textToSpeechMap);
    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        listenToKeyWords();
    }

    class listenToKeyWordsWaiter extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            while (textToSpeech.isSpeaking()){
                try{Thread.sleep(200);}catch (Exception e){}
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listenToKeyWords();
        }
    }
}
