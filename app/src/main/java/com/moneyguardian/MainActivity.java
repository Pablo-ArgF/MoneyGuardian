package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.ui.PagosConjuntosFragment;
import com.moneyguardian.userAuth.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView profileBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, SocialActivity.class);
        startActivity(intent);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        //TODO revisar porque al borrar la cuenta a veces hace login
        if(user == null){
            //if no user -> send to login page
            //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }

        profileBtn = findViewById(R.id.profileButton);

        //we load the image of the user into the profile btn
        //we get the uri from database user info 'profilePicture'
        db.collection("users")
                        .document(auth.getUid())
                                .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    Uri profileUri =Uri.parse(documentSnapshot.get("profilePicture",String.class));
                                    Glide.with(getApplicationContext())
                                            .load(profileUri)
                                            .into(profileBtn);
                                }
                            }
                        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });


    }
}