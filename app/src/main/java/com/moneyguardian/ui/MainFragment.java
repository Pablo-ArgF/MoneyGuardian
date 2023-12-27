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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.moneyguardian.FormularioGastoActivity;
import com.moneyguardian.MainActivity;
import com.moneyguardian.ProfileActivity;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.ui.charts.AbstractChartFragment;
import com.moneyguardian.ui.charts.EstadisticasChartFragment;
import com.moneyguardian.ui.charts.IngresosPieChartFragment;
import com.moneyguardian.ui.charts.LinearChartFragment;
import com.moneyguardian.ui.charts.NoChartFragment;
import com.moneyguardian.ui.charts.GastosPieChartFragment;
import com.moneyguardian.userAuth.LoginActivity;
import com.moneyguardian.util.LoadDataHelper;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.future.FutureKt;


public class MainFragment extends Fragment implements LifecycleOwner {

    public ListenerRegistration documentListener; //listener that updates the fragment
    private MainActivity mainActivity;
    private CircleImageView profileBtn;


    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private View root;
    private FragmentContainerView chartFragmentContainer;
    private TextView btnMenuBarChart;
    private TextView btnMenuGraphLine;
    private TextView btnMenuGraphPieGastos;
    private TextView btnMenuGraphPieIngresos;
    private EstadisticasChartFragment barChartFragment = EstadisticasChartFragment.newInstance(new ArrayList<>());
    private LinearChartFragment linearChartFragment = LinearChartFragment.newInstance(new ArrayList<>());
    private GastosPieChartFragment gastosPieChartFragment = GastosPieChartFragment.newInstance(new ArrayList<>());
    private IngresosPieChartFragment ingresosPieChartFragment = IngresosPieChartFragment.newInstance(new ArrayList<>());
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
    private TextView balanceThisMonth;
    private TextView compareBalanceLastMonth;
    private ImageView iconCompareBalanceLastMonth;


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

        currentFragment = linearChartFragment;
        if(mainActivity.getGastos().size() > 0) {
            enableChartView();
            //we mark the all filter as marked
            markSelectedFilter(filterAll);
            //we mark as the selected graph the line graph
            markSelectedGraph(btnMenuGraphLine);
        }
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
        balanceThisMonth = root.findViewById(R.id.balance);
        compareBalanceLastMonth = root.findViewById(R.id.compareBalance);
        iconCompareBalanceLastMonth = root.findViewById(R.id.iconCompareBalance);
        chartFragmentContainer = root.findViewById(R.id.chartFragmentContainer);
        btnMenuBarChart = root.findViewById(R.id.btn_barChart);
        btnMenuGraphLine = root.findViewById(R.id.btn_lineChart);
        btnMenuGraphPieGastos = root.findViewById(R.id.btn_pieChartGastos);
        btnMenuGraphPieIngresos = root.findViewById(R.id.btn_pieChartIngresos);
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
        //we mark as the selected graph the line graph
        markSelectedGraph(btnMenuGraphLine);



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

        btnMenuBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.chartFragmentContainer, barChartFragment)
                        .commit();
                markSelectedFilter(filterAll);
                markSelectedGraph(btnMenuBarChart);
                currentFragment = barChartFragment;
                //filter selector to all
                updateFilterOnGraphs(AbstractChartFragment.Filter.ALL);

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
                markSelectedGraph(btnMenuGraphLine);
                currentFragment = linearChartFragment;
                //filter selector to all
                updateFilterOnGraphs(AbstractChartFragment.Filter.ALL);

            }
        });

        btnMenuGraphPieGastos.setOnClickListener(v -> {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chartFragmentContainer, gastosPieChartFragment)
                    .commit();
            markSelectedFilter(filterAll);
            markSelectedGraph(btnMenuGraphPieGastos);
            currentFragment = gastosPieChartFragment;
            updateFilterOnGraphs(AbstractChartFragment.Filter.ALL);
        });

        btnMenuGraphPieIngresos.setOnClickListener(v -> {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chartFragmentContainer, ingresosPieChartFragment)
                    .commit();
            markSelectedFilter(filterAll);
            markSelectedGraph(btnMenuGraphPieIngresos);
            currentFragment = ingresosPieChartFragment;
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
            documentListener = db.collection("users").document(auth.getUid())
                    .addSnapshotListener((value, error) -> {
                        //if we do not have the context return
                        if(getContext() == null)
                            return;

                        /*if(mainActivity.getGastos().size() ==
                                ((List<DocumentReference>)value.get("gastos")).size())
                            return; //if no update in the number of gastos*/

                        //if user not authenticated, we detach the listener to the view
                        if(auth.getUid() == null){
                            documentListener.remove();
                            return;
                        }
                        mainActivity.setUser(UsuarioMapper.mapBasics(value));
                        //we update name and picture if needed
                        updateUserInfo();
                        //we update the Gastos data
                        updateAllGastos(value);
                    });

        }else{
            if(documentListener != null)
                documentListener.remove();
        }
        return root;
    }

    private void markSelectedGraph(TextView graph) {
        //we mark all as not seleceted
        btnMenuGraphPieIngresos.setTextColor(notSelectedColors);
        btnMenuGraphPieGastos.setTextColor(notSelectedColors);
        btnMenuGraphLine.setTextColor(notSelectedColors);
        btnMenuBarChart.setTextColor(notSelectedColors);

        //we mark the selected one
        graph.setTextColor(ContextCompat.getColor(getContext(),R.color.blue));
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
            List<Gasto> gs = gastos.get();
            gs = gs.stream().filter(g -> g.getUUID() != null).collect(Collectors.toList()); //filter the empty gastos that could come from a listener trigger on removed items
            mainActivity.setGastos(gs);
            addEntrysToGraphs(gs);
            computeThisMonthData(gs);
            //disable the loading of the data
            mainActivity.setLoading(false);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setToLastMinuteOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    /**
     * Shows information on the balance for this month and the comparisson with last month
     */
    private void computeThisMonthData(List<Gasto> gs) {
        if(getContext() == null)
            return; //avoid no context attached

        Calendar calendar = Calendar.getInstance();

        // Last day of two months ago
        calendar.add(Calendar.MONTH, -2);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setToLastMinuteOfTheDay(calendar);
        Date lastDayOfTwoMonthsAgo = calendar.getTime();

        // Last day of last month
        calendar.setTime(new Date()); // Reset to the current date
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the current month
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Move to the last day of the previous month
        setToLastMinuteOfTheDay(calendar);
        Date lastDayOfLastMonth = calendar.getTime();

        // Last day of this month
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setToLastMinuteOfTheDay(calendar);
        Date lastDayOfThisMonth = calendar.getTime();


        List<Gasto> gastosLastMonth = gs.stream()
                .filter(gasto ->
                        gasto.getFechaCreacionAsDate().after(lastDayOfTwoMonthsAgo) &&
                        gasto.getFechaCreacionAsDate().before(lastDayOfLastMonth))
                .collect(Collectors.toList());
        float lastMonth = gastosLastMonth.stream().map(Gasto::getBalance).reduce(0f,Float::sum);

        List<Gasto> gastosThisMonth = gs.stream()
                .filter(gasto ->
                        gasto.getFechaCreacionAsDate().after(lastDayOfLastMonth) &&
                        gasto.getFechaCreacionAsDate().before(lastDayOfThisMonth)
                ).collect(Collectors.toList());
        float thisMonth = gastosThisMonth.stream().map(Gasto::getBalance).reduce(0f,Float::sum);


        //we personalize the balance for this month
        balanceThisMonth.setText(String.format("%.02f€", thisMonth));
        if(thisMonth >= 0)
            balanceThisMonth.setTextColor(getResources().getColor(R.color.green));
        else
            balanceThisMonth.setTextColor(getResources().getColor(R.color.red));
        //we personalize the last month comparison
        if(thisMonth >= lastMonth){
            iconCompareBalanceLastMonth.setImageDrawable(getResources().getDrawable(R.drawable.check_circle));
            compareBalanceLastMonth.setText(getResources().getString(R.string.stats_balance_masQueElMesPasado,
                    thisMonth -lastMonth));
        }else{
            iconCompareBalanceLastMonth.setImageDrawable(getResources().getDrawable(R.drawable.circle_minus));
            compareBalanceLastMonth.setText(getResources().getString(R.string.stats_balance_menosQueElMesPasado,
                    lastMonth - thisMonth));
        }
    }


    private void updateUserInfo() {
        if(getContext() == null)
            return; //avoids not attached to a context

        Uri profileUri =Uri.parse(mainActivity.getUser().getUriImg());
        Glide.with(root.getContext())
                .load(profileUri)
                .into(profileBtn);
    }

    /**
     * adds the given gasto to all the graphs
     * @param gs
     */
    private void addEntrysToGraphs(List<Gasto> gs) {
        barChartFragment.updateData(gs);
        linearChartFragment.updateData(gs);
        gastosPieChartFragment.updateData(gs);
        ingresosPieChartFragment.updateData(gs);
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
        barChartFragment.updateFilter(filter);
        linearChartFragment.updateFilter(filter);
        gastosPieChartFragment.updateFilter(filter);
        ingresosPieChartFragment.updateFilter(filter);

    }

    private void showNoChartView(){
        if(getContext() == null)
            return;

        getChildFragmentManager().beginTransaction()
                .replace(R.id.chartFragmentContainer, noChartFragment)
                .commit();

        //we disable the buttons to change the graph
        btnMenuBarChart.setEnabled(false);
        btnMenuGraphPieGastos.setEnabled(false);
        btnMenuGraphPieIngresos.setEnabled(false);
        btnMenuGraphLine.setEnabled(false);
    }

    private void enableChartView() {
        if(getContext() == null)
            return;

        linearChartFragment.onResume();
        //we disable the buttons to change the graph
        btnMenuBarChart.setEnabled(true);
        btnMenuGraphPieGastos.setEnabled(true);
        btnMenuGraphPieIngresos.setEnabled(true);
        btnMenuGraphLine.setEnabled(true);
        if(!getChildFragmentManager().isStateSaved())
            getChildFragmentManager().beginTransaction()
                .replace(R.id.chartFragmentContainer, linearChartFragment)
                .commit();
    }

}