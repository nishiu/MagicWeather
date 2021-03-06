package com.frostsowner.magic.weather.logic;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.frostsowner.magic.weather.view.WaveView;
import com.umeng.commonsdk.debug.W;

import java.util.ArrayList;
import java.util.List;

public class WaveHelper {

    public static final int AQI = 1001;
    public static final int WEATHER_BACKGROUND = 1002;

    private WaveView mWaveView;
    private AnimatorSet mAnimatorSet;

    public WaveHelper(WaveView waveView) {
        this(waveView,WEATHER_BACKGROUND);
    }

    public WaveHelper(WaveView waveView,int type){
        mWaveView = waveView;
        switch (type){
            case AQI:
                initAqiAnimation();
                break;
            default:
                initBackgroundAnimation();
                break;
        }
    }

    public void start() {
        mWaveView.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    private void initAqiAnimation() {
        List<Animator> animators = new ArrayList<>();

        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f);
//        waveShiftAnim.setRepeatCount(1);
//        waveShiftAnim.setRepeatMode(ValueAnimator.REVERSE);
        waveShiftAnim.setDuration(10500);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

        // vertical animation.
        // water level increases from 0 to center of WaveView
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveView, "waterLevelRatio", 0f, 0.6f);
        waterLevelAnim.setDuration(5000);
//        waterLevelAnim.setRepeatCount(1);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        animators.add(waterLevelAnim);

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", 0.0001f, 0.05f);
//        amplitudeAnim.setRepeatCount(1);
        amplitudeAnim.setDuration(5000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animators.add(amplitudeAnim);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
    }

    private void initBackgroundAnimation() {
        List<Animator> animators = new ArrayList<>();
        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
//        waveShiftAnim.setRepeatMode(ValueAnimator.REVERSE);
        waveShiftAnim.setDuration(20000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

        // vertical animation.
        // water level increases from 0 to center of WaveView
//        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
//                mWaveView, "waterLevelRatio", 0f, 0.65f);
//        waterLevelAnim.setDuration(5000);
//        waterLevelAnim.setRepeatCount(99);
//        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
//        animators.add(waterLevelAnim);

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", 0.018f, 0.15f);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(10000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animators.add(amplitudeAnim);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
    }

    public void cancel() {
        if (mAnimatorSet != null) {
//            mAnimatorSet.cancel();
            mAnimatorSet.end();
        }
    }
}
