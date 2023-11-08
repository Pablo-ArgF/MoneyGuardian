package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.userAuth.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button btnEditPicture;
    private Button btnEditUsername;
    private Button btnLogout;
    private EditText txtUsername;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnEditPicture = findViewById(R.id.btnEditProfilePic);
        btnEditUsername = findViewById(R.id.btnEditUsername);
        btnLogout = findViewById(R.id.btnLogout);
        txtUsername = findViewById(R.id.usernameTxt);

        txtUsername.setEnabled(false);


        //TODO añadir los listeners de los botones de edit

        //we load the name of the current user
        db.collection("users")
                        .document(auth.getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            //TODO meter la foto
                            txtUsername.setText(documentSnapshot.get("name",String.class));
                        }
                    }
                });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }
}