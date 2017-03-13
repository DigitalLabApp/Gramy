package org.telegram.hamrahgram.ui;
//Activity By Hossein
/**
 * <h1>java UpdateActivity class in org.telegram.ui</h1>
 *
 * @author Hossein Moradi
 * @since 1394
 */

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.telegram.hamrahgram.database.Database;
import org.telegram.hamrahgram.adapter.UpdateList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

import java.util.ArrayList;
public class UpdateActivity extends Activity {
    ListView list;
    /**
     * <p>show special contacts photo from database</p>
     *
     * @param savedInstanceState

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(ApplicationLoader.applicationTheme)));
        getActionBar().setTitle("لیست آپدیت تصاویر مخاطبین");
        setContentView(R.layout.helpactivity);

        try {

            Database Database = new Database();
            ArrayList<String> user = new ArrayList<>();
            ArrayList<String> photo = new ArrayList<>();
            if (org.telegram.hamrahgram.database.Database.select) {
                if (Database.getUserUpdateById(this, org.telegram.hamrahgram.database.Database.current_user) == null && Database.getUserPhotoUpdateById(this, org.telegram.hamrahgram.database.Database.current_user) == null) {
                    Toast.makeText(this, "هنوز مخاطب خاصی عکس خود را تغییر نداده است", Toast.LENGTH_LONG).show();

                } else {
                    user = Database.getUserUpdateById(this, org.telegram.hamrahgram.database.Database.current_user);
                    photo = Database.getUserPhotoUpdateById(this, org.telegram.hamrahgram.database.Database.current_user);
                    UpdateList adapter = new UpdateList(UpdateActivity.this, user, photo);
                    list = (ListView) findViewById(R.id.listView3);
                    list.setAdapter(adapter);
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) list.getLayoutParams();
                    marginLayoutParams.setMargins(0, 50, 0, 0);
                }
            } else {
                if (Database.getFavoriteUsers(this) != null && Database.getFavoriteUsers(this).size() > 0) {
                    user = Database.getFavoriteUsers(this);
                    photo = Database.getUpdatedPhotos(this);

                    UpdateList adapter = new UpdateList(UpdateActivity.this, user, photo);
                    list = (ListView) findViewById(R.id.listView3);
                    list.setAdapter(adapter);
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) list.getLayoutParams();
                    marginLayoutParams.setMargins(0, 50, 0, 0);
                } else {
                    Toast.makeText(this, "هنوز مخاطب خاصی عکس خود را تغییر نداده است", Toast.LENGTH_LONG).show();
                }
            }
            org.telegram.hamrahgram.database.Database.select = false;
        } catch (Exception e) {
            Toast.makeText(this, "عکسی یافت نشد", Toast.LENGTH_LONG).show();

        }
    }
}

