package com.android.usernfc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONObject;

public class HttpConnection {
	
	public HttpConnection() {
	}
	
	public String postHttp(String url, List<NameValuePair> params) {
		// Making the HTTP request
		InputStream is = null;
		JSONObject json = null;
	    String outPut = "";
	    String response = "";
		
        try {
             
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            response = convertInputStreamToString(is);
            
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
//        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            is.close();
//            outPut = sb.toString();
//            Log.e("JSON", outPut);
//        } catch (Exception e) {
//            Log.e("Buffer Error", "Error converting result " + e.toString());
//        }
// 
//        try {
//            json = new JSONObject(outPut);          
//        } catch (JSONException e) {
//            Log.e("JSON Parser", "Error parsing data " + e.toString());
//        }
// 
//        // return JSON String
//        if (json == null)
//        	return "";
        
//        return json.toString();
        
        return response;
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }
 
        inputStream.close();
        return result;
 
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
//	    Log.i(LOG_TAG, "Sending request to " + request.getURI());
	    HttpResponse httpResponse;
	    HttpClient mHttpClient = new DefaultHttpClient();
	    try {
	      httpResponse = mHttpClient.execute(request);
	    } catch (ClientProtocolException e) {
	      throw new IOException(String.valueOf(e));
	    } catch (IOException e) {
	      throw new IOException("Failed due to connectivity issues: " + e);
	    }
	
	    try {
	      Header dateHeader = httpResponse.getLastHeader("Date");
//	      Log.i(LOG_TAG, "Received response with Date header: " + dateHeader);
	      if (dateHeader == null) {
	        throw new IOException("No Date header in response");
	      }
	      String dateHeaderValue = dateHeader.getValue();
	      try {
	        Date networkDate = DateUtils.parseDate(dateHeaderValue);
	        return networkDate.getTime();
	      } catch (DateParseException e) {
	        throw new IOException(
	            "Invalid Date header format in response: \"" + dateHeaderValue + "\"");
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
