package despotoski.nikola.github.com.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import despotoski.nikola.github.com.bottomnavigationlayout.BottomNavigationItem;
import despotoski.nikola.github.com.bottomnavigationlayout.BottomNavigationItemBuilder;
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
        BottomTabLayout.BottomTabsBuilder builder = new BottomTabLayout.BottomTabsBuilder();
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
        /*builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder()
                        .setText("Text 5")
                        .setIcon(R.drawable.ic_favorite_white_24dp)
                        .setParentColorBackground(android.R.color.holo_green_light)
                        .build()); */
//        tabLayout.populateBottomTabItems(builder);
    }

    @Override
    public void onBottomNavigationItemSelected(BottomNavigationItem item) {
        Log.i(TAG, "Item selected: " + item.getPosition());
        TextFragment textFragment = new TextFragment();
        textFragment.setArguments(wrapInBundle(item.getPosition()));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.fragment_container, textFragment);
        ft.commit();
    }

    private Bundle wrapInBundle(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(TextFragment.ITEM_TEXT_STRING, "Selected item: "+position);
        return bundle;
    }

    @Override
    public void onBottomNavigationItemUnselected(BottomNavigationItem item) {

    }
}
