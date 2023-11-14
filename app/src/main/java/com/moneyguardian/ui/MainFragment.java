package com.moneyguardian.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.MainActivity;
import com.moneyguardian.ProfileActivity;
import com.moneyguardian.R;
import com.moneyguardian.userAuth.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private CircleImageView profileBtn;
    private TextView txtWelcome;
    private LinearLayout tipsLayout;


    private FirebaseAuth auth;
    private FirebaseFirestore db;



    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            //if no user -> send to login page
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(intent);

        }
        View root = inflater.inflate(R.layout.fragment_main,container, false);
        profileBtn = root.findViewById(R.id.profileButton);
        txtWelcome = root.findViewById(R.id.txtWelcome);
        tipsLayout = root.findViewById(R.id.tipsLayout);


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
                            Glide.with(root.getContext())
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
                Intent i = new Intent(root.getContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        return root;
    }
}