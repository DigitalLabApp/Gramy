package org.telegram.hamrahgram.util;

import android.graphics.drawable.AnimationDrawable;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

/**
 * @deprecated
 */
public class ActionbarAnimation {
    private static final int spiderDuration = 50;
    private static final int ghosDuration = 50;

    public static AnimationDrawable getSpiderAnimation() {
        AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider1), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider2), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider3), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider4), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider5), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider6), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider7), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider8), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider9), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider10), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider11), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider12), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider13), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider14), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider15), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider16), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider17), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider18), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider19), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider20), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider21), spiderDuration);
        animation.addFrame(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.spider22), spiderDuration);
        animation.setOneShot(false);
        animation.setVisible(true, true);
        return animation;
    }


}
