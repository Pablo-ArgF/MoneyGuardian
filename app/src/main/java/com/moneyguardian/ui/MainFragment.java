package com.moneyguardian.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
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
import com.moneyguardian.ui.charts.AbstractChartFragment;
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

    private List<Gasto> data = new ArrayList<>();

    //filters
    private TextView filter1Month;
    private TextView filter3Month;
    private TextView filter1Year;
    private TextView filterAll;
    private ColorStateList notSelectedColors; //color of not selected filter


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
        if(usuario == null)
            loadUserInfo(root);
        else
            updateUserInfo();

        //we reload the fragments
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.chartFragmentContainer, linearChartFragment)
                .commit();
        linearChartFragment.onResume();
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
        root = inflater.inflate(R.layout.fragment_main,container, false);
        profileBtn = root.findViewById(R.id.profileButton);
        txtWelcome = root.findViewById(R.id.txtWelcome);
        tipsLayout = root.findViewById(R.id.tipsLayout);
        chartFragmentContainer = root.findViewById(R.id.chartFragmentContainer);
        btnMenuGraphLine = root.findViewById(R.id.btn_lineChart);
        btnMenuGraphPie = root.findViewById(R.id.btn_pieChart);
        filter1Month = root.findViewById(R.id.filter_1month);
        filter3Month = root.findViewById(R.id.filter_3month);
        filter1Year = root.findViewById(R.id.filter_1year);
        filterAll = root.findViewById(R.id.filter_all);
        //we store the not selected color
        this.notSelectedColors = filter1Month.getTextColors();
        //we mark the all filter as marked
        markSelectedFilter(filterAll);

        //by default we load the line graph
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.chartFragmentContainer, linearChartFragment)
                .commit();

        if(usuario == null)
            loadUserInfo(root);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(root.getContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

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

        //filter configuarion
        filter1Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update filter button colors
                markSelectedFilter(filter1Month);
                //update graph
                updateFilterOnGraphs(AbstractChartFragment.Filter.ONE_MONTH);
            }
        });
        filter3Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update filter button colors
                markSelectedFilter(filter3Month);
                //update graph
                updateFilterOnGraphs(AbstractChartFragment.Filter.THREE_MONTHS);
            }
        });
        filter1Year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update filter button colors
                markSelectedFilter(filter1Year);
                //update graph
                updateFilterOnGraphs(AbstractChartFragment.Filter.ONE_YEAR);
            }
        });
        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update filter button colors
                markSelectedFilter(filterAll);
                //update graph
                updateFilterOnGraphs(AbstractChartFragment.Filter.ALL);
            }
        });

        return root;
    }

    /**
     * Marks passed text view as the currently selected filter and the others to not selected
     */
    private void markSelectedFilter(TextView tv) {
        //we mark all as not selected
        filter1Month.setTextColor(notSelectedColors);
        filter3Month.setTextColor(notSelectedColors);
        filter1Year.setTextColor(notSelectedColors);
        filterAll.setTextColor(notSelectedColors);

        //we mark the passed one with blue color
        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.blue));
    }


    private void loadUserInfo(View root){
        if(usuario != null)
            return; //if user is logged in and stored we do nothing
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

                            updateUserInfo();

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
                                    data = gastos.get();
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

    private void updateUserInfo() {
        Uri profileUri =Uri.parse(usuario.getUriImg());
        Glide.with(root.getContext())
                .load(profileUri)
                .into(profileBtn);

        //we load the name in the welcome msg
        txtWelcome.setText( getString(R.string.welcome_msg,
                usuario.getNombre()));
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

    private void updateFilterOnGraphs(AbstractChartFragment.Filter filter) {
        linearChartFragment.updateFilter(filter);
        pieChartFragment.updateFilter(filter);
    }

}