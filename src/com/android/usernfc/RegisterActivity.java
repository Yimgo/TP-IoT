package com.android.usernfc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class RegisterActivity extends Activity {
	
	private static final String TAG = RegisterActivity.class.getName();
	
	private static final String BASE_URL = "http://yimgo.fr:3000";
	
	// JSON Node names
    private static final String TOTP_SECRET_ASCII = "totp_secret_ascii";
    private static final String TOTP_SECRET_HEX = "totp_secret_hex";

	private TextView loginScreen;
	private Button btnRegister;
	private EditText etEmail;
	
	private ProgressDialog mProgressDialog;
	private HttpConnection con;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);
 
        loginScreen = (TextView) findViewById(R.id.link_to_login);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.reg_email);
        
        con = new HttpConnection();
 
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
					String http_url = BASE_URL + "/users/signup";
					
					// check if you are connected or not
			        if(!isConnected()){
			        	Toast.makeText(getApplicationContext(), "You are NOT connected!", Toast.LENGTH_LONG).show();
			        }
			        else {
			        	// call AsynTask to perform network operation on separate thread
					    new HttpsAsyncTask().execute(http_url, param);
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
	        mProgressDialog = ProgressDialog.show(RegisterActivity.this, "Wait", "Work in progress...");
	        mProgressDialog.setCancelable(true);
	    }
    	
        @Override
        protected String doInBackground(String... args) {
        	Log.d(TAG, "doInBackground - Initiating post request...");
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", args[1]));
            
        	return con.postHttp(args[0], params);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	mProgressDialog.dismiss();
        	
        	saveTotpSecret(result);
        	
        	Log.d(TAG, result);
       }
    }
	
	public void saveTotpSecret(String totp) {
		Log.d(TAG, "saveTotpSecret - " + totp);
		
		
		
	}
}
