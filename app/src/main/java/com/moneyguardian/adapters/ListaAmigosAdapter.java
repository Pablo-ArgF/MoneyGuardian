package com.moneyguardian.adapters;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Usuario;

import java.security.AccessControlContext;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListaAmigosAdapter extends RecyclerView.Adapter<ListaAmigosAdapter.AmigoViewHolder> {

    private List<Usuario> listaAmigos = new LinkedList<>();
    private OnItemClickListener listener;
    private ViewGroup parent;

    public void clear() {
        notifyItemRangeRemoved(0,listaAmigos.size());
        this.listaAmigos.clear();
    }

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Usuario item);
        void onDeleteClick(Usuario item);
    }

    public ListaAmigosAdapter(List<Usuario> listaAmigos, OnItemClickListener listener) {
        this.listaAmigos = listaAmigos;
        this.listener = listener;
    }

    public void deleteAmigo(Usuario amigo) {
        listaAmigos.remove(amigo);
    }

    public void addAmigo(Usuario amigo){
        if(listaAmigos.contains(amigo))
            return;
        listaAmigos.add(amigo);
        notifyItemInserted(listaAmigos.size()-1);
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
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
        holder.bindUser(amigo, listener, parent.getContext());
    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }



    public static class AmigoViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre;
        private ImageButton deleteButton;
        private CircleImageView profilePic;

        // Meter la imagen aqui también si se mete en el usuario

        public AmigoViewHolder(View itemView) {
            super(itemView);

            nombre= (TextView)itemView.findViewById(R.id.nombreSolicitante);
            deleteButton = (ImageButton) itemView.findViewById(R.id.btnBorrarAmigo);
            profilePic =(CircleImageView) itemView.findViewById(R.id.imgFriend);
        }

        // asignar valores a los componentes
        public void bindUser(final Usuario amigo, final OnItemClickListener listener, Context context) {
            nombre.setText(amigo.getNombre());

            Uri profileUri =Uri.parse(amigo.getUriImg());
            Glide.with(context)
                    .load(profileUri)
                    .into(profilePic);

            //funcionalidad de borrado de usuarios
            // Set a click listener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the delete button click, e.g., call a method to delete the item
                    listener.onDeleteClick(amigo);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.i("ListaAmigosAdapter", "OnClick bindUser usuario " + amigo.getNombre());
                    listener.onItemClick(amigo);
                }
            });
        }
    }

}