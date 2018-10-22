package android.apps.cashu.com.fingerprint_app.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefHelper {

    public static String EMAIL_PREF_KEY = "email_pref";
    public static String ENABLE_FINGER_PRINT_PREF_KEY = "enable_finger_print_pref";
    private static String PREF_NAME = "pref_name";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveStringPref(Context context, String key, String string) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, string);
        editor.apply();
    }

    public static String getStringPref(Context context, String key) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(key, null);
    }

    public static void saveBooleanPref(Context context, String key, boolean bool) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    public static boolean getBooleanPref(Context context, String key) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(key, false);
    }

    public static void clearStringPref(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(EMAIL_PREF_KEY);
        editor.remove(ENABLE_FINGER_PRINT_PREF_KEY);
        editor.apply();
    }
}
