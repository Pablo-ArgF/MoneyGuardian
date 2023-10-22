package com.moneyguardian;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;

import java.util.List;

public class PagosConjuntosListaAdapter extends RecyclerView.Adapter<PagosConjuntosListaAdapter.PagosConjuntosListaViewHolder> {

    private List<PagoConjunto> listaPagosConjuntos;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PagoConjunto item);
    }

    public PagosConjuntosListaAdapter(List<PagoConjunto> listaPagosConjuntos, OnItemClickListener listener) {
        this.listaPagosConjuntos = listaPagosConjuntos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PagosConjuntosListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_pago_conjunto, parent, false);
        return new PagosConjuntosListaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PagosConjuntosListaViewHolder holder, int position) {
        PagoConjunto pagoConjunto = listaPagosConjuntos.get(position);

        holder.bindUser(pagoConjunto, listener);
    }

    @Override
    public int getItemCount() {
        return listaPagosConjuntos.size();
    }

    public static class PagosConjuntosListaViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView money;

        public PagosConjuntosListaViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.textNombrePagoConjunto);
            money = (TextView) itemView.findViewById(R.id.editTextPagoConjuntoCoste);
        }

        // asignar valores a los componentes
        public void bindUser(final PagoConjunto pagoConjunto, final OnItemClickListener listener) {
            name.setText(pagoConjunto.getNombre());
            money.setText(calculatePrecio(pagoConjunto));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(pagoConjunto);
                }
            });
        }

        private int calculatePrecio(PagoConjunto pagoConjunto){
            int total = 0;
            for(ItemPagoConjunto item : pagoConjunto.getItems()){
                // TODO: lo dejo sin hacer ya que se va a cambiar el modelo
                // para que no haya errores
            }
            return total;
        }
    }

}
