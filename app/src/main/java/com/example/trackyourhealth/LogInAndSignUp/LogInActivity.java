package com.example.trackyourhealth.LogInAndSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackyourhealth.MainActivity;
import com.example.trackyourhealth.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    @BindView(R.id.emailedittext)
    EditText emailedittext;
    @BindView(R.id.passwordedittext)
    EditText passwordedittext;
    @BindView(R.id.loginbutton)
    Button loginbutton;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.facebook_login_button)
    Button facebookLoginButton;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.gmail_login_button)
    Button gmailLoginButton;
    @BindView(R.id.GreateUserbutton)
    ImageView GreateUserbutton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.forgetpassButton)
    ImageView forgetpassButton;
    int RC_SIGN_IN = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.GONE);
        // facebook callback
        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(LogInActivity.this, gso);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                progressBar.setVisibility(View.GONE);

                Toast.makeText(LogInActivity.this, task.getException().getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }


    @OnClick(R.id.loginbutton)
    public void onLoginbuttonClicked() {

        String email = emailedittext.getText().toString();
        String password = passwordedittext.getText().toString();

        if (email.isEmpty() == false && password.isEmpty() == false) {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                if (mAuth.getCurrentUser().isEmailVerified()) {

                                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    progressBar.setVisibility(View.GONE);

                                    finish();
                                } else
                                    Toast.makeText(LogInActivity.this, "Check your EMail for Verify Your Email First",
                                            Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                // Sign in success, update UI with the signed-in user's information

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LogInActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }

                            // ...
                        }
                    });
        } else {
            Toast.makeText(LogInActivity.this, "Empty Field", Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.forgetpassButton)
    public void onForgetpassButtonClicked() {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.GreateUserbutton)
    public void onGreateUserbuttonClicked() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();

    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                                          // Write new User to DataBse
                            Helper helper = new Helper();

                            String propicUrl = "https://graph.facebook.com/" + token.getUserId() + "/picture?type=large";
                            helper.writeNewUser(mAuth.getCurrentUser().getUid(), propicUrl, mAuth.getCurrentUser().getDisplayName()
                                    , mAuth.getCurrentUser().getEmail()
                                    , true);


                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            finish();

                        } else {
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(LogInActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @OnClick(R.id.facebook_login_button)
    public void onFacebookLoginButtonClicked() {

        progressBar.setVisibility(View.VISIBLE);

        LoginManager.getInstance().logInWithReadPermissions(LogInActivity.this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        handleFacebookAccessToken(loginResult.getAccessToken());


                    }

                    @Override
                    public void onCancel() {
                        progressBar.setVisibility(View.GONE);

                        // App code
                        Toast.makeText(LogInActivity.this, "canceled",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(LogInActivity.this, exception.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @OnClick(R.id.gmail_login_button)
    public void onGmailLoginButtonClicked() {
        progressBar.setVisibility(View.VISIBLE);
        signIn();


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //  Log.d(TAG, "signInWithCredential:success");
                            // updateUI(user);
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Write new User to DataBse
                          Helper helper = new Helper();

                            String propicUrl = acct.getPhotoUrl().toString();
                            helper.writeNewUser(user.getUid(), propicUrl, acct.getDisplayName()
                                    , mAuth.getCurrentUser().getEmail()
                                    , true);


                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(LogInActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }


}
