package com.android.usernfc.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.usernfc.R;
import com.android.usernfc.common.ConnectionServiceHandler;

public class StatsActivity extends Activity {

	private static final String TAG = StatsActivity.class.getName();
	
	private static final String BASE_URL = "http://yimgo.fr:3000";
	private static final String PATH = "/users/:user/authorizations";
	
	private ProgressDialog mProgressDialog;
	private ConnectionServiceHandler connectionHandler;
	
	private TextView view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.stats);
       
        view = (TextView) findViewById(R.id.stats);
        
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
		    new HttpAsyncTask().execute(http_url);
        }
	}
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	
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
        		view.setText(connectionHandler.getResponseMessage());
        	}
       }
    }
}
