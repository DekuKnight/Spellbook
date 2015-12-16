package com.rdwright.spellbook;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Ryan on 12/15/2015.
 */
public class KnownDatabase {
    private static final String TAG = "KnownDatabase";

    //The columns we'll include in the dictionary table
    public static final String KEY_SPELL = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DESC = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String KEY_PAGE = "PAGE";
    public static final String KEY_RANGE = "RANGE";
    public static final String KEY_COMPONENTS = "COMPONENTS";
    public static final String KEY_MATERIAL = "MATERIAL";
    public static final String KEY_RITUAL = "RITUAL";
    public static final String KEY_DURATION = "DURATION";
    public static final String KEY_CONCENTRATION = "CONCENTRATION";
    public static final String KEY_CASTING_TIME = "CASTING_TIME";
    public static final String KEY_LEVEL = "LEVEL";
    public static final String KEY_SCHOOL = "SCHOOL";
    public static final String KEY_CLASSES = "CLASSES";

    //Known database columns
    public static final String KEY_PREPARED = "PREPARED";
    public static final String PREPARED = "TRUE";
    public static final String UNPREPARED = "FALSE";

    private static final String KNOWN_DATABASE_NAME = "known";
    private static final String KNOWN_DATABASE_TABLE = "FTSknown";
    private static final int DATABASE_VERSION = 2;

    private final KnownOpenHelper mDatabaseOpenHelper;
    private static final HashMap<String,String> mColumnMap = buildKnownColumnMap();

    public KnownDatabase(Context context) {

        mDatabaseOpenHelper = new KnownOpenHelper(context);
    }

    private static HashMap<String,String> buildKnownColumnMap(){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(KEY_SPELL, KEY_SPELL);
        map.put(KEY_DESC, KEY_DESC);
        map.put(KEY_PREPARED,KEY_PREPARED);
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        return map;
    }


    //SELECT <columns> FROM <table> WHERE <KEY_SPELL> MATCH '*'
    public Cursor getAllKnownSpells(){
        Log.d(TAG, "Getting all known spells...");
        return mDatabaseOpenHelper.getReadableDatabase().rawQuery("select docid as _id, " + KEY_SPELL + ", " + KEY_DESC + " from " + KNOWN_DATABASE_TABLE, null);
    }

    public void insertKnownSpell(String[] columns){
        Log.d(TAG, "inserting " + columns[0] + " into known database...");
        mDatabaseOpenHelper.addKnownSpell(columns);
    }

    //SELECT <columns> FROM <table> WHERE <KEY_SPELL> MATCH '*'
    public Cursor getAllPreparedSpells(){
        Log.d(TAG, "Getting all prepared spells...");
        return mDatabaseOpenHelper.getReadableDatabase().rawQuery("select docid as _id, " + KEY_SPELL + ", " + KEY_DESC + " from " + KNOWN_DATABASE_TABLE + " where " + KEY_PREPARED + "='" + PREPARED + "'", null);
    }



    public static class KnownOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        /* Note that FTS3 does not support column constraints and thus, you cannot
         * declare a primary key. However, "rowid" is automatically used as a unique
         * identifier, so when making requests, we will use "_id" as an alias for "rowid"
         */
        private static final String FTS_KNOWN_CREATE =
                "CREATE VIRTUAL TABLE " + KNOWN_DATABASE_TABLE +
                        " USING fts3 (" +
                        KEY_SPELL + ", " +
                        KEY_DESC + ", " +
                        KEY_PREPARED + ");";

        KnownOpenHelper(Context context) {
            super(context, KNOWN_DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
            Log.d(TAG, context.toString() + " " + KNOWN_DATABASE_NAME);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "in onCreate");
            db = getWritableDatabase();
            mDatabase = db;
            mDatabase.execSQL(FTS_KNOWN_CREATE);
        }

        /**
         * Add a word to the dictionary.
         * @return rowId or -1 if failed
         */

        public long addKnownSpell(String[] columns){
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_SPELL, columns[0]);
            initialValues.put(KEY_DESC, columns[1]);
            initialValues.put(KEY_PREPARED, columns[2]);
            if(mDatabase == null){
                Log.d(TAG, "isnull");
            }
            return mDatabase.insert(KNOWN_DATABASE_TABLE, null, initialValues);
        }

        public long removeKnownSpell(){return 1;};

        public long setPrepared(){return 1;};

        public long setNotPrepared(){return 1;};

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + KNOWN_DATABASE_TABLE);
            onCreate(db);
        }
    }
}
