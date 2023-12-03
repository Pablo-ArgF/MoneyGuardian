package com.moneyguardian.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.modelo.dto.DeudaDTO;
import com.moneyguardian.util.UsuarioMapper;
import com.squareup.picasso.Picasso;

import java.security.acl.Owner;
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

    public void updateList(PagoConjunto pago) {
    db.collection("users").document(pago.getOwner()).get().
        addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UsuarioParaParcelable owner =  UsuarioMapper.mapBasicsParcelable(documentSnapshot);

                for(ItemPagoConjunto ipg : pago.getItems()){
                    for (Map.Entry<UsuarioParaParcelable, Double> users : ipg.getPagos().entrySet()){
                        db.collection("users").document(users.getKey().getId()).get().
                        addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot d) {
                                UsuarioParaParcelable user = UsuarioMapper.mapBasicsParcelable(d);
                                deudas.add(new DeudaDTO(owner,user,users.getValue()));
                                notifyItemChanged(deudas.size()-1);
                            }
                        });
                    }
                }
            }
        });
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
