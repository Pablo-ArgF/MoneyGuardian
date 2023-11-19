package com.moneyguardian.adapters;

import android.content.Context;
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

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListaEnviarSolicitudAmistadAdapter extends RecyclerView.Adapter<ListaEnviarSolicitudAmistadAdapter.EnviarSolicitudViewHolder> {

    private List<Usuario> listaUsuarios = new LinkedList<>();
    private OnItemClickListener listener;
    private Context context;

    public void addUsuario(Usuario u) {
        if(listaUsuarios.contains(u))
            return;
        this.listaUsuarios.add(u);
        notifyItemInserted(listaUsuarios.size() -1);
    }

    /**
     * This method is called once we have sent a request to one of the users in the list.
     * The user will be deleted from the list
     * @param u user to be deleted from the list
     */
    public void RemoveUsuario(Usuario u) {
        int index = listaUsuarios.indexOf(u);
        listaUsuarios.remove(index);
        notifyItemRemoved(index);
    }

    public void clearUsuarios() {
        notifyItemRangeRemoved(0,listaUsuarios.size());
        listaUsuarios.clear();
    }

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onEnviarSolicitud(Usuario item);
    }

    public ListaEnviarSolicitudAmistadAdapter(List<Usuario> listaUsuarios, OnItemClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    @NonNull
    @Override
    public EnviarSolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_enviar_solicitud_amistad, parent, false);
        return new EnviarSolicitudViewHolder(itemView);
    }


    /** Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro AmigoHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull EnviarSolicitudViewHolder holder, int position) {
        Usuario amigo= listaUsuarios.get(position);
        holder.bindUser(amigo,context, listener);
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }



    public static class EnviarSolicitudViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImg;
        private TextView nombre;
        private ImageButton enviarBtn;

        public EnviarSolicitudViewHolder(View itemView) {
            super(itemView);

            nombre= (TextView)itemView.findViewById(R.id.nombreSolicitante);
            enviarBtn = (ImageButton) itemView.findViewById(R.id.btnEnviarSolicitud);
            userImg = (CircleImageView) itemView.findViewById(R.id.imgUsuarioSolicitud);
        }

        // asignar valores a los componentes
        public void bindUser(final Usuario solicitante, final Context context, final OnItemClickListener listener) {
            nombre.setText(solicitante.getNombre());
            //cargamos la imagen del usuario
            Glide.with(context)
                    .load(solicitante.getUriImg())
                    .into(userImg);
            enviarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the delete button click, e.g., call a method to delete the item
                    listener.onEnviarSolicitud(solicitante);
                }
            });


        }
    }

}