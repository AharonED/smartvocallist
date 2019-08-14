package com.example.ronen.smartvocallist.Model;

import android.speech.tts.TextToSpeech;

import com.example.ronen.smartvocallist.Controller.MyApplication;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechApi {
    public SetupSuccessful setupSuccessfulTask;
    private HashMap<String, String> textToSpeechMap;
    private TextToSpeech textToSpeech;

    public interface SetupSuccessful {
        public void SuccessTask();
    }

    public TextToSpeechApi(){
        textToSpeechMap = new HashMap<String, String>();
        textToSpeechMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
    }

    public void setupTextToSpeech(){
        textToSpeech = new TextToSpeech(MyApplication.getContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);

                if(setupSuccessfulTask != null){
                    setupSuccessfulTask.SuccessTask();
                }
            }
        });
    }

    public void shutdown(){
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

    public boolean isSpeaking(){
        return textToSpeech.isSpeaking();
    }

    public void speakNow(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, textToSpeechMap);
    }

    public void speakWhenFinished(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, textToSpeechMap);
    }
}
