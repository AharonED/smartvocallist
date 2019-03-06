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
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.speech.*;
import android.media.VolumeShaper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import DataObjects.BaseModelObject;
import DataObjects.ChecklistItems;
import DataObjects.Checklists;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class PocketSphinxActivity extends Activity implements
        RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String OPTIONS_SEARCH = "options";
    private static final String DIGITS_SEARCH = "digits";
    private static final String BOOLEAN_SEARCH = "boolean";
    private static final String MODEL_LANGUAGE = "NEW_NGRAM";

    private static final String LIST_OPTIONS = "list options";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private static int currentQuestionIndex;

    private SpeechRecognizer recognizer;
    private LinkedHashMap<String, Integer> questions;
    private TextToSpeech textToSpeech;
    private Boolean isListOptionKeyUsed = false;

    @Override
    public void onCreate(Bundle state) {
        //BaseModelObject obj = new BaseModelObject("111");
        Checklists chk = new Checklists("222","Say a Number","You should say a number","",null);
        ChecklistItems item =new ChecklistItems("222.1",1,"Say a number between one to six","", "",null);
        item.getOptions().add("True");
        item.getOptions().add("False");
        chk.getChecklistsItems().add(item);

        super.onCreate(state);

        // Prepare the questions list for UI
        questions = new LinkedHashMap<>();
        //questions.put(chk.getName(), R.string.options_caption);

        questions.put(OPTIONS_SEARCH, R.string.options_caption);
        questions.put(DIGITS_SEARCH, R.string.digits_caption);
        questions.put(BOOLEAN_SEARCH, R.string.boolean_caption);


        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");
        ((TextView) findViewById(R.id.resulst_text)).setText("alex was here");

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new SetupTask(this).execute();


        final Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recognizer.stop();
                //recognizer.getDecoder().dicS


               //int type = recognizer.getDecoder().getLm(MODEL_LANGUAGE).addWord("twenty one",1);
                recognizer.getDecoder().getLm(MODEL_LANGUAGE).addWord("twenty two",2);
                recognizer.getDecoder().getLm(MODEL_LANGUAGE).addWord("two hundred",3);
                recognizer.getDecoder().getLm(MODEL_LANGUAGE).addWord("alex was here",4);
                recognizer.startListening(MODEL_LANGUAGE);

                ((TextView) findViewById(R.id.recognition_results)).setText("Recording the language model");
            }
        });
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
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
                activityReference.get().switchDisplayedQuestion(currentQuestionIndex);
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
        if (text.length() > 0) {
            CharSequence temp = ((TextView) findViewById(R.id.resulst_text)).getText();
            ((TextView) findViewById(R.id.resulst_text)).setText(temp + text);
            ((TextView) findViewById(R.id.resulst_text)).setText(text);


            // Detect a list option key use
            if (recognizer.getSearchName().equals(LIST_OPTIONS) ) {
                if (text.equals("set") || text.equals("next") || text.equals("back")) {
                    isListOptionKeyUsed = true;
                    playTextToSpeechNow(text);
                }

                if (text.equals("set")) {
                    listenToAnswerForCurrentQuestion();
                } else if (text.equals("next")) {
                    nextQuestion();
                } else if (text.equals("back")) {
                    previousQuestion();
                }
            }
            else if (recognizer.getSearchName().equals(MODEL_LANGUAGE))
            {
                playTextToSpeechNow(text);
            }
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.resulst_text)).setText("");
        if (hypothesis != null) {
            if (recognizer.getSearchName().equals(MODEL_LANGUAGE))
            {
               // hypothesis.setBestScore(100);
                //hypothesis.setProb(100);
                int tmp  = hypothesis.getProb();
                double res = recognizer.getDecoder().getLogmath().exp(hypothesis.getProb());
                double res2 = recognizer.getDecoder().getLogmath().exp(hypothesis.getBestScore());
                String[] title = {"alex","twenty one","ten"};
                //Config c = recognizer.getDecoder()..getConfig();

                int p = recognizer.getDecoder().getLm(MODEL_LANGUAGE).prob(title);
                //String s = recognizer.getDecoder().getLm(MODEL_LANGUAGE).typeToStr(type);
                double ltitles = recognizer.getDecoder().getLogmath().exp(p);


                int high_score = hypothesis.getBestScore();
                //makeText(getApplicationContext(), high_score, Toast.LENGTH_SHORT).show();
                String text = String.valueOf(high_score);
                // Play the result when it is not a list option key use
                if (!isListOptionKeyUsed) {
                    playTextToSpeechNow(text);
                }
            }
            else {
                String text = hypothesis.getHypstr();
                makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                // Play the result when it is not a list option key use
                if (!isListOptionKeyUsed) {
                    playTextToSpeechNow(text);
                }
            }
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        isListOptionKeyUsed = false;
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if(!recognizer.getSearchName().equals(LIST_OPTIONS)) {
            listenToKeyWords();
        }
    }

    private void nextQuestion(){
        currentQuestionIndex++;

        if(currentQuestionIndex >= questions.size())
            currentQuestionIndex = 0;

        switchDisplayedQuestion(currentQuestionIndex);
    }

    private void previousQuestion(){
        currentQuestionIndex--;

        if(currentQuestionIndex < 0)
            currentQuestionIndex = questions.size() - 1;

        switchDisplayedQuestion(currentQuestionIndex);
    }

    private void switchDisplayedQuestion(int questionIndex) {
        String searchName = (String)questions.keySet().toArray()[questionIndex];

        if(questions.keySet().contains(searchName)){
            String caption = getResources().getString(questions.get(searchName));
            ((TextView) findViewById(R.id.caption_text)).setText(caption);
            playTextToSpeechWhenDoneSpeaking(caption);
        }

        listenToKeyWords();
    }

    private void listenToKeyWords(){
        recognizer.stop();
        recognizer.startListening(LIST_OPTIONS);
        ((TextView) findViewById(R.id.listOptions_text)).setVisibility(View.VISIBLE);
    }

    private void listenToAnswerForCurrentQuestion(){
        String searchName = (String)questions.keySet().toArray()[currentQuestionIndex];

        recognizer.stop();
        recognizer.startListening(searchName, 5000);
        ((TextView) findViewById(R.id.listOptions_text)).setVisibility(View.INVISIBLE);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))




         //       .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);
        recognizer.addNgramSearch(MODEL_LANGUAGE,new File(assetsDir,"0667.lm"));


        //recognizer.

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create multiple keyword-activation search
        File keywords = new File(assetsDir, "keywords.gram");
        recognizer.addKeywordSearch(LIST_OPTIONS, keywords);




        // Create grammar-based search for selection between demos
        File optionsGrammar = new File(assetsDir, "options.gram");
        recognizer.addGrammarSearch(OPTIONS_SEARCH, optionsGrammar);

        // Create grammar-based search for digit recognition
        File digitsGrammar = new File(assetsDir, "digits.gram");
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);

        File booleanKeys = new File(assetsDir, "boolean.gram");
        recognizer.addGrammarSearch(BOOLEAN_SEARCH, booleanKeys);
    }

    private void setupTextToSpeech(){
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void playTextToSpeechNow(String text){
        if(textToSpeech == null)
            return;

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void playTextToSpeechWhenDoneSpeaking(String text){
        if(textToSpeech == null)
            return;

        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        listenToKeyWords();
    }
}
