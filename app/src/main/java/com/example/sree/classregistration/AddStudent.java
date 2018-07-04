package com.example.sree.classregistration;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class AddStudent extends Fragment {

    Button done;
    TextView message;
    EditText firstName;
    EditText lastName;
    EditText redId;
    EditText emailId;
    EditText password;
    View inflatedView = null;
    StudentData studentData;

    public AddStudent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_add_student, container, false);

        firstName = (EditText) inflatedView.findViewById(R.id.firstNameText);
        lastName = (EditText) inflatedView.findViewById(R.id.lastNameText);
        emailId = (EditText) inflatedView.findViewById(R.id.emailText);
        redId = (EditText) inflatedView.findViewById(R.id.redIdText);
        done = (Button) inflatedView.findViewById(R.id.done);
        password = (EditText) inflatedView.findViewById(R.id.passwordText);
        message = (TextView) inflatedView.findViewById(R.id.message);

        //Add student data to server and save data
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            CertificateApplication certificateApplication = new CertificateApplication();
                            certificateApplication.trustBismarckCertificate();

                            JSONObject studentData = new JSONObject();
                            try
                            {
                                studentData.put("firstname",firstName.getText());
                                studentData.put("lastname",lastName.getText());
                                studentData.put("redid",redId.getText());
                                studentData.put("password",password.getText());
                                studentData.put("email",emailId.getText());

                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            HttpURLConnection urlConnection = null;
                            try
                            {
                                URL url = new URL("https://bismarck.sdsu.edu/registration/addstudent");
                                urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                urlConnection.setRequestMethod("POST");

                                Log.i("Connection Check","Reached");

                                OutputStream os = urlConnection.getOutputStream();
                                os.write(studentData.toString().getBytes("UTF-8"));
                                os.close();

                                int HttpResult = urlConnection.getResponseCode();
                                Log.i("Result",String.valueOf(HttpResult));

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
                                    if(json.getString("ok").equals("Student Added"))
                                    {
                                        message.setTextColor(Color.rgb(0,255,0));
                                        message.setText("Student Added Successfully!!");

                                        // Create object of SharedPreferences.
                                        SharedPreferences sharedPref = getActivity().getSharedPreferences("my", 0);
                                        //now get Editor
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        //put your value
                                        editor.putString("enteredFirstName", firstName.getText().toString());
                                        editor.putString("enteredLastName", lastName.getText().toString());
                                        editor.putString("enteredRedId", redId.getText().toString());
                                        editor.putString("enteredEmailId", emailId.getText().toString());
                                        editor.putString("enteredPassword", password.getText().toString());
                                        //commits your edits
                                        editor.commit();
                                    }
                                }
                                else
                                {
                                    if(json.getString("error").equals("Red Id already in use"))
                                    {
                                        message.setTextColor(Color.rgb(255,0,0));
                                        message.setText("Red Id already in use");
                                    }
                                    else if(json.getString("error").equals("Invalid Red Id"))
                                    {
                                        message.setTextColor(Color.rgb(255,0,0));
                                        message.setText("Red Id Invalid");
                                    }
                                    else if(json.getString("error").equals("Password too short"))
                                    {
                                        message.setTextColor(Color.rgb(255,0,0));
                                        message.setText("Password too short");
                                    }
                                    else if(json.getString("error").equals("Invalid email no @"))
                                    {
                                        message.setTextColor(Color.rgb(255,0,0));
                                        message.setText("Invalid E-mail");
                                    }
                                }
                                Log.i("Output",result.toString());
                                in.close();
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
                        studentData = (StudentData) getActivity();
                        studentData.saveStudentData(redId.getText().toString(),password.getText().toString());
                    }
                });
                thread.start();
            }
        });

       return inflatedView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("my", 0);
        String savedFirstName = sharedPref.getString("enteredFirstName", "");
        firstName.setText(savedFirstName);
        String savedLastName = sharedPref.getString("enteredLastName", "");
        lastName.setText(savedLastName);
        String savedRedId = sharedPref.getString("enteredRedId", "");
        redId.setText(savedRedId);
        String savedEmailId = sharedPref.getString("enteredEmailId", "");
        emailId.setText(savedEmailId);
        String savedPassword = sharedPref.getString("enteredPassword", "");
        password.setText(savedPassword);

        studentData = (StudentData) getActivity();
        studentData.saveStudentData(sharedPref.getString("enteredRedId", ""),sharedPref.getString("enteredPassword", ""));
    }
}
interface StudentData
{
    public void saveStudentData(String redId,String password);
}