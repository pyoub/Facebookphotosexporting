package org.hiddenfounders.pyoub.minifacebookphotosexporting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class login_facebook extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    TextView textView;
    Bundle bundle;
    album_show as;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        if (AccessToken.getCurrentAccessToken() != null) {
            Intent intent = new Intent(login_facebook.this, album_show.class);
            Bundle bundle = new Bundle();
            intent.putExtra("tok", AccessToken.getCurrentAccessToken());
            intent.putExtra("auth",bundle);
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Log.d("auth", "onStart: "+currentUser.getUid());
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login_facebook);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        Log.d("user", "onCreate: "+"aa");
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("email", "public_profile","user_photos"));
            textView = (TextView) findViewById(R.id.text);

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                    if (loginResult.getAccessToken() != null) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(login_facebook.this, album_show.class);
                        intent.putExtra("tok", AccessToken.getCurrentAccessToken());
                        Log.d("a", "onCreateView: "+mAuth.getCurrentUser().getEmail());
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("signout", "signInWithCredential:success");
                            Toast.makeText(login_facebook.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



}