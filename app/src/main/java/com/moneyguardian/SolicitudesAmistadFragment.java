package com.moneyguardian;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.adapters.ListaSolicitudAmistadAdapter;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.util.AmistadesUtil;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesAmistadFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    private RecyclerView recyclerSolicitudes;
    private RecyclerView recyclerEnviarSolicitud;

    public SolicitudesAmistadFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_solicitudes_amistad, container, false);
        recyclerSolicitudes = root.findViewById(R.id.recyclerListaPeticiones);
        recyclerEnviarSolicitud = root.findViewById(R.id.recyclerListaAmigosEnviar);


        //we add the layout manager to the friend list
        RecyclerView.LayoutManager solicitudesLayoutManager = new LinearLayoutManager(container.getContext());
        recyclerSolicitudes.setLayoutManager(solicitudesLayoutManager);

        //we add the layout manager to the friend list
        RecyclerView.LayoutManager enviarLayoutManager = new LinearLayoutManager(container.getContext());
        recyclerEnviarSolicitud.setLayoutManager(enviarLayoutManager);

        cargarSolicitudesAmistad();

        return root;
    }

    private void cargarSolicitudesAmistad() {
        //cargamos solicitudes de amistad
        List<Usuario> solicitantes = new ArrayList<>();
        db.collection("users").document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //obtenemos las referencias de los usuarios que le piden solicitud
                        ArrayList<DocumentReference> refsSolicitantes = (ArrayList<DocumentReference>)  documentSnapshot.get("friendRequests");
                        //iteramos por la lista de referencias de usuarios que nos solicitan
                        for(DocumentReference ref : refsSolicitantes){
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            //convertimos este usuario en un Usuario y lo añadimos a la lista
                                            String name = document.get("name",String.class);
                                            String email = document.get("email",String.class);
                                            String uriImg = document.get("profilePicture",String.class);
                                            String id = ref.getId();

                                            solicitantes.add(new Usuario(id,name, email, uriImg,null,null));
                                            //if all added, update the adapter to show them
                                            if(solicitantes.size() == refsSolicitantes.size()){
                                                ListaSolicitudAmistadAdapter adapter = new ListaSolicitudAmistadAdapter(solicitantes,
                                                        new ListaSolicitudAmistadAdapter.OnItemClickListener() {
                                                            @Override
                                                            public void onAceptarUsuario(Usuario item) {
                                                                //TODO
                                                                Log.i("a","que bien");
                                                            }

                                                            @Override
                                                            public void onDenegarUsuario(Usuario item) {
                                                                //TODO
                                                                Log.i("a","que mal");
                                                            }
                                                        });

                                                recyclerSolicitudes.setAdapter(adapter);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }
}