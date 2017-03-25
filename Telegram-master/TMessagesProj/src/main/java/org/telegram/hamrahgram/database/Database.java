package org.telegram.hamrahgram.database;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.widget.Toast;

import org.telegram.hamrahgram.model.Analytics;
import org.telegram.hamrahgram.ui.Alert;
import org.telegram.hamrahgram.util.GhostOption;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.DialogsActivity;

import java.io.File;
import java.util.ArrayList;

public class Database {
    private static Database instance = null;
    public static String current_user;
    public static boolean select;

    public static Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }

    public void addToFavorite(String user_id, Context ctx) {
        if (user_id != null && ctx != null) {
            SQLiteDatabase db;
            db = ctx.openOrCreateDatabase("db_fav", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR);");
            db.execSQL("INSERT INTO fav(ID) VALUES('" + user_id + "')");
            ApplicationLoader.favoriteDialogs.add(user_id);
            MessagesController.getInstance().loadDialogs(-1, 1, !MessagesController.getInstance().dialogsEndReached);
            Toast.makeText(ctx, ctx.getResources().getString(R.string.addfav), Toast.LENGTH_LONG).show();
        }
    }

    public void addSetting(String SID, String Sval, Context ctx) {
        SharedPreferences.Editor shareEdit = ctx.getSharedPreferences("dialogs", Context.MODE_PRIVATE).edit();
        SharedPreferences shareRead = ctx.getSharedPreferences("dialogs", Context.MODE_PRIVATE);
        createDatabase(ctx);
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        db.execSQL("UPDATE setting SET SID='" + SID + "', Sval='" + Sval + "'  WHERE SID='" + SID + "';");
        if (SID.equals("7") && Sval.equals("1")) {
            GhostOption.getInstance().setGhostMode(true);
            if (shareRead.getBoolean("showsoulhelpdialog", true)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(DialogsActivity.fragmentHolder.getParentActivity());
                        alert.setTitle(ctx.getResources().getString(R.string.SoulModeActived));
                        alert.setMessage(ctx.getResources().getString(R.string.ghosthelptoast));
                        alert.setCheckBoxText(ctx.getResources().getString(R.string.DontShowAgain));
                        alert.addActionListener(ctx.getResources().getString(R.string.okdialog), new Alert.ActionListener() {
                            @Override
                            public void doAction(boolean enable) {
                                return;
                            }
                        });
                        alert.addCheckBoxActionListener(new Alert.ActionListener() {
                            @Override
                            public void doAction(boolean enable) {
                                shareEdit.putBoolean("showsoulhelpdialog", !enable).commit();
                            }
                        });
                        alert.show();
                    }
                }, 400);
            } else {
                Toast.makeText(ctx, ctx.getResources().getString(R.string.rooh), Toast.LENGTH_LONG).show();
            }
        }
        if (SID.equals("7") && Sval.equals("0")) {
            GhostOption.getInstance().setGhostMode(false);
            if (shareRead.getBoolean("showsoulhelpdialog", true)) {
                Alert alert = new Alert(DialogsActivity.fragmentHolder.getParentActivity());
                alert.setTitle(ctx.getResources().getString(R.string.SoulModeDeactived));
                alert.setMessage(ctx.getResources().getString(R.string.exitghostmodetoast));
                alert.setCheckBoxText(ctx.getResources().getString(R.string.DontShowAgain));
                alert.addActionListener(ctx.getResources().getString(R.string.okdialog), new Alert.ActionListener() {
                    @Override
                    public void doAction(boolean enable) {
                        return;
                    }
                });
                alert.addCheckBoxActionListener(new Alert.ActionListener() {
                    @Override
                    public void doAction(boolean enable) {
                        shareEdit.putBoolean("showsoulhelpdialog", !enable).commit();
                    }
                });
                alert.show();
            } else {
                Toast.makeText(ctx, ctx.getResources().getString(R.string.soulmode_disable), Toast.LENGTH_LONG).show();
            }

        }
    }

    public void createDatabase(Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS setting(SID VARCHAR,Sval VARCHAR);");
        Cursor c = db.rawQuery("SELECT * FROM setting", null);
        if (c.getCount() < 1) {
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('1','0' )");
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('2','0' )"); //font
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('3','0' )"); //theme
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('4','0' )"); //perview sticker
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('5','0' )"); //hidden number
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('6','0' )"); //hide mode
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('7','0' )"); //halate rooh
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('8','0' )"); //hidden typing
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('9','0' )"); //forward bdoone naghlo ghol
            db.execSQL("INSERT INTO setting(SID,Sval) VALUES('10','0' )"); //auto download
            db.close();
        }
    }

    public String readSetting(String SID, Context ctx) {
        createDatabase(ctx);
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM setting", null);
        while (c.moveToNext()) {
            if (c.getString(0).toString().equals(SID)) return c.getString(1).toString();
        }
        return "Not Found";
    }

    public void deleteFromFavorite(String user_id, Context ctx) {
        if (user_id != null && ctx != null) {
            SQLiteDatabase db;
            db = ctx.openOrCreateDatabase("db_fav", Context.MODE_PRIVATE, null);
            db.execSQL("DELETE FROM fav WHERE ID='" + user_id + "'");
            db.close();
            ApplicationLoader.favoriteDialogs.remove(user_id);
            Toast.makeText(ctx, ctx.getResources().getString(R.string.delfav), Toast.LENGTH_SHORT).show();
            MessagesController.getInstance().loadDialogs(-1, 1, !MessagesController.getInstance().dialogsEndReached);
        }
    }

    public void hideDialogById(String user_id, Context ctx) {
        if (user_id != null && ctx != null && !ApplicationLoader.hiddenDialogs.contains(user_id)) {
            ApplicationLoader.hiddenDialogs.add(user_id);
            SQLiteDatabase db;
            db = ctx.openOrCreateDatabase("db_hidden_chat", Context.MODE_PRIVATE, null);
            db.execSQL("INSERT INTO hide(ID) VALUES('" + user_id + "')");
            db.close();
            MessagesController.getInstance().loadDialogs(0, 0, false);
            Toast.makeText(ctx, ctx.getResources().getString(R.string.hided), Toast.LENGTH_LONG).show();
            if (checkHiddenMode(ctx)) {
                setHiddenMode(ctx, false);
                DialogsActivity.restartApplication(ctx, 1);
            }
        }
    }

    public boolean checkForHidden(String user_id, Context ctx) {
        return ApplicationLoader.hiddenDialogs.contains(user_id);
    }

    public void deleteFromHidden(String user_id, Context ctx) {
        if (user_id != null & ctx != null) {
            SQLiteDatabase dbh;
            dbh = ctx.openOrCreateDatabase("db_hidden_chat", Context.MODE_PRIVATE, null);
            dbh.execSQL("DELETE FROM hide WHERE ID='" + user_id + "'");
            dbh.close();
            ApplicationLoader.hiddenDialogs.remove(user_id);
            Toast.makeText(ctx, ctx.getResources().getString(R.string.visabled), Toast.LENGTH_LONG).show();
        }
    }

    public void addToFavoriteContacts(String user_id, Context ctx) {
        if (user_id != null && ctx != null) {
            SQLiteDatabase db;
            db = ctx.openOrCreateDatabase("db_q", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS q(ID VARCHAR);");
            db.execSQL("INSERT INTO q(ID) VALUES('" + user_id + "')");
            db.close();
            SQLiteDatabase xdb;
            xdb = ctx.openOrCreateDatabase("db_rington_contacts", Context.MODE_PRIVATE, null);
            xdb.execSQL("CREATE TABLE IF NOT EXISTS main(user VARCHAR , rington VARCHAR)");
            Cursor c = xdb.rawQuery("SELECT * FROM main WHERE user='" + user_id + "'", null);
            if (c.getCount() < 1) {
                xdb.execSQL("INSERT INTO main(user,rington) VALUES('" + user_id + "',' ')");
                xdb.close();
            } else
                xdb.close();
            ApplicationLoader.specificContacts.add(user_id);
            Toast.makeText(ctx, ctx.getResources().getString(R.string.adddlgmessage), Toast.LENGTH_LONG).show();
        }
    }

    public void deleteFromFavoriteContacts(String user_id, Context ctx) {
        if (user_id != null && ctx != null) {
            SQLiteDatabase db;
            db = ctx.openOrCreateDatabase("db_q", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS q(ID VARCHAR);");
            db.execSQL("DELETE FROM q WHERE ID='" + user_id + "'");
            db.close();
            ApplicationLoader.specificContacts.remove(user_id);
            Toast.makeText(ctx, ctx.getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<String> getFavoriteUsers(Context ctx) {
        ArrayList<String> Temp = new ArrayList<>();
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_fav_photo", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR , name VARCHAR , lname VARCHAR ,date_update VARCHAR ,path VARCHAR,long_date VARCHAR);");
        Cursor c = db.rawQuery("SELECT * FROM fav", null);
        if (c.getCount() < 1) {
            return null;
        }
        while (c.moveToNext()) {
            Temp.add(c.getString(1));
        }
        db.close();
        return Temp;
    }

    public ArrayList<String> getUpdatedPhotos(Context ctx) {
        ArrayList<String> Temp = new ArrayList<>();
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_fav_photo", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR , name VARCHAR , lname VARCHAR ,date_update VARCHAR ,path VARCHAR,long_date VARCHAR);");
        Cursor c = db.rawQuery("SELECT * FROM fav", null);
        if (c.getCount() < 1) {
            return null;
        }
        while (c.moveToNext()) {
            Temp.add(c.getString(4));
        }
        db.close();
        return Temp;
    }

    public ArrayList<String> getUserUpdateById(Context ctx, String ID) {
        ArrayList<String> Temp = new ArrayList<>();
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_fav_photo", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR , name VARCHAR , lname VARCHAR ,date_update VARCHAR ,path VARCHAR,long_date VARCHAR);");
        Cursor c = db.rawQuery("SELECT * FROM fav WHERE ID ='" + ID + "'", null);
        if (c.getCount() < 1) {
            return null;
        }
        while (c.moveToNext()) {
            Temp.add(c.getString(3));
        }
        db.close();
        return Temp;
    }

    public ArrayList<String> getUserPhotoUpdateById(Context ctx, String ID) {
        ArrayList<String> Temp = new ArrayList<>();
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_fav_photo", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR , name VARCHAR , lname VARCHAR ,date_update VARCHAR ,path VARCHAR,long_date VARCHAR);");
        Cursor c = db.rawQuery("SELECT * FROM fav WHERE ID ='" + ID + "'", null);
        if (c.getCount() < 1) {
            return null;
        }
        while (c.moveToNext()) {
            Temp.add(c.getString(4));
        }
        db.close();
        return Temp;
    }

    public void setRingtonForContact(String user_id, Context ctx, String rington) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_rington_contacts", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS main(user VARCHAR , rington VARCHAR)");
        db.execSQL("UPDATE main SET rington='" + rington + "' WHERE user='" + user_id + "'");
        db.close();
    }

    public boolean checkForRington(String user, Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_rington_contacts", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS main(user VARCHAR , rington VARCHAR)");
        Cursor c1 = db.rawQuery("SELECT * FROM main WHERE user='" + user + "'", null);
        return c1.getCount() > 0;
    }

    public void deleteRingtonByUserId(String user_id, Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_rington_contacts", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS main(user VARCHAR , rington VARCHAR)");
        db.execSQL("DELETE FROM main WHERE user='" + user_id + "'");
        db.close();
    }

    public void addAppTheme(String color_code, Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("themes", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS theme(color VARCHAR);");
        db.execSQL("INSERT INTO theme(color) VALUES('" + color_code + "');");
        db.close();
    }

    public void deleteAppTheme(String color_code, Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("themes", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS theme(color VARCHAR);");
        db.execSQL("DELETE FROM theme WHERE color='" + color_code + "';");
        db.close();
    }

    public int getThemeCount(Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("themes", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS theme(color VARCHAR);");
        Cursor cursor = db.rawQuery("SELECT * FROM theme", null);
        return cursor.getCount();
    }

    public ArrayList<String> getColorAtPosition(Context ctx) {
        ArrayList<String> temp = new ArrayList<>();
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("themes", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS theme(color VARCHAR);");
        Cursor cursor = db.rawQuery("SELECT * FROM theme", null);
        if (cursor.getCount() < 1) return null;
        while (cursor.moveToNext()) {
            int int_value = Integer.parseInt(cursor.getString(0));
            temp.add("#" + Integer.toHexString(int_value).substring(2));
        }
        return temp;
    }

    public void clearAppThemes(Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("themes", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS theme(color VARCHAR);");
        db.execSQL("DROP TABLE theme;");
        db.close();
    }

    public void setHiddenMode(Context ctx, boolean mode) {
        SharedPreferences.Editor share1 = ctx.getSharedPreferences("share1", Context.MODE_PRIVATE).edit();
        share1.putBoolean("hiddenmode", mode);
        share1.commit();
    }

    public boolean checkHiddenMode(Context ctx) {
        SharedPreferences share1 = ctx.getSharedPreferences("share1", Context.MODE_PRIVATE);
        return share1.getBoolean("hiddenmode", false);
    }

    public ArrayList<String> getFavoriteDialogs(Context ctx) {
        ArrayList<String> d = new ArrayList<>();
        String Temp = "";
        SQLiteDatabase db_fav;
        db_fav = ctx.openOrCreateDatabase("db_fav", Context.MODE_PRIVATE, null);
        db_fav.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR);");
        Cursor c = db_fav.rawQuery("SELECT * FROM fav", null);
        if (c.getCount() < 1) return null;
        while (c.moveToNext())
            d.add(c.getString(0));
        db_fav.close();
        return d;
    }

    public ArrayList<String> getHiddenDialogs(Context ctx) {
        ArrayList<String> d = new ArrayList<>();
        SQLiteDatabase db_hide;
        db_hide = ctx.openOrCreateDatabase("db_hidden_chat", Context.MODE_PRIVATE, null);
        db_hide.execSQL("CREATE TABLE IF NOT EXISTS hide(ID VARCHAR);");
        Cursor c = db_hide.rawQuery("SELECT * FROM hide", null);
        if (c.getCount() < 1) return null;
        while (c.moveToNext()) {
            d.add(c.getString(0));
        }
        db_hide.close();
        return d;
    }

    public ArrayList<String> getUpdate(Context ctx) {
        ArrayList<String> d = new ArrayList<>();
        SQLiteDatabase db_q;
        db_q = ctx.openOrCreateDatabase("db_q", Context.MODE_PRIVATE, null);
        db_q.execSQL("CREATE TABLE IF NOT EXISTS q(ID VARCHAR);");
        Cursor c = db_q.rawQuery("SELECT * FROM q", null);
        if (c.getCount() < 1) return null;
        while (c.moveToNext()) {
            d.add(c.getString(0));
        }
        db_q.close();
        return d;
    }

    public void addNewPhotoUpdate(Context ctx, String name, String lname, String user_id, String date_update, String path, String date) {
        if (user_id != null && ctx != null) {
            SQLiteDatabase db;
            db = ctx.openOrCreateDatabase("db_fav_photo", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR , name VARCHAR , lname VARCHAR ,date_update VARCHAR ,path VARCHAR,long_date VARCHAR);");
            db.execSQL("INSERT INTO fav(ID , name , lname , date_update , path) VALUES('" + user_id + "','" + name + "','" + lname + "','" + date_update + "','" + path + "'" + ")");
            db.close();
        }
    }

    public boolean checkForRepeatNewPhoto(String user_id, String date, Context ctx) {
        if (user_id != null && ctx != null) {
            SQLiteDatabase db;
            db = ctx.openOrCreateDatabase("db_fav_photo", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS fav(ID VARCHAR , name VARCHAR , lname VARCHAR ,date_update VARCHAR ,path VARCHAR,long_date VARCHAR);");
            Cursor c = db.rawQuery("SELECT * FROM fav WHERE ID='" + user_id + "' AND long_date='" + date + "';", null);
            if (c.getCount() > 0) return true;
        }
        return false;
    }

    public String getRingtoneById(String user, Context ctx) {
        SQLiteDatabase db;
        db = ctx.openOrCreateDatabase("db_rington_contacts", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS main(user VARCHAR , rington VARCHAR)");
        Cursor cursor = db.rawQuery("SELECT * FROM main WHERE user='" + user + "'", null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    public int getTextSize() {
        SharedPreferences share1 = ApplicationLoader.mContext.getSharedPreferences("font", Context.MODE_PRIVATE);
        return share1.getInt("textsize", 16);

    }

    public void setLastMessage(int dialogId, Context context) {
        SharedPreferences.Editor share1 = context.getSharedPreferences("lastmessage", Context.MODE_PRIVATE).edit();
        share1.putInt("lastmessage", dialogId).commit();

    }

    public int getLastMessage(Context context) {
        SharedPreferences share1 = context.getSharedPreferences("lastmessage", Context.MODE_PRIVATE);
        return share1.getInt("lastmessage", -1);

    }

    public void addAppAnalyticsTime(Context context, int year, int month, int day, int dayOfWeek, long duration) {
        SQLiteDatabase database = context.openOrCreateDatabase("analytics", Context.MODE_PRIVATE, null);
        database.setVersion(6);
        database.execSQL("CREATE TABLE IF NOT EXISTS appanalytics(dateyear VARCHAR(255) , datemonth VARCHAR(255) , dateday VARCHAR(255) ,dayofweek VARCHAR(255) , duration VARCHAR(255));");
        database.execSQL("INSERT INTO appanalytics(dateyear , datemonth , dateday ,dayofweek ,duration) VALUES('" + year + "','" + month + "','" + day + "','" + dayOfWeek + "','" + duration + "');");
        database.close();

    }

    public void addChatsAnalyticsTime(Context context, int year, int month, int day, long duration, long chatId, int type, String photo) {
        SQLiteDatabase database = context.openOrCreateDatabase("analytics", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS chatanalytics(dateyear VARCHAR(255) , datemonth VARCHAR(255) , dateday VARCHAR(255) ,duration VARCHAR(255),chatid VARCHAR(255),type INT,photo VARCHAR(255));");
        database.execSQL("INSERT INTO chatanalytics(dateyear , datemonth , dateday ,duration , chatid , type,photo) VALUES('" + year + "','" + month + "','" + day + "','" + duration + "','" + chatId + "'," + type + ",'" + photo + "');");
        database.close();
    }

    public ArrayList<Analytics> getAppAnalytics(Context context) {
        ArrayList<org.telegram.hamrahgram.model.Analytics> result = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase("analytics", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS appanalytics(dateyear VARCHAR(255) , datemonth VARCHAR(255) , dateday VARCHAR(255) ,dayofweek VARCHAR(255) , duration VARCHAR(255));");
        Cursor cursor = database.rawQuery("SELECT * FROM appanalytics", null);
        while (cursor.moveToNext()) {
            Analytics obj = new Analytics();
            obj.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex("dateyear"))));
            obj.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex("datemonth"))));
            obj.setDay(Integer.parseInt(cursor.getString(cursor.getColumnIndex("dateday"))));
            obj.setDayNumber(Integer.parseInt(cursor.getString(cursor.getColumnIndex("dayofweek"))));
            obj.setDuration(Long.parseLong(cursor.getString(cursor.getColumnIndex("duration"))));
            result.add(obj);
        }
        return result;
    }

    public ArrayList<Analytics> getChatAnalytics(Context context) {
        ArrayList<org.telegram.hamrahgram.model.Analytics> result = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase("analytics", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS chatanalytics(dateyear VARCHAR(255) , datemonth VARCHAR(255) , dateday VARCHAR(255) ,duration VARCHAR(255),chatid VARCHAR(255),type INT,photo VARCHAR(255));");
        Cursor cursor = database.rawQuery("SELECT * FROM chatanalytics", null);
        while (cursor.moveToNext()) {
            Analytics obj = new Analytics();
            obj.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex("dateyear"))));
            obj.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex("datemonth"))));
            obj.setDay(Integer.parseInt(cursor.getString(cursor.getColumnIndex("dateday"))));
            obj.setDuration(Integer.parseInt(cursor.getString(cursor.getColumnIndex("duration"))));
            obj.setChatId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("chatid"))));
            obj.setChatType(cursor.getInt(cursor.getColumnIndex("type")));
            obj.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
            result.add(obj);
        }
        return result;
    }

    public Analytics getAppAnalyticsByDate(Context context, int y, int m, int d) {
        long duration = 0;

        Analytics result = null;
        SQLiteDatabase database = context.openOrCreateDatabase("analytics", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS appanalytics(dateyear VARCHAR(255) , datemonth VARCHAR(255) , dateday VARCHAR(255) ,dayofweek VARCHAR(255) , duration VARCHAR(255));");
        Cursor cursor = database.rawQuery("SELECT * FROM appanalytics WHERE dateyear='" + y + "' AND datemonth='" + m + "' AND dateday='" + d + "'", null);
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            duration += Long.parseLong(cursor.getString(cursor.getColumnIndex("duration")));


        }
        result = new Analytics();
        result.setYear(y);
        result.setMonth(m);
        result.setDay(d);
        result.setDuration(duration);
        return result;
    }

    public ArrayList<Analytics> getChatAnalyticsByDate(Context context, int y, int m, int d) {
        ArrayList<org.telegram.hamrahgram.model.Analytics> result = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase("analytics", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS chatanalytics(dateyear VARCHAR(255) , datemonth VARCHAR(255) , dateday VARCHAR(255) ,duration VARCHAR(255),chatid VARCHAR(255),type INT,photo VARCHAR(255));");
        Cursor cursor = database.rawQuery("SELECT * FROM chatanalytics WHERE dateyear='" + y + "' AND datemonth='" + m + "' AND dateday='" + d + "'", null);
        while (cursor.moveToNext()) {
            Analytics obj = new Analytics();
            obj.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex("dateyear"))));
            obj.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex("datemonth"))));
            obj.setDay(Integer.parseInt(cursor.getString(cursor.getColumnIndex("dateday"))));
            obj.setDuration(Integer.parseInt(cursor.getString(cursor.getColumnIndex("duration"))));
            obj.setChatId(Long.parseLong(cursor.getString(cursor.getColumnIndex("chatid"))));
            obj.setChatType(cursor.getInt(cursor.getColumnIndex("type")));
            obj.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
            result.add(obj);
        }
        return result;
    }

    public void lockDialog(long dialogId, Context context, String password) {

        SQLiteDatabase database = context.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS lock(dialogid VARCHAR(255) ,password VARCHAR(255));");
        database.execSQL("INSERT INTO lock(dialogid,password) VALUES('" + dialogId + "','" + password + "');");
        database.close();
    }

    public void unlockDialog(long dialogId, Context context, String password) {
        SQLiteDatabase database = context.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS lock(dialogid VARCHAR(255) ,password VARCHAR(255));");
        database.execSQL("DELETE FROM lock WHERE dialogid='" + dialogId + "';");
        database.close();
    }

    public boolean checkLock(long dialogId, Context context) {
        boolean result = false;
        SQLiteDatabase database = context.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS lock(dialogid VARCHAR(255) ,password VARCHAR(255));");
        Cursor cursor = database.rawQuery("SELECT * FROM lock WHERE dialogid='" + dialogId + "'", null);
        result = cursor.getCount() > 0 ? true : false;
        cursor.close();
        database.close();
        return result;
    }

    public boolean loginDialog(long dialogId, String password, Context context) {
        boolean result = false;
        SQLiteDatabase database = context.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS lock(dialogid VARCHAR(255) ,password VARCHAR(255));");
        Cursor cursor = database.rawQuery("SELECT * FROM lock WHERE dialogid='" + dialogId + "' AND password='" + password + "'", null);
        result = cursor.getCount() > 0 ? true : false;
        cursor.close();
        database.close();
        return result;
    }

    public void createTag(String tagName, int mId, Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        SQLiteDatabase database2 = SQLiteDatabase.openDatabase(new File(ApplicationLoader.getFilesDirFixed(), "cache4.db").getPath(), null, Context.MODE_PRIVATE);
        database2.execSQL("CREATE TABLE IF NOT EXISTS tagscontent(tid INTEGER , mid INTEGER , tname VARCHAR(255));");
        database.execSQL("CREATE TABLE IF NOT EXISTS tags(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR(255));");
        database.execSQL("CREATE TABLE IF NOT EXISTS tagscontent(tid INTEGER , mid INTEGER , tname VARCHAR(255));");
        database.execSQL("INSERT INTO tags(name) VALUES('" + tagName + "');");
        Cursor cursor = database.rawQuery("SELECT id FROM tags WHERE name='" + tagName + "'", null);
        if (cursor != null)
            cursor.moveToFirst();
        int tid = cursor.getInt(cursor.getColumnIndex("id"));
        database.execSQL("INSERT INTO tagscontent(tid,mid,tname) VALUES(" + tid + "," + mId + ",'" + tagName + "');");
        database2.execSQL("INSERT INTO tagscontent(tid,mid,tname) VALUES(" + tid + "," + mId + ",'" + tagName + "');");
        database.close();
    }

    public ArrayList<String> getTagsById(int mid, Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase("db_setting", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS tags(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR(255));");
        database.execSQL("CREATE TABLE IF NOT EXISTS tagscontent(tid INTEGER , mid INTEGER , tname VARCHAR(255));");
        Cursor cursor = database.rawQuery("SELECT tname FROM tagscontent WHERE mid=" + mid, null);
        if (cursor != null) {
            ArrayList<String> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(cursor.getString(cursor.getColumnIndex("tname")));
            }
            return result;


        }
        return null;
    }


}
