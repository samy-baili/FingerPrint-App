package android.apps.cashu.com.fingerprint_app.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.apps.cashu.com.fingerprint_app.R;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

@TargetApi(Build.VERSION_CODES.M)
public class HandlingFingerprint extends FingerprintManager.AuthenticationCallback {

    public final static String FINGER_PRINT_REGISTER_ACTION = "finger_print_register_action";

    private CancellationSignal cancellationSignal;
    private Context context;

    public HandlingFingerprint(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        this.cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
            return;

        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Toast.makeText(context, context.getString(R.string.authentication_error) + errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, context.getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context, context.getString(R.string.authentication_help) + helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        PrefHelper.saveBooleanPref(context, PrefHelper.ENABLE_FINGER_PRINT_PREF_KEY, true);
        sendBroadCast();
    }

    private void sendBroadCast() {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(FINGER_PRINT_REGISTER_ACTION);
        context.sendBroadcast(broadCastIntent);
    }

}
