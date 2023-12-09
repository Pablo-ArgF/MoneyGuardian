package com.moneyguardian.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.R;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.UsuarioMapper;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListaBalanceItemAdapter extends RecyclerView.Adapter<ListaBalanceItemAdapter.BalanceViewHolder> {

    private final Map<UsuarioParaParcelable, Double> balance;

    public ListaBalanceItemAdapter(Map<UsuarioParaParcelable, Double> balance) {
        this.balance = balance;
    }


    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_item_pago, parent, false);
        return new BalanceViewHolder(itemView);
    }


    /**
     * Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro AmigoHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        Object[] entradas = balance.entrySet().toArray();

        Map.Entry<UsuarioParaParcelable, Double> userInPosition = (Map.Entry<UsuarioParaParcelable, Double>) entradas[position];

        holder.bindUser(userInPosition.getKey(),userInPosition.getValue());
    }

    @Override
    public int getItemCount() {
        return this.balance.size();
    }


    public static class BalanceViewHolder extends RecyclerView.ViewHolder {

        private final TextView nombre;
        private final TextView balance;
        private final CircleImageView perfil;

        // Meter la imagen aqui también si se mete en el usuario

        public BalanceViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.nombreAmigoEnBalance);
            balance = (TextView) itemView.findViewById(R.id.txtBalance);
            perfil = (CircleImageView) itemView.findViewById(R.id.imgUsuarioSolicitud);

        }

        // asignar valores a los componentes
        public void bindUser(final UsuarioParaParcelable amigo, Double balance) {
            nombre.setText(amigo.getNombre());
            if (perfil != null) {
                Picasso.get().load(Uri.parse(amigo.getImageURI())).into(perfil);
            }
            StringBuilder sb = new StringBuilder();
            if (balance > 0) {//color verde
                this.balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.green));
                sb.append("+");
            } else {
                if (balance == 0) {
                    this.balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.grey));
                    sb.append("+");
                } else {
                    this.balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.red));
                }
            }

            this.balance.setText(sb.toString() + balance + "€");
        }
    }

}