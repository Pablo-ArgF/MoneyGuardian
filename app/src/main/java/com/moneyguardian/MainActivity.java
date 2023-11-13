package com.moneyguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.userAuth.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView profileBtn;
    private TextView txtWelcome;
    private LinearLayout tipsLayout;


    private FirebaseAuth auth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            //if no user -> send to login page
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }

        profileBtn = findViewById(R.id.profileButton);
        txtWelcome = findViewById(R.id.txtWelcome);
        tipsLayout = findViewById(R.id.tipsLayout);


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

                                    //we load the name in the welcome msg
                                    txtWelcome.setText( getString(R.string.welcome_msg,
                                            documentSnapshot.get("name",String.class)));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.home_menu_btn) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        if(id == R.id.lista_menu_btn){
            //TODO implement lista view
            return false;
        }
        if(id == R.id.amigos_menu_btn){
            startActivity(new Intent(this, SocialActivity.class));
            return true;
        }
        if(id == R.id.pagos_conjuntos_menu_btn){
            startActivity(new Intent(this,SocialActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}