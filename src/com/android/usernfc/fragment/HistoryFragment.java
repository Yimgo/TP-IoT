package com.android.usernfc.fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.usernfc.R;
 
public class HistoryFragment extends Fragment {

	private TableLayout table;
	private String stats = "";
	private List<String[]> dataParsed = new ArrayList<String[]>();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
    	stats = getArguments().getString("stats");   
    	
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        table = (TableLayout) rootView.findViewById(R.id.table);
        
        
        dataParsed.addAll(parseData());
        addRows();
        
        return rootView;
    }
    
    public void addRows(){
    	
    	DateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	
    	for (int i=0; i<dataParsed.size(); i++){
    		TableRow row = new TableRow(getActivity());
            TextView t = new TextView(getActivity());
           
            try {
				t.setText("Plug id : " + dataParsed.get(i)[0] + "\n"
						+ "Power consumption : " + dataParsed.get(i)[1] + "\n"
						+ "Authorization issue time : " + formatter.format(parser.parse(dataParsed.get(i)[2])).toString() + "\n"
						+ "Authorization revocation time : " + formatter.format(parser.parse(dataParsed.get(i)[3])).toString());
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
            
            row.addView(t);
            row.setPadding(15, 25, 0, 25);
            
            if (i%2 != 0){
            	row.setBackgroundColor(Color.parseColor("#EEEEEE"));
            }
            
            table.addView(row);
    	}
    }
    
    public List<String[]> parseData(){
    	
    	List<String[]> datas = new ArrayList<String[]>();
    	
    	try {
    		JSONArray array = new JSONArray(stats);
    		for (int i=0; i<array.length(); i++){
    			String[] data = new String[4];
    			JSONObject json = array.getJSONObject(i);
    			data[0] = json.getString("plug_id");
    			data[1] = json.getString("authorization_power_consumption");
    			data[2] = json.getString("authorization_issue_time");
    			data[3] = json.getString("authorization_revocation_time");
    			datas.add(data);
    		}
    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	
    	return datas;
    }
}
