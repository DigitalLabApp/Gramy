package org.telegram.hamrahgram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.telegram.hamrahgram.IFloatingAction;
import org.telegram.hamrahgram.database.Database;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.ChannelCreateActivity;
import org.telegram.ui.ChannelIntroActivity;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.LaunchActivity;

import static org.telegram.ui.LaunchActivity.presentFragment;


public class FloatingActionDialog extends Dialog {
    private IFloatingAction listener;
    private int rowCounter = -1;
    private ActionBarMenuItem promotionItem;
    private ActionBarMenuItem soulItem;
    private Drawable soul_icon;
    private ListAdapter listAdapter;
    private ActionBarMenu menu;
    private TLRPC.User maxUnread;
    private TLRPC.User last;
    private ListView listView;
    private Context context;
    private ImageView close;

    public FloatingActionDialog(Context context, IFloatingAction listener) {
        super(context, R.style.DialogSlideAnim);
        this.listener = listener;
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                listener.onDialogClose();
            }
        });
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                listener.onDialogClose();
            }
        });
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        this.context = context;
        getWindow().setLayout(width, height);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RelativeLayout linearLayout = new RelativeLayout(context); /*{
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                Paint paint = new Paint();
                paint.setColor(Color.TRANSPARENT);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
                canvas.drawCircle(DialogsActivity.floatingX, DialogsActivity.floatingY, AndroidUtilities.dp(25), paint);

            }
        };*/
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionDialog.this.dismiss();
            }
        });
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //    linearLayout.setGravity(Gravity.CENTER_VERTICAL);

        linearLayout.setBackgroundColor(Color.parseColor("#CC000000"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //   params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.setMargins(AndroidUtilities.dp(15), AndroidUtilities.dp(30), AndroidUtilities.dp(5), 0);
        listView = new ListView(context);
        listView.setLayoutParams(params);
        close = new ImageView(context);
        close.setLayoutParams(params2);
        close.setImageResource(R.drawable.ic_close_float);
        close.setScaleType(ImageView.ScaleType.CENTER);
        //  linearLayout.addView(close);
        linearLayout.addView(close);
        linearLayout.addView(listView);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listAdapter = new ListAdapter(context);
        listView.setAdapter(listAdapter);
        setContentView(linearLayout);
        if (LocaleController.isRTL)
            listView.setX(DialogsActivity.floatingRawX);
        else
            listView.setPadding(AndroidUtilities.dp(10), 0, 0, 0);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            LaunchActivity.groupCreatePermision = MessagesController.isFeatureEnabled("chat_create", DialogsActivity.fragmentHolder);
            LaunchActivity.channelCreatePermision = MessagesController.isFeatureEnabled("broadcast_create", DialogsActivity.fragmentHolder);
            if (i == 5) {
                Bundle args = new Bundle();
                args.putBoolean("destroyAfterSelect", true);
                presentFragment(new ContactsActivity(args));

            } else if (i == 4) {
                if (!LaunchActivity.groupCreatePermision) return;
                presentFragment(new GroupCreateActivity());

            } else if (i == 3) {
                if (!LaunchActivity.channelCreatePermision) return;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                if (preferences.getBoolean("channel_intro", false)) {
                    Bundle args2 = new Bundle();
                    args2.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(args2));
                } else {
                    presentFragment(new ChannelIntroActivity());
                    preferences.edit().putBoolean("channel_intro", true).commit();
                }

            } else if (i == 2) {
                Bundle args = new Bundle();
                args.putBoolean("onlyUsers", true);
                args.putBoolean("destroyAfterSelect", true);
                args.putBoolean("createSecretChat", true);
                args.putBoolean("allowBots", false);
                presentFragment(new ContactsActivity(args));
            } else if (i == 0) {
                MessagesController.getInstance().openChatOrProfileWith(maxUnread, null, DialogsActivity.fragmentHolder, 1, false);

            } else if (i == 1) {
                MessagesController.getInstance().openChatOrProfileWith(last, null, DialogsActivity.fragmentHolder, 1, false);
            }
            dismiss();
        });
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            rowCounter = -1;
            return 6;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            if (i <= 1) {


                if (i == 0) {
                    if (MessagesController.getInstance().getMaxUserUnread() > 0) {
                        TLRPC.User user = MessagesController.getInstance().getUser(MessagesController.getInstance().getMaxUserUnread());//Database.getInstance().getLastMessage(context));
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeFile(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE) + "/" + user.photo.photo_small.volume_id + "_" + user.photo.photo_small.local_id + ".jpg");
                        } catch (Exception e) {
                        }
                        if (bitmap == null)
                            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_user);

                        String displayName = user.first_name;
                        if (user.last_name != null)
                            displayName = displayName + " " + user.last_name;
                        else displayName = user.first_name;
                        view = createView(displayName, getCircularBitmap(bitmap));
                        maxUnread = user;
                    } else
                        view = new EmptyCell(mContext);
                } else if (i == 1) {
                    TLRPC.User user = MessagesController.getInstance().getUser(Database.getInstance().getLastMessage(mContext));//Database.getInstance().getLastMessage(context));
                    if (user != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeFile(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE) + "/" + user.photo.photo_small.volume_id + "_" + user.photo.photo_small.local_id + ".jpg");
                        } catch (Exception e) {
                        }
                        if (bitmap == null)
                            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_user);
                        String displayName = "";
                        if (user.last_name != null)
                            displayName = user.first_name + " " + user.last_name;
                        else displayName = user.first_name;
                        view = createView(displayName, getCircularBitmap(bitmap));

                        last = user;
                    } else view = new EmptyCell(mContext);
                }
            } else {
                rowCounter++;

                String[] items = ApplicationLoader.mContext.getResources().getStringArray(R.array.floatingactions);
                int[] icons = {R.drawable.ic_secret_chat, R.drawable.ic_new_channel, R.drawable.ic_new_group, R.drawable.ic_new_chat};
                Drawable drawable = mContext.getResources().getDrawable(icons[rowCounter]);
                view = createView(items[rowCounter], drawable);
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0 || i == 1) {
                return 0;
            } else return 1;

        }

        @Override
        public int getViewTypeCount() {
            return 7;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;
        if (bitmap == null) return null;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float r = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_new_channel);
        return getResizedBitmap(output, bm.getWidth(), bm.getHeight());

    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private View createView(String text, int res) {
        LinearLayout view = new LinearLayout(context);
        ImageView imageView = new ImageView(context);
        TextView textView = new TextView(context);
        view.setOrientation(LinearLayout.HORIZONTAL);
        imageView.setImageResource(res);
        textView.setText(text);
        textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        textView.setTextColor(Color.WHITE);
        view.addView(imageView);
        view.addView(textView);
        ((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).setMargins(0, AndroidUtilities.dp(10), 0, 0);
        view.setPadding(AndroidUtilities.dp(25), 0, 0, 0);
        return view;
    }

    private View createView(String text, Bitmap bitmap) {
        LinearLayout view = new LinearLayout(context);
        ImageView imageView = new ImageView(context);
        TextView textView = new TextView(context);
        view.setOrientation(LinearLayout.HORIZONTAL);
        imageView.setImageBitmap(bitmap);
        textView.setText(text);
        textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        textView.setTextColor(Color.WHITE);
        view.addView(imageView);
        view.addView(textView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        ((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).setMargins(AndroidUtilities.dp(10), 0, 0, 0);
        view.setPadding(0, AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10));
        return view;
    }

    private View createView(String text, Drawable drawable) {
        LinearLayout view = new LinearLayout(context);
        ImageView imageView = new ImageView(context);
        TextView textView = new TextView(context);
        view.setOrientation(LinearLayout.HORIZONTAL);
        imageView.setImageDrawable(drawable);
        textView.setText(text);
        textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        view.addView(imageView);
        view.addView(textView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        ((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).setMargins(AndroidUtilities.dp(10), 0, 0, 0);
        view.setPadding(0, AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10));
        return view;
    }

}
