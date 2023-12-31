package com.moneyguardian.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.FormularioGastoActivity;
import com.moneyguardian.MainActivity;
import com.moneyguardian.R;
import com.moneyguardian.adapters.GastoListaAdapter;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.util.Animations;
import com.moneyguardian.util.GastosUtil;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaGastosFragment extends Fragment {
    public static final String GASTO_CREADO = "GASTO_CREADO";
    public static final int GESTION_GASTO = 10;
    private RecyclerView recyclerView;
    private GastoListaAdapter adapter;
    private MainActivity mainActivity;

    // Base de datos
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Botones
    private Animations animations;
    // UI
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout msgNoGastos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        View root = inflater.inflate(R.layout.fragment_lista_gastos, container, false);
        mainActivity = ((MainActivity) getActivity());


        // Animaciones de botones
        animations = new Animations(root);

        //Botones
        FloatingActionButton buttonOpen = root.findViewById(R.id.buttonOpenMenuGastos);
        FloatingActionButton buttonAddIngreso = root.findViewById(R.id.buttonAddIngreso);
        FloatingActionButton buttonAddGasto = root.findViewById(R.id.buttonAddGasto);

        FloatingActionButton buttonDelete = root.findViewById(R.id.btnEliminarGasto);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBorrarGasto();
            }
        });

        // UI
        animations.setOnClickAnimationAndVisibility(buttonOpen);
        animations.setOtherButtons(Arrays.asList(buttonAddIngreso, buttonAddGasto));
        animations.setButtonDelete(buttonDelete);
        msgNoGastos = root.findViewById(R.id.msgNoGastos);

        // Manejo de refresh
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshGastos);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).setLoading(true);

                int nightModeFlags = getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
                adapter = new GastoListaAdapter(nightModeFlags);
                recyclerView.setAdapter(adapter);
                cargarDatos();
                swipeRefreshLayout.setRefreshing(false);
                updateUIGastos();
            }
        });

        recyclerView = root.findViewById(R.id.recyclerGastos);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(root.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        // Para no rehacer el adapter cuando cambiamos de fragment
        if (adapter == null) {
            int nightModeFlags = getResources().getConfiguration().uiMode &
                    Configuration.UI_MODE_NIGHT_MASK;
            adapter = new GastoListaAdapter(nightModeFlags);
        }

        // Si el adapter ya existe, lo colocamos
        // Se que parece codigo innecesario, pero puede dar errores quitando el condicional
        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }

        // Recuperar gastos del usuario

        updateUIGastos();
        // Si no hay adapter, o no hay items los cargamos
        if (adapter == null || adapter.getItemCount() == 0) {
            //we enable the loading view until data is loaded
            mainActivity.setLoading(true);
            recuperarGastos();
        } else {
            mainActivity.setLoading(false);
        }


        buttonAddIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormularioGastoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Ingreso", true);
                intent.putExtras(bundle);
                startActivityForResult(intent, GESTION_GASTO);
            }
        });


        buttonAddGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormularioGastoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Ingreso", false);
                intent.putExtras(bundle);
                startActivityForResult(intent, GESTION_GASTO);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.getItemCount() == 0) {
            //we enable the loading screen
            mainActivity.setLoading(true);
            recuperarGastos();
            updateUIGastos();
        }
    }

    /**
     * Diferencia principal con cargar gastos: va a buscarlos al MainActivity si
     * es que ya están cargados
     * SOLO UTILZIAR cuando se inicializa el fragmento para no dar sobrecarga a la carga de listas
     * pero es importante que cuando se borra un gasto se actualize la lista actual y la de
     * MainActivity
     */
    private void recuperarGastos() {
        if (mainActivity.getGastos() != null && mainActivity.getGastos().size() > 0) {
            mainActivity.getGastos().forEach(g -> adapter.add(g));
            //we disable the loading screen
            mainActivity.setLoading(false);
            return;
        }
        cargarDatos();
        updateUIGastos();
    }

    private void cargarDatos() {
        db.collection("users/").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
                    if (documentSnapshot.get("gastos") != null) {
                        List<DocumentReference> gastos = (List<DocumentReference>) documentSnapshot.get("gastos");
                        gastos.forEach(gasto -> {
                            gasto.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        // No podemos usarlo por culpa de la fecha :D
                                        // Gasto g = documentSnapshot.toObject(Gasto.class);
                                        String nombre = (String) documentSnapshot.get("nombre");
                                        double balance = (double) documentSnapshot.get("balance");
                                        String fecha = (String) documentSnapshot.get("fechaCreacion");
                                        String categoria = (String) documentSnapshot.get("categoria");
                                        Gasto g = new Gasto(nombre, (float) balance, categoria, fecha);
                                        g.setReference(gasto);
                                        g.setUUID(gasto.getId());
                                        adapter.add(g);
                                    }
                                }
                            });
                        });
                    }
                    //we disable the loading screen
                    mainActivity.setLoading(false);
                    updateUIGastos();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle b = data.getExtras();
            if (b.get(GASTO_CREADO) != null) {
                Gasto gastoCreado = (Gasto) b.get(GASTO_CREADO);
                gastoCreado.setReference(db.document("/gastos/" + gastoCreado.getUUID()));
                this.mainActivity.getGastos().add(gastoCreado);
                this.adapter.add(gastoCreado);
            }
        }
    }

    private void alertBorrarGasto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.question_remove_gasto)
                .setPositiveButton(R.string.confirm_remove_friend, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                        List<Gasto> gastosList = new ArrayList<>();
                        // Si hay mapa y hay gastos seleccionados
                        if (adapter.getCheckedGastos() != null && adapter.getNumberOfChecked() > 0) {
                            gastosList = GastosUtil.deleteGastos(adapter.getCheckedGastos());
                            mainActivity.getGastos().removeAll(gastosList);
                            adapter.deleteGastos(gastosList);
                            updateUIGastos();
                            // Si no hay mapa, o no hay ninguno seleccionado
                        } else {
                            Toast.makeText(getContext(), getString(R.string.no_gasto_selected), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                }).create().show();
    }

    private void updateUIGastos() {
        if (adapter.getItemCount() == 0 && mainActivity.getGastos().size() == 0) {
            msgNoGastos.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            msgNoGastos.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }
}
