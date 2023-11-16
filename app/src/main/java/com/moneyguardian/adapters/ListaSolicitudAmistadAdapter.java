package com.moneyguardian.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.util.AmistadesUtil;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListaSolicitudAmistadAdapter extends RecyclerView.Adapter<ListaSolicitudAmistadAdapter.SolicitudViewHolder> {

    private List<Usuario> listaSolicitudes = new LinkedList<>();
    private OnItemClickListener listener;
    private Context context;

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onAceptarUsuario(Usuario item);
        void onDenegarUsuario(Usuario item);
    }

    public ListaSolicitudAmistadAdapter(List<Usuario> listaSolicitudes, OnItemClickListener listener) {
        this.listaSolicitudes = listaSolicitudes;
        this.listener = listener;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_solicitud_amistad, parent, false);
        return new SolicitudViewHolder(itemView);
    }


    /** Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro AmigoHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Usuario amigo= listaSolicitudes.get(position);
        Log.i("Lista","Visualiza elemento: "+amigo);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.bindUser(amigo,context, listener);
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }



    public static class SolicitudViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImg;
        private TextView nombre;
        private ImageButton denegarBtn;
        private ImageButton aceptarBtn;

        public SolicitudViewHolder(View itemView) {
            super(itemView);

            nombre= (TextView)itemView.findViewById(R.id.nombreSolicitante);
            denegarBtn = (ImageButton) itemView.findViewById(R.id.btnDenegarSolicitud);
            aceptarBtn = (ImageButton) itemView.findViewById(R.id.btnAceptarSolicitud);
            userImg = (CircleImageView) itemView.findViewById(R.id.imgUsuarioSolicitud);
        }

        // asignar valores a los componentes
        public void bindUser(final Usuario solicitante, final Context context, final OnItemClickListener listener) {
            nombre.setText(solicitante.getNombre());
            //cargamos la imagen del usuario
            Glide.with(context)
                    .load(solicitante.getUriImg())
                    .into(userImg);
            denegarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the delete button click, e.g., call a method to delete the item
                    listener.onDenegarUsuario(solicitante);
                }
            });
            aceptarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the delete button click, e.g., call a method to delete the item
                    listener.onAceptarUsuario(solicitante);
                }
            });

        }
    }

}