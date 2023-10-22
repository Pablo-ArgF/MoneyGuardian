package com.moneyguardian.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.moneyguardian.ListaAmigosAdapter;
import com.moneyguardian.ListaGruposAdapter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.GrupoUsuarios;
import com.moneyguardian.modelo.Usuario;

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


    public ListaAmigosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListaAmigosFragment.
     */
    // TODO: Revisar que no necesito pasarle nada en constructor
    public static ListaAmigosFragment newInstance() {
        ListaAmigosFragment fragment = new ListaAmigosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void clickonAmigo (Usuario amigo){
        Log.i("Click adapter","Item Clicked "+amigo.getNombre());

        //Paso el modo de apertura
        /*
        //TODO meter aqui si se quiere logica al clickar un amigo
        Intent intent=new Intent (MainRecyclerActivity.this, ShowMovie.class);
        intent.putExtra(PELICULA_SELECCIONADA, peli);

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        */
    }

    private void cargarListaAmigos() {
        //TODO añadir boton de eliminar amigos en la lista de amigos
        listaAmigos = new LinkedList<>();
        // Dummy data
        String[] nombres = {"John Doe", "Alice Johnson", "Bob Smith", "Emma Brown", "David Davis", "Olivia Wilson", "Michael Lee", "Sophia White", "James Harris", "Ava Robinson"};
        String[] correos = {"john.doe@example.com", "alice.johnson@example.com", "bob.smith@example.com", "emma.brown@example.com", "david.davis@example.com", "olivia.wilson@example.com", "michael.lee@example.com", "sophia.white@example.com", "james.harris@example.com", "ava.robinson@example.com"};

        for (int i = 0; i < nombres.length; i++) {
            Usuario usuario = new Usuario(nombres[i], correos[i], null, null);
            listaAmigos.add(usuario);
        }

        ListaAmigosAdapter amigosAdapter = new ListaAmigosAdapter(listaAmigos,
                (amigo) ->{
                    clickonAmigo(amigo);
                });
        listaAmigosView.setAdapter(amigosAdapter); //TODO mirar donde inicializar el valor de la view
    }

    private void cargarListaGruposAmigos() {
        String[] nombresGrupos = {"Amigos de la Universidad", "Familiares", "Compañeros de Trabajo", "Vecinos", "Amigos de la Infancia", "Equipo de Deportes", "Amigos de Club de Lectura", "Compañeros de Clase", "Vecinos de la Calle A", "Amigos de Juegos en Línea"};
        List usuarios = IntStream.range(0, 30)
                .mapToObj(i -> new Usuario("Usuario " + (i + 1), "usuario" + (i + 1) + "@example.com", null, null))
                .collect(Collectors.toList());
        List<List<Usuario>> miembrosGrupos = Arrays.asList(usuarios.subList(0, 3), usuarios.subList(3, 6), usuarios.subList(6, 9), usuarios.subList(9, 12), usuarios.subList(12, 15), usuarios.subList(15, 18), usuarios.subList(18, 21), usuarios.subList(21, 24), usuarios.subList(24, 27), usuarios.subList(27, 30));


        listaGrupos = new LinkedList<>();
        for (int i = 0; i < nombresGrupos.length; i++) {
            GrupoUsuarios grupo = new GrupoUsuarios(nombresGrupos[i], miembrosGrupos.get(i));
            listaGrupos.add(grupo);
        }


        ListaGruposAdapter gruposAdapter = new ListaGruposAdapter(listaGrupos,
                (grupo) ->{
                    clickonGrupo(grupo);
                });
        listaGruposView.setAdapter(gruposAdapter); //TODO mirar donde inicializar el valor de la view
    }

    private void clickonGrupo(GrupoUsuarios grupo) {

        Log.i("Click adapter","Item Clicked "+grupo.getNombre());

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
        listaGruposView = root.findViewById(R.id.recyclerListaGruposAmigos);
        listaAmigosView = root.findViewById(R.id.recyclerListaAmigos);

        //cargamos los datos en la vista
        cargarListaGruposAmigos();
        cargarListaAmigos();
        return root;
    }


}