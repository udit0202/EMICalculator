package studio.idle.emicalculator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Fragment fragment = new CalculatorFragment();
        ActionBar.Tab tabFirst = actionBar.
                newTab().
                setText("Calculate EMI").
                setTabListener(new MyTabListener(fragment));

        ActionBar.Tab tabSecond = actionBar.
                newTab().
                setText("Compare EMI").
                setTabListener(new MyTabListener(new ComparatorFragment()));

        actionBar.addTab(tabFirst);
        actionBar.addTab(tabSecond);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.about_us :
                showAboutActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);                
        }

    }

    private void showAboutActivity() {
    }


    public class MyTabListener implements ActionBar.TabListener {

        Fragment fragment;

        public MyTabListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                fragmentTransaction.replace(R.id.fragment_container, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            fragmentTransaction.remove(fragment);
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }
    }

}
