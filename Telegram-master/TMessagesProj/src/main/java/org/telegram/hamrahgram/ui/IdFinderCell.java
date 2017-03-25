package org.telegram.hamrahgram.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class IdFinderCell extends RelativeLayout {
    private CircleImageView imageView;
    private TextView textView;
    public IdFinderCell(Context context) {
        super(context);


        RelativeLayout.LayoutParams imageparam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        imageparam.addRule(ALIGN_PARENT_RIGHT);

        setGravity(Gravity.RIGHT);
        addView(imageView);
        addView(textView);



    }
}
