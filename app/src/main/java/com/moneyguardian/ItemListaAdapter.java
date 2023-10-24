package com.moneyguardian;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.modelo.ItemPagoConjunto;

import java.util.List;

public class ItemListaAdapter extends RecyclerView.Adapter<ItemListaAdapter.ItemListaViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(ItemPagoConjunto item);
    }

    private List<ItemPagoConjunto> listaItemsPago;
    private final OnItemClickListener listener;

    public ItemListaAdapter(List<ItemPagoConjunto> listaItemsPago, OnItemClickListener listener) {
        this.listaItemsPago = listaItemsPago;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ItemListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pago_lista_recycler, parent, false);
        return new ItemListaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListaViewHolder holder, int position) {
        ItemPagoConjunto itemPago= listaItemsPago.get(position);

        holder.bindUser(itemPago, listener);
    }

    @Override
    public int getItemCount() {
        if(listaItemsPago == null){
            return 0;
        }
        return listaItemsPago.size() ;
    }

    public static class ItemListaViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView userPay;
        private TextView money;

        public ItemListaViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.nameItemPago);
            userPay= (TextView)itemView.findViewById(R.id.userPaid);
            money= (TextView)itemView.findViewById(R.id.moneyText);
        }

        // asignar valores a los componentes
        public void bindUser(final ItemPagoConjunto itemPago, final OnItemClickListener listener) {
            name.setText(itemPago.getNombre());
            userPay.setText(itemPago.getUser());
            money.setText(itemPago.getMoney()+ "€");


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(itemPago);
                }
            });
        }
    }
}
