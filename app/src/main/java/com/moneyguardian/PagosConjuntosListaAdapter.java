package com.moneyguardian;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PagosConjuntosListaAdapter extends RecyclerView.Adapter<PagosConjuntosListaAdapter.PagosConjuntosListaViewHolder> {

    //private List<PagoConjunto> listaPagosConjuntos;

    //private final OnItemClickListener listener;

    public interface OnItemClickListener {
        //void onItemClick(PagoConjunto item);
    }

/**
    public ItemListaAdapter(List<PagoConjunto> listaPagosConjuntos, OnItemClickListener listener) {
        this.listaPagosConjuntos = listaPagosConjuntos;
        this.listener = listener;
    }
**/

    @NonNull
    @Override
    public PagosConjuntosListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pagos_conjuntos, parent, false);
        return new PagosConjuntosListaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PagosConjuntosListaViewHolder holder, int position) {
        //PagoConjunto pagoConjunto = listaPagosConjuntos.get(position);

        //holder.bindUser(pagoConjunto, listener);
    }

    @Override
    public int getItemCount() {
        // TODO return listaPagosConjuntos.size();
        return 0;
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
        /**
        public void bindUser(final PagoConjunto pagoConjunto, final OnItemClickListener listener) {
            name.setText(pagoConjunto.getName());
            money.setText(pagoConjunto.getMoney());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(pagoConjunto);
                }
            });
        }
         **/
    }

}
