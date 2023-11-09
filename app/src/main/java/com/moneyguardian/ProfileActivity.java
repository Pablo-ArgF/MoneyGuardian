package com.moneyguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.userAuth.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button btnEditPicture;
    private ImageButton btnEditUsername;
    private Button btnLogout;
    private EditText txtUsername;
    private TextView txtReestablecer;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean inEditUsernameMode;
    private String previousUsername;


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
        txtReestablecer = findViewById(R.id.ReestablecerTxt);

        txtUsername.setEnabled(false);

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

        btnEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] changeToFixedName = new boolean[]{true};
                //if the button is on editMode we confirm the change of username
                if(inEditUsernameMode){
                    //if the username has changed from the prev one we update db
                    if(!previousUsername.equals(txtUsername.getText().toString())){
                        //we validate the username is available in db
                        db.collection("users")
                            .whereEqualTo("name",txtUsername.getText().toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot result) {
                                    if(result.isEmpty()){ //if no users with this name
                                        db.collection("users")
                                                .document(auth.getUid())
                                                .update("name",txtUsername.getText().toString())
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),
                                                                        getString(R.string.error_update_username),
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                    }
                                                })
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getApplicationContext(),
                                                                        getString(R.string.ok_update_username),
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                        //we update the previous name
                                                        previousUsername = txtUsername.getText().toString();
                                                        inEditUsernameMode = false;
                                                        //restore to the button
                                                        ViewCompat.setBackgroundTintList(btnEditUsername,
                                                                ColorStateList.valueOf(getColor(R.color.blue)));
                                                        btnEditUsername.setImageDrawable(
                                                                ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit));
                                                        //restore the restore text to invisible
                                                        txtReestablecer.setVisibility(View.INVISIBLE);
                                                        //make txt field unabled
                                                        txtUsername.setEnabled(false);
                                                    }
                                                });
                                    }
                                    else{ //in case another user has this username
                                        //we show an error to the user
                                        txtUsername.setError(getString(
                                                R.string.error_update_username_alredyUsed));
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                        getString(R.string.error_update_username),
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                    }
                    else{
                        //we update the previous name
                        previousUsername = txtUsername.getText().toString();
                        inEditUsernameMode = false;
                        //restore to the button
                        ViewCompat.setBackgroundTintList(btnEditUsername,
                                ColorStateList.valueOf(getColor(R.color.blue)));
                        btnEditUsername.setImageDrawable(
                                ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit));
                        //restore the restore text to invisible
                        txtReestablecer.setVisibility(View.INVISIBLE);
                        //make txt field unabled
                        txtUsername.setEnabled(false);
                    }

                }
                else{ //if not in edit mode we enable edit mode
                    //we store the initial username in case user wants to go back
                    previousUsername = txtUsername.getText().toString();
                    //we enable the txt field
                    txtUsername.setEnabled(true);
                    //we modify the button to be the confirmation button
                    ViewCompat.setBackgroundTintList(btnEditUsername,
                            ColorStateList.valueOf(getColor(R.color.green)));
                    btnEditUsername.setImageDrawable(
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_done_24));
                    //we show the text to go back to previoius username
                    txtReestablecer.setVisibility(View.VISIBLE);

                    inEditUsernameMode = true;
                }

            }
        });

        //if clicked on the reestablecer text the previus stored username is placed
        txtReestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUsername.setText(previousUsername);
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