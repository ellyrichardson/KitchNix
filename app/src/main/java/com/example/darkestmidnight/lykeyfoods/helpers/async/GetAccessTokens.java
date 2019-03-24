package com.example.darkestmidnight.lykeyfoods.helpers.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAccessTokens extends AsyncTask<String, String, String> {
    // Add a pre-execute thing

    SharedPreferences ShPreference;
    SharedPreferences.Editor PrefEditor;
    static String MyPREFERENCES = "API Authentication";
    String accessToken = "Access Token";

    GetUserInfo getUserID;

    private WeakReference<Context> mSignInReference;
    Activity activity;

    // constructor
    public GetAccessTokens(Context context, Activity activity){
        mSignInReference = new WeakReference<>(context);
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        Log.e("TAG", params[0]);
        Log.e("TAG", params[1]);

        StringBuilder result = new StringBuilder();

        HttpURLConnection httpURLConnection = null;
        try {

            // Sets up connection to the URL (params[2] from .execute in "login")
            httpURLConnection = (HttpURLConnection) new URL(params[2]).openConnection();

            // Sets the request method for the URL
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept","application/json");

            // Tells the URL that I am sending a POST request body
            httpURLConnection.setDoOutput(true);
            // Tells the URL that I want to read the response data
            httpURLConnection.setDoInput(true);

            // JSON object for the REST API
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("client_id", "mYIHBd321Et3sgn7DqB8urnyrMDwzDeIJxd8eCCE");
            jsonParam.put("client_secret", "qkFYdlvikU4kfhSMBoLNsGleS2HNVHcPqaspCDR0Wdrdex5dHyiFHPXctedNjugnoTq8Ayx7D3v1C1pHeqyPh1BjRlBTQiJYSuH6pi9EVeuyjovxacauGVeGdsBOkHI3");
            jsonParam.put("username", params[0]);
            jsonParam.put("password", params[1]);
            jsonParam.put("grant_type", "password");

            Log.i("JSON", jsonParam.toString());

            // To write primitive Java data types to an output stream in a portable way
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            // Writes out a byte to the underlying output stream of the data posted from .execute function
            wr.writeBytes(jsonParam.toString());
            // Flushes the jsonParam to the output stream
            wr.flush();
            wr.close();

            // // Representing the input stream to URL response
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // reading the input stream / response from the url
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Disconnects socket after using
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        Log.e("TAG", result.toString());
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(result);
        // expecting a response code fro my server upon receiving the POST data
        Log.e("TAG", result);

        // retrieves the context passed
        Context context = mSignInReference.get();

        ShPreference = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        getUserID = new GetUserInfo(context, activity);

        // edits shared preferences for authentication and authorization
        PrefEditor = ShPreference.edit();

        // to save the Access Token from the API
        try {
            JSONObject pJObject = new JSONObject(result);
            PrefEditor.putString(accessToken, pJObject.getString("access_token"));
            PrefEditor.commit();

            // calls getUserID to keep track of the user id throughout hte app
            getUserID.execute("http://192.168.1.4:8000/api/userinfo/");

        } catch (JSONException e) {
            Log.d("Json","Exception = "+e.toString());
        }
    }
}
