package android.apps.cashu.com.fingerprint_app.helper;

import android.app.Activity;
import android.apps.cashu.com.fingerprint_app.R;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

public class ToolHelper {

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void launchFingerPrintProcess(Activity activity) {
        new FingerPrintHelper(activity, PrefHelper.getStringPref(activity, PrefHelper.EMAIL_PREF_KEY));
        displayFingerPrintDialog(activity);
    }

    private static void displayFingerPrintDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(R.layout.dialog_finger_print_layout)
                .setTitle(R.string.enable_finger_print_dialog_title)
                .setMessage(R.string.enable_finger_print_dialog_message)
                .setNeutralButton(R.string.enable_finger_print_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        builder.show();
    }

    public static boolean isFingerPrintEnable(Activity activity) {
        return PrefHelper.getBooleanPref(activity, PrefHelper.ENABLE_FINGER_PRINT_PREF_KEY);
    }
}
