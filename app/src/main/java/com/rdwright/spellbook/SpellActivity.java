package com.rdwright.spellbook;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by WrighRya on 12/10/2015.
 */
public class SpellActivity extends ActionBarActivity {
    private String TAG = "SpellActivity";
    private KnownDatabase kd = new KnownDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Uri uri = getIntent().getData();
        final Cursor cursor = managedQuery(uri, null, null, null, null);

        if (cursor == null) {
            finish();
        } else {
            cursor.moveToFirst();

            TextView definition = (TextView) findViewById(R.id.desc);
            TextView range = (TextView) findViewById(R.id.range);
            TextView comp_mat = (TextView) findViewById(R.id.components_material);
            //TextView ritual = (TextView) findViewById(R.id.ritual);
            TextView dura_conc = (TextView) findViewById(R.id.concentration_duration);
            TextView cast = (TextView) findViewById(R.id.casting_time);
            TextView level = (TextView) findViewById(R.id.level);
            TextView school = (TextView) findViewById(R.id.school);
            TextView classes = (TextView) findViewById(R.id.classes);

            final int wIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_SPELL);
            final int dIndex = cursor.getColumnIndexOrThrow(SpellDatabase.KEY_DESC);
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

            getSupportActionBar().setTitle(cursor.getString(wIndex));
            definition.setText(cursor.getString(dIndex) + "\n\n" + cursor.getString(pageIndex));
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

            final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addButton.hide();
                    Toast.makeText(getApplicationContext(), cursor.getString(wIndex) + " added to known spells", Toast.LENGTH_LONG).show();

                    String[] columns = {cursor.getString(wIndex), cursor.getString(dIndex), KnownDatabase.UNPREPARED};

                    //kd.insertKnownSpell(columns);

                }
            });
            addButton.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    int diameter = getResources().getDimensionPixelSize(R.dimen.round_button_diameter);
                    outline.setOval(0, 0, diameter, diameter);
                }
            });
            addButton.setClipToOutline(true);

        }
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
