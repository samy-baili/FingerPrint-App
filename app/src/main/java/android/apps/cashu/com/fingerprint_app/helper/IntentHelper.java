package android.apps.cashu.com.fingerprint_app.helper;

import android.apps.cashu.com.fingerprint_app.activity.LoginActivity;
import android.apps.cashu.com.fingerprint_app.activity.MainActivity;
import android.content.Context;
import android.content.Intent;

public class IntentHelper {

    public static Intent getMainActivityIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static Intent getLoginActivityIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
