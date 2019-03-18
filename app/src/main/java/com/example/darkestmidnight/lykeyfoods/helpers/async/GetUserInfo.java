package com.example.darkestmidnight.lykeyfoods.helpers.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;

import com.example.darkestmidnight.lykeyfoods.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUserInfo extends AsyncTask<String, String, String> {
    // Add a pre-execute thing

    SharedPreferences ShPreference;
    SharedPreferences.Editor PrefEditor;
    static String MyPREFERENCES = "API Authentication";
    String accessToken = "Access Token";

    TextView uFullName;

    private WeakReference<Context> mUserInfoReference;
    Activity activity;
    //private WeakReference<GetHomePostsCallback> callbackWeakReference;

    // constructor
    public GetUserInfo(Context context, Activity activity){
        mUserInfoReference = new WeakReference<>(context);
        this.activity = activity;
        //callbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected String doInBackground(String... params) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        StringBuilder result = new StringBuilder();

        // retrieves the context passed
        Context context = mUserInfoReference.get();
        // gets the AccessToken
        ShPreference = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String APIAuthentication = "Bearer " + ShPreference.getString(accessToken, "");

        HttpURLConnection httpURLConnection = null;
        try {

            // Sets up connection to the URL (params[2] from .execute in "login")
            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            // Sets the request method for the URL
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty ("Authorization", APIAuthentication);

            // Tells the URL that I want to read the response data
            httpURLConnection.setDoInput(true);

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
        // expecting a response code from my server upon receiving the POST data
        Log.e("TAG", result);

        Context context = mUserInfoReference.get();

        uFullName = (TextView) activity.findViewById(R.id.uFullNameET);

        String uStrFullN="";

        try {
            // DRF API returns an array for this
            JSONArray pJObjArray = new JSONArray(result);

            // algorithm for parsing the JSONArray from the Django REST API
            for (int i = 0; i < pJObjArray.length(); i++) {
                // since the JSON data is an array type, it has to be put as object
                JSONObject pJObj_data = pJObjArray.getJSONObject(i);
                // Sets the profile name to the user's full name
                uStrFullN = pJObj_data.getString("first_name")+ " " + pJObj_data.getString("last_name");
            }

        } catch (JSONException e) {
            //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.d("Json","Exception = "+e.toString());
        } finally {
            if (context != null) {
                // Combines the first name and last name of the current user
                uFullName.setText(uStrFullN);
            }
        }
    }
}
