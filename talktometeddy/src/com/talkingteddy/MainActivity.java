package com.talkingteddy;

import com.talkingteddy.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Activity launcher.
 */
public class MainActivity extends ListActivity {
    /**
     * start launch screen activity initialization.
     */
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        activities = new Item[]{
                new Item(new Intent(this, StartScreenActivity.class),
                        getString(R.string.app_name_launch_screen))
        };

        startActivity(activities[0].intent);
    }

    /**
     * Represents an activity for the list view.
     */
    private class Item {
        final Intent intent;
        final String title;

        Item(Intent intent, String title) {
            this.intent = intent;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private Item[] activities;

    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        startActivity(activities[position].intent);
    }
}
