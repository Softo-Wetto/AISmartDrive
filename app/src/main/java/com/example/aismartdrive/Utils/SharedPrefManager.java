package com.example.aismartdrive.Utils;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPrefManager {
    private static SharedPreferences sharedPreferences = null;

    private static SharedPreferences getSharedPreference(){
        if (sharedPreferences == null){
            sharedPreferences = MyApp.getPreferences();
        }
        return sharedPreferences;
    }

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "user_email"; // Key to store the user's email
    private static final String KEY_USER_ROLE = "userRole";

    public static void setLoginState(boolean isLoggedIn) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public static boolean isLoggedIn() {
        boolean isLoggedIn = getSharedPreference().getBoolean(KEY_IS_LOGGED_IN, false);
        return isLoggedIn;
    }

    private static final String PREF_NAME = "MyAppPrefs";

    private static SharedPreferences preferences;

    // Initialize shared preferences in your application's context
    public static void init(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Set the logged-in user's email
    public static void setUserEmail(String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    // Get the logged-in user's email
    public static String getUserEmail() {
        return getSharedPreference().getString(KEY_USER_EMAIL, null);
    }

    //FUNCTION FOR LOGGING OUT DOWN BELOW
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    public static void clearUserData() {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_USER_ROLE);
        editor.remove(KEY_EMAIL);

        editor.apply();
    }
}

