package com.android.usernfc.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionServiceHandler {
	
	private static final String TAG = ConnectionServiceHandler.class.getName();
	
	private String responseMessage;
	private int responseStatusCode;
	
	public Context mContext;
    public final static int GET = 1;
    public final static int POST = 2;
    
	public ConnectionServiceHandler(Context mContext) { 
		this.mContext = mContext;
	}
	
	public String getResponseMessage() {
		return this.responseMessage;
	}
	
	public int getResponseStatusCode() {
		return this.responseStatusCode;
	}
	
	/**
     * Check for connectivity
     * 
     * */
	public boolean isConnected(){
    
		ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            
        if (networkInfo != null && networkInfo.isConnected()) {
        	return true;
        }
        else {
            return false;  
        }
    }
	
    /**
     * Making service call
     * @url - URL to make request
     * @method - HTTP request method
     * @params - HTTP request parameters
     * */
    public void makeServiceCall(String url, int method, List<NameValuePair> params) {
        try {
            // HTTP client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            
            // HTTP parameters - timeout
	        HttpParams httpParameters = new BasicHttpParams();
	        // Set the timeout in milliseconds until a connection is established.
	        // The default value is zero, that means the timeout is not used. 
	        int timeoutConnection = 3000;
	        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	        // Set the default socket timeout (SO_TIMEOUT) 
	        // in milliseconds which is the timeout for waiting for data.
	        int timeoutSocket = 5000;
	        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	        httpClient.setParams(httpParameters);
             
            // Checking HTTP request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post parameters
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
 
                httpResponse = httpClient.execute(httpPost);
 
            } else if (method == GET) {
                // appending parameters to URL
                if (params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
 
                httpResponse = httpClient.execute(httpGet);
 
            }
            
            httpEntity = httpResponse.getEntity();
            responseMessage = EntityUtils.toString(httpEntity);
            responseStatusCode = httpResponse.getStatusLine().getStatusCode();
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Gets the system time by issuing a request over the network.
	 *
	 * @return time (milliseconds since epoch).
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	 public long getNetworkTime(String url) throws IOException {
		 
		 HttpHead request = new HttpHead(url);
		 HttpResponse httpResponse;
		 HttpClient mHttpClient = new DefaultHttpClient();
		 
		 Log.i(TAG, "Sending request to " + request.getURI());
		    
		 try {
			 httpResponse = mHttpClient.execute(request);
		    
		 } catch (ClientProtocolException e) {
			 throw new IOException(String.valueOf(e));  
		 } catch (IOException e) {
		     throw new IOException("Failed due to connectivity issues: " + e);
		 }
	
	    try {
	      Header dateHeader = httpResponse.getLastHeader("Date");
	      
	      Log.i(TAG, "Received response with Date header: " + dateHeader);
	      
	      if (dateHeader == null) {
	        throw new IOException("No Date header in response");
	      }
	      
	      String dateHeaderValue = dateHeader.getValue();
	      
	      try {
	        Date networkDate = DateUtils.parseDate(dateHeaderValue);
	        return networkDate.getTime();
	        
	      } catch (DateParseException e) {
	        throw new IOException("Invalid Date header format in response: \"" + dateHeaderValue + "\"");
	      }
	    
	    } finally {
	      // Consume all of the content of the response to facilitate HTTP 1.1 persistent connection
	      // reuse and to avoid running out of connections when this methods is scheduled on different
	      // threads.
	      try {
	        HttpEntity responseEntity = httpResponse.getEntity();
	        if (responseEntity != null) {
	          responseEntity.consumeContent();
	        }
	        
	      } catch (IOException e) {
	        // Ignored because this is not an error that is relevant to clients of this transport.
	      }
	    }
	  }
}
