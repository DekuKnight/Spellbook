package com.rdwright.spellbook;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Created by WrighRya on 12/10/2015.
 */
public class SplashScreenActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);

        new AsyncPrimeDatabase().execute();
    }

    private class AsyncPrimeDatabase extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            // show your progress dialog
        }

        @Override
        protected Void doInBackground(Void... voids){
            // load your xml feed asynchronously
            return null;
        }

        @Override
        protected void onPostExecute(Void params){
            // dismiss your dialog
            // launch your News activity
            Intent intent = new Intent(SplashScreenActivity.this, SearchableSpellbook.class);
            startActivity(intent);

            // close this activity
            finish();
        }
    }
}
