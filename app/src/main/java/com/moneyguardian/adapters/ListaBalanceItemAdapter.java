package com.moneyguardian.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.R;
import com.moneyguardian.modelo.Usuario;

import java.util.Map;


public class ListaBalanceItemAdapter extends RecyclerView.Adapter<ListaBalanceItemAdapter.BalanceViewHolder> {

    private Map<Usuario,Integer> balance;

    public ListaBalanceItemAdapter( Map<Usuario,Integer> balance) {
        this.balance = balance;
    }


    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_item_pago, parent, false);
        return new BalanceViewHolder(itemView);
    }


    /** Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro AmigoHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Object[] entradas = balance.entrySet().toArray();
        Map.Entry<Usuario,Integer> entrada = (Map.Entry<Usuario,Integer>)(entradas[position]);
        holder.bindUser(entrada.getKey(), entrada.getValue());
    }

    @Override
    public int getItemCount() {
        return this.balance.size();
    }



    public static class BalanceViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre;
        private TextView balance;

        // Meter la imagen aqui también si se mete en el usuario

        public BalanceViewHolder(View itemView) {
            super(itemView);

            nombre= (TextView)itemView.findViewById(R.id.nombreAmigoEnBalance);
            balance= (TextView)itemView.findViewById(R.id.txtBalance);
        }

        // asignar valores a los componentes
        public void bindUser(final Usuario amigo,int balance) {
            nombre.setText(amigo.getNombre());
            StringBuilder sb = new StringBuilder();
            if(balance > 0) {//color verde
                this.balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.green));
                sb.append("+");
            }
            else {
                if(balance == 0){
                    this.balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.grey));
                    sb.append("+");
                }
                else {
                    this.balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.red));
                }
            }

            this.balance.setText(sb.toString() + balance + "€");
        }
    }

}