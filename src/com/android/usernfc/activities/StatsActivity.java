package com.android.usernfc.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.android.usernfc.R;
import com.android.usernfc.common.AsyncResponse;
import com.android.usernfc.common.ConnectionServiceHandler;
import com.android.usernfc.fragment.ConsumptionsFragment;
import com.android.usernfc.fragment.HistoryFragment;
import com.android.usernfc.fragment.ZoomOutPageTransformer;

public class StatsActivity extends FragmentActivity implements ActionBar.TabListener, AsyncResponse {

	private static final String TAG = StatsActivity.class.getName();
	
	private static final String BASE_URL = "http://tpiotcloudapp-env.elasticbeanstalk.com";
	private static final String PATH = "/users/:user/authorizations";
	
	private ProgressDialog mProgressDialog;
	private ConnectionServiceHandler connectionHandler;
	private HttpAsyncTask httpAsyncTask = new HttpAsyncTask();
	
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    
    private ActionBar actionBar;
    private String[] tabs = { "History", "Consumptions" };
    
    private String stats;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        connectionHandler = new ConnectionServiceHandler(getApplicationContext());
        
        String username = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "");
        String http_url = BASE_URL + PATH;
        http_url = http_url.replaceAll(":user", username);
        
        Log.d(TAG, "httpURL: " + http_url);
        
        // check if you are connected or not
        if(!connectionHandler.isConnected()){
        	Toast.makeText(getApplicationContext(), "You are NOT connected!", Toast.LENGTH_LONG).show();
        }
        else {
        	// call AsynTask to perform network operation on separate thread
        	httpAsyncTask.delegate = this;
        	httpAsyncTask.execute(http_url);
        }
	}
	
	public void setView(){
		// setting default screen to login.xml
        setContentView(R.layout.stats);
        
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
        
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(addDataToFragment(new HistoryFragment()));
        fragments.add(addDataToFragment(new ConsumptionsFragment()));
        
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        mPager.setAdapter(mPagerAdapter);
        
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        
        /**
         * on swiping the viewpager make respective tab selected
         * */
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
	}
	
	public Fragment addDataToFragment(Fragment fragment){
		Bundle args = new Bundle();
		args.putString("stats", stats);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
    	mPager.setCurrentItem(tab.getPosition());
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
	@Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		
		private List<Fragment> mFragments = new ArrayList<Fragment>();
		
        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
        	if (!mFragments.isEmpty()){
        		return mFragments.get(position);
        	}
     
            return new Fragment();
        }
        
        @Override
        public int getCount() {
            return 	mFragments.size();
        }
    }
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		
		public AsyncResponse delegate=null;
		
		@Override
	    protected void onPreExecute() {
    		super.onPreExecute();
	        mProgressDialog = ProgressDialog.show(StatsActivity.this, "Wait", "Work in progress...");
	        mProgressDialog.setCancelable(true);
	    }
    	
        @Override
        protected String doInBackground(String... args) {
        	Log.d(TAG, "doInBackground - Initiating post request...");
            
            connectionHandler.makeServiceCall(args[0], ConnectionServiceHandler.GET, null);
            
            return "";
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	mProgressDialog.dismiss();
        	
        	Log.d(TAG, "responseCode: " + connectionHandler.getResponseStatusCode());
        	
        	if (connectionHandler.getResponseStatusCode() / 100 != 2) {
        		Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
        		finish();
        	}
        	else {
	        	Log.d(TAG, "Response body: " + connectionHandler.getResponseMessage());
	        	// Return response message to the main thread
	        	JSONArray jObject;
	        	try {
	        	    jObject = new JSONArray(connectionHandler.getResponseMessage());
	        	    delegate.processFinish(jObject.toString());
	        	    setView();
	        	} catch (JSONException e) {
	        	    e.printStackTrace();
	        	}
        	}
       }
    }
	
	public void processFinish(String output) {
		this.stats = output;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  actionBar.removeAllTabs();
	  setView();
	}
}
