package com.android.usernfc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private static final String TAG = LoginActivity.class.getName();
	private static final String BASE_URL = "http://yimgo.fr:3000";
	
	private TextView registerScreen;
	private Button btnLogin;
	private EditText etEmail;
	
	private ProgressDialog mProgressDialog;
	private HttpConnection con;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);
 
        registerScreen = (TextView) findViewById(R.id.link_to_register);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEmail = (EditText) findViewById(R.id.email);
        
        con = new HttpConnection();
 
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
				String name = etEmail.getText().toString();
				
				if (name.length() != 0) {
					String http_url = BASE_URL + "/users/signin";
					
					// check if you are connected or not
			        if(!isConnected()){
			        	Toast.makeText(getApplicationContext(), "You are NOT connected!", Toast.LENGTH_LONG).show();
			        }
			        else {
			        	// call AsynTask to perform network operation on separate thread
			        	
			        	
					    new HttpsAsyncTask().execute(http_url, name);
			        }
				}
			}
		});
    }
	
	public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;   
    }
	
	private class HttpsAsyncTask extends AsyncTask<String, Void, String> {
    	
		@Override
	    protected void onPreExecute() {
    		super.onPreExecute();
	        mProgressDialog = ProgressDialog.show(LoginActivity.this, "Wait", "Work in progress...");
	        mProgressDialog.setCancelable(true);
	    }
    	
        @Override
        protected String doInBackground(String... args) {
        	
        	String totp_secret = getTotpSecret();
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", args[1]));
            params.add(new BasicNameValuePair("totp_secret", totp_secret));
            
            Log.d(TAG, "doInBackground - Initiating post request...");
        	return con.postHttp(args[0], params);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	mProgressDialog.dismiss();
        	
        	Log.d(TAG, result);
       }
    }
	
	public String getTotpSecret() {
		Log.d(TAG, "doInBackground - Generating TOTP secret...");
    	
		String totp_secret = "";
		
		try {
			Long networkTime = con.getNetworkTime(BASE_URL);
	    	totp_secret = TOTP.generateTOTP("72647973405845735257624e442626487d7b4963", Long.toHexString((networkTime / 1000L) / 30).toUpperCase(), "6");
	    	Log.d(TAG, "TOTP secret: " + totp_secret);
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return totp_secret;
	}
}
