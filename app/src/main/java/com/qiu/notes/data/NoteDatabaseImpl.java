package com.qiu.notes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoteDatabaseImpl {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBEntry.TABLE_NAME
                    + " ("
                    + DBEntry.ID + " INTEGER PRIMARY KEY,"
                    + DBEntry.CREATE_TIME + " INTEGER,"
                    + DBEntry.UPDATE_TIME + " INTEGER,"
                    + DBEntry.CONTENT + " TEXT"
                    + ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBEntry.TABLE_NAME;

    public static class DBEntry {
        static final String DATABASE_NAME = "Quick_Note_DB.db";
        static final int VERSION = 1;
        static final String TABLE_NAME = "NoteTable";
        static final String ID = "id";
        static final String CREATE_TIME = "create_time";
        static final String UPDATE_TIME = "update_time";
        static final String CONTENT = "content";

        private DBEntry() {
        }

        static String[] getColumns() {
            return new String[]{
                    ID, CREATE_TIME, UPDATE_TIME, CONTENT
            };
        }
    }

    private static class NoteSQLiteOpenHelper extends SQLiteOpenHelper {

        public NoteSQLiteOpenHelper(Context context, String name,
                SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    @NonNull
    private NoteSQLiteOpenHelper mNoteSQLiteOpenHelper;

    public NoteDatabaseImpl(@NonNull Context context) {
        mNoteSQLiteOpenHelper =
                new NoteSQLiteOpenHelper(context, DBEntry.DATABASE_NAME, null, DBEntry.VERSION);
    }

    void insert(long id, long createTime, long updateTime, @Nullable String content) {
        final SQLiteDatabase db = mNoteSQLiteOpenHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(DBEntry.ID, id);
        values.put(DBEntry.CREATE_TIME, createTime);
        values.put(DBEntry.UPDATE_TIME, updateTime);
        values.put(DBEntry.CONTENT, content);
        db.insert(DBEntry.TABLE_NAME, null, values);
    }

    void update(long id, @NonNull ContentValues contentValues) {
        final SQLiteDatabase db = mNoteSQLiteOpenHelper.getWritableDatabase();
        db.update(DBEntry.TABLE_NAME, contentValues, DBEntry.ID + "=" + id, null);
    }

    void delete(long id) {
        final SQLiteDatabase db = mNoteSQLiteOpenHelper.getWritableDatabase();
        db.delete(DBEntry.TABLE_NAME, DBEntry.ID + "=" + id, null);
    }

    @NonNull
    List<TextContentEntry> queryAll() {
        final SQLiteDatabase db = mNoteSQLiteOpenHelper.getReadableDatabase();
        final Cursor cursor =
                db.query(DBEntry.TABLE_NAME, DBEntry.getColumns(), null, null, null, null,
                        DBEntry.CREATE_TIME + " DESC");
        final List<TextContentEntry> entryList = new ArrayList<>();
        while (cursor.moveToNext()) {
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBEntry.ID));
            final long createTime =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DBEntry.CREATE_TIME));
            final long updateTime =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DBEntry.UPDATE_TIME));
            final String content = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.CONTENT));
            TextContentEntry entry = new TextContentEntry(id, createTime, updateTime, content);
            entryList.add(entry);
        }
        cursor.close();
        return entryList;
    }
}