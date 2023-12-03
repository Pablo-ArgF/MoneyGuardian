package com.moneyguardian.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.moneyguardian.R;
import com.moneyguardian.adapters.DeudaListaAdapter;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.util.PagosConjuntosUtil;

import java.util.ArrayList;
import java.util.List;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_deudas_list, container, false);

        pagosConjuntos = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.recyclerDeudas);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(root.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DeudaListaAdapter();

        recyclerView.setAdapter(adapter);

        cargarDatos();

        return root;
    }

    private void cargarDatos() {
        DocumentReference referenceUser = db.document("users/" + auth.getUid());
        // Obtenemos todos los pagos en los que el usuario está participando
        db.collection("/pagosConjuntos").whereArrayContains("participantes", referenceUser).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                pagosConjuntos.add(PagosConjuntosUtil.getPagoConjuntoFrom(document));
                            }
                            adapter.update(pagosConjuntos, auth.getUid());
                        }
                    }
                });
    }
}