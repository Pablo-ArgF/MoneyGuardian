package com.moneyguardian.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.MainActivity;
import com.moneyguardian.R;
import com.moneyguardian.adapters.ListaAmigosAdapter;
import com.moneyguardian.adapters.ListaGruposAdapter;
import com.moneyguardian.modelo.GrupoUsuarios;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.util.AmistadesUtil;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaAmigosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaAmigosFragment extends Fragment {

    //This view will hand other views the selected items
    private static final String GRUPO_AMIGOS_SELECCIONADO = "Grupo de amigos seleccionado";
    private static final String AMIGO_SELECCIONADO = "Amigo seleccionado";


    private List<GrupoUsuarios> listaGrupos;
    private List<Usuario> listaAmigos;

    private RecyclerView listaAmigosView;
    private RecyclerView listaGruposView;
    private SwipeRefreshLayout swipeRefreshLayoutAmigos;
    private SwipeRefreshLayout swipeRefreshLayoutGrupos;

    private ListaAmigosAdapter amigosAdapter;
    private Button btnGestionAmigos;
    private Button btnAddGroup;
    private MainActivity mainActivity;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout msgNoFriends;
    private LinearLayout msgNoGroups;
    private ListaGruposAdapter gruposAdapter;


    public ListaAmigosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListaAmigosFragment.
     */
    public static ListaAmigosFragment newInstance() {
        ListaAmigosFragment fragment = new ListaAmigosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        listaAmigos = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();
        cargarListaAmigos();
        cargarListaGruposAmigos();
        updateUIFriends();
    }

    public void clickonDeleteAmigo(Usuario amigo) {
        Log.i("Click adapter", "Item Clicked to be removed " + amigo.getNombre());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.question_remove_friend)
                .setPositiveButton(R.string.confirm_remove_friend, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                        // Handle delete button click
                        AmistadesUtil.borrarAmigo(amigo);
                        int index = listaAmigos.indexOf(amigo);
                        amigosAdapter.deleteAmigo(amigo);
                        listaAmigos.remove(amigo);
                        amigosAdapter.notifyItemRemoved(index);
                        updateUIFriends();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                        //Do nothing
                    }
                }).create().show();
    }

    private void updateUIFriends() {
        if (amigosAdapter.getItemCount() == 0) {
            msgNoFriends.setVisibility(View.VISIBLE);
            swipeRefreshLayoutAmigos.setVisibility(View.GONE);
        } else {
            msgNoFriends.setVisibility(View.GONE);
            swipeRefreshLayoutAmigos.setVisibility(View.VISIBLE);
        }
    }

    private void updateUIGroups() {
        if (gruposAdapter.getItemCount() == 0) {
            msgNoGroups.setVisibility(View.VISIBLE);
            swipeRefreshLayoutGrupos.setVisibility(View.GONE);
        } else {
            msgNoGroups.setVisibility(View.GONE);
            swipeRefreshLayoutGrupos.setVisibility(View.VISIBLE);
        }
    }

    private void cargarListaAmigos() {
        mainActivity.getAmigos().clear();
        //we load current user info related to friends
        db.collection("users")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<DocumentReference> friendRefs =
                                (List<DocumentReference>) documentSnapshot.get("friends");

                        if (friendRefs == null || friendRefs.size() == 0){
                            amigosAdapter.clear();
                            updateUIFriends();
                            return;
                        }

                        //we load all the friends
                        for (int i = 0; i < friendRefs.size(); i++) {
                            friendRefs.get(i).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            //we map this element to a user and we add it to the list
                                            //of friends
                                            Usuario usuario = UsuarioMapper.mapBasics(documentSnapshot);
                                            amigosAdapter.addAmigo(usuario);
                                            mainActivity.getAmigos().add(usuario);
                                            updateUIFriends();
                                        }
                                    });

                        }

                    }
                });
    }

    private void cargarListaGruposAmigos() {
        //TODO cambiar esto por llamada bd


        String[] nombresGrupos = {"Amigos de la Universidad", "Familiares", "Compañeros de Trabajo", "Vecinos", "Amigos de la Infancia", "Equipo de Deportes", "Amigos de Club de Lectura", "Compañeros de Clase", "Vecinos de la Calle A", "Amigos de Juegos en Línea"};
        List usuarios = IntStream.range(0, 30)
                .mapToObj(i -> new Usuario("a", "Usuario " + (i + 1), "usuario" + (i + 1) + "@example.com", null, null, null))
                .collect(Collectors.toList());
        List<List<Usuario>> miembrosGrupos = Arrays.asList(usuarios.subList(0, 3), usuarios.subList(3, 6), usuarios.subList(6, 9), usuarios.subList(9, 12), usuarios.subList(12, 15), usuarios.subList(15, 18), usuarios.subList(18, 21), usuarios.subList(21, 24), usuarios.subList(24, 27), usuarios.subList(27, 30));


        listaGrupos = new LinkedList<>();
        for (int i = 0; i < nombresGrupos.length; i++) {
            GrupoUsuarios grupo = new GrupoUsuarios(nombresGrupos[i], miembrosGrupos.get(i));
            listaGrupos.add(grupo);
        }


        updateUIGroups();
    }

    private void clickonGrupo(GrupoUsuarios grupo) {

        Log.i("Click adapter", "Item Clicked " + grupo.getNombre());

        //Paso el modo de apertura
        /*
        //TODO meter aqui si se quiere logica al clickar un grupo
        Intent intent=new Intent (MainRecyclerActivity.this, ShowMovie.class);
        intent.putExtra(PELICULA_SELECCIONADA, peli);

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_lista_amigos, container, false);
        mainActivity = (MainActivity) getActivity();
        listaGruposView = root.findViewById(R.id.recyclerListaGruposAmigos);
        listaAmigosView = root.findViewById(R.id.recyclerListaAmigos);
        btnGestionAmigos = root.findViewById(R.id.btnGestionAmigos);
        btnAddGroup = root.findViewById(R.id.btnNuevoGrupoAmigos);
        msgNoFriends = root.findViewById(R.id.msgNoFriends);
        msgNoGroups = root.findViewById(R.id.msgNoGroups);
        swipeRefreshLayoutAmigos = root.findViewById(R.id.swipeRefreshAmigos);
        swipeRefreshLayoutAmigos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarListaAmigos();
                swipeRefreshLayoutAmigos.setRefreshing(false);
            }
        });
        swipeRefreshLayoutGrupos = root.findViewById(R.id.swipeRefreshGruposAmigos);
        swipeRefreshLayoutGrupos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarListaGruposAmigos();
                swipeRefreshLayoutGrupos.setRefreshing(false);
            }
        });


        //we add the layout manager to the group list
        RecyclerView.LayoutManager groupLayoutManager = new LinearLayoutManager(container.getContext());
        listaGruposView.setLayoutManager(groupLayoutManager);

        //we add the layout manager to the friend list
        RecyclerView.LayoutManager friendLayoutManager = new LinearLayoutManager(container.getContext());
        listaAmigosView.setLayoutManager(friendLayoutManager);

        //we create the adapter
        amigosAdapter = new ListaAmigosAdapter(listaAmigos,
                new ListaAmigosAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Usuario item) {
                        //
                    }

                    @Override
                    public void onDeleteClick(Usuario item) {
                        clickonDeleteAmigo(item);
                    }
                });
        listaAmigosView.setAdapter(amigosAdapter);

        gruposAdapter = new ListaGruposAdapter(new ArrayList<>(),
                (grupo) -> {
                    clickonGrupo(grupo);
                });
        listaGruposView.setAdapter(gruposAdapter);


        //cargamos los datos en la vista
        cargarListaGruposAmigos();
        if(mainActivity.getAmigos() == null || mainActivity.getAmigos().size() == 0)
            cargarListaAmigos();
        else
            mainActivity.getAmigos().forEach(a -> amigosAdapter.addAmigo(a));
        //si no hay amigos enseñamos el mensaje
        updateUIFriends();

        //listener al boton de gestion de amigos
        btnGestionAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SolicitudesAmistadFragment fragmentFriendManagement = new SolicitudesAmistadFragment();
                //we pass as arguments the list of friends
                Bundle args = new Bundle();
                args.putParcelableArrayList("friends", new ArrayList<>(listaAmigos));
                fragmentFriendManagement.setArguments(args);
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                        fragmentFriendManagement).addToBackStack(null).commit();
            }
        });

        //por ahora cargamos un listado falso de grupos al apretar para ver la funcionalidad
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] nombresGrupos = {"Amigos de la Universidad", "Familiares", "Compañeros de Trabajo", "Vecinos", "Amigos de la Infancia", "Equipo de Deportes", "Amigos de Club de Lectura", "Compañeros de Clase", "Vecinos de la Calle A", "Amigos de Juegos en Línea"};
                List usuarios = IntStream.range(0, 30)
                        .mapToObj(i -> new Usuario("a", "Usuario " + (i + 1), "usuario" + (i + 1) + "@example.com", null, null, null))
                        .collect(Collectors.toList());
                List<List<Usuario>> miembrosGrupos = Arrays.asList(usuarios.subList(0, 3), usuarios.subList(3, 6), usuarios.subList(6, 9), usuarios.subList(9, 12), usuarios.subList(12, 15), usuarios.subList(15, 18), usuarios.subList(18, 21), usuarios.subList(21, 24), usuarios.subList(24, 27), usuarios.subList(27, 30));

                for (int i = 0; i < nombresGrupos.length; i++) {
                    GrupoUsuarios grupo = new GrupoUsuarios(nombresGrupos[i], miembrosGrupos.get(i));
                    gruposAdapter.addGrupo(grupo);
                }
                updateUIGroups();
            }
        });


        //listener to document changes in the db
        db.collection("users").document(auth.getUid())
                .addSnapshotListener((value, error) -> {
                    mainActivity.setUser(UsuarioMapper.mapBasics(value));
                    cargarListaAmigos();
                    cargarListaGruposAmigos();
                });

        return root;
    }


}