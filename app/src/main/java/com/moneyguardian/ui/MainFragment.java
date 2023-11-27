package com.moneyguardian.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleOwnerKt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.ProfileActivity;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.ui.charts.LinearChartFragment;
import com.moneyguardian.ui.charts.PieChartFragment;
import com.moneyguardian.userAuth.LoginActivity;
import com.moneyguardian.userAuth.SignInActivity;
import com.moneyguardian.util.GastoMapper;
import com.moneyguardian.util.LoadDataHelper;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.future.FutureKt;


public class MainFragment extends Fragment implements LifecycleOwner {

    private CircleImageView profileBtn;
    private TextView txtWelcome;
    private LinearLayout tipsLayout;

    private Usuario usuario;


    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private View root;
    private FragmentContainerView chartFragmentContainer;
    private Button btnMenuGraphLine;
    private Button btnMenuGraphPie;
    private LinearChartFragment linearChartFragment = LinearChartFragment.newInstance(new ArrayList<>());
    private PieChartFragment pieChartFragment = PieChartFragment.newInstance(new ArrayList<>());



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
        btnMenuGraphLine = root.findViewById(R.id.btn_lineChart);
        btnMenuGraphPie = root.findViewById(R.id.btn_pieChart);


        if(usuario == null)
            loadUserInfo(root);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(root.getContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        //by default we load the line graph
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.chartFragmentContainer, linearChartFragment)
                .commit();

        btnMenuGraphLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.chartFragmentContainer, linearChartFragment)
                        .commit();
            }
        });

        btnMenuGraphPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.chartFragmentContainer, pieChartFragment)
                        .commit();
            }
        });




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

                            //we load the gastos of the user
                            //we get the references for the different gastos
                            Object obj = documentSnapshot.get("gastos");
                            //if has gastos, load them
                            if(obj != null){
                                List<DocumentReference> refs = (List<DocumentReference>) obj;
                                updateChartDatasetSize(refs.size());

                                //this code works god knows why
                                CompletableFuture<List<Gasto>> gastos = FutureKt.future(
                                        CoroutineScopeKt.CoroutineScope(EmptyCoroutineContext.INSTANCE),
                                        EmptyCoroutineContext.INSTANCE,
                                        CoroutineStart.DEFAULT,
                                        (scope,continuation) -> {
                                            return LoadDataHelper.loadGastosData(
                                                    LifecycleOwnerKt.getLifecycleScope(MainFragment.this),
                                                    refs,
                                                    (Continuation<? super List<? extends Gasto>>) continuation);
                                        }
                                        );
                                try {
                                    gastos.get().forEach(g -> addEntryToGraphs(g));
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                /*for(DocumentReference ref : refs){
                                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            //we map this to a Gasto object
                                            Gasto g = GastoMapper.map(documentSnapshot);
                                            addEntryToGraphs(g);
                                        }
                                    });
                                }*/
                            }
                        }
                    }
                });
    }

    /**
     * adds the given gasto to all the graphs
     * @param g
     */
    private void addEntryToGraphs(Gasto g) {
        linearChartFragment.addData(g);
        pieChartFragment.addData(g);
    }

    /**
     * Goes one by one throug all the charts and tells them the size of data. When charts detect
     * that all data has been received, they update
     * @param size
     */
    private void updateChartDatasetSize(int size) {
        linearChartFragment.setDatasetSize(size);
        pieChartFragment.setDatasetSize(size);
    }
}