package com.moneyguardian.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

public class DeudasListFragment extends Fragment {

    private DeudaListaAdapter adapter;
    private RecyclerView recyclerView;

    // Base de datos
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Datos
    private List<PagoConjunto> pagosConjuntos;

    public DeudasListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_deudas_list, container, false);

        pagosConjuntos = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.recyclerDeudas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DeudaListaAdapter();

        recyclerView.setAdapter(adapter);

        cargarDatos();

        return root;
    }

    private void cargarDatos() {

        // Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ArrayList<PagoConjunto> pagos = new ArrayList<>();

        db.collection("users").document(auth.getCurrentUser().getUid()).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                List<DocumentReference> referenciasPagos = (List<DocumentReference>) documentSnapshot.get("pagosConjuntos");

                if (referenciasPagos != null) {

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

                                                UsuarioParaParcelable userTHatPays =
                                                        new UsuarioParaParcelable(itemPago.getString("usuarioPago"));

                                                HashMap<String, Double> cantidadesConUsersReferences =
                                                        (HashMap<String, Double>) itemPago.get("UsuariosConPagos");

                                                for (Map.Entry<String, Double> user : cantidadesConUsersReferences.entrySet()) {
                                                    cantidadesConUsers.put(new UsuarioParaParcelable(user.getKey()), user.getValue());
                                                }


                                                itemsPago.add(new ItemPagoConjunto(id, nombre, cantidadesConUsers,userTHatPays));
                                            }

                                            if (nombre == null || fechaLimite == null || fechaPago == null) {
                                                throw new RuntimeException(String.valueOf(R.string.ErrorBaseDatosPago));
                                            }

                                            Log.i("Firebase GET", document.getData().toString());

                                            adapter.updateList(new PagoConjunto(document.getId(), nombre,
                                                    fechaPago, new ArrayList<>(participantes),
                                                    finalImagen, fechaLimite, itemsPago, owner));
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}