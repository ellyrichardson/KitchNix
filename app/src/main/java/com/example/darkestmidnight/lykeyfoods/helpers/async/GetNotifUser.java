package com.example.darkestmidnight.lykeyfoods.helpers.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters.HomePostsAdapter;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters.SearchResultsAdapter;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.adapters.ShowNotificationsAdapter;
import com.example.darkestmidnight.lykeyfoods.models.Notifications;
import com.example.darkestmidnight.lykeyfoods.models.Post;
import com.example.darkestmidnight.lykeyfoods.models.User;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

public class GetNotifUser extends AsyncTask<String, String, String> {
    // Add a pre-execute thing

    private SharedPreferences ShPreference;
    static private String MyPREFERENCES = "API Authentication";
    private String accessToken = "Access Token";
    private String currentUserID = "Current User ID";

    List<Notifications> notifications;

    //private RecyclerView notifUserRecyclerView;
    //private ShowNotificationsAdapter nAdapter;

    private RecyclerView notificationsRecycVw;
    private ShowNotificationsAdapter notificationsAdapter;

    private WeakReference<Context> mNotifUserReference;
    Activity activity;
    //private WeakReference<GetHomePostsCallback> callbackWeakReference;

    // constructor
    public GetNotifUser(Context context, Activity activity, List<Notifications> friendReqNotifList){
        mNotifUserReference = new WeakReference<>(context);
        this.activity = activity;
        this.notifications = friendReqNotifList;
        //callbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected String doInBackground(String... params) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        StringBuilder result = new StringBuilder();
        List<User> users = new ArrayList<>();

        Context context = mNotifUserReference.get();

        // gets the AccessToken
        ShPreference = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String APIAuthentication = "Bearer " + ShPreference.getString(accessToken, "");

        for (int i = 0; i < notifications.size(); i++) {
            HttpURLConnection httpURLConnection = null;
            try {

                // Sets up connection to the URL (params[2] from .execute in "login")
                httpURLConnection = (HttpURLConnection) new URL(params[0] + notifications.get(i).getNotifUsername()).openConnection();
                // Sets the request method for the URL
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Authorization", APIAuthentication);
                httpURLConnection.setRequestProperty("Accept", "application/json");

                // Tells the URL that I want to read the response data
                httpURLConnection.setDoInput(true);

                // // Representing the input stream to URL response
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

                Log.e("TAG", "Length" + in);
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

        }
        Log.e("TAG", result.toString());
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(result);
        // expecting a response code fro my server upon receiving the POST data
        Log.e("TAG", result);

        Context context = mNotifUserReference.get();

        // will get the JSON files based from the Post model
        List<User> usersOfNotif = new ArrayList<>();

        final String strSignedInUID = ShPreference.getInt(currentUserID, 0) + "";

        if (context != null) {
            notificationsAdapter = new ShowNotificationsAdapter(context, notifications, usersOfNotif, strSignedInUID);
            RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(context);
            notificationsRecycVw = (RecyclerView) activity.findViewById(R.id.notifRcyclrView);
            notificationsRecycVw.setLayoutManager(nLayoutManager);
            notificationsRecycVw.setItemAnimator(new DefaultItemAnimator());
            notificationsRecycVw.setAdapter(notificationsAdapter);
            notificationsAdapter.notifyDataSetChanged();

            for (int i = 0; i < notifications.size(); i++) {
                try {
                    JSONArray pJObjArray = new JSONArray(result);

                    Log.e("TAG", "Length" + pJObjArray.length());

                    for (int j = 0; j < pJObjArray.length(); j++) {
                        // puts the current iterated JSON object from the array to another temporary object
                        JSONObject pJObj_data = pJObjArray.getJSONObject(j);

                        // inputs necesarry elements for the User
                        usersOfNotif.add(new User(pJObj_data.getString("first_name"), pJObj_data.getString("last_name"), pJObj_data.getString("username"), pJObj_data.getString("email"), pJObj_data.getInt("id")));
                    }

                } catch (JSONException e) {
                    //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    Log.d("Json","Exception = "+e.toString());
                }
            }
            notificationsAdapter.notifyDataSetChanged();
        }
    }
}