package com.groundupworks.partyphotobooth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 14.09.2016.
 */
public class MyBDHelper extends SQLiteOpenHelper {

    private static MyBDHelper sInstance;

    private static final String KEY_HOST_ADDRESS = "hostAddressInBD";

    private static final String KEY_HOST_PASSWORD = "hostPasswordInBD";

    private static final String KEY_USER_ADDRESS = "userAddressInBD";

    private static final String KEY_FILE_PATH = "filePathInBD";

    private static final String KEY_SUBJECT = "subjectInBD";

    private static final String KEY_MESSAGE = "messageInBD";

    private static final int DATABASE_VERSION = 1;
    private static final String KEY_ID = "id";
    private static final String DATABASE_NAME = "sendMailManager";
    private static final String TABLE_MAIL_OBJECTS = "mailobjects";

    public static synchronized MyBDHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MyBDHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private MyBDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_MAIL_OBJECTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_HOST_ADDRESS + " TEXT,"
                + KEY_HOST_PASSWORD + " TEXT,"
                + KEY_USER_ADDRESS + " TEXT,"
                + KEY_FILE_PATH + " TEXT,"
                + KEY_SUBJECT + " TEXT,"
                + KEY_MESSAGE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
//        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIL_OBJECTS);
        onCreate(db);
//        db.close();
    }

    public void addMailObject(MailObject mailObject) {
        SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(KEY_HOST_ADDRESS, mailObject.getHostAddress());
            cv.put(KEY_HOST_PASSWORD, mailObject.getHostPassword());
            cv.put(KEY_USER_ADDRESS, mailObject.getUserAddress());
            cv.put(KEY_FILE_PATH, mailObject.getFilePath());
            cv.put(KEY_SUBJECT, mailObject.getSubject());
            cv.put(KEY_MESSAGE, mailObject.getMessage());

            db.insert(TABLE_MAIL_OBJECTS, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cv.clear();
//        db.close();
    }

    public ArrayList<MailObject> getAllContacts() {

        ArrayList<MailObject> contactList = new ArrayList<MailObject>();
        String selectQuery = "SELECT  * FROM " + TABLE_MAIL_OBJECTS;
        SQLiteDatabase db = sInstance.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    MailObject mailObject = new MailObject(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6));
                    mailObject.id = id;
                    contactList.add(mailObject);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
        return contactList;
    }

    public void deleteMailObject(MailObject mailObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (mailObject.id != "1000000") {
            db.delete(TABLE_MAIL_OBJECTS, "id = " + mailObject.id, null);
        }

//        db.delete(TABLE_MAIL_OBJECTS, KEY_FILE_PATH + " = ?", new String[] { String.valueOf(mailObject.getFilePath()) });
//        db.close();
    }
}
