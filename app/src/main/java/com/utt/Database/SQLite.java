package com.utt.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.utt.model.DataPothole;
import com.utt.model.Notify;

import java.util.ArrayList;
import java.util.List;

public class SQLite extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";
    //DataPothole list bus stop
    private static final String DATABASE_NAME = "DB_PORO";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_POTHOLE = "pothole";
    private static final String TABLE_NOTIFY = "notify";
    private static final String ID = "id";

    private static final String NAME_COLUMN = "name";
    private static final String EMAIL_COLUMN = "email";
    private static final String LONGITUDE_COLUMN = "latitude";
    private static final String LATITUDE_COLUMN = "longitude";
    private static final String IMAGE_COLUMN = "image_url";
    private static final String NOTE_COLUMN = "note";
    private static final String VOTE_COLUMN = "vote";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";

    private static final String CREATE_POTHOLE_TABLE_SQL = "CREATE TABLE " + TABLE_POTHOLE + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            NAME_COLUMN + " TEXT, " +
            EMAIL_COLUMN + " TEXT, " +
            LATITUDE_COLUMN + " DOUBLE," +
            LONGITUDE_COLUMN + " DOUBLE," +
            IMAGE_COLUMN + " TEXT," +
            NOTE_COLUMN + " TEXT," +
            VOTE_COLUMN + " INTEGER," +
            CREATED_AT_COLUMN + " TEXT," +
            UPDATED_AT_COLUMN + " TEXT" +

            ")";
    private static final String ID_COLUMN = "id";
    private static final String NAME = "name";
    private static final String DATE_TIME = "date";
    private static final String MESSAGE = "message";

    private static final String CREATE_NOTIFY_TABLE_SQL = "CREATE TABLE " + TABLE_NOTIFY + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            NAME + " TEXT, " +
            DATE_TIME + " TEXT, " +
            MESSAGE + " DOUBLE" +

            ")";


    public SQLite(@Nullable Context context, @Nullable String name, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    private static SQLite sInstance;
    public static SQLite getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLite(context.getApplicationContext());
        }
        return sInstance;
    }

    private SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate: ");
        db.execSQL(CREATE_POTHOLE_TABLE_SQL);
        db.execSQL(CREATE_NOTIFY_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "onUpgrade: ");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POTHOLE);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFY);
        onCreate(db);
    }

    //insert DataPothole route detail 1
    public boolean insertDataPothole(DataPothole dataPothole){
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, dataPothole.getId());
        values.put(NAME_COLUMN, dataPothole.getName());
        values.put(EMAIL_COLUMN, dataPothole.getEmail());
        values.put(LATITUDE_COLUMN, dataPothole.getLatitude());
        values.put(LONGITUDE_COLUMN, dataPothole.getLongitude());
        values.put(IMAGE_COLUMN, dataPothole.getImage());
        values.put(NOTE_COLUMN, dataPothole.getNote());
        values.put(VOTE_COLUMN, dataPothole.getVote());
        values.put(CREATED_AT_COLUMN, dataPothole.getCreated_at());
        values.put(UPDATED_AT_COLUMN, dataPothole.getUpdated_at());

        long rowId = db.insert(TABLE_POTHOLE, null, values);
        db.close();
        if (rowId != -1)
            return true;
        return false;
    }


    public List<DataPothole> getAllPothole() {
        SQLiteDatabase db = getReadableDatabase();
        List<DataPothole> words = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_POTHOLE;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("cursor", ""+cursor.toString());
            do {
                words.add(new DataPothole(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getInt(7),
                        cursor.getString(8),
                        cursor.getString(9)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return words;
    }
    public int getTotalPotholeTB() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_POTHOLE;
        Cursor cursor = db.rawQuery(sql, null);
        int totalRows = cursor.getCount();
        cursor.close();
        return totalRows;
    }

        public int deletePothole() {
            SQLiteDatabase db = getReadableDatabase();
            int rowEffect = db.delete(TABLE_POTHOLE, null, null);
            db.close();
            return rowEffect;
        }


    //insert DataPothole route detail 1
    public boolean insertDataNotify(Notify notify){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, notify.getId());
        values.put(NAME_COLUMN, notify.getName());
        values.put(EMAIL_COLUMN, notify.getDate());
        values.put(LATITUDE_COLUMN, notify.getMessage());

        long rowId = db.insert(TABLE_NOTIFY, null, values);
        db.close();
        if (rowId != -1)
            return true;
        return false;
    }

    public List<Notify> getAllNotify() {
        SQLiteDatabase db = getReadableDatabase();
        List<Notify> words = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NOTIFY;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                words.add(new Notify(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return words;
    }
    public int getTotalNotifyTB() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NOTIFY;
        Cursor cursor = db.rawQuery(sql, null);
        int totalRows = cursor.getCount();
        cursor.close();
        return totalRows;
    }
//
//    //    public int updateLocation(Data DataPothole) {
////        SQLite db = getWritableDatabase();
////        ContentValues values = new ContentValues();
////        values.put(ID_COLUMN, DataPothole.getId());
////        int rowEffect = db.update(TABLE_POLYGON, values, ID_COLUMN + " = ?", new String[]{DataPothole.getName()});
////        db.close();
////        return rowEffect;
////    }
////
//    public int deleteBusStop() {
//        SQLite db = getReadableDatabase();
//        int rowEffect = db.delete(TABLE_LISTBUSSTOP, null, null);
//        db.close();
//        return rowEffect;
//    }
//
//    //VERSION
//    private boolean insertVersionAPI(VersionAPI DataPothole){
//        SQLite db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(ID_CL, DataPothole.getDataPothole().id);
//        values.put(VERSIONDATA_CL, DataPothole.getDataPothole().VersionData);
//        values.put(UPDATE_DATE_CL, DataPothole.getDataPothole().UpdateDate);
//        values.put(VERSIONDATA_CL, DataPothole.getDataPothole().VersionApi);
//        long rowId = db.insert(TABLE_VERSION, null, values);
//        db.close();
//        if (rowId != -1)
//            return true;
//        return false;
//    }
//    private int deleteVersion(VersionAPI DataPothole){
//        SQLite db = getReadableDatabase();
//        int rowEffect = db.delete(TABLE_VERSION, ID_CL + " = ?", new String[]{String.valueOf(DataPothole.getDataPothole().id)});
//        db.close();
//        return rowEffect;
//    }

}