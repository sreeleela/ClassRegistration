package com.example.sree.classregistration;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
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
import android.widget.Button;
import android.widget.ListView;
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
import java.util.List;

public class RegisteredClassesFragment extends ListFragment implements
        AdapterView.OnItemClickListener{

    String[] registeredClassesDetails;
    JSONArray registeredClasses;
    String redId;
    String password;
    String msg;
    TextView message;
    View inflatedView = null;
    int courseId;
    Button delete;
    RefreshRegisteredClasses refreshRegisteredClasses;

    public RegisteredClassesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_registeredclasses_list, container, false);

        message = (TextView) inflatedView.findViewById(R.id.msg);
        message.setTextColor(Color.rgb(255,0,0));
        message.setText(msg);

        delete = (Button) inflatedView.findViewById(R.id.delete);

        if(!msg.equals(""))
        {
            delete.setVisibility(inflatedView.INVISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String urlString = "https://bismarck.sdsu.edu/registration/unregisterclass?redid="
                                    + redId + "&password=" + password + "&courseid=" + courseId;
                            CertificateApplication certificateApplication = new CertificateApplication();
                            certificateApplication.trustBismarckCertificate();
                            HttpURLConnection urlConnection = null;
                            try {
                                URL url = new URL(urlString);
                                urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                urlConnection.setRequestMethod("GET");

                                int HttpResult = urlConnection.getResponseCode();

                                // read the response
                                StringBuilder result = new StringBuilder();
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    result.append(line);
                                }
                                JSONObject json = new JSONObject(result.toString());
                                Log.i("************", json.toString());
                            } catch (MalformedURLException badURL) {
                                Log.e("rew", "Bad URL", badURL);
                            } catch (IOException io) {
                                Log.e("rew", "nework issue", io);
                            } finally {
                                urlConnection.disconnect();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        refresh();
                    }
                });
                thread.start();
            }
        });

        return inflatedView;
    }

    public void setData(String[] registeredClassesDetails, String redId, String password,JSONArray registeredClasses,String msg)
    {
        this.registeredClasses = registeredClasses;
        this.registeredClassesDetails = registeredClassesDetails;
        this.redId = redId;
        this.password = password;
        this.msg = msg;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, registeredClassesDetails);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        try
        {
            courseId = registeredClasses.getInt(position);
            Log.i("**********",String.valueOf(courseId));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void refresh()
    {
        refreshRegisteredClasses = (RefreshRegisteredClasses) getActivity();
        refreshRegisteredClasses.doRefreshRegisteredClasses();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
interface RefreshRegisteredClasses
{
    public void doRefreshRegisteredClasses();
}