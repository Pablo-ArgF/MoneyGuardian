package com.moneyguardian.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.moneyguardian.FormularioGastoActivity;
import com.moneyguardian.MainActivity;
import com.moneyguardian.ProfileActivity;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.ui.charts.AbstractChartFragment;
import com.moneyguardian.ui.charts.LinearChartFragment;
import com.moneyguardian.ui.charts.NoChartFragment;
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
    private MainActivity mainActivity;
    private CircleImageView profileBtn;
    private TextView txtWelcome;
    private LinearLayout tipsLayout;


    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private View root;
    private FragmentContainerView chartFragmentContainer;
    private Button btnMenuGraphLine;
    private Button btnMenuGraphPie;
    private LinearChartFragment linearChartFragment = LinearChartFragment.newInstance(new ArrayList<>());
    private PieChartFragment pieChartFragment = PieChartFragment.newInstance(new ArrayList<>());
    private NoChartFragment noChartFragment = new NoChartFragment();
    private AbstractChartFragment currentFragment = linearChartFragment;

    //filters
    private TextView filter1Month;
    private TextView filter3Month;
    private TextView filter1Year;
    private TextView filterAll;
    private ColorStateList notSelectedColors; //color of not selected filter
    private Button btnRegistrarGasto;
    private Button btnRegistrarIngreso;


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
        if(mainActivity.getUser() == null)
            loadUserInfo(root);
        else
            updateUserInfo();

        if(mainActivity.getGastos().size() > 0)
            enableChartView();
        else
            showNoChartView();
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
        chartFragmentContainer = root.findViewById(R.id.chartFragmentContainer);
        btnMenuGraphLine = root.findViewById(R.id.btn_lineChart);
        btnMenuGraphPie = root.findViewById(R.id.btn_pieChart);
        filter1Month = root.findViewById(R.id.filter_1month);
        filter3Month = root.findViewById(R.id.filter_3month);
        filter1Year = root.findViewById(R.id.filter_1year);
        filterAll = root.findViewById(R.id.filter_all);
        btnRegistrarGasto = root.findViewById(R.id.btnGasto);
        btnRegistrarIngreso = root.findViewById(R.id.btnIngreso);
        mainActivity = ((MainActivity)getActivity());
        //we store the not selected color
        this.notSelectedColors = filter1Month.getTextColors();
        //we mark the all filter as marked
        markSelectedFilter(filterAll);



        if(mainActivity.getUser() == null) {
            ((MainActivity)getActivity()).setLoading(true);
            loadUserInfo(root);
        }

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
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.chartFragmentContainer, linearChartFragment)
                        .commit();
                markSelectedFilter(filterAll);
                currentFragment = linearChartFragment;
                //filter selector to all
                updateFilterOnGraphs(AbstractChartFragment.Filter.ALL);

            }
        });

        btnMenuGraphPie.setOnClickListener(v -> {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chartFragmentContainer, pieChartFragment)
                    .commit();
            markSelectedFilter(filterAll);
            currentFragment = pieChartFragment;
            updateFilterOnGraphs(AbstractChartFragment.Filter.ALL);
        });

        //filter configuarion
        filter1Month.setOnClickListener(v -> {
            //update filter button colors
            markSelectedFilter(filter1Month);
            //update graph
            updateFilterOnGraphs(AbstractChartFragment.Filter.ONE_MONTH);
        });
        filter3Month.setOnClickListener(v -> {
            //update filter button colors
            markSelectedFilter(filter3Month);
            //update graph
            updateFilterOnGraphs(AbstractChartFragment.Filter.THREE_MONTHS);
        });
        filter1Year.setOnClickListener(v -> {
            //update filter button colors
            markSelectedFilter(filter1Year);
            //update graph
            updateFilterOnGraphs(AbstractChartFragment.Filter.ONE_YEAR);
        });
        filterAll.setOnClickListener(v -> {
            //update filter button colors
            markSelectedFilter(filterAll);
            //update graph
            updateFilterOnGraphs(AbstractChartFragment.Filter.ALL);
        });

        //listeners to buttons to register gastos and ingresos
        btnRegistrarIngreso.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FormularioGastoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("Ingreso", true);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btnRegistrarGasto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FormularioGastoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("Ingreso", false);
            intent.putExtras(bundle);
            startActivity(intent);
        });


        if(auth.getUid() != null) {
            //listener to get changes on the user be represented here
            db.collection("users").document(auth.getUid())
                    .addSnapshotListener((value, error) -> {
                        mainActivity.setUser(UsuarioMapper.mapBasics(value));
                        //we update name and picture if needed
                        updateUserInfo();
                        //we update the Gastos data
                        updateAllGastos(value);
                    });

        }
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
        if(mainActivity.getUser() != null)
            return; //if user is logged in and stored we do nothing
        //we check if user is logged in, if not send to login view
        if(auth.getUid() == null) {
            startActivity(new Intent(getContext(),LoginActivity.class));
        }else {
            //we load the image of the user into the profile btn
            //we get the uri from database user info 'profilePicture'
            db.collection("users")
                    .document(auth.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            mainActivity.setUser(UsuarioMapper.mapBasics(documentSnapshot));
                            updateUserInfo();
                            updateAllGastos(documentSnapshot);
                        }
                    });
        }
    }

    private void updateAllGastos(DocumentSnapshot documentSnapshot) {
        //we load the gastos of the user
        //we get the references for the different gastos
        Object obj = documentSnapshot.get("gastos");
        //if has gastos, load them
        if(obj != null){
            List<DocumentReference> refs = (List<DocumentReference>) obj;
            if(refs.size() == 0) {
                showNoChartView();
                //we remove the loading
                mainActivity.setLoading(false);
            }
            else {
                enableChartView();
                //we remove the loading
                mainActivity.setLoading(false);
            }
            loadGastosData(refs);
        }
        else{ //if the user has no gastos/ingresos we display an empty fragment
             showNoChartView();
            //we remove the loading
            mainActivity.setLoading(false);
        }
    }

    private void loadGastosData(List<DocumentReference> refs) {
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
            mainActivity.setGastos(gastos.get());
            addEntrysToGraphs(mainActivity.getGastos());
            //disable the loading of the data
            mainActivity.setLoading(false);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void updateUserInfo() {
        if(getContext() == null)
            return; //avoids not attached to a context

        Uri profileUri =Uri.parse(mainActivity.getUser().getUriImg());
        Glide.with(root.getContext())
                .load(profileUri)
                .into(profileBtn);

        //we load the name in the welcome msg
        txtWelcome.setText( getString(R.string.welcome_msg,
                mainActivity.getUser().getNombre()));
    }

    /**
     * adds the given gasto to all the graphs
     * @param gs
     */
    private void addEntrysToGraphs(List<Gasto> gs) {
        linearChartFragment.updateData(gs);
        pieChartFragment.updateData(gs);
    }


    private void updateFilterOnGraphs(AbstractChartFragment.Filter filter) {
        //if the result of filtering we show in the view the empty view
        if(linearChartFragment.numberOfItemsIfFilterApplied(filter) == 0){
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chartFragmentContainer, noChartFragment)
                    .commit();
        }
        else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.chartFragmentContainer,currentFragment)
                    .commit();
        }
        linearChartFragment.updateFilter(filter);
        pieChartFragment.updateFilter(filter);

    }

    private void showNoChartView(){
        if(getContext() == null)
            return;

        getChildFragmentManager().beginTransaction()
                .replace(R.id.chartFragmentContainer, noChartFragment)
                .commit();

        //we disable the buttons to change the graph
        btnMenuGraphPie.setEnabled(false);
        btnMenuGraphLine.setEnabled(false);
    }

    private void enableChartView() {
        if(getContext() == null)
            return;

        linearChartFragment.onResume();
        //we disable the buttons to change the graph
        btnMenuGraphPie.setEnabled(true);
        btnMenuGraphLine.setEnabled(true);
        if(!getChildFragmentManager().isStateSaved())
            getChildFragmentManager().beginTransaction()
                .replace(R.id.chartFragmentContainer, linearChartFragment)
                .commit();
    }

}