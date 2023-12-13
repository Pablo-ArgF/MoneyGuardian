package com.moneyguardian.userAuth;


import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.MainActivity;
import com.moneyguardian.R;
import com.moneyguardian.SocialActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity  extends AppCompatActivity {
    private TextInputEditText emailTextView, passwordTextView;
    private Button btn_register;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            //launch the main activity
            Intent intent
                    = new Intent(SignInActivity.this,
                    MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        btn_register = findViewById(R.id.btnregister);
        progressbar = findViewById(R.id.progressbar);

        // Set on Click Listener on Registration button
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });


    }

    private void registerNewUser()
    {

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

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

        // create new user or register new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            getString(R.string.singin_correcto),
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);

                            //we store the user info in the database, the default name for the
                            //user will be the mail used to login
                            storeUserInDB();
                        }
                        else {
                            //we check if it was a collision on the mail
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                emailTextView.setError(getString(R.string.error_singin_usedMail));
                            else {// Registration failed
                                Toast.makeText(
                                                getApplicationContext(),
                                                getText(R.string.signin_error),
                                                Toast.LENGTH_LONG)
                                        .show();
                            }

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void storeUserInDB(){
        Map<String, Object> user = new HashMap<>();
        user.put("name", mAuth.getCurrentUser().getEmail());
        user.put("email", mAuth.getCurrentUser().getEmail());
        user.put("profilePicture", getString(R.string.default_profilePicture));
        user.put("friends",new ArrayList<>());
        user.put("friendRequests",new ArrayList<>());
        user.put("gastos",new ArrayList<>());

        db.collection("users")
                .document(mAuth.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        // if the user created intent to login activity
                        Intent intent
                                = new Intent(SignInActivity.this,
                                SignInActivity.class);
                        startActivity(intent);
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
