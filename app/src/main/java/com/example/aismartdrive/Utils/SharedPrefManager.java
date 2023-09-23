package com.example.aismartdrive.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.aismartdrive.DB.user.User;

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

    //FOR REVIEW:
    private static final String KEY_USER_RATING = "userRating"; // Key to store the user's rating
    private static final String KEY_USER_COMMENT = "userComment"; // Key to store the user's comment
    private static final String KEY_USER_VEHICLE_NAME = "vehicleName"; // Key to store the user's comment

    // Set the user's vehicleName
    public static void setVehicleName(String vehicleName) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(KEY_USER_VEHICLE_NAME, vehicleName);
        editor.apply();
    }
    // Get the user's vehicleName
    public static String getVehicleName() {
        return getSharedPreference().getString(KEY_USER_VEHICLE_NAME, null);
    }
    public static void setUserRating(float rating) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putFloat(KEY_USER_RATING, rating);
        editor.apply();
    }
    // Get the user's rating
    public static float getUserRating() {
        return getSharedPreference().getFloat(KEY_USER_RATING, 0.0f);
    }
    // Set the user's comment
    public static void setUserComment(String comment) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(KEY_USER_COMMENT, comment);
        editor.apply();
    }
    // Get the user's comment
    public static String getUserComment() {
        return getSharedPreference().getString(KEY_USER_COMMENT, null);
    }
    public static void saveUser(User user) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putFloat(KEY_USER_RATING, user.getRating());
        editor.putString(KEY_USER_COMMENT, user.getComment());
        editor.apply();
    }
    public static User loadUser() {
        String userEmail = getSharedPreference().getString(KEY_USER_EMAIL, null);
        String userVehicleName = getSharedPreference().getString(KEY_USER_VEHICLE_NAME, null);
        float userRating = getSharedPreference().getFloat(KEY_USER_RATING, 0.0f);
        String userComment = getSharedPreference().getString(KEY_USER_COMMENT, "");

        // Create and return a User object with the loaded data
        User user = new User();
        user.setEmail(userEmail);
        user.setVehicleName(userVehicleName);
        user.setRating(userRating);
        user.setComment(userComment);
        return user;
    }
}

