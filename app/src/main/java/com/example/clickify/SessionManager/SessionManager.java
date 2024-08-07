package com.example.clickify.SessionManager;

import android.content.SharedPreferences;
import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SessionManager {

    // SharedPreferences file name
    private static final String PREF_NAME = "ClickifyPrefs";
    private static final String ADMIN_EMAIL = "sarmadaslammemon@gmail.com";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_ADMIN_LOGIN = "IsAdminLogin";
    private static final String KEY_CLICK_SPEED = "ClickSpeed";
    private static final String KEY_TIME_INTERVAL = "TimeInterval";
    private static final String KEY_CONTINUE_TIME = "ContinueTime";
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final String X_CORODINATES ="x";
    private static final String Y_CORODINATES ="y";
    private static final String IS_STARTED = "isStarted";

    // SharedPreferences and Editor
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Method to manage login session
    public void createLoginSession() {
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
    public boolean isAdminLoggedIn() {
        return pref.getBoolean(IS_ADMIN_LOGIN, false);
    }
    public void isAdminLogin()
    {
        editor.putBoolean(IS_ADMIN_LOGIN,true);
        editor.commit();
    }

    public void logoutUser() {
        editor.putBoolean(IS_LOGIN, false);
        editor.putBoolean(IS_ADMIN_LOGIN, false);
        editor.commit();
    }

    public String getClickSpeed() {
        return pref.getString(KEY_CLICK_SPEED, "");
    }

    public String getTimeInterval() {
        return pref.getString(KEY_TIME_INTERVAL, "");
    }

    // Method to store continue time
    public void setClickSetting(String speed,String interval,String continueTime) {
        editor.putString(KEY_CONTINUE_TIME, continueTime);
        editor.putString(KEY_TIME_INTERVAL, interval);
        editor.putString(KEY_CLICK_SPEED, speed);
        editor.commit();
    }


    public String getContinueTime() {
        return pref.getString(KEY_CONTINUE_TIME, "");
    }

    public boolean isAdmin(String email)
    {
        if(email.equals(ADMIN_EMAIL))
        {
            return true;
        }
        return false;
    }


    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void savePointerCoordinates(int x, int y) {
        editor.putInt(X_CORODINATES,x);
        editor.putInt(Y_CORODINATES,y);
        editor.commit();
    }


    public void isStarted(boolean val) {
        editor.putBoolean(IS_STARTED,val);
        editor.commit();
    }


    public int getXAxis() {return pref.getInt(X_CORODINATES,0);}
    public int getYAxis() {return pref.getInt(Y_CORODINATES,0);}

    public boolean getIsStarted() {
        return pref.getBoolean(IS_STARTED,true);
    }
}
