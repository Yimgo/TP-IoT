package com.android.usernfc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
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

	private TextView loginScreen;
	private Button btnRegister;
	private EditText etEmail;
	
	private ProgressDialog mProgressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);
 
        loginScreen = (TextView) findViewById(R.id.link_to_login);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.reg_email);
 
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
				if (etEmail != null) {
					String http_url = BASE_URL + "/users/signup";
					
					// check if you are connected or not
			        if(!isConnected()){
			        	Toast.makeText(getApplicationContext(), "You are NOT connected!", Toast.LENGTH_LONG).show();
			        }
			        else {
			        	// call AsynTask to perform network operation on separate thread
					    new HttpsAsyncTask().execute(http_url);
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
        protected String doInBackground(String... urls) {
        	Log.d(TAG, "doInBackground - Initiating post request...");
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", "dangelo"));
        	return postHttp(urls[0], params);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	mProgressDialog.dismiss();
        	
        	// TODO Save secret to SE
        	Log.d(TAG, result);
       }
    }
	
	public String postHttp(String url, List<NameValuePair> params) {
		// Making the HTTP request
		InputStream is = null;
		JSONObject json = null;
	    String outPut = "";
		
        try {
             
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            outPut = sb.toString();
            Log.e("JSON", outPut);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        try {
            json = new JSONObject(outPut);          
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        if (json == null)
        	return "";
        
        return json.toString();
	}
}
