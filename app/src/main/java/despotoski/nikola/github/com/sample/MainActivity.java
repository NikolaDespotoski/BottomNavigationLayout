package despotoski.nikola.github.com.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import despotoski.nikola.github.com.bottomnavigationlayout.BottomNavigationItem;
import despotoski.nikola.github.com.bottomnavigationlayout.BottomTabLayout;
import despotoski.nikola.github.com.bottomnavigationlayout.sample.R;

public class MainActivity extends AppCompatActivity implements BottomTabLayout.OnNavigationItemSelectionListener {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomTabLayout tabLayout = (BottomTabLayout) findViewById(R.id.tabs);
        tabLayout.setOnNavigationItemSelectionListener(this);
        tabLayout.setShiftingMode(true);
        tabLayout.setActiveItemColorResource(R.color.active_color);
        /*BottomTabLayout.BottomTabsBuilder builder = new BottomTabLayout.BottomTabsBuilder();
        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder()
                        .setText("Text 1")
                        .setIcon(R.drawable.ic_android_white_24dp)
                        .setParentColorBackground(android.R.color.holo_blue_bright)
                        .build());

        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder()
                        .setText("Text 2")
                        .setIcon(R.drawable.ic_favorite_white_24dp)
                        .setParentColorBackground(android.R.color.holo_orange_light)
                        .build());
        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder().setText("Text 3")
                        .setIcon(R.drawable.ic_favorite_white_24dp)
                        .setParentColorBackground(android.R.color.holo_blue_dark)
                        .build());
        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder()
                        .setText("Text 4")
                        .setIcon(R.drawable.ic_place_white_24dp)
                        .setParentColorBackground(android.R.color.holo_blue_bright)
                        .build());
        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder()
                        .setText("Text 5")
                        .setIcon(R.drawable.ic_favorite_white_24dp)
                        .setParentColorBackground(android.R.color.holo_green_light)
                        .build());
        tabLayout.populateBottomTabItems(builder);*/
    }

    @Override
    public void onBottomNavigationItemSelected(BottomNavigationItem item) {
        Log.i(TAG, "Item selected: " + item.getPosition());
    }

    @Override
    public void onBottomNavigationItemUnselected(BottomNavigationItem item) {

    }
}
