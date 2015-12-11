package com.rdwright.spellbook;

import android.app.Activity;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Created by WrighRya on 12/10/2015.
 */
public class SpellActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Uri uri = getIntent().getData();
        Cursor cursor = managedQuery(uri, null, null, null, null);

        if (cursor == null) {
            finish();
        } else {
            cursor.moveToFirst();

            TextView spell = (TextView) findViewById(R.id.spell);
            TextView definition = (TextView) findViewById(R.id.desc);
            TextView page = (TextView) findViewById(R.id.page);
            TextView range = (TextView) findViewById(R.id.range);
            TextView comp_mat = (TextView) findViewById(R.id.components_material);
            //TextView ritual = (TextView) findViewById(R.id.ritual);
            TextView dura_conc = (TextView) findViewById(R.id.concentration_duration);
            TextView cast = (TextView) findViewById(R.id.casting_time);
            TextView level = (TextView) findViewById(R.id.level);
            TextView school = (TextView) findViewById(R.id.school);
            TextView classes = (TextView) findViewById(R.id.classes);

            int wIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_SPELL);
            int dIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_DESC);
            int pageIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_PAGE);
            int rIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_RANGE);
            int cIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_COMPONENTS);
            int mIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_MATERIAL);
           // int ritIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_RITUAL);
            int duraIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_DURATION);
            int concIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_CONCENTRATION);
            int castIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_CASTING_TIME);
            int levelIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_LEVEL);
            int schoolIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_SCHOOL);
            int classesIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_CLASSES);


            spell.setText(cursor.getString(wIndex));
            definition.setText(cursor.getString(dIndex));
            page.setText(cursor.getString(pageIndex));
            range.setText(cursor.getString(rIndex));
            if(cursor.getString(mIndex).equals("none")){
                comp_mat.setText(cursor.getString(cIndex));
            }
            else{
                comp_mat.setText(cursor.getString(cIndex) + " ("+ cursor.getString(mIndex) + ")");
            }
            if(cursor.getString(concIndex).equals("no")){
                dura_conc.setText(cursor.getString(duraIndex));
            }
            else{
                dura_conc.setText("Concentration, " + cursor.getString(duraIndex));
            }
            cast.setText(cursor.getString(castIndex));
            level.setText(cursor.getString(levelIndex));
            school.setText(cursor.getString(schoolIndex));
            classes.setText(cursor.getString(classesIndex));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, SearchableSpellbook.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

}
