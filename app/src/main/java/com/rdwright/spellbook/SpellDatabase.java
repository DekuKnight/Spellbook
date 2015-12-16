package com.rdwright.spellbook;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by WrighRya on 12/10/2015.
 */
public class SpellDatabase {

    private static final String TAG = "SpellDatabase";

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

    private static final String SPELL_DATABASE_NAME = "spellbook";
    private static final String KNOWN_DATABASE_NAME = "known";
    private static final String SPELL_DATABASE_TABLE = "FTSspellbook";
    private static final String KNOWN_DATABASE_TABLE = "FTSknown";
    private static final int DATABASE_VERSION = 2;

    private final SpellbookOpenHelper mDatabaseOpenHelper;
    private static final HashMap<String,String> mColumnMap = buildColumnMap();

    /**
     * Constructor
     * @param context The Context within which to work, used to create the DB
     */
    public SpellDatabase(Context context) {
        mDatabaseOpenHelper = new SpellbookOpenHelper(context);
    }

    /**
     * Builds a map for all columns that may be requested, which will be given to the
     * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
     * all columns, even if the value is the key. This allows the ContentProvider to request
     * columns w/o the need to know real column names and create the alias itself.
     */
    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(KEY_SPELL, KEY_SPELL);
        map.put(KEY_DESC, KEY_DESC);
        map.put(KEY_PAGE, KEY_PAGE);
        map.put(KEY_RANGE, KEY_RANGE);
        map.put(KEY_COMPONENTS,KEY_COMPONENTS);
        map.put(KEY_MATERIAL,KEY_MATERIAL);
        map.put(KEY_RITUAL,KEY_RITUAL);
        map.put(KEY_DURATION,KEY_DURATION);
        map.put(KEY_CONCENTRATION,KEY_CONCENTRATION);
        map.put(KEY_CASTING_TIME,KEY_CASTING_TIME);
        map.put(KEY_LEVEL,KEY_LEVEL);
        map.put(KEY_SCHOOL,KEY_SCHOOL);
        map.put(KEY_CLASSES,KEY_CLASSES);
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }


    /**
     * Returns a Cursor positioned at the word specified by rowId
     *
     * @param rowId id of word to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching word, or null if not found.
     */
    public Cursor getSpell(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {rowId};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
         */
    }

    /**
     * Returns a Cursor over all words that match the given query
     *
     * @param query The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all words that match, or null if none found.
     */
    public Cursor getSpellMatches(String query, String[] columns) {
        String selection = KEY_SPELL + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <KEY_SPELL> MATCH 'query*'
         * which is an FTS3 search for the query text (plus a wildcard) inside the word column.
         *
         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
         *   for suggestions to carry the proper intent data.
         *   These aliases are defined in the DictionaryProvider when queries are made.
         * - This can be revised to also search the definition text with FTS3 by changing
         *   the selection clause to use SPELL_DATABASE_TABLE instead of KEY_SPELL (to search across
         *   the entire table, but sorting the relevance could be difficult.
         */
    }

    //SELECT <columns> FROM <table> WHERE <KEY_SPELL> MATCH '*'
    public Cursor getAllSpells(String[] columns){
        return mDatabaseOpenHelper.getReadableDatabase().rawQuery("select docid as _id, " + KEY_SPELL + ", " + KEY_DESC + " from " + SPELL_DATABASE_TABLE, null);
    }

    /**
     * Performs a database query.
     * @param selection The selection clause
     * @param selectionArgs Selection arguments for "?" components in the selection
     * @param columns The columns to return
     * @return A Cursor over all rows matching the query
     */
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(SPELL_DATABASE_TABLE);
        builder.setProjectionMap(mColumnMap);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            Log.d(TAG, "here");
            cursor.close();
            return null;
        }
        return cursor;
    }


    /**
     * This creates/opens the database.
     */
    public static class SpellbookOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        /* Note that FTS3 does not support column constraints and thus, you cannot
         * declare a primary key. However, "rowid" is automatically used as a unique
         * identifier, so when making requests, we will use "_id" as an alias for "rowid"
         */
        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + SPELL_DATABASE_TABLE +
                        " USING fts3 (" +
                        KEY_SPELL + ", " +
                        KEY_DESC +", " +
                        KEY_PAGE +", " +
                        KEY_RANGE +", " +
                        KEY_COMPONENTS +", " +
                        KEY_MATERIAL +", " +
                        KEY_RITUAL +", " +
                        KEY_DURATION +", " +
                        KEY_CONCENTRATION +", " +
                        KEY_CASTING_TIME +", " +
                        KEY_LEVEL +", " +
                        KEY_SCHOOL +", " +
                        KEY_CLASSES + ");";

        SpellbookOpenHelper(Context context) {
            super(context, SPELL_DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
            Log.d(TAG, context.toString() + " " + SPELL_DATABASE_NAME);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG,"in onCreate");
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            Log.d(TAG, "load spellbook");
            loadSpellbook();
        }

        /**
         * Starts a thread to load the database table with words
         */
        private void loadSpellbook() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadSpells();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadSpells() throws IOException {
            Log.v(TAG, "Loading spells...");
            final Resources resources = mHelperContext.getResources();
            InputStream is = resources.openRawResource(R.raw.spells);
            String json = null;
            try {
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                JSONArray array = new JSONArray(json.replace("\\n","\n").replace("\\t","\t"));
                int iter = 0;
                while (iter < array.length()){
                    JSONObject obj = array.getJSONObject(iter);
                    long id = addSpell(obj);
                    if(id < 0){
                        Log.e(TAG, "unable to add spell: " + obj.getString("name"));
                    }
                    else{
                        Log.v(TAG, "added spell: " +obj.getString("name"));
                    }
                    iter++;
                }
            } catch(Exception e) {
                Log.e(TAG,"Logged Exception: ", e);
            }
            Log.v(TAG, "DONE loading spells.");
        }

        /**
         * Add a word to the dictionary.
         * @return rowId or -1 if failed
         */

        public long addSpell(JSONObject spell){
            ContentValues initialValues = new ContentValues();
            try {
                initialValues.put(KEY_SPELL, spell.getString("name"));
                initialValues.put(KEY_DESC, spell.getString("desc"));
                initialValues.put(KEY_PAGE, spell.getString("page"));
                initialValues.put(KEY_RANGE, spell.getString("range"));
                initialValues.put(KEY_COMPONENTS, spell.getString("components"));
                if(spell.getString("components").contains("M")){
                    initialValues.put(KEY_MATERIAL, spell.getString("material"));
                }
                else {
                    initialValues.put(KEY_MATERIAL, "none");
                }
                initialValues.put(KEY_RITUAL, spell.getString("ritual"));
                initialValues.put(KEY_DURATION, spell.getString("duration"));
                initialValues.put(KEY_CONCENTRATION, spell.getString("concentration"));
                initialValues.put(KEY_CASTING_TIME, spell.getString("casting_time"));
                initialValues.put(KEY_LEVEL, spell.getString("level"));
                initialValues.put(KEY_SCHOOL, spell.getString("school"));
                initialValues.put(KEY_CLASSES, spell.getString("class"));
            } catch(JSONException e){
                Log.e(TAG, "JSON Parsing error", e);
            }

            return mDatabase.insert(SPELL_DATABASE_TABLE, null, initialValues);
        }

/*        public long addSpell(String spell, String definition) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_SPELL, spell);
            initialValues.put(KEY_DESC, definition);

            return mDatabase.insert(SPELL_DATABASE_TABLE, null, initialValues);
        }*/

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SPELL_DATABASE_TABLE);
            onCreate(db);
        }
    }
}
