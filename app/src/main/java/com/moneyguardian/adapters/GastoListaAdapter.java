package com.moneyguardian.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.util.GastosUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GastoListaAdapter extends RecyclerView.Adapter<GastoListaAdapter.GastoViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Gasto gasto);
    }

    private static Map<Gasto, Boolean> checkedGastos = new HashMap<>();
    private static Map<Gasto, CheckBox> checkBoxMap = new HashMap<>();
    private List<Gasto> listaGastos;

    public GastoListaAdapter() {
        this.listaGastos = new ArrayList<>();
    }

    public void selectAll(Boolean isChecked) {
        for (Map.Entry<Gasto, Boolean> entry : checkedGastos.entrySet()) {
            entry.setValue(isChecked);
        }
        // TODO la funcionalidad funcina, el marcado de checkboxes, no
        /**
         for (Map.Entry<Gasto, CheckBox> entry : checkBoxMap.entrySet()) {
         entry.getValue().setSelected(isChecked);
         }
         notifyDataSetChanged();
         */
    }

    /**
     * Este método devuelve el número de gastos que tienen el value en el mapa puesto a true
     *
     * @return int el número de gastos seleccionados
     */
    public int getNumberOfChecked() {
        return Collections.frequency(checkedGastos.values(), true);
    }

    public void add(Gasto g) {
        this.listaGastos.add(g);
        //we sort the list
        listaGastos.sort((g1, g2) -> g2.getFechaCreacionAsDate().compareTo(g1.getFechaCreacionAsDate()));
        checkedGastos.put(g, false);
        notifyDataSetChanged();
    }

    public void deleteGastos(List<Gasto> gastosList) {
        this.listaGastos.removeAll(gastosList);
        for (Gasto g : gastosList) {
            checkedGastos.remove(g);
        }
        notifyDataSetChanged();
    }

    public Map<Gasto, Boolean> getCheckedGastos() {
        return new HashMap<>(checkedGastos);
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
        holder.bindUser(gasto);
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

        public void bindUser(final Gasto gasto) {
            nombre.setText(gasto.getNombre());
            fecha.setText(gasto.getFechaCreacion());

            int imageMoney = GastosUtil.getImageFor(gasto);
            imagenGasto.setImageResource(imageMoney);
            imagenGasto.setImageTintList(new ColorStateList(new int[][]{}, new int[]{R.color.white}));

            // Si es un gasto o un ingreso
            String balancePago = (gasto.getBalance() > 0 ? "+" : "") + (gasto.getBalance()) + "€";
            balance.setText(balancePago);
            int color = gasto.getBalance() > 0 ? R.color.green : R.color.red;
            balance.setTextColor(ContextCompat.getColor(this.itemView.getContext(), color));

            CheckBox checkBox = itemView.findViewById(R.id.checkBoxGasto);
            checkBoxMap.put(gasto, checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Ponemos el contrario de si está o no cambiado
                    checkedGastos.put(gasto, isChecked);
                }
            });
        }
    }

}
