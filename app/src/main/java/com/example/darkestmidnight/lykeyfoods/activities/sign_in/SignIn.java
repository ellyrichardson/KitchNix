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

import org.json.JSONException;
import org.json.JSONObject;

public class SignIn extends FragmentActivity {

    Button RegBtn, SignInBtn;
    EditText uUserName, uPassWord;
    GetAccessTokens accessTokensHelper;

    // for checking accessToken availability
    SharedPreferences ShPreference;
    SharedPreferences.Editor PrefEditor;
    static String MyPREFERENCES = "API Authentication";
    String accessToken = "Access Token";
    String APIUrl = "http://192.168.1.4:8000/auth/token/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInBtn = (Button) findViewById(R.id.signInApp);
        RegBtn = (Button) findViewById(R.id.createAccSignInPP);

        uUserName = (EditText) findViewById(R.id.usernameEditT);
        uPassWord = (EditText) findViewById(R.id.passwordEditT);

        accessTokensHelper = new GetAccessTokens(this);

        // --------- implement better way for clicking
        // when create sign in button is pressed
        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gets the username and password from the EditText
                String strUserName = uUserName.getText().toString();
                String strPassWord = uPassWord.getText().toString();

                accessTokensHelper.execute(strUserName, strPassWord, APIUrl);

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
}
