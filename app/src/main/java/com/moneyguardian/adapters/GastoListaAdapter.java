package com.moneyguardian.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GastoListaAdapter extends RecyclerView.Adapter<GastoListaAdapter.GastoViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Gasto gasto);
    }

    private boolean[] checked;
    private List<Gasto> listaGastos;
    private OnItemClickListener listener;

    public GastoListaAdapter(OnItemClickListener listener) {
        this.listaGastos = new ArrayList<>();
        this.listener = listener;
    }

    public void add(Gasto g) {
        this.listaGastos.add(g);
        notifyDataSetChanged();
    }

    /**
     * Listener para las checkbox
     */
    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checked[(Integer) buttonView.getTag()] = isChecked;
        }
    };

    public boolean isChecked(int i) {
        return checked[i];
    }


    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_gasto_usuario, parent, false);
        return new GastoListaAdapter.GastoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        Gasto gasto = listaGastos.get(position);
        holder.bindUser(gasto, listener);
    }

    @Override
    public int getItemCount() {
        if (listaGastos == null)
            return 0;
        return listaGastos.size();
    }

    public static class GastoViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre;
        private TextView balance;
        private CircleImageView imagenGasto;
        private TextView fecha;


        public GastoViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.nombreGasto);
            balance = (TextView) itemView.findViewById(R.id.balanceGasto);
            imagenGasto = (CircleImageView) itemView.findViewById(R.id.imagenGasto);
            fecha = (TextView) itemView.findViewById(R.id.textFechaGasto);
        }

        public void bindUser(final Gasto gasto, final OnItemClickListener listener) {
            // TODO falta validar
            nombre.setText(gasto.getNombre());
            fecha.setText(gasto.getFechaCreacion());

            int imageMoney = gasto.getBalance() > 0 ? R.drawable.ic_money : R.drawable.ic_money_off;
            imagenGasto.setImageResource(imageMoney);

            // Si es un gasto o un ingreso
            String balancePago = (gasto.getBalance() > 0 ? "+" : "") + (gasto.getBalance()) + "€";
            balance.setText(balancePago);
            int color = gasto.getBalance() > 0 ? R.color.green : R.color.red;
            balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), color));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(gasto);
                }
            });
        }
    }

}
