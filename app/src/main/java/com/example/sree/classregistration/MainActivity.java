package com.example.sree.classregistration;

import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
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
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,FilterData,StudentData,RefreshRegisteredClasses,RefreshWaitlistedClasses{

    String[] classes = new String[2];
    String[] registeredClassesDetails;
    String[] waitlistedClassDetailsList;
    JSONArray classIds;
    JSONArray registeredClasses;
    JSONArray waitlistedClasses;
    List<String> levelList = new ArrayList<String>();
    List<String> majorList = new ArrayList<String>();
    List<Integer> majorIdList = new ArrayList<Integer>();
    List<Integer> seatsList = new ArrayList<Integer>();
    String redId;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        AddStudent addStudent = new AddStudent();
        fragmentTransaction.replace(R.id.fragment_container,addStudent);
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account)
        {
            FragmentManager fragments = getSupportFragmentManager();
            fragments.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction fragmentTransaction = fragments.beginTransaction();
            AddStudent addStudent = new AddStudent();
            fragmentTransaction.replace(R.id.fragment_container,addStudent);
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_class)
        {
            setDropDowns();
        }
        else if (id == R.id.nav_viewclasses)
        {
            getRegisteredClasses();
        }
        else if (id == R.id.nav_viewwaitlist)
        {
            getWaitlistedClasses();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void getFilterData(int courseId, String level, String startTimeHH, String startTimeMM, String endTimeHH, String endTimeMM) {
        getClasses(courseId,level,startTimeHH,startTimeMM,endTimeHH,endTimeMM);
    }

    public void getClasses(int courseId, String level, String startTimeHH, String startTimeMM, String endTimeHH, String endTimeMM)
    {
        //classes[0] = "AB";
        //classes[1] = "FG";
        final String urlString = "https://bismarck.sdsu.edu/registration/classidslist?subjectid="+
                String.valueOf(courseId)+"&level="+level+"&"+"starttime="+startTimeHH+startTimeMM+
                "&"+"endtime="+endTimeHH+endTimeMM;
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
                        URL url = new URL(urlString);
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
                        JSONArray json = new JSONArray(result.toString());
                        classIds = json;
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
                getClassesList();
                //Log.i("length...",String.valueOf(classIds.length()));
            }
        });
        thread.start();
    }

    public void getClassesList()
    {
        final ArrayList<String> classDetailsList = new ArrayList<String>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Looper.prepare();
                String msg ="";
                if(classIds.length()==0)
                {
                    msg = "No items to display for your selection.";
                }
                for(int i=0;i<classIds.length();i++) {
                    try {
                        final String urlString = "https://bismarck.sdsu.edu/registration/classdetails?classid="+classIds.get(i);
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
                            seatsList.add(json.getInt("seats"));
                            String classDetail = json.get("course#") + "     "+json.get("title")+"    "
                                    +json.get("startTime")+"-"+json.get("endTime");
                            classDetailsList.add(classDetail);
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
                }
                classes = classDetailsList.toArray(new String[classDetailsList.size()]);
                getFilteredClasses(msg);
            }
        });
        thread.start();
    }
    public void getFilteredClasses(String msg)
    {
        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        RegisterStudentFragment registerStudentFragment = new RegisterStudentFragment();
        registerStudentFragment.setClasses(classes,msg,classIds,seatsList,redId,password);
        fragmentTransaction.replace(R.id.fragment_container,registerStudentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void setDropDowns()
    {
        levelList.clear();
        majorList.clear();
        levelList.add("--Select Level--");
        levelList.add("lower");
        levelList.add("upper");
        levelList.add("graduate");
        majorList.add("--Select Major--");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Looper.prepare();
                try
                {
                    CertificateApplication certificateApplication = new CertificateApplication();
                    certificateApplication.trustBismarckCertificate();
                    HttpURLConnection urlConnection = null;
                    try
                    {
                        URL url = new URL("https://bismarck.sdsu.edu/registration/subjectlist");
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
                        JSONArray json = new JSONArray(result.toString());
                        for(int i=0;i<json.length();i++)
                        {
                            majorList.add(json.getJSONObject(i).getString("title"));
                            majorIdList.add(json.getJSONObject(i).getInt("id"));
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
                FragmentManager fragments = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragments.beginTransaction();
                FilterMajor filterMajor = new FilterMajor();
                filterMajor.setDropDowns(levelList,majorList,majorIdList);
                fragmentTransaction.replace(R.id.fragment_container,filterMajor);
                fragmentTransaction.commit();
            }
        });
        thread.start();
    }

    @Override
    public void saveStudentData(String redid,String password) {
        this.redId = redid;
        this.password =password;
    }

    public void getRegisteredClasses()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Looper.prepare();
                String msg ="";
                    try {
                        final String urlString = " https://bismarck.sdsu.edu/registration/studentclasses?redid="
                        +redId+"&password="+password;
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
                            Log.i("&&&&",json.toString());
                            registeredClasses = json.getJSONArray("classes");
                            waitlistedClasses = json.getJSONArray("waitlist");
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
                getRegisterdClassesDetails();
            }
        });
        thread.start();
    }
    public void getRegisterdClassesDetails()
    {
        final ArrayList<String> RegisteredClassDetailsList = new ArrayList<String>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Looper.prepare();
                String msg ="";
                if(registeredClasses == null)
                {
                    msg = "Register student to enroll";
                }
                else  {

                    if(registeredClasses.length()==0) {
                        msg = "No registered classes.";
                    }

                    for (int i = 0; i < registeredClasses.length(); i++) {
                        try {
                            final String urlString = "https://bismarck.sdsu.edu/registration/classdetails?classid=" + registeredClasses.get(i);
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
                                String classDetail = json.get("course#") + "     " + json.get("title") + "    "
                                        + json.get("startTime") + "-" + json.get("endTime");
                                RegisteredClassDetailsList.add(classDetail);
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
                    }
                }
                registeredClassesDetails = RegisteredClassDetailsList.toArray(new String[RegisteredClassDetailsList.size()]);
                callRegisteredClassesFragment(msg);
            }
        });
        thread.start();
    }
    public void callRegisteredClassesFragment(String msg)
    {
        FragmentManager fragments = getSupportFragmentManager();
        fragments.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        RegisteredClassesFragment registeredClassesFragment = new RegisteredClassesFragment();
        registeredClassesFragment.setData(registeredClassesDetails,redId,password,registeredClasses,msg);
        fragmentTransaction.replace(R.id.fragment_container,registeredClassesFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void doRefreshRegisteredClasses() {
        getRegisteredClasses();
    }

    public void getWaitlistedClasses()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Looper.prepare();
                String msg ="";
                try {
                    final String urlString = " https://bismarck.sdsu.edu/registration/studentclasses?redid="
                            +redId+"&password="+password;
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
                        registeredClasses = json.getJSONArray("classes");
                        waitlistedClasses = json.getJSONArray("waitlist");
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
                getWaitlistedClassesDetails();
            }
        });
        thread.start();
    }
    public void getWaitlistedClassesDetails()
    {
        final ArrayList<String> WaitlistedClassDetailsList = new ArrayList<String>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                Looper.prepare();
                String msg ="";
                if(waitlistedClasses == null)
                {
                    msg = "Register student to enroll";
                }
                else
                {
                    if(waitlistedClasses.length()==0) {
                        msg = "No classes added.";
                    }

                    for (int i = 0; i < waitlistedClasses.length(); i++) {
                        try {
                            final String urlString = "https://bismarck.sdsu.edu/registration/classdetails?classid=" + waitlistedClasses.get(i);
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
                                String classDetail = json.get("course#") + "     " + json.get("title") + "    "
                                        + json.get("startTime") + "-" + json.get("endTime");
                                WaitlistedClassDetailsList.add(classDetail);
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
                    }
                }
                waitlistedClassDetailsList = WaitlistedClassDetailsList.toArray(new String[WaitlistedClassDetailsList.size()]);
                callWaitlistedClassesFragment(msg);
            }
        });
        thread.start();
    }
    public void callWaitlistedClassesFragment(String msg)
    {
        FragmentManager fragments = getSupportFragmentManager();
        fragments.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        WaitlistFragment waitlistFragment = new WaitlistFragment();
        waitlistFragment.setData(waitlistedClassDetailsList,redId,password,waitlistedClasses,msg);
        fragmentTransaction.replace(R.id.fragment_container,waitlistFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void doRefreshWaitlistedClasses() {
        getWaitlistedClasses();
    }
}
