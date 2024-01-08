package com.moneyguardian;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moneyguardian.userAuth.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnEditPicture;
    private ImageButton btnEditUsername;
    private Button btnLogout;
    private EditText txtUsername;
    private TextView txtReestablecer;
    private CircleImageView imgUser;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference userImageRef;

    private boolean inEditUsernameMode;
    private String previousUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        userImageRef = storage.getReference()
                .child("users/"+auth.getCurrentUser().getUid()+".jpg");

        btnEditPicture = findViewById(R.id.btnEditProfilePic);
        btnEditUsername = findViewById(R.id.btnEditUsername);
        btnLogout = findViewById(R.id.btnLogout);
        txtUsername = findViewById(R.id.usernameTxt);
        txtReestablecer = findViewById(R.id.ReestablecerTxt);
        imgUser = findViewById(R.id.profile_image);

        txtUsername.setEnabled(false);

        //we load the name of the current user
        db.collection("users")
                        .document(auth.getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Uri profileUri =Uri.parse(documentSnapshot.get("profilePicture",String.class));
                            Glide.with(getApplicationContext())
                                    .load(profileUri)
                                    .into(imgUser);
                            txtUsername.setText(documentSnapshot.get("name",String.class));
                        }
                    }
                });


        //logic to change the profile picture of the user
        ActivityResultLauncher<Intent> launchSomeActivity
                = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                result -> {
                    if (result.getResultCode()
                            == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null
                                && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(),
                                        selectedImageUri);
                            } catch (FileNotFoundException ef) {
                                ef.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //we store the image into the store and link it to the user entity
                            //in the database for it to be accessed
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                            byte[] bytes = baos.toByteArray();

                            UploadTask uploadTask = userImageRef.putBytes(bytes);
                            Bitmap finalSelectedImageBitmap = selectedImageBitmap;
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),
                                                    getString(R.string.error_upload_user_image),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            }).addOnSuccessListener(taskSnapshot -> {
                                //we update the image on the image show
                                imgUser.setImageBitmap(finalSelectedImageBitmap);
                                //we store the link to the image in the store in the db
                                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                         db.collection("users")
                                                 .document(auth.getUid())
                                                 .update("profilePicture",uri)
                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void unused) {
                                                         Toast.makeText(getApplicationContext(),
                                                                         getString(R.string.ok_update_profilePicture),
                                                                         Toast.LENGTH_LONG)
                                                                 .show();
                                                     }
                                                 }).addOnFailureListener(new OnFailureListener() {
                                                     @Override
                                                     public void onFailure(@NonNull Exception e) {
                                                         Toast.makeText(getApplicationContext(),
                                                                         getString(R.string.error_update_profilePicture),
                                                                         Toast.LENGTH_LONG)
                                                                 .show();
                                                     }
                                                 });
                                    }
                                });
                            });
                        }
                    }
                });

        btnEditPicture.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);

            launchSomeActivity.launch(i);
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
        txtReestablecer.setOnClickListener(v -> txtUsername.setText(previousUsername));

        btnLogout.setOnClickListener(v -> {
            auth = FirebaseAuth.getInstance();

            auth.signOut();
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
        });

    }
}