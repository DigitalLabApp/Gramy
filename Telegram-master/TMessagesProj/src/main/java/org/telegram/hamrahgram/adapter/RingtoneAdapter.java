package org.telegram.hamrahgram.adapter;
//Hossein
/**
 * <h1>Adapter For HelpActivity</h1>
 * @see org.telegram.ui.HelpActivity
 * @since 1394
 * @author Hossein Moradi
 */

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

public class RingtoneAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;

    public RingtoneAdapter(Activity context,
                           String[] web, Integer[] imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

    }

    /**
     * <p>Overided getView Item Of ArrayAdapter Class</p>
     * @param position
     * @param view
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ApplicationLoader.textSize);
        txtTitle.setText(web[position]);
        txtTitle.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        imageView.setImageResource(imageId[position]);
        imageView.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);


        return rowView;
    }
}