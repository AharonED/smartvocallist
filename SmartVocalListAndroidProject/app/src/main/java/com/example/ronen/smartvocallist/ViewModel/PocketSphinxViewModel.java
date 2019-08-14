package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.Model.TextToSpeechApi;
import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.example.ronen.smartvocallist.Dialogs.DialogFlow;
import com.example.ronen.smartvocallist.Model.ModelChecklists;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;


public class PocketSphinxViewModel extends ViewModel {
    public boolean firstSpeaking = true;
    public boolean isReadingQuestion = false;
    public volatile boolean isWaitingTaskRunning = false;

    private Checklist checklist;
    private DialogFlow<ChecklistItem> dlg;
    private TextToSpeechApi textToSpeech;
    private SpeechRecognizer recognizer;

    public void loadCheckList(String checkListId){
        checklist = mdl.getItemByID(checkListId);
    }

    public void initDialogFlow(Checklist checklist){
        dlg = new DialogFlow<>();
        dlg.items = checklist.getChecklistItems();
    }

    public void initTextToSpeech(){
        textToSpeech = new TextToSpeechApi();
    }

    public void initRecognizer(RecognitionListener listener, File assetsDir)  throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .getRecognizer();
        recognizer.addListener(listener);

        // Create multiple keyword-activation search
        List<File> createdFiles = dlg.createKeyWordsFiles(assetsDir);

        for (File file : createdFiles) {
            recognizer.addKeywordSearch(file.getName(), file);
        }
    }

    public SpeechRecognizer getRecognizer(){
        return recognizer;
    }

    public TextToSpeechApi getTextToSpeech(){
        return textToSpeech;
    }

    public DialogFlow<ChecklistItem> getDialogFlow(){
        return dlg;
    }

    public ModelChecklists mdl = ModelChecklists.getInstance();

    public Checklist getChecklist() {
        return checklist;
    }
}
