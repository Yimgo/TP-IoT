package com.android.usernfc.fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
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
import android.widget.LinearLayout;

import com.android.usernfc.R;
 
public class ConsumptionsFragment extends Fragment {
 
	private GraphicalView mChartView;
	
	private String stats = "";
	private Integer[] dataParsed;
	
	private String[] mDay = new String[] {
	        "Mon", "Tue" , "Wed", "Thu", "Fri", "Sat", "Sun"
	    };
	  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
    	stats = getArguments().getString("stats");  
    	dataParsed = parseData();
    	
        View rootView = inflater.inflate(R.layout.fragment_consumptions, container, false);
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph_layout);

        mChartView = ChartFactory.getLineChartView(getActivity(), getDemoDataset(), getDemoRenderer());
        layout.addView(mChartView);
        
        return rootView;
    }
    
    public Integer[] parseData(){
    	
    	Integer[] datas = {0,0,0,0,0,0,0};
    	DateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	
    	try {
    		JSONArray array = new JSONArray(stats);
    		for (int i=0; i<array.length(); i++){
    			Integer data;
    			JSONObject json = array.getJSONObject(i);
    			data = Integer.valueOf(json.getString("authorization_power_consumption"));
    			
				Date date = parser.parse(json.getString("authorization_issue_time"));
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
				
				switch (dayOfWeek){
					case Calendar.MONDAY :
						datas[0] = data;
						break;
					case Calendar.TUESDAY :
						datas[1] = data;
						break;
					case Calendar.WEDNESDAY :
						datas[2] = data;
						break;
					case Calendar.THURSDAY :
						datas[3] = data;
						break;
					case Calendar.FRIDAY :
						datas[4] = data;
						break;
					case Calendar.SATURDAY :
						datas[5] = data;
						break;
					case Calendar.SUNDAY :
						datas[6] = data;
						break;
				}
				
    		}
    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    	} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	return datas;
    }
    
    private XYMultipleSeriesDataset getDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        
        XYSeries series = new XYSeries("Test");
            
        for (int i=0; i<dataParsed.length; i++){
        	series.add(i, Integer.valueOf(dataParsed[i]));
        }
        
        dataset.addSeries(series);
        
        return dataset;
    }
    
    private XYMultipleSeriesRenderer getDemoRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(20);
        renderer.setLabelsTextSize(35);
        renderer.setLegendTextSize(20);
        renderer.setPointSize(8f);
        renderer.setMargins(new int[] { 20, 30, 15, 0 });
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLUE);
        r.setLineWidth(5);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
        r.setLineWidth(6);
        renderer.addSeriesRenderer(r);
        setChartSettings(renderer);
        return renderer;
    }
    
    private void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setXTitle("Time");
        renderer.setYTitle("Consumptions");
        renderer.setRange(new double[] {0,100,0,7});
        renderer.setFitLegend(false);
        renderer.setAxesColor(Color.WHITE);
        renderer.setShowGrid(true);
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(8);
        renderer.setYAxisMin(0);
        renderer.setZoomEnabled(false);
        renderer.setYAxisMax(110);
        renderer.setXLabels(0);
        for(int i=0;i<7;i++){
        	renderer.addXTextLabel(i+1, mDay[i]);
        }
      }
}
