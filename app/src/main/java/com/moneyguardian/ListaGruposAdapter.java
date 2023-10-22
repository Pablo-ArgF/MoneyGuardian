package com.moneyguardian;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.modelo.GrupoUsuarios;

import java.util.LinkedList;
import java.util.List;


public class ListaGruposAdapter extends RecyclerView.Adapter<ListaGruposAdapter.GrupoViewHolder> {

    private List<GrupoUsuarios> listaGrupos = new LinkedList<>();
    private OnItemClickListener listener;

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(GrupoUsuarios item);
    }

    public ListaGruposAdapter(List<GrupoUsuarios> listaGrupos, OnItemClickListener listener) {
        this.listaGrupos = listaGrupos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_grupo_amigos, parent, false);
        return new GrupoViewHolder(itemView);
    }


    /** Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro PeliculaViewHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        GrupoUsuarios grupo= listaGrupos.get(position);
        Log.i("Lista","Visualiza elemento: "+grupo);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.bindUser(grupo, listener);
    }

    @Override
    public int getItemCount() {
        return listaGrupos.size();
    }



    public static class GrupoViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre;
        private TextView usuarios;


        // Meter la imagen aqui también si se mete en el usuario

        public GrupoViewHolder(View itemView) {
            super(itemView);

            nombre= (TextView)itemView.findViewById(R.id.nombreGrupo);
            usuarios= (TextView)itemView.findViewById(R.id.listaParticipantes);

        }

        // asignar valores a los componentes
        public void bindUser(final GrupoUsuarios grupo, final OnItemClickListener listener) {
            nombre.setText(grupo.getNombre());
            StringBuilder s = new StringBuilder();
            grupo.getUsuarios().forEach(u -> s.append(u.getNombre()+ ", "));
            //we delete the coma on the last element and the last space
            s.deleteCharAt(s.length()-2);
            s.deleteCharAt(s.length()-1);
            usuarios.setText(s.toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.i("ListaGruposAdapter", "OnClick bindUser grupo " + grupo.getNombre());
                    listener.onItemClick(grupo);
                }
            });
        }
    }

}