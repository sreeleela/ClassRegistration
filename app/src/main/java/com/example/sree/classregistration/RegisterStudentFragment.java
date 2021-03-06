package com.example.sree.classregistration;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sree.classregistration.dummy.DummyContent;
import com.example.sree.classregistration.dummy.DummyContent.DummyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegisterStudentFragment extends ListFragment implements
        AdapterView.OnItemClickListener{

    View inflatedView = null;
    String[] classes;
    String msg;
    TextView message;
    String redId;
    String password;
    JSONArray classIds;
    List<Integer> seatsList;

    public RegisterStudentFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        inflatedView = inflater.inflate(R.layout.fragment_classes_list, container, false);
        message = (TextView) inflatedView.findViewById(R.id.msg);
        message.setTextColor(Color.rgb(255,0,0));
        message.setText(msg);
        return inflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, classes);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }


    public void setClasses(String[] classes,String msg,JSONArray classIds,List<Integer> seatsList,String redId,String password)
    {
        this.classes = classes;
        this.msg = msg;
        this.redId = redId;
        this.password = password;
        this.seatsList =seatsList;
        this.classIds = classIds;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(seatsList.get(position)>0)
        {
            String classId = "";
            try {
                classId = String.valueOf(classIds.get(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String  stringURL = "https://bismarck.sdsu.edu/registration/registerclass?redid="+
                        redId+"&password="+password+"&courseid="+classId;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        CertificateApplication certificateApplication = new CertificateApplication();
                        certificateApplication.trustBismarckCertificate();
                        HttpURLConnection urlConnection = null;
                        try
                        {
                            URL url = new URL(stringURL);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            urlConnection.setRequestMethod("GET");

                            // read the response
                            StringBuilder result = new StringBuilder();
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            JSONObject json = new JSONObject(result.toString());
                            if(json.has("ok"))
                            {
                                msg = "Class Added";
                                message.setTextColor(Color.rgb(0,255,0));
                                message.setText(msg);
                            }
                            else if(json.has("error"))
                            {
                                if(json.getString("error").equals("Student already in course"))
                                {
                                    msg = "Already Added";
                                    message.setTextColor(Color.rgb(255,0,0));
                                    message.setText(msg);
                                }
                                else  if(json.getString("error").equals("Student already enrolled in 3 classes"))
                                {
                                    msg = "Already Added 3 Courses";
                                    message.setTextColor(Color.rgb(255,0,0));
                                    message.setText(msg);
                                }
                                else  if(json.getString("error").equals("No student with red id "))
                                {
                                    msg = "Add student to register";
                                    message.setTextColor(Color.rgb(255,0,0));
                                    message.setText(msg);
                                }
                            }
                        } catch(MalformedURLException badURL)
                        {
                            Log.e("rew", "Bad URL", badURL);
                        } catch (IOException io)
                        {
                            Log.e("rew", "nework issue", io);
                        }
                        finally
                        {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        else
        {
            String classId = "";
            try {
                classId = String.valueOf(classIds.get(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String  stringURL = "https://bismarck.sdsu.edu/registration/waitlistclass?redid="+
                    redId+"&password="+password+"&courseid="+classId;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        CertificateApplication certificateApplication = new CertificateApplication();
                        certificateApplication.trustBismarckCertificate();
                        HttpURLConnection urlConnection = null;
                        try
                        {
                            URL url = new URL(stringURL);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            urlConnection.setRequestMethod("GET");

                            // read the response
                            StringBuilder result = new StringBuilder();
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            JSONObject json = new JSONObject(result.toString());
                            if(json.has("ok"))
                            {
                                msg = "Class Added to Waitlist";
                                message.setTextColor(Color.rgb(0,255,0));
                                message.setText(msg);
                            }
                            else  if(json.getString("error").equals("No student with red id "))
                            {
                                msg = "Add student to register";
                                message.setTextColor(Color.rgb(255,0,0));
                                message.setText(msg);
                            }
                            else if(json.has("error"))
                            {
                                    msg = "Not Allowed";
                                    message.setTextColor(Color.rgb(255,0,0));
                                    message.setText(msg);
                            }
                        } catch(MalformedURLException badURL)
                        {
                            Log.e("rew", "Bad URL", badURL);
                        } catch (IOException io)
                        {
                            Log.e("rew", "nework issue", io);
                        }
                        finally
                        {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }

    }
}
