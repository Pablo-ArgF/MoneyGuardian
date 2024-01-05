package com.moneyguardian.userAuth;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.MainActivity;
import com.moneyguardian.R;
import com.moneyguardian.util.Conexion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTextView;
    private TextInputEditText passwordTextView;
    private Button btn_login;
    private Button btn_register;
    private Button btn_google;
    private ProgressBar progressbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private SignInClient oneTapClient;;
    private BeginSignInRequest signUpRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();
        
        
        setContentView(R.layout.activity_login);
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        btn_login = findViewById(R.id.login);
        btn_register = findViewById(R.id.register);
        progressbar = findViewById(R.id.progressBar);
        btn_google = findViewById(R.id.googleBtn);

        Conexion con = new Conexion(getApplicationContext());

        // Set on Click Listener on Sign-in button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(con.compruebaConexion())
                    loginUserAccount();
                else //show msg -> no network conection
                    Toast.makeText(LoginActivity.this,
                            getText(R.string.error_sin_conexion),
                            Toast.LENGTH_SHORT).show();
            }
        });
        //For the register one we load the register activity
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignInActivity.class );
                startActivity(i);
            }
        });

        /*
        This object is the one in charge of google auth launch window and the result of it
        */
        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if(result.getResultCode() == Activity.RESULT_OK){
                                    try {
                                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                                        String idToken = credential.getGoogleIdToken();
                                        if (idToken !=  null) {
                                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                                            mAuth.signInWithCredential(firebaseCredential)
                                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            //if user already on db we login, if not
                                                            //we add to db
                                                            db.collection("users")
                                                                .document(mAuth.getUid())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                       if(task.getResult().exists()){
                                                                           //we just login
                                                                           startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                                                       }
                                                                       else{
                                                                           //we create the user in db
                                                                           storeUserInDB();
                                                                       }
                                                                    }
                                                                });
                                                        }
                                                    });
                                        }
                                    } catch (ApiException e) {
                                        Toast.makeText(LoginActivity.this,
                                                getText(R.string.error_singin_google),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!con.compruebaConexion()) {
                    Toast.makeText(LoginActivity.this,
                            getText(R.string.error_sin_conexion),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                oneTapClient.beginSignIn(signUpRequest)
                        .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                IntentSenderRequest intentSenderRequest =
                                        new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                                activityResultLauncher.launch(intentSenderRequest);
                            }
                        })
                        .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(e.getMessage() != null){
                                    if(e.getMessage().equals("16: Cannot find a matching credential.")) {
                                        //the error is because no google account on the device
                                        Toast.makeText(LoginActivity.this,
                                                getText(R.string.error_singin_google_noAccountOnDevice),
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                // No Google Accounts found. Just continue presenting the signed-out UI.
                                Toast.makeText(LoginActivity.this,
                                        getText(R.string.error_singin_google),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            //launch the main activity
            Intent intent
                    = new Intent(LoginActivity.this,
                    MainActivity.class);
            startActivity(intent);
        }
    }

    private void loginUserAccount()
    {
        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        boolean valid = true;

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            emailTextView.setError(getText(R.string.error_empty_mail));
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordTextView.setError(getText(R.string.error_password_empty));
            valid = false;

        } else if (password.length() < 6) {
            passwordTextView.setError(getString(R.string.error_password_length));
            valid = false;
        }
        if(!valid) //Errors in the fields
        {
            return;
        }

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {

                                    // hide the progress bar
                                    progressbar.setVisibility(View.INVISIBLE);

                                    // if sign-in is successful
                                    // intent to home activity
                                    Intent intent
                                            = new Intent(LoginActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                }

                                else {

                                    // sign-in failed
                                    Toast.makeText(getApplicationContext(),
                                                    getText(R.string.error_singin),
                                                    Toast.LENGTH_LONG)
                                            .show();

                                    // hide the progress bar
                                    progressbar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
    }

    public void storeUserInDB(){
        Map<String, Object> user = new HashMap<>();
        user.put("name", mAuth.getCurrentUser().getEmail());
        user.put("email", mAuth.getCurrentUser().getEmail());
        user.put("gastos",new ArrayList<>());
        user.put("pagosConjuntos",new ArrayList<>());
        user.put("profilePicture", getString(R.string.default_profilePicture));
        db.collection("users")
                .document(mAuth.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        // if the user created intent to login activity
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null){ //Login complete
                            Intent intent
                                    = new Intent(LoginActivity.this,
                                    SignInActivity.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //we delete the user, it could not be created
                        mAuth.getCurrentUser().delete();
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error_db_add_user),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
