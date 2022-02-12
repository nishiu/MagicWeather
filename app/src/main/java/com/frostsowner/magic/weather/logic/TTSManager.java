package com.frostsowner.magic.weather.logic;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class TTSManager implements TextToSpeech.OnInitListener {

    private boolean isInit = false;
    private Context context;
    private TextToSpeech textToSpeech;

    public TTSManager(Context context) {
        this.context = context;
        init();
    }

    public void init(){
        textToSpeech = new TextToSpeech(context,this);
    }

    public void speak(String msg){
        textToSpeech.speak(msg,TextToSpeech.QUEUE_FLUSH,null,"test");
    }

    @Override
    public void onInit(int status) {
        isInit = status == TextToSpeech.SUCCESS;
        Log.d("ijimu","init status : "+status);
    }

    public boolean isInit(){
        return isInit;
    }

    public void stop(){
        if(textToSpeech != null && isInit){
            textToSpeech.stop();
        }
    }

    public void shutdown(){
        if(textToSpeech != null && isInit){
            textToSpeech.stop();
            textToSpeech.shutdown();
            Log.e("ijimu","tts shutdown");
        }
    }

    public void addListener(UtteranceProgressListener utteranceProgressListener){
        if(utteranceProgressListener == null)return;
        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
    }
}
