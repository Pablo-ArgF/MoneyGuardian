package com.moneyguardian.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moneyguardian.MainActivity;
import com.moneyguardian.R;
import com.moneyguardian.adapters.DeudaListaAdapter;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class DeudasListFragment extends Fragment {

    private DeudaListaAdapter adapter;
    private RecyclerView recyclerView;

    // Base de datos
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    // UI
    private LinearLayout msgNoDeudas;
    private SwipeRefreshLayout swipeRefreshLayout;

    public DeudasListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_deudas, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.recyclerDeudas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        if (adapter == null) {
            adapter = new DeudaListaAdapter();
        }

        recyclerView.setAdapter(adapter);

        // No recargar los datos de firebase varias veces
        if (adapter != null && this.adapter.getItemCount() == 0) {
            ((MainActivity) getActivity()).setLoading(true);
            cargarDatos();
        } else {
            ((MainActivity) getActivity()).setLoading(false);
        }

        // UI
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshDeudas);
        msgNoDeudas = root.findViewById(R.id.msgNoDeudas);
        updateUIGastos();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).setLoading(true);
                adapter = new DeudaListaAdapter();
                recyclerView.setAdapter(adapter);
                cargarDatos();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    private void cargarDatos() {

        // Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();

        db.collection("users").document(auth.getCurrentUser().getUid()).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        List<DocumentReference> referenciasPagos = (List<DocumentReference>) documentSnapshot.get("pagosConjuntos");

                        if (referenciasPagos != null) {
                            ((MainActivity) getActivity()).setLoading(false);
                            for (DocumentReference document : referenciasPagos) {

                                db.collection("pagosConjuntos").document(document.getId()).get().
                                        addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot document) {
                                                if (document.getData() != null) {
                                                    String nombre = (String) document.getData().get("nombre");
                                                    Uri imagen = null;
                                                    if (document.getData().get("imagen") != null) {
                                                        imagen = Uri.parse((String) document.getData().get("imagen"));
                                                    }
                                                    Date fechaPago = ((Timestamp) document.getData().get("fechaPago")).toDate();
                                                    Date fechaLimite = ((Timestamp) document.getData().get("fechaLimite")).toDate();

                                                    String owner = ((DocumentReference) document.getData().get("pagador")).getId();

                                                    List<DocumentReference> users = (List<DocumentReference>) document.getData().get("participantes");

                                                    List<UsuarioParaParcelable> participantes = new ArrayList<>();

                                                    users.forEach(user -> {
                                                        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot d) {
                                                                participantes.add(UsuarioMapper.mapBasicsParcelable(d));
                                                            }
                                                        });
                                                    });

                                                    List<ItemPagoConjunto> itemsPago = new ArrayList<>();

                                                    Uri finalImagen = imagen;

                                                    db.collection("pagosConjuntos").document(document.getId()).
                                                            collection("itemsPago").orderBy("nombre").
                                                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    List<DocumentSnapshot> itemsPagoSnapshot = queryDocumentSnapshots.getDocuments();

                                                                    for (DocumentSnapshot itemPago : itemsPagoSnapshot) {
                                                                        HashMap<UsuarioParaParcelable, Double> cantidadesConUsers = new HashMap<>();
                                                                        String id = itemPago.getId();
                                                                        String nombre = itemPago.getString("nombre");
                                                                        Double totalMOney = itemPago.getDouble("totalDinero");


                                                                        UsuarioParaParcelable userTHatPays =
                                                                                new UsuarioParaParcelable(itemPago.getString("usuarioPago"));

                                                                        HashMap<String, Double> cantidadesConUsersReferences =
                                                                                (HashMap<String, Double>) itemPago.get("UsuariosConPagos");

                                                                        for (Map.Entry<String, Double> user : cantidadesConUsersReferences.entrySet()) {
                                                                            cantidadesConUsers.put(new UsuarioParaParcelable(user.getKey()), user.getValue());
                                                                        }


                                                                        itemsPago.add(new ItemPagoConjunto(id, nombre, cantidadesConUsers, userTHatPays,totalMOney));
                                                                    }

                                                                    if (nombre == null || fechaLimite == null || fechaPago == null) {
                                                                        throw new RuntimeException(String.valueOf(R.string.ErrorBaseDatosPago));
                                                                    }

                                                                    Log.i("Firebase GET", document.getData().toString());

                                                                    adapter.updateList(new PagoConjunto(document.getId(), nombre,
                                                                            fechaPago, new ArrayList<>(participantes),
                                                                            finalImagen, fechaLimite, itemsPago, owner), new Callable<Void>() {
                                                                        @Override
                                                                        public Void call() throws Exception {
                                                                            // TODO ¿Funcional approach en Java? Como te quedas
                                                                            // Ojalá haber hecho esta clase en Kotlin
                                                                            updateUIGastos();
                                                                            return null;
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                }
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                updateUIGastos();
                                            }
                                        });
                            }
                        }
                    }
                });

    }

    private void updateUIGastos() {
        if (adapter.getItemCount() == 0) {
            msgNoDeudas.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            msgNoDeudas.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }
}