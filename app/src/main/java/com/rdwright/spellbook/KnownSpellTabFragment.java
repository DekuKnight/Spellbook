package com.rdwright.spellbook;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private final String TAG = "KnownSpellTabFragment";
    private TextView mTextView;
    private ListView mListView;
    private KnownDatabase kd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.tab_known, container, false);

        mListView = (ListView) view.findViewById(R.id.known_list);
        mTextView = (TextView) view.findViewById(R.id.known_text);

        kd = new KnownDatabase(getContext());
        Cursor cursor = kd.getAllKnownSpells();

        if (cursor == null) {
            // There are no results
            mTextView.setText("No known spells. Try adding some from search or from the list.");
        } else {
            // Specify the columns we want to display in the result
            String[] from = new String[] { SpellDatabase.KEY_SPELL,
                    SpellDatabase.KEY_DESC };

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.spell,
                    R.id.definition };

            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter spells = new SimpleCursorAdapter(getContext(), R.layout.result, cursor, from, to);
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
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("KnownSpellTabFragment", "Here");
        View view = getView();

    }

}
