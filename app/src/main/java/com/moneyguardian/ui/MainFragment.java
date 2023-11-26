package com.moneyguardian.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.ProfileActivity;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.userAuth.LoginActivity;
import com.moneyguardian.userAuth.SignInActivity;
import com.moneyguardian.util.UsuarioMapper;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainFragment extends Fragment {

    private CircleImageView profileBtn;
    private TextView txtWelcome;
    private LinearLayout tipsLayout;

    private Usuario usuario;


    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private View root;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /* Cuando se selecciona uno de los botones / ítems*/
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();

            //TODO cambiar las new LinkedLists por la lista con datos cargados en ambas

            if (itemId == R.id.menu_graph_linear) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.chartFragmentContainer, LinearChartFragment.newInstance(new LinkedList<>()))
                        .commit();
                return true;
            }

            if (itemId == R.id.menu_graph_pie) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.chartFragmentContainer, PieChartFragment.newInstance(new LinkedList<>()))
                        .commit();
                return true;
            }


            //Si no es nula y no entra... Algo falla.
            throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
    };
    private FragmentContainerView chartFragmentContainer;
    private BottomNavigationView navGraphs;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //we reload user data
        loadUserInfo(root);

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
        this.root = root;
        profileBtn = root.findViewById(R.id.profileButton);
        txtWelcome = root.findViewById(R.id.txtWelcome);
        tipsLayout = root.findViewById(R.id.tipsLayout);
        chartFragmentContainer = root.findViewById(R.id.chartFragmentContainer);
        navGraphs = root.findViewById(R.id.nav_graphs);

        if(usuario == null)
            loadUserInfo(root);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(root.getContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        //listener to the navigation menu for the graph selection
        navGraphs.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return root;
    }


    private void loadUserInfo(View root){
        //we check if user is logged in, if not send to login view
        if(auth.getUid() == null) {
            startActivity(new Intent(getContext(),SignInActivity.class));
        }
        //we load the image of the user into the profile btn
        //we get the uri from database user info 'profilePicture'
        db.collection("users")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            usuario = UsuarioMapper.mapBasics(documentSnapshot);

                            Uri profileUri =Uri.parse(usuario.getUriImg());
                            Glide.with(root.getContext())
                                    .load(profileUri)
                                    .into(profileBtn);

                            //we load the name in the welcome msg
                            txtWelcome.setText( getString(R.string.welcome_msg,
                                    usuario.getNombre()));
                        }
                    }
                });
    }
}