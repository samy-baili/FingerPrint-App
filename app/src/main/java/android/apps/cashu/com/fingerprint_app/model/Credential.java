package android.apps.cashu.com.fingerprint_app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Credential extends RealmObject {

    @PrimaryKey
    public String email;

    public String password;

    public Credential() {
        super();
    }

    public Credential(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
