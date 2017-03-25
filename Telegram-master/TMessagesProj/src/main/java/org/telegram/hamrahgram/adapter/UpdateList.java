package org.telegram.hamrahgram.adapter;
/**
 * <h1>Java UpdateList Class in org.telegram.ui</h1>
 * @author Hossein Moradi
 * @since 1394
 *
 */
//Class By Hossein
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.util.ArrayList;

public class UpdateList extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> web;
    private final ArrayList<String> imageId;
    public UpdateList(Activity context, ArrayList<String> web, ArrayList<String> imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    /**
     * <p>return list row of update avatar of special contacts</p>
     * @param position
     * @param view
     * @param parent
     * @return View
     * @since org.telegram.hamrahgram.ui.UpdateActivity
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        txtTitle.setText(web.get(position));
        txtTitle.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        File file = new File(imageId.get(position));
        if (file.exists()) {
            Bitmap bmp1 = BitmapFactory.decodeFile(imageId.get(position));
            imageView.setImageBitmap(bmp1);
        } else {
            imageView.setImageResource(R.drawable.unknown);
        }
        return rowView;
    }
}