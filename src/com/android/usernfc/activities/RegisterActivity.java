package com.android.usernfc.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.usernfc.R;
import com.android.usernfc.common.ConnectionServiceHandler;

public class RegisterActivity extends Activity {
	
	private static final String TAG = RegisterActivity.class.getName();
	
	private static final String BASE_URL = "http://yimgo.fr:3000";
	private static final String PATH = "/users/signup";

	private TextView loginScreen;
	private Button btnRegister;
	private EditText etEmail;
	
	private ProgressDialog mProgressDialog;
	private ConnectionServiceHandler connectionHandler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);
 
        loginScreen = (TextView) findViewById(R.id.link_to_login);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.reg_email);
        
        connectionHandler = new ConnectionServiceHandler(getApplicationContext());
 
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View arg0) {
                // Switching to Login Screen/closing register screen
                finish();
            }
        });
        
        // Listening to registration button
        btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String param = etEmail.getText().toString();
				
				if (param.length() != 0) {
					String http_url = BASE_URL + PATH;
					
					// check if you are connected or not
			        if(!connectionHandler.isConnected()){
			        	Toast.makeText(getApplicationContext(), "You are NOT connected!", Toast.LENGTH_LONG).show();
			        }
			        else {
			        	// call AsynTask to perform network operation on separate thread
					    new HttpAsyncTask().execute(http_url, param);
			        }
				}
			}
		});
    }
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	
		String name = "";
		
		@Override
	    protected void onPreExecute() {
    		super.onPreExecute();
	        mProgressDialog = ProgressDialog.show(RegisterActivity.this, "Wait", "Work in progress...");
	        mProgressDialog.setCancelable(true);
	    }
    	
        @Override
        protected String doInBackground(String... args) {
        	Log.d(TAG, "doInBackground - Initiating post request...");
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", args[1]));
            
            connectionHandler.makeServiceCall(args[0], ConnectionServiceHandler.POST, params);
            
            name = args[1];
            
            return "";
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	mProgressDialog.dismiss();
        	
        	Log.d(TAG, "responseCode: " + connectionHandler.getResponseStatusCode());
        	
        	if (connectionHandler.getResponseStatusCode() / 100 != 2) {
        		Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
        	}
        	else {
        		// Retrieve hex totp secret from the response
        		String totp_secret = parseJSON(connectionHandler.getResponseMessage(), "totp_secret_hex");
        		Log.d(TAG, "totp_secret: " + connectionHandler.getResponseMessage());
        		
        		// Save secret and username on shared preferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString("username", name).commit();
                prefs.edit().putString("totp_secret", totp_secret).commit();
        	}
       }
    }
	
	public String parseJSON(String data, String key) {
		try {
			JSONObject jObj = new JSONObject(data);
			String value = jObj.getString(key);
			
			return value;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
