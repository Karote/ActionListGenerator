package utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Jia on 2016/5/27.
 */
public class Util {
    private final static String APP_FOLDER_NAME = "ALG";

    public static void removeKeyFromSharedPreferences(Context context, String name, String key) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }

    public static void removeKeyFromSharedPreferences(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().remove(key).apply();
    }

    public static void sharedPreferencesPutString(Context context, String name, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static String sharedPreferencesGetString(Context context, String name, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static void sharedPreferencesPutBoolean(Context context, String name, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean sharedPreferencesGetBoolean(Context context, String name, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static void sharedPreferencesPutString(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).apply();
    }

    public static String sharedPreferencesGetString(Context context, String key, String defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, defValue);
    }

    public static void sharedPreferencesPutLong(Context context, String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(key, value).apply();
    }

    public static long sharedPreferencesGetLong(Context context, String key, long defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, defValue);
    }

    public static void sharedPreferencesPutBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean sharedPreferencesGetBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, defValue);
    }

    public static void showShortToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showShortToast(Context context, @StringRes int messageId) {
        showToast(context, messageId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, @StringRes int messageId) {
        showToast(context, messageId, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    private static void showToast(Context context, @StringRes int messageId, int duration) {
        Toast.makeText(context, messageId, duration).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    public static void hideSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int getResourceId(Context context, String variableName, String resourceName, String packageName) {
        try {
            return context.getResources().getIdentifier(variableName, resourceName, packageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean hasNavBar(Context context) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            return resources.getBoolean(id);
        } else {    // Check for keys
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !hasMenuKey && !hasBackKey;
        }
    }

    public static void saveFile(String dirPath, String fileName, String data) {
        File file = new File(dirPath, fileName);
        file.getParentFile().mkdirs();
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(data);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String dirPath, String fileName) {
        String aBuffer = "";
        try {
            File myFile = new File(dirPath, fileName);
            FileInputStream fIn;
            fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(
                    fIn));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aBuffer;
    }
}
