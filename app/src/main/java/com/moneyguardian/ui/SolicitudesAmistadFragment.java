package com.moneyguardian.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moneyguardian.R;
import com.moneyguardian.adapters.ListaEnviarSolicitudAmistadAdapter;
import com.moneyguardian.adapters.ListaSolicitudAmistadAdapter;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.util.AmistadesUtil;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesAmistadFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    private RecyclerView recyclerSolicitudes;
    private RecyclerView recyclerEnviarSolicitud;
    private ListaEnviarSolicitudAmistadAdapter enviarAdapter;
    private SearchView searchbar;
    private LinearLayout noRequestView;
    private ImageButton btnReloadRequests;
    private ListaSolicitudAmistadAdapter solicitudesAdapter;

    public SolicitudesAmistadFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(solicitudesAdapter != null)
            updateEmptyRequestsView(solicitudesAdapter.getItemCount());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_solicitudes_amistad, container, false);
        recyclerSolicitudes = root.findViewById(R.id.recyclerListaPeticiones);
        recyclerEnviarSolicitud = root.findViewById(R.id.recyclerListaAmigosEnviar);
        searchbar = root.findViewById(R.id.buscadorAmigos);
        noRequestView = root.findViewById(R.id.msgNoSolicitudes);
        btnReloadRequests = root.findViewById(R.id.btnReloadRequests);


        //we add the layout manager to the friend list
        RecyclerView.LayoutManager solicitudesLayoutManager = new LinearLayoutManager(container.getContext());
        recyclerSolicitudes.setLayoutManager(solicitudesLayoutManager);

        //we add the layout manager to the friend list
        RecyclerView.LayoutManager enviarLayoutManager = new LinearLayoutManager(container.getContext());
        recyclerEnviarSolicitud.setLayoutManager(enviarLayoutManager);

        //we create adapter for the enviarSolicitud recycler
        enviarAdapter = new ListaEnviarSolicitudAmistadAdapter(new ArrayList<>(), new ListaEnviarSolicitudAmistadAdapter.OnItemClickListener() {
            @Override
            public void onEnviarSolicitud(Usuario usuario) {
                AmistadesUtil.enviarSolicitudAmistad(usuario);
                enviarAdapter.RemoveUsuario(usuario);
                Toast.makeText(getContext(),getString(R.string.msg_friend_request_sent_to, usuario.getNombre()),
                        Toast.LENGTH_LONG).show();
            }
        });
        //we link adapter with recycler
        recyclerEnviarSolicitud.setAdapter(enviarAdapter);

        //we create empty adapter for the requests
        solicitudesAdapter = new ListaSolicitudAmistadAdapter(new ArrayList<>(),
                new ListaSolicitudAmistadAdapter.OnItemClickListener() {
                    @Override
                    public void onAceptarUsuario(Usuario user) {
                        AmistadesUtil.aceptarSolicitudAmistad(user);
                        solicitudesAdapter.removeRequest(user);
                        updateEmptyRequestsView(solicitudesAdapter.getItemCount());
                    }

                    @Override
                    public void onDenegarUsuario(Usuario user) {
                        AmistadesUtil.denegarSolicitudAmistad(user);
                        solicitudesAdapter.removeRequest(user);
                        updateEmptyRequestsView(solicitudesAdapter.getItemCount());
                    }
                });

        recyclerSolicitudes.setAdapter(solicitudesAdapter);

        cargarSolicitudesAmistad();

        //logic for the search bar
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //load the users with that name and similar ones
                db.collection("users")
                        .orderBy("name").startAt(query).endAt(query+'\uf8ff')
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                //we load all the users
                                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                                    Usuario u = UsuarioMapper.mapBasics(doc);
                                    if(!u.getId().equals(auth.getUid())) //we can not add ourselves
                                        enviarAdapter.addUsuario(u);
                                }
                            }
                        });
                //we clear the recycler
                enviarAdapter.clearUsuarios();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //we do not update on write to avoid query overload
                return false;
            }
        });

        //by default, 5 users from the db will be displayed in the recycler for the user to have
        //quick access to other users and for it to not be empty
        db.collection("users")
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            Usuario u = UsuarioMapper.mapBasics(doc);
                            if(!u.getId().equals(auth.getUid())) //we can not add ourselves
                                enviarAdapter.addUsuario(u);
                        }
                    }
                });

        btnReloadRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarSolicitudesAmistad();
            }
        });

        return root;
    }

    private void updateEmptyRequestsView(int size){
        if(size == 0){
            //we show the view containing the information that no request found
            noRequestView.setVisibility(View.VISIBLE);
            //we hide the recycler
            recyclerSolicitudes.setVisibility(View.INVISIBLE);
        }
        else{
            //back to normal
            //we show the view containing the information that no request found
            noRequestView.setVisibility(View.INVISIBLE);
            //we hide the recycler
            recyclerSolicitudes.setVisibility(View.VISIBLE);
        }
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

                        updateEmptyRequestsView(refsSolicitantes.size());

                        //iteramos por la lista de referencias de usuarios que nos solicitan
                        for(DocumentReference ref : refsSolicitantes){
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            //convertimos este usuario en un Usuario y lo añadimos a la lista
                                            Usuario u = UsuarioMapper.mapBasics(task.getResult());
                                            solicitudesAdapter.addSolicitante(u);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }
}