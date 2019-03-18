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

public class GetSearchBoxResults extends AsyncTask<String, String, String> {
    // Add a pre-execute thing

    SharedPreferences ShPreference;
    SharedPreferences.Editor PrefEditor;
    static String MyPREFERENCES = "API Authentication";
    String accessToken = "Access Token";

    private RecyclerView srchBoxRecyclerView;
    private SearchResultsAdapter uAdapter;

    private WeakReference<Context> mSearchBoxReference;
    Activity activity;
    //private WeakReference<GetHomePostsCallback> callbackWeakReference;

    // constructor
    public GetSearchBoxResults(Context context, Activity activity){
        mSearchBoxReference = new WeakReference<>(context);
        this.activity = activity;
        //callbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected String doInBackground(String... params) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        StringBuilder result = new StringBuilder();

        // retrieves the context passed
        Context context = mSearchBoxReference.get();
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

            // JSON object for the REST API
            /*JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", params[1]);

            Log.i("JSON", jsonParam.toString());

            // To write primitive Java data types to an output stream in a portable way
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            // Writes out a byte to the underlying output stream of the data posted from .execute function
            wr.writeBytes(jsonParam.toString());
            // Flushes the jsonParam to the output stream
            wr.flush();
            wr.close();*/

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

        Context context = mSearchBoxReference.get();

        // will get the JSON files based from the Post model
        List<User> usersToShow = new ArrayList<>();

        // For posts
        try {
            JSONArray pJObjArray = new JSONArray(result);

            Log.e("TAG", "Length" + pJObjArray.length());

            if (context != null) {
                // sets the adapter to output users with view holder
                uAdapter = new SearchResultsAdapter(usersToShow);
                // sets the recycler view to use for the users view holders to be put
                RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(context.getApplicationContext());
                srchBoxRecyclerView = (RecyclerView) activity.findViewById(R.id.searchBoxRcyclerV);
                srchBoxRecyclerView.setLayoutManager(pLayoutManager);
                srchBoxRecyclerView.setItemAnimator(new DefaultItemAnimator());
                // uses the adapter
                srchBoxRecyclerView.setAdapter(uAdapter);

                // algorithm for parsing the JSONArray from the Django REST API
                for (int i = 0; i < pJObjArray.length(); i++) {
                    // puts the current iterated JSON object from the array to another temporary object
                    JSONObject pJObj_data = pJObjArray.getJSONObject(i);

                    // inputs necesarry elements for the User
                    usersToShow.add(new User(pJObj_data.getString("first_name"), pJObj_data.getString("last_name"), pJObj_data.getString("username"), pJObj_data.getString("email"), pJObj_data.getInt("id")));
                }

                // notifies the adapter when dataset is updated to not reference original dataset post
                uAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.d("Json","Exception = "+e.toString());
        }
    }
}