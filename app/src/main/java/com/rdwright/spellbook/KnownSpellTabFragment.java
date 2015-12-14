package com.rdwright.spellbook;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Ryan on 12/14/2015.
 */
public class KnownSpellTabFragment extends Fragment {
    private TextView mTextView;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_known, container, false);
    }

    public void showResults(String query, Context context) {

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(SpellbookProvider.CONTENT_URI, null, null,
                new String[] {query}, null);

        if (cursor == null) {
            // There are no results
            mTextView.setText(getString(R.string.no_results, new Object[] {query}));
        } else {
            // Display the number of results
            int count = cursor.getCount();
            String countString = getResources().getQuantityString(R.plurals.search_results,
                    count, new Object[] {count, query});
            mTextView.setText(countString);

            // Specify the columns we want to display in the result
            String[] from = new String[] { SpellDatabase.KEY_SPELL,
                    SpellDatabase.KEY_DESC };

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.spell,
                    R.id.definition };

            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter spells = new SimpleCursorAdapter(context, R.layout.result, cursor, from, to);
            mListView.setAdapter(spells);

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Build the Intent used to open WordActivity with a specific spell Uri
                    Intent spellIntent = new Intent(getActivity().getApplicationContext(), SpellActivity.class);
                    Uri data = Uri.withAppendedPath(SpellbookProvider.CONTENT_URI, String.valueOf(id));
                    spellIntent.setData(data);
                    startActivity(spellIntent);
                }
            });
        }
    }
}
