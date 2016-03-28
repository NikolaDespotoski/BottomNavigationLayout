# BottomNavigationLayout

Simple lightweight library that works out of the box implementing [Bottom navigation component of Material Design guidelines](https://www.google.com/design/spec/components/bottom-navigation.html)

 
  
  [Three items](http://i.imgur.com/uQjIy1O.gif)
  
  [Shifting mode off with more than 3 items](http://i.imgur.com/OrADI77.gif)
  
  [Shifting mode on with more than 3 items] (https://fat.gfycat.com/UnsteadyGrotesqueCoral.webm)
  
  Tablet mode:
  
  ![](https://raw.githubusercontent.com/NikolaDespotoski/BottomNavigationLayout/master/tablet_mode.png "Tablet support")

# Usage 
 1. Add jitpack.io to your project gradle:
```
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

 2.  Add this library to your module gradle dependencies: 
```
 compile 'com.github.NikolaDespotoski:BottomNavigationLayout:0.4.2'
```
 3. In xml layout resource

```
<despotoski.nikola.github.com.bottomnavigationlayout.BottomTabLayout
  android:id="@+id/tabs"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:layout_gravity="bottom"
  android:background="?attr/selectableItemBackgroundBorderless"
  app:inactive_item_text_color="@color/colorPrimary"
  app:active_item_color_filter="@android:color/holo_red_dark"
  app:bottom_tabs_menu="@menu/bottom_navigation_items"
  app:bottom_tabs_menu_parent_background_colors="@array/menu_background_colors"/>
```
        


 4. Programatically:

```
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
                        .setText("Item 2")
                        .setIcon(R.drawable.ic_favorite_white_24dp)
                        .setParentColorBackground(android.R.color.holo_orange_light)
                        .build());
        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder().setText("Item 3")
                        .setIcon(R.drawable.ic_favorite_white_24dp)
                        .setParentColorBackground(android.R.color.holo_blue_dark)
                        .build());
        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder()
                        .setText("Item 4")
                        .setIcon(R.drawable.ic_place_white_24dp)
                        .setParentColorBackground(android.R.color.holo_blue_bright)
                        .build());
        builder.addBottomNavigationItem(
                new BottomNavigationItemBuilder()
                        .setText("Item 5")
                        .setIcon(R.drawable.ic_favorite_white_24dp)
                        .setParentColorBackground(android.R.color.holo_green_light)
                        .build());
        tabLayout.populateBottomTabItems(builder);
```
 5. Tablet mode
 
In order to offset the content from BottomNavigatonLayout direct child of the CoordinatorLayout must use the provided tablet behavior like:

````
  <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        app:layout_behavior="@string/bottom_bar_tablet_behavior"
        android:layout_height="match_parent">

        <FrameLayout
            android:id="@+id/fragment_container"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

        </FrameLayout>
    </android.support.v4.widget.NestedScrollView>
    
````

#Notes

1. Number of items must be between 3 and 5 according to Material Design Bottom Navigation Guidelines
2. Shifting mode is on when the number of bottom items is greater than 3
3. This library does not handle cross fade between top-level sections when item is clicked.
4. Incomplete support to preHoneycomb.


If you spot any oddities please open an issue! Thanks!
