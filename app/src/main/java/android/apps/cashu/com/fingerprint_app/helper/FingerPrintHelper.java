package android.apps.cashu.com.fingerprint_app.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.apps.cashu.com.fingerprint_app.R;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import androidx.core.app.ActivityCompat;

@TargetApi(Build.VERSION_CODES.M)
public class FingerPrintHelper {

    private static final int FINGER_PRINT_REQUEST_CODE = 9823;
    private String KEY_NAME;
    private Cipher cipher;
    private KeyStore keyStore;

    public FingerPrintHelper(Activity activity, String email) {
        this.KEY_NAME = email;
        launchFingerPrintProcess(activity);
    }

    public boolean launchFingerPrintProcess(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            FingerprintManager fingerprintManager = (FingerprintManager) activity.getSystemService(Context.FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);

            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected()) {
                    Toast.makeText(activity, R.string.no_finger_print_feature, Toast.LENGTH_SHORT).show();
                }
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, R.string.enable_finger_print_feature, Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.USE_FINGERPRINT}, FINGER_PRINT_REQUEST_CODE);
                }

                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(activity, "Your Device has no registered Fingerprints! Please register atleast one in your Device settings", Toast.LENGTH_LONG).show();
                }

                if (!keyguardManager.isKeyguardSecure()) {
                    Toast.makeText(activity, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_LONG).show();

                } else {
                    try {
                        generateKey();
                    }
                    catch (FingerprintException e) {e.printStackTrace(); }

                    if (initCipher()) {
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        HandlingFingerprint helper = new HandlingFingerprint(activity);
                        helper.startAuth(fingerprintManager, cryptoObject);
                    }
                }
            }


        }
        return false;
    }

    private void generateKey() throws FingerprintException {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());

            keyGenerator.generateKey();
        }
        catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CertificateException | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        }
        catch (KeyPermanentlyInvalidatedException e) {
            return false;
        }
        catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }
}
