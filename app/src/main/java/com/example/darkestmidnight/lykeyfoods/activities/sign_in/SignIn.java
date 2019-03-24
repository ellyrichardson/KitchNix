package com.example.darkestmidnight.lykeyfoods.activities.sign_in;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.activities.create_account.CreateAccount;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.MainNavigation;
import com.example.darkestmidnight.lykeyfoods.helpers.async.GetAccessTokens;
import com.example.darkestmidnight.lykeyfoods.helpers.async.GetUserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignIn extends FragmentActivity {

    Button RegBtn, SignInBtn;
    EditText uUserName, uPassWord;
    GetAccessTokens accessTokensHelper;
    GetUserInfo getUserID;

    // for checking accessToken availability
    SharedPreferences ShPreference;
    SharedPreferences.Editor PrefEditor;
    static String MyPREFERENCES = "API Authentication";
    String accessToken = "Access Token";
    String APIUrl = "http://192.168.1.4:8000/auth/token/";
    String currentUserID = "Current User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInBtn = (Button) findViewById(R.id.signInApp);
        RegBtn = (Button) findViewById(R.id.createAccSignInPP);

        uUserName = (EditText) findViewById(R.id.usernameEditT);
        uPassWord = (EditText) findViewById(R.id.passwordEditT);

        accessTokensHelper = new GetAccessTokens(this, this);
        getUserID = new GetUserInfo(this, this);

        // --------- implement better way for clicking
        // when create sign in button is pressed
        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gets the username and password from the EditText
                String strUserName = uUserName.getText().toString();
                String strPassWord = uPassWord.getText().toString();

                accessTokensHelper.execute(strUserName, strPassWord, APIUrl);
                getUserID.execute("http://192.168.1.4:8000/api/userinfo/");
                //getAccessTokens(strUserName, strPassWord, APIUrl);

                // to empty access Token before login
                emptyAccessToken();

                //startActivity(new Intent(SignIn.this, MainNavigation.class));

                // if access token exists for user, then user can go to main navigation page
                // ------ sign in allows user to login with "none" accessToken!
                if (checkAccessToken() == true) {
                    // goes to the mainNavigation activity
                    startActivity(new Intent(SignIn.this, MainNavigation.class));
                }
                else {
                    // add functionality if user doesn't have access Token
                }
            }
        });

        // when create account button is pressed
        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goes to the createAccount activity
                startActivity(new Intent(SignIn.this, CreateAccount.class));
            }
        });
    }

    private void emptyAccessToken(){
        ShPreference = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // puts invalid accessToken
        PrefEditor = ShPreference.edit();
        PrefEditor.putString(accessToken, "none");
        PrefEditor.commit();
    }

    private boolean checkAccessToken(){
        // checks if accessToken for user exists
        String aToken = ShPreference.getString(accessToken, "");
        if (ShPreference.getString(accessToken, "") != "none") {
            Log.e("TAG", ShPreference.getString(accessToken, ""));
            return true;
        }

        return true;
    }

    public void jRecipesOnClick(View view) {

    }

    /*private void getAccessTokens(String username, String password, String url) {
        StringBuilder result = new StringBuilder();

        HttpURLConnection httpURLConnection = null;
        try {

            // Sets up connection to the URL
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

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
            jsonParam.put("username", username);
            jsonParam.put("password", password);
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

        ShPreference = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // edits shared preferences for authentication and authorization
        PrefEditor = ShPreference.edit();

        // to save the Access Token from the API
        try {
            JSONObject pJObject = new JSONObject(result.toString());
            PrefEditor.putString(accessToken, pJObject.getString("access_token"));
            PrefEditor.commit();

        } catch (JSONException e) {
            Log.d("Json","Exception = "+e.toString());
        }
    }*/

    //TODO: always reset the currentUserID in sharedpreference before signin
    private void getUserID() {
        StringBuilder result = new StringBuilder();

        // gets the AccessToken
        ShPreference = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String APIAuthentication = "Bearer " + ShPreference.getString(accessToken, "");

        HttpURLConnection httpURLConnection = null;
        try {

            // Sets up connection to the URL (params[2] from .execute in "login")
            httpURLConnection = (HttpURLConnection) new URL("http://192.168.1.4:8000/api/userinfo/").openConnection();
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

        try {
            // DRF API returns an array for this
            JSONArray pJObjArray = new JSONArray(result.toString());

            // algorithm for parsing the JSONArray from the Django REST API
            for (int i = 0; i < pJObjArray.length(); i++) {
                // since the JSON data is an array type, it has to be put as object
                JSONObject pJObj_data = pJObjArray.getJSONObject(i);

                PrefEditor = ShPreference.edit();
                // to save currentUserID
                PrefEditor.putInt(currentUserID, pJObj_data.getInt("id"));
                PrefEditor.commit();
            }

        } catch (JSONException e) {
            //Toast.makeText(JSonActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.d("Json","Exception = "+e.toString());
        }
    }
}
