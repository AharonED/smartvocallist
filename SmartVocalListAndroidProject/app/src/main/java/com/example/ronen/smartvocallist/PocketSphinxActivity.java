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

package com.example.ronen.smartvocallist;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import DataObjects.ChecklistItem;
import DataObjects.Checklist;
import Dialogs.DialogFlow;
import Model.ModelChecklists;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class PocketSphinxActivity extends Activity implements
        RecognitionListener {
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private HashMap<String, String> textToSpeechMap;
    private TextToSpeech textToSpeech;
    private Checklist chk = null;
    private DialogFlow<ChecklistItem> dlg = null;
    private AsyncTask<Void,Void,Void> listenToKeyWordsWaiterTask = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        dlg = new DialogFlow<>();
        String checkListId = (String)getIntent().getExtras().get("checkListId");
        ModelChecklists mdl =  ModelChecklists.getInstance();
        //(Model)getIntent().getSerializableExtra("model");
        chk = mdl.getItemByID(checkListId);
        dlg.items=chk.checklistItems;

        dlg.execute = (item)-> {
            ChecklistItem itm =((ChecklistItem)item);
            String text = itm.getName();
            int stepNumber = dlg.step + 1;
            String caption = "Step " + stepNumber + "/" + dlg.items.size() + ": " + text;
            ((TextView) findViewById(R.id.caption_text)).setText(caption);
            String textToSpeech = "Step " + stepNumber + " out of " + dlg.items.size() + ": " + text;
            playTextToSpeechNow(textToSpeech);
            updateStateBarColors();
            displayAnswerToTheQuestion((ChecklistItem) item);
            listenToKeyWords();
        };

        dlg.set = value -> {
            playTextToSpeechNow(value);
            listenToKeyWords();
            return true;
        };

        dlg.sof  = (item)->{
            String caption ="This is the first item";
            playTextToSpeechNow(caption);
            makeText(getApplicationContext(), caption, Toast.LENGTH_SHORT).show();
            listenToKeyWords();
        };

        dlg.eof  = (item)->{
            String caption = "This is the last item";
            playTextToSpeechNow(caption);
            makeText(getApplicationContext(), caption, Toast.LENGTH_SHORT).show();
            displayAnswerToTheQuestion((ChecklistItem) item);
            listenToKeyWords();
        };

        dlg.readItem = (item)->{
            this.dlg.execute.execute(((ChecklistItem)item));
        };

        dlg.readOptions = (item)->{
            ChecklistItem itm =((ChecklistItem)item);
            StringBuilder optionsString = new StringBuilder();
            optionsString.append("The options are:");

            for (String option : itm.options) {
                optionsString.append(";");
                optionsString.append(option);
            }

            playTextToSpeechNow(optionsString.toString());
            listenToKeyWords();
        };

        dlg.mustAnswerItem = (item)->{
            String text = "You must answer to continue";
            playTextToSpeechNow(text);
            listenToKeyWords();
        };

        textToSpeechMap = new HashMap<String, String>();
        textToSpeechMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        setContentView(R.layout.activity_pocket_sphinx);
        ((TextView) findViewById(R.id.caption_text)).setText("Preparing the recognizer");
        ((TextView) findViewById(R.id.listening_text)).setText("");
        ((TextView) findViewById(R.id.answer_textView)).setText("");

        ((Button)findViewById(R.id.back_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.previous();
            }
        });

        ((Button)findViewById(R.id.next_Button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.next();
            }
        });

        ((Button)findViewById(R.id.options_Button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.setCommand("options", "");
            }
        });

        initStateBar();

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

    private void initStateBar() {
        ArrayList<ChecklistItem> items = dlg.items;
        LinearLayout stateBar = findViewById(R.id.StateLinearLayout);
        stateBar.setVisibility(View.INVISIBLE);

        for (int itemIndex=0; itemIndex<items.size(); itemIndex++){
            Button button = new Button(getApplicationContext());
            button.setAllCaps(false);
            String buttonText = String.valueOf(itemIndex + 1);
            button.setText(buttonText);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button)v;
                    int buttonTextNumber = Integer.parseInt(button.getText().toString());
                    int itemIndex = buttonTextNumber - 1;
                    dlg.jumpToStep(itemIndex);
                }
            });

            stateBar.addView(button);
        }
    }

    private void updateStateBarColors(){
        LinearLayout stateBar = findViewById(R.id.StateLinearLayout);
        int lightBlueColor = Color.rgb(0x35,0xd3,0xd2);
        int BlueColor = Color.rgb(0x35,0x92,0xd2);

        for(int itemIndex=0; itemIndex<stateBar.getChildCount(); itemIndex++) {
            View button = stateBar.getChildAt(itemIndex);
            int color;

            if(itemIndex%2 == 0)
                color = BlueColor;
            else
                color = lightBlueColor;

            if(itemIndex == dlg.step){
                ShapeDrawable shapedrawable = new ShapeDrawable();
                shapedrawable.setShape(new RectShape());
                shapedrawable.getPaint().setColor(color);
                shapedrawable.getPaint().setStrokeWidth(24f);
                shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                button.setBackground(shapedrawable);
            }
            else{
                button.setBackgroundColor(color);
            }
        }

        if(stateBar.getVisibility() != View.VISIBLE)
            stateBar.setVisibility(View.VISIBLE);
    }

    private void displayAnswerToTheQuestion(ChecklistItem item) {
        String answerToQuestion = item.getResult();

        if(answerToQuestion == null || answerToQuestion.isEmpty()) {
            ((TextView) findViewById(R.id.answer_textView)).setText("*No answer*");
        }
        else {
            ((TextView) findViewById(R.id.answer_textView)).setText(answerToQuestion);
        }
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

        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if(listenToKeyWordsWaiterTask != null &&
                listenToKeyWordsWaiterTask.getStatus() == AsyncTask.Status.RUNNING){
            listenToKeyWordsWaiterTask.cancel(true);
        }

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }

        try {
            Assets assets = new Assets(this);
            File assetDir = assets.syncAssets();
            dlg.removeKeyWordsFiles(assetDir);
        } catch (IOException e) {
            e.printStackTrace();
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
        String[] textSplited = text.split(" ");

        for (String word:textSplited) {
            if(dlg.keywords.contains(word)) {
                makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
                dlg.setCommand(word, "");
                break;
            }
            else if(dlg.items.get(dlg.step).options.contains(word)) {
                dlg.items.get(dlg.step).setResult(word);
                makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
                dlg.next();
                break;
            }
            else {
                playTextToSpeechNow(word + " is not an answer");
                listenToKeyWords();
            }
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
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
            if(recognizer != null){
                recognizer.stop();
                //recognizer.startListening(dlg.step + ".lst");
                recognizer.startListening(dlg.getCurrentItemKeyWordsFileName());
                ((TextView) findViewById(R.id.listening_text)).setText("Listening :)");
            }
        }else{
            listenToKeyWordsWaiterTask = new ListenToKeyWordsWaiter();
            listenToKeyWordsWaiterTask.execute();
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

        // Create multiple keyword-activation search
        List<File> createdFiles = dlg.createKeyWordsFiles(assetsDir);

        for (File file : createdFiles) {
            recognizer.addKeywordSearch(file.getName(), file);
        }

        // Create grammar-based search for digit recognition
        //      File digitsGrammar = new File(assetsDir, "digits.gram");
        //      recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
    }

    private void setupTextToSpeech(){
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {

                textToSpeech.setLanguage(Locale.US);

                String caption = chk.getName();
                dlg.setCommand("read","");
            }
        });
    }

    private void playTextToSpeechNow(String text){
        if(textToSpeech == null || text == "")
            return;

        recognizer.stop();
        ((TextView) findViewById(R.id.listening_text)).setText("");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, textToSpeechMap);
        while(!textToSpeech.isSpeaking());
    }

    private void playTextToSpeechWhenDoneSpeaking(String text){
        if(textToSpeech == null || text == "")
            return;

        recognizer.stop();
        ((TextView) findViewById(R.id.listening_text)).setText("");
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, textToSpeechMap);
        while(!textToSpeech.isSpeaking());
    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        listenToKeyWords();
    }

    class ListenToKeyWordsWaiter extends AsyncTask<Void,Void,Void>{
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
