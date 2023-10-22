package com.moneyguardian;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.modelo.Usuario;

import java.util.LinkedList;
import java.util.List;


public class ListaAmigosAdapter extends RecyclerView.Adapter<ListaAmigosAdapter.AmigoViewHolder> {

    private List<Usuario> listaAmigos = new LinkedList<>();
    private OnItemClickListener listener;

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Usuario item);
    }

    public ListaAmigosAdapter(List<Usuario> listaAmigos, OnItemClickListener listener) {
        this.listaAmigos = listaAmigos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_amigo, parent, false);
        return new AmigoViewHolder(itemView);
    }


    /** Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro AmigoHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Usuario amigo= listaAmigos.get(position);
        Log.i("Lista","Visualiza elemento: "+amigo);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.bindUser(amigo, listener);
    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }



    public static class AmigoViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre;

        // Meter la imagen aqui también si se mete en el usuario

        public AmigoViewHolder(View itemView) {
            super(itemView);

            nombre= (TextView)itemView.findViewById(R.id.nombreAmigo);

        }

        // asignar valores a los componentes
        public void bindUser(final Usuario amigo, final OnItemClickListener listener) {
            nombre.setText(amigo.getNombre());

            //funcionalidad de borrado de usuarios


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.i("ListaAmigosAdapter", "OnClick bindUser usuario " + amigo.getNombre());
                    listener.onItemClick(amigo);
                }
            });
        }
    }

}