package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1 ;
    EditText logemail, logpassword;
    TextView signuptxt;
    ImageView googlelogin;
    Button loginbtn;
    private FirebaseAuth mAuth;
    static FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logemail= findViewById(R.id.logemail);
        logpassword= findViewById(R.id.logpassword);
        signuptxt= findViewById(R.id.signuptxt);
        googlelogin= findViewById(R.id.google_login);
        loginbtn= findViewById(R.id.logbtn);
        final InputMethodManager iManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        signuptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });


        if (user != null){
            startActivity(new Intent(LoginActivity.this, ChooseActivity.class));
            LoginActivity.this.finish();
        }

        else
        {
            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uemail= logemail.getText().toString().trim();
                    String upassword= logpassword.getText().toString().trim();
                    assert iManager != null;
                    iManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (TextUtils.isEmpty(uemail) || TextUtils.isEmpty(upassword)){
                        Toast.makeText(LoginActivity.this, "Empty Credential(s)", Toast.LENGTH_SHORT).show();
                    }

                    else{
                        loginUser(uemail, upassword);
                    }
                }

                private void loginUser(String email,String password) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, ChooseActivity.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Error Logging You Up", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();


            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            googlelogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        }
                        else
                            {

                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser o) {
        if (o != null) {
            startActivity(new Intent(LoginActivity.this, ChooseActivity.class));
            finish();
        }
    }
}