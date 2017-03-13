/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerActionCell extends FrameLayout {

    private TextView textView;
    private View divider; //Hossein


    public DrawerActionCell(Context context) {
        super(context);
        //Hossein


        divider = new View(context);
        divider.setBackgroundColor(Color.parseColor("#26d9d9d9"));//0xffd9d9d9
        divider.setVisibility(View.GONE);
        //-----------
        textView = new TextView(context);
        textView.setTextColor(0xff444444);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        //Hossein

            textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        //--------------------
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(34));
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 14, 0, 16, 0));
        //Hossein
        addView(divider, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 1, Gravity.BOTTOM, 0, 0, 0, 0));
        //------------------------------
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
    }

    //Hossein-----------------------------------
    public void setTextAndIcon(String text, int resId) {
        try {
            Drawable d1 = ApplicationLoader.mContext.getResources().getDrawable(resId);
            d1.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            textView.setText(text);
            textView.setCompoundDrawablesWithIntrinsicBounds(d1, null, null, null);
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
    }

    //-----------------------------------------------------

    //Hossein

    public void setDivier(boolean value) {
        this.divider.setVisibility(value ? View.VISIBLE : View.GONE);


    }
    public void setTextAndIcon(SpannableStringBuilder text, int resId) {
        try {
            Drawable d1 = ApplicationLoader.mContext.getResources().getDrawable(resId);
            d1.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            textView.setText(text);
            textView.setCompoundDrawablesWithIntrinsicBounds(d1, null, null, null);

        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }

    }

    //----------=


}
