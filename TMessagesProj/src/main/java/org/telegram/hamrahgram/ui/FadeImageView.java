package org.telegram.hamrahgram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.widget.ImageView;

/**
 * Created by User on 14/12/2016.
 */

public class FadeImageView extends ImageView {
    private static final int FADE_MILLISECONDS = 3000; // 3 second fade effect
    private static final int FADE_STEP = 120;          // 120ms refresh

    // Calculate our alpha step from our fade parameters
    private static final int ALPHA_STEP = 255 / (FADE_MILLISECONDS / FADE_STEP);

    // Initializes the alpha to 255
    private Paint alphaPaint = new Paint();

    // Need to keep track of the current alpha value
    private int currentAlpha = 255;
    public FadeImageView(Context context) {
        super(context);
    }

}
