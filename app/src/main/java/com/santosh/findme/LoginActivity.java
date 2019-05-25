package com.santosh.findme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.SimpleTimeZone;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.customFirebaseUI)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.launcher)
                        .setTosAndPrivacyPolicyUrls("https://www.google.com/","https://www.google.com/")
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("name",user.getDisplayName());
                editor.putString("email",user.getEmail());
                editor.putString("img_url",user.getPhotoUrl()+"");
                editor.putString("uid",user.getUid());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                editor.putString("user_since",sdf.format(new Date(Objects.requireNonNull(user.getMetadata()).getCreationTimestamp())));
/*                if (user.getProviderData().get(0).getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                    editor.putString("provider","google");
                }else{
                    editor.putString("provider","fb");
                }*/
                editor.apply();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                LoginActivity.this.finish();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                assert response != null;
                Toast.makeText(this, Objects.requireNonNull(response.getError()).getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
