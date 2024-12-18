package com.moneyguardian.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PagosConjuntosListaAdapter extends RecyclerView.Adapter<PagosConjuntosListaAdapter.PagosConjuntosListaViewHolder> {

    private List<PagoConjunto> listaPagosConjuntos;

    private final OnItemClickListener listener;

    public void addItem(PagoConjunto pg) {
        listaPagosConjuntos.add(pg);
        notifyItemInserted(listaPagosConjuntos.size() - 1);
    }

    public void changeAllList(List<PagoConjunto> newPagos) {
        listaPagosConjuntos = newPagos;
        notifyDataSetChanged();

    }

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

    public void updateList(List<PagoConjunto> pagos) {
        this.listaPagosConjuntos = new ArrayList<>(pagos);
        notifyDataSetChanged();
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
        private TextView fecha;
        private ImageView image;

        public PagosConjuntosListaViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.TextNombrePagoConjuntoLinea);
            money = (TextView) itemView.findViewById(R.id.editTextPagoConjuntoCoste);
            fecha = (TextView) itemView.findViewById(R.id.TextFechaPagoConjuntoLinea);
            image = (ImageView) itemView.findViewById(R.id.imgViewPagoConjunto);
        }

        // asignar valores a los componentes
        public void bindUser(final PagoConjunto pagoConjunto, final OnItemClickListener listener) {
            name.setText(pagoConjunto.getNombre());
            money.setText(calculatePrecio(pagoConjunto));
            String creacionPago = new SimpleDateFormat("dd-MM-yyyy", new Locale("es")).format(pagoConjunto.getFechaPago());
            fecha.setText(String.format(creacionPago));
            // Colocamos la imagen si es que hay
            if (pagoConjunto.getImagen() != null) {
                Picasso.get().load(pagoConjunto.getImagen()).into(image);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(pagoConjunto);
                }
            });
        }

        private String calculatePrecio(PagoConjunto pagoConjunto) {
            int total = 0;
            for (ItemPagoConjunto item : pagoConjunto.getItems()) {
                total += item.getMoney();
            }
            return total + "€";
        }
    }

}
