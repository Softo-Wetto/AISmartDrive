package com.example.aismartdrive.Utils;

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
    private static final String KEY_USER_ROLE = "userRole";
    public static void setLoginState(boolean isLoggedIn){
        SharedPreferences.Editor editor =
                getSharedPreference().edit();
// Saving login state
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn); // or false if not logged in
        editor.apply();
    }
    public static boolean isLoggedIn(){
        boolean isLoggedIn =
                getSharedPreference().getBoolean(KEY_IS_LOGGED_IN, false);
        return isLoggedIn;
    }
    public static void setAdmin(boolean isAdmin){
        SharedPreferences.Editor editor =
                getSharedPreference().edit();
        editor.putBoolean(KEY_USER_ROLE, isAdmin);
        editor.apply();
    }
    public static boolean isAdmin(){
        boolean isAdmin =
                getSharedPreference().getBoolean(KEY_USER_ROLE, false);
        return isAdmin;
    }

    //FUNCTION FOR LOGGING OUT DOWN BELOW
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    public static void clearUserData() {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ROLE);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_EMAIL);

        editor.apply();
    }
}
