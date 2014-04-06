package com.android.usernfc.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.usernfc.R;
import com.android.usernfc.common.ConnectionServiceHandler;
import com.android.usernfc.common.TOTP;

public class LoginActivity extends Activity {
    
	private static final String TAG = LoginActivity.class.getName();
	
	private static final String BASE_URL = "http://yimgo.fr:3000";
	private static final String PATH = "/users/signin";
	
	private TextView registerScreen;
	private Button btnLogin;
	
	private String username = "";
	private String totp_secret = "";
	
	private ProgressDialog mProgressDialog;
	private ConnectionServiceHandler connectionHandler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);
        
        // check for preferences
        setPreferences();
        
        registerScreen = (TextView) findViewById(R.id.link_to_register);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        
        connectionHandler = new ConnectionServiceHandler(getApplicationContext());
        
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
        
        // Listening to login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (username.length() != 0 && totp_secret.length() != 0) {
					String http_url = BASE_URL + PATH;
					
					// check if you are connected or not
			        if(!connectionHandler.isConnected()){
			        	Toast.makeText(getApplicationContext(), "You are NOT connected!", Toast.LENGTH_LONG).show();
			        }
			        else {
			        	// call AsynTask to perform network operation on separate thread
					    new HttpAsyncTask().execute(http_url, username, totp_secret);
			        }
				}
				else {
					Toast.makeText(getApplicationContext(), "You do not have an account! Please register.", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	
		String name = "";
		String totp_secret = "";
		
		@Override
	    protected void onPreExecute() {
    		super.onPreExecute();
	        mProgressDialog = ProgressDialog.show(LoginActivity.this, "Wait", "Work in progress...");
	        mProgressDialog.setCancelable(true);
	    }
    	
        @Override
        protected String doInBackground(String... args) {
        	Log.d(TAG, "doInBackground - Generating TOTP secret...");
        	String totp_token = getTotpToken(args[2]);
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", args[1]));
            params.add(new BasicNameValuePair("totp_secret", totp_token));

            Log.d(TAG, "doInBackground - Initiating post request...");
        	connectionHandler.makeServiceCall(args[0], ConnectionServiceHandler.POST, params);
        	
        	name = args[1];
        	totp_secret = args[2];
        	
        	return "";
        }
        
        @Override
        protected void onPostExecute(String result) {
        	mProgressDialog.dismiss();
        	
        	Intent intent = new Intent(getApplicationContext(), BabamActivity.class);
		    intent.putExtra("username", name);
		    intent.putExtra("totp_secret", totp_secret);
		    startActivity(intent);
        }
	}
	
	public String getTotpToken(String totp_secret) {
		String totp_token = "";

		try {
			Long networkTime = connectionHandler.getNetworkTime(BASE_URL);
	    	totp_token = TOTP.generateTOTP(totp_secret, Long.toHexString((networkTime / 1000L) / 30).toUpperCase(), "6");
	    	Log.d(TAG, "TOTP token: " + totp_token);
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return totp_token;
	}
	
	public void setPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = prefs.getString("username", "");
        totp_secret = prefs.getString("totp_secret", "");
	}
}