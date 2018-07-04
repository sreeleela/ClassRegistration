package com.example.sree.classregistration;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilterMajor extends Fragment {

    Spinner major;
    Spinner level;
    int courseIdSelected;
    String levelSelected;
    EditText startTimeHHText;
    EditText startTimeMMText;
    EditText endTimeHHText;
    EditText endTimeMMText;
    String startTimeHH;
    String startTimeMM;
    String endTimeHH;
    String endTimeMM;
    List<String> majorList;
    List<String> levelList;
    List<Integer> majorIdList;
    Button majorButton;
    View inflatedView = null;
    FilterData filterData;

    public FilterMajor() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_filter_major, container, false);
        major = (Spinner) inflatedView.findViewById(R.id.majorSpinner);
        level = (Spinner) inflatedView.findViewById(R.id.levelSpinner);
        majorButton = (Button) inflatedView.findViewById(R.id.majorButton);
        startTimeHHText = (EditText) inflatedView.findViewById(R.id.startTimeHH);
        startTimeMMText = (EditText) inflatedView.findViewById(R.id.startTimeMM);
        endTimeHHText = (EditText) inflatedView.findViewById(R.id.endTimeHH);
        endTimeMMText = (EditText) inflatedView.findViewById(R.id.endTimeMM);

        major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if (position > 0) {
                    courseIdSelected = majorIdList.get(position-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    levelSelected = (String) parent.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        majorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeHH = startTimeHHText.getText().toString();
                startTimeMM = startTimeMMText.getText().toString();
                endTimeHH = endTimeHHText.getText().toString();
                endTimeMM = endTimeMMText.getText().toString();
                filterData = (FilterData) getActivity();
                filterData.getFilterData(courseIdSelected,levelSelected,startTimeHH,startTimeMM,endTimeHH,endTimeMM);
            }
        });

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,majorList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        major.setAdapter(spinnerArrayAdapter);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,levelList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        level.setAdapter(adapter);

        return inflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setDropDowns(List<String> levelList,List<String> majorList,List<Integer> majorIdList)
    {
        this.levelList = levelList;
        this.majorList = majorList;
        this.majorIdList = majorIdList;
    }

    }
interface FilterData
{
    public void getFilterData(int courseId,String level,String startTimeHH,String startTimeMM,String endTimeHH,String endTimeMM);
}