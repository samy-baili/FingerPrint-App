package android.apps.cashu.com.fingerprint_app;

import android.app.Application;

import com.github.tntkhang.realmencryptionhelper.RealmEncryptionHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FingerPrintApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        intiRealm();
    }

    private void intiRealm() {
        RealmEncryptionHelper realmEncryptionHelper = RealmEncryptionHelper.initHelper(this, getString(R.string.app_name));

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().name("realm_encrypt.realm").encryptionKey(realmEncryptionHelper.getEncryptKey()).build());
    }
}
