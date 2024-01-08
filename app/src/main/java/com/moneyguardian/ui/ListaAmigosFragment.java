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
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.util.AmistadesUtil;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaAmigosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaAmigosFragment extends Fragment {
    private List<Usuario> listaAmigos;

    private RecyclerView listaAmigosView;
    private SwipeRefreshLayout swipeRefreshLayoutAmigos;

    private ListaAmigosAdapter amigosAdapter;
    private Button btnGestionAmigos;
    private MainActivity mainActivity;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout msgNoFriends;
    private LinearLayout msgNoGroups;



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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_lista_amigos, container, false);
        mainActivity = (MainActivity) getActivity();
        listaAmigosView = root.findViewById(R.id.recyclerListaAmigos);
        btnGestionAmigos = root.findViewById(R.id.btnGestionAmigos);
        msgNoFriends = root.findViewById(R.id.msgNoFriends);
        swipeRefreshLayoutAmigos = root.findViewById(R.id.swipeRefreshAmigos);
        swipeRefreshLayoutAmigos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarListaAmigos();
                swipeRefreshLayoutAmigos.setRefreshing(false);
            }
        });


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




        //cargamos los datos en la vista

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



        //listener to document changes in the db
        db.collection("users").document(auth.getUid())
                .addSnapshotListener((value, error) -> {
                    if(auth.getUid() == null)
                        return;
                    mainActivity.setUser(UsuarioMapper.mapBasics(value));
                    cargarListaAmigos();
                });

        return root;
    }


}