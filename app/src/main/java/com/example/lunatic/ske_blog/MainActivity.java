package com.example.lunatic.ske_blog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity is first page in android app
 *
 * @author napong dungduangsasitorn
 */
public class MainActivity extends AppCompatActivity {

    /**
     * onCreate is create page view by activity_main
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * onCreateOptionsMenu set menu icon and action.
     * @param menu is menu from main_menu.
     * @return new menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * onOptionsItemSelected set action on add icon
     * to start new page(new Activity).
     * @param item is MenuItem.
     * @return item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
