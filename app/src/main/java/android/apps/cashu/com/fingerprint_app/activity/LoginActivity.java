package android.apps.cashu.com.fingerprint_app.activity;

import android.apps.cashu.com.fingerprint_app.R;
import android.apps.cashu.com.fingerprint_app.helper.HandlingFingerprint;
import android.apps.cashu.com.fingerprint_app.helper.IntentHelper;
import android.apps.cashu.com.fingerprint_app.helper.PrefHelper;
import android.apps.cashu.com.fingerprint_app.model.Credential;
import android.apps.cashu.com.fingerprint_app.helper.ToolHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity_layout);

        init();

        if (ToolHelper.isFingerPrintEnable(this))
            ToolHelper.launchFingerPrintProcess(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null)
            unregisterReceiver(receiver);
    }

    private void init() {
        EditText emailEdit = findViewById(R.id.email);
        emailEdit.setText(PrefHelper.getStringPref(this, PrefHelper.EMAIL_PREF_KEY));

        Button button = findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAuthProcess();
            }
        });

        registerReceiver();
    }

    private void launchAuthProcess() {
        String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();

        if (emailValid(email) && !password.isEmpty()) {
            Credential credential = findCredential(email);

            if (credential != null && !credential.password.equals(password)) {
                launchDialogChangePassword(email);
                return;
            }

            logUser(email, password);
        }
    }

    private boolean emailValid(String email) {
        if (!ToolHelper.isValidEmail(email)) {
            Toast.makeText(this, R.string.email_invalid_message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Credential findCredential(String email) {
        return Realm.getDefaultInstance().where(Credential.class).equalTo("email", email).findFirst();
    }

    private void launchDialogChangePassword(final String email) {
        ViewGroup group = createDialogEditText();

        final EditText editText = (EditText) group.getChildAt(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(group)
                .setTitle(R.string.change_password_dialog_title)
                .setMessage(R.string.change_password_dialog_decription)
                .setPositiveButton(R.string.change_password_dialog_yes_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newPassword = editText.getText().toString().trim();
                        if (!newPassword.isEmpty()) {
                            logUser(email, newPassword);
                        }
                    }
                })
                .setNegativeButton(R.string.change_password_dialog_no_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();
    }

    private ViewGroup createDialogEditText() {
        EditText editText = new EditText(this);
        editText.setHint(R.string.change_password_dialog_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        int pad = getResources().getDimensionPixelSize(R.dimen.dialog_padding);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setPadding(pad, pad, pad, pad);
        frameLayout.addView(editText);

        return frameLayout;
    }

    private void logUser(final String email, final String password) {
        PrefHelper.saveStringPref(this, PrefHelper.EMAIL_PREF_KEY, email);

        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(new Credential(email, password));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        startActivity(IntentHelper.getMainActivityIntent(this));
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(HandlingFingerprint.FINGER_PRINT_REGISTER_ACTION);

        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startMainActivity();
            }
        };

        registerReceiver(receiver, filter);
    }
}
