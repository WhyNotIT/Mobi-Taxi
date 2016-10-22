package com.whynotit.MobiTaxi.Drawer;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.whynotit.MobiTaxi.Activity.MainActivity;
import com.whynotit.MobiTaxi.Activity.TutorielActivity;
import com.whynotit.MobiTaxi.Fragment.APropos;
import com.whynotit.MobiTaxi.Fragment.ConditionsGenerales;
import com.whynotit.MobiTaxi.R;

/**
 * Created by Harzallah on 13/04/2016.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private RelativeLayout mDrawer;
    private MainActivity activity;
    private FragmentTransaction ft;

    public DrawerItemClickListener(MainActivity activity, RelativeLayout mDrawer, ListView mDrawerListView, DrawerLayout mDrawerLayout) {
        this.mDrawerListView = mDrawerListView;
        this.mDrawerLayout = mDrawerLayout;
        this.mDrawer = mDrawer;
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {

        android.support.v4.app.Fragment fragment = null;

        switch (position) {
            case 0:

                if (activity.getSupportFragmentManager().getBackStackEntryCount() > 0)
                {
                    activity.getSupportFragmentManager().popBackStack();
                }

                break;
            case 1:

                if (activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    activity.getSupportFragmentManager().popBackStack();
                }

                ft = activity.getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.fragment_view, new ConditionsGenerales());
                // Append this transaction to the backstack
                ft.addToBackStack("");
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                break;
            case 2:
                if (activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    activity.getSupportFragmentManager().popBackStack();
                }

                activity.startActivity(new Intent(activity, TutorielActivity.class));
                break;

            case 3:
                if (activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    activity.getSupportFragmentManager().popBackStack();
                }

                ft = activity.getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.fragment_view, new APropos());
                // Append this transaction to the backstack
                ft.addToBackStack("");
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                break;
            /*case 4:
                //retourDepuisIntent = true;
                choisirUneFiliere();
                break;
            /*
            case 6:
                new UpdateDataBase(this).execute();
                break;
            case 7:
                rate();
                break;
            case 5:
                ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.main_fragment, new FragmentClubs());
                // Append this transaction to the backstack
                ft.addToBackStack("");
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();

                break; */
            default:
                break;
        }


        mDrawerListView.setItemChecked(position, true);
        mDrawerListView.setSelection(position);
        //getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
        mDrawerLayout.closeDrawer(mDrawer);
    }

}
