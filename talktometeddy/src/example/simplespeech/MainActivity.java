package example.simplespeech;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * A simple activity launcher.
 */
public class MainActivity extends ListActivity {
    /**
     * Display a list of sample activities.
     */
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Display the list of sample activities in a standard Android layout.

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
