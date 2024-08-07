package com.example.clickify.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.clickify.API_Manager.ApiManager;
import com.example.clickify.AdminDashBoard.AdminDashBoard;

import com.example.clickify.OverLayService.OverlayActivity;
import com.example.clickify.R;
import com.example.clickify.SessionManager.SessionManager;
import com.example.clickify.UtilityClass;
import com.google.android.material.snackbar.Snackbar;

public class LoginScreen extends AppCompatActivity {

    EditText userLoginEmail;
    Button btnLogin;
    SessionManager sm;
    View rootview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        sm=new SessionManager(this);

        hooks();
        clickListner();


    }

    private void clickListner() {
        btnLogin.setOnClickListener(v -> {
            String email=userLoginEmail.getText().toString();
            if(email.isEmpty())
            {userLoginEmail.setError("Email Required!");
            }else if(!SessionManager.isValidEmail(email))
            {userLoginEmail.setError("Invalid Email!");}
            else {
                if (sm.isAdmin(email)) {
                    startActivity(new Intent(LoginScreen.this, AdminDashBoard.class));
                    finish();
                    sm.createLoginSession();
                    sm.isAdminLogin();
                } else {
                    if(UtilityClass.isInternetAvailable(this)) {
                        addclienttodb(email);
                    }else
                    {
                        Snackbar.make(rootview, "Please Check Your Internet Connection!", Snackbar.LENGTH_LONG).show();
                    }
                    ////fetchAllUser();
                  //  sendToApiManager(email);
//                    databaseHelper.addUser(email, false);
//                    btnLogin.setClickable(false);
                }
            }

        });
    }
    private  void addclienttodb(String email)
    {
        ApiManager api=new ApiManager(this);
        api.addUser(email, s -> {

            if(s.equals("Approved User"))
            {
                // intent from here
                startActivity(new Intent(LoginScreen.this, OverlayActivity.class));
                sm.createLoginSession();
                finish();
                Snackbar.make(rootview, "Success", Snackbar.LENGTH_LONG).show();
            }else
            {
                Snackbar.make(rootview, "Please Wait for Admin Confirmation!", Snackbar.LENGTH_LONG).show();
//                btnLogin.setClickable(false);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.getMessage() == null) {
                   Snackbar.make(rootview, "No Internet Connection", Snackbar.LENGTH_LONG).show();
               }else if(volleyError.getMessage().contains("java.net.UnknownHostException:")) {
                    Snackbar.make(rootview, "Check Your Internet Connection", Snackbar.LENGTH_LONG).show();
                }else
               {
                   Snackbar.make(rootview, "Try again Later !", Snackbar.LENGTH_LONG).show();
               }
            }
        });
    }
    private void hooks() {
        userLoginEmail=findViewById(R.id.userLoginEmail);
        btnLogin=findViewById(R.id.btnNext);
        rootview = findViewById(android.R.id.content);
    }
}