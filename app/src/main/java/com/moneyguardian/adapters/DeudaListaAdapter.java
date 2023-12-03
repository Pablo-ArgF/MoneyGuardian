package com.moneyguardian.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.modelo.dto.DeudaDTO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class DeudaListaAdapter extends RecyclerView.Adapter<DeudaListaAdapter.DeudaViewHolder> {
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<DeudaDTO> deudas;

    public DeudaListaAdapter() {
        this.deudas = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_deuda, parent, false);
        return new DeudaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeudaViewHolder holder, int position) {
        holder.bindDeuda(deudas.get(position));
    }

    @Override
    public int getItemCount() {
        return deudas.size();
    }

    // TODO no se como hacer que la db no vaya en el adapter...
    public void update(@NonNull List<PagoConjunto> pagosConjuntos, String userID) {
        for (PagoConjunto pago : pagosConjuntos) {
            HashMap<UsuarioParaParcelable, Double> pagos = new HashMap<>();
            for (ItemPagoConjunto item : pago.getItems()) {
                for (Map.Entry<UsuarioParaParcelable, Double> entry : item.getPagos().entrySet()) {
                    // Obtener los datos de cada item desde la DB
                    db.collection("/users").document(entry.getKey().getNombre()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                String nombre = (String) doc.get("name");
                                String email = (String) doc.get("email");
                                String imagen = (String) doc.get("profilePicture");
                                UsuarioParaParcelable user = new UsuarioParaParcelable(nombre, email, imagen, doc.getId());
                                pagos.put(user, entry.getValue());
                            }
                        }
                    });
                }
                while (pagos.size() != item.getPagos().size()) {
                    // No me mates Pablo please
                }
                item.setPagos(pagos);
                calculateDeuda(item.getPagos(), userID);
                notifyDataSetChanged();
            }
        }
    }

    private void calculateDeuda(@NonNull Map<UsuarioParaParcelable, Double> mapItems, String userID) {
        if (this.deudas == null) {
            this.deudas = new ArrayList<>();
        }
        UsuarioParaParcelable pagador = null;
        UsuarioParaParcelable mainUser = null;
        List<UsuarioParaParcelable> usuario = new ArrayList<>();
        Double total = 0.0;
        boolean isUserPagador = false;
        for (Map.Entry<UsuarioParaParcelable, Double> pagoItem : mapItems.entrySet()) {
            // El usuario no es el que paga
            if (pagoItem.getValue() > 0.0 && !pagoItem.getKey().getNombre().equals(userID)) {
                pagador = pagoItem.getKey();
            }
            // El usuario debe
            if (pagoItem.getValue() < 0.0 && pagoItem.getKey().getNombre().equals(userID)) {
                mainUser = pagoItem.getKey();
                total = pagoItem.getValue();
            }
            // El usuario si es el que paga
            if (pagoItem.getValue() > 0.0 && pagoItem.getKey().getNombre().equals(userID)) {
                pagador = pagoItem.getKey();
                isUserPagador = true;
            }
            // Alguien debe dinero (No sabemos aún a quien)
            if (pagoItem.getValue() < 0.0 && !pagoItem.getKey().getNombre().equals(userID)) {
                usuario.add(pagoItem.getKey());
            }
        }
        // Si el usuario es el pagador, necesitaremos hacer varias deudas
        if (isUserPagador && pagador != null && !usuario.isEmpty()) {
            for (UsuarioParaParcelable usuarioDebe : usuario) {
                DeudaDTO deuda = new DeudaDTO(pagador, usuarioDebe, total);
                deuda.setUserPagador(true);
                this.deudas.add(deuda);
            }
        } else if (pagador != null && mainUser != null) {
            DeudaDTO deuda = new DeudaDTO(pagador, mainUser, total);
            this.deudas.add(deuda);
        }
    }

    public class DeudaViewHolder extends RecyclerView.ViewHolder {

        TextView nombreUsuario;
        TextView nombreAmigo;
        TextView total;
        CircleImageView imageViewUsuario;
        CircleImageView imageViewAmigo;

        public DeudaViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreUsuario = (TextView) itemView.findViewById(R.id.nombreUsuarioDeuda);
            nombreAmigo = (TextView) itemView.findViewById(R.id.nombrePagadorDeuda);
            total = (TextView) itemView.findViewById(R.id.textMoneyDeuda);
            imageViewUsuario = (CircleImageView) itemView.findViewById(R.id.imgUserDeuda);
            imageViewAmigo = (CircleImageView) itemView.findViewById(R.id.imgPagadorDeuda);
        }

        public void bindDeuda(DeudaDTO deuda) {
            if (deuda.isUserPagador()) {
                nombreUsuario.setText(deuda.getPagador().getNombre());
                nombreAmigo.setText(deuda.getUsuario().getNombre());
                if (deuda.getPagador().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getPagador().getImageURI())).into(imageViewUsuario);
                if (deuda.getUsuario().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getUsuario().getImageURI())).into(imageViewAmigo);
            } else {
                // TODO el getNombre da nullpointer
                nombreUsuario.setText(deuda.getUsuario().getNombre());
                nombreAmigo.setText(deuda.getPagador().getNombre());
                if (deuda.getUsuario().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getUsuario().getImageURI())).into(imageViewUsuario);
                if (deuda.getPagador().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getPagador().getImageURI())).into(imageViewAmigo);
            }
            total.setText(String.valueOf(deuda.getCantidad()));
        }
    }

}
