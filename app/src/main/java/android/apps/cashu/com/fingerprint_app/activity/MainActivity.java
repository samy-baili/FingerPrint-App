package android.apps.cashu.com.fingerprint_app.activity;

import android.apps.cashu.com.fingerprint_app.R;
import android.apps.cashu.com.fingerprint_app.helper.IntentHelper;
import android.apps.cashu.com.fingerprint_app.helper.PrefHelper;
import android.apps.cashu.com.fingerprint_app.helper.ToolHelper;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureFingerPringButton();
    }

    private void configureFingerPringButton() {
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect(MainActivity.this);
            }
        });

        if (!ToolHelper.isFingerPrintEnable(this))
            ToolHelper.launchFingerPrintProcess(this);
    }

    private void disconnect(Context context) {
        PrefHelper.clearStringPref(context);
        startActivity(IntentHelper.getLoginActivityIntent(context));
    }

}
