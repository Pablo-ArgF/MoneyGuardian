package com.moneyguardian.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.R;
import com.moneyguardian.modelo.DeudaDTO;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.UsuarioMapper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import de.hdodenhof.circleimageview.CircleImageView;


public class DeudaListaAdapter extends RecyclerView.Adapter<DeudaListaAdapter.DeudaViewHolder> {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<DeudaDTO> deudas;

    public DeudaListaAdapter() {
        this.deudas = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_deuda, parent, false);
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

    public void updateList(PagoConjunto pago, Callable<Void> callback) {

        for (ItemPagoConjunto ipg : pago.getItems()) {
            db.collection("users").document(ipg.getUserThatPays().getId()).get().addOnSuccessListener(documentSnapshot -> {
                UsuarioParaParcelable owner = UsuarioMapper.mapBasicsParcelable(documentSnapshot);
                for (Map.Entry<UsuarioParaParcelable, Double> users : ipg.getPagos().entrySet()) {
                    db.collection("users").document(users.getKey().getId()).get().addOnSuccessListener(d -> {
                        UsuarioParaParcelable user = UsuarioMapper.mapBasicsParcelable(d);
                        // Evita deudas a si mismo
                        if (!owner.getId().equals(user.getId())) {
                            // Evita que el usuario no esté en la deuda
                            if (((owner.getId().equals(auth.getUid())) || user.getId().equals(auth.getUid())) && (users.getValue() > 0.0 || users.getValue() < 0.0)) {
                                deudas.add(new DeudaDTO(owner, user, users.getValue()));
                                notifyItemChanged(deudas.size() - 1);
                                try {
                                    callback.call();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                }
            });
        }
        // Ordenar la lista de gastos
        Collections.sort(this.deudas);
    }


    public class DeudaViewHolder extends RecyclerView.ViewHolder {

        TextView nombreUsuario;
        TextView nombreAmigo;
        TextView total;
        CircleImageView imageViewUsuario;
        CircleImageView imageViewAmigo;
        ImageView flechaEnd;
        ImageView flechaInicio;

        public DeudaViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreUsuario = itemView.findViewById(R.id.nombreUsuarioDeuda);
            nombreAmigo = itemView.findViewById(R.id.nombrePagadorDeuda);
            total = itemView.findViewById(R.id.textMoneyDeuda);
            imageViewUsuario = itemView.findViewById(R.id.imgUserDeuda);
            imageViewAmigo = itemView.findViewById(R.id.imgPagadorDeuda);
            flechaEnd = itemView.findViewById(R.id.flechaDeuda2);
            flechaInicio = itemView.findViewById(R.id.flechaDeuda);
        }

        public void bindDeuda(DeudaDTO deuda) {
            if (deuda.getPagador().getId().equals(auth.getUid())) {
                nombreUsuario.setText(deuda.getPagador().getNombre());
                nombreAmigo.setText(deuda.getUsuario().getNombre());
                if (deuda.getPagador().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getPagador().getImageURI())).into(imageViewUsuario);
                if (deuda.getUsuario().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getUsuario().getImageURI())).into(imageViewAmigo);
                flechaInicio.setImageResource(R.drawable.ic_flecha_gasto_izquierda);
                flechaEnd.setImageResource(R.drawable.ic_flecha_gasto_izquierda_final
                );
                total.setText(String.valueOf(deuda.getCantidad() * -1));
                total.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.green));
            } else {
                nombreUsuario.setText(deuda.getUsuario().getNombre());
                nombreAmigo.setText(deuda.getPagador().getNombre());
                if (deuda.getUsuario().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getUsuario().getImageURI())).into(imageViewUsuario);
                if (deuda.getPagador().getImageURI() != null)
                    Picasso.get().load(Uri.parse(deuda.getPagador().getImageURI())).into(imageViewAmigo);
                flechaEnd.setImageResource(R.drawable.ic_flecha_gasto_derecha);
                flechaInicio.setImageResource(R.drawable.ic_flecha_gasto_derecha_inicio);
                total.setText(String.valueOf(deuda.getCantidad()));
                total.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.red));
            }
        }
    }

}
