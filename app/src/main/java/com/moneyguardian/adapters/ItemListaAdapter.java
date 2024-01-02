package com.moneyguardian.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;

import java.util.ArrayList;
import java.util.List;

public class ItemListaAdapter extends RecyclerView.Adapter<ItemListaAdapter.ItemListaViewHolder> {
    private static boolean isSelectedModeOn = false;
    private static List<ItemPagoConjunto> itemsPagoSeleccionados;
    private final OnItemClickListener listener;
    private final OnLongClickListener onLongPress;
    private List<ItemPagoConjunto> listaItemsPago;

    public ItemListaAdapter(List<ItemPagoConjunto> listaItemsPago, OnItemClickListener listener, OnLongClickListener longPress) {
        this.listaItemsPago = listaItemsPago;
        this.listener = listener;
        this.onLongPress = longPress;
        itemsPagoSeleccionados = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return listaItemsPago.get(position).hashCode();
    }

    public static void changeSelectMode(boolean change) {
        isSelectedModeOn = change;
    }

    public static boolean isSelectedModeOn() {
        return isSelectedModeOn;
    }

    public void changeAllList(List<ItemPagoConjunto> itemPagoConjuntos) {
        this.listaItemsPago = itemPagoConjuntos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_items_pago_lista, parent, false);
        return new ItemListaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListaViewHolder holder, int position) {
        ItemPagoConjunto itemPago = listaItemsPago.get(position);

        holder.bindUser(itemPago, listener, onLongPress);
        holder.itemView.setBackgroundColor(0);
        holder.itemView.setSelected(false);

    }

    @Override
    public int getItemCount() {
        if (listaItemsPago == null) {
            return 0;
        }
        return listaItemsPago.size();
    }

    public void addItem(ItemPagoConjunto item) {
        listaItemsPago.add(item);
        notifyItemInserted(listaItemsPago.size() - 1);
        notifyDataSetChanged();
    }

    public List<ItemPagoConjunto> getItemsPagoSeleccionados() {
        return itemsPagoSeleccionados;
    }

    public static void setItemsPagoSeleccionados(List<ItemPagoConjunto> itemsPagoSeleccionados) {
        ItemListaAdapter.itemsPagoSeleccionados = itemsPagoSeleccionados;
    }

    public interface OnItemClickListener {
        void onItemClick(ItemPagoConjunto item);
    }

    public static class ItemListaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView isPaid;
        private final TextView name;
        private final TextView money;

        public ItemListaViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.nameItemPago);
            money = (TextView) itemView.findViewById(R.id.moneyText);
            isPaid = itemView.findViewById(R.id.itemPagado);
        }

        // asignar valores a los componentes
        public void bindUser(final ItemPagoConjunto itemPago, final OnItemClickListener listener, final OnLongClickListener longClickListener) {
            name.setText(itemPago.getNombre());

            if (itemPago.isPagado()) {
                money.setVisibility(View.GONE);
                isPaid.setVisibility(View.VISIBLE);
            } else {
                money.setText(itemPago.getMoney() + "€");
                money.setVisibility(View.VISIBLE);
                isPaid.setVisibility(View.GONE);
            }

            itemView.setOnLongClickListener(v -> {
                onLongPress(this, listener, itemPago);
                longClickListener.onLongClick(v);
                return true;
            });
            itemView.setOnClickListener(v -> {
                if(isSelectedModeOn){
                    onLongPress(this, listener, itemPago);
                }
                listener.onItemClick(itemPago);
            });
        }

        public void onLongPress(ItemListaViewHolder holder, OnItemClickListener listener, ItemPagoConjunto itemPago) {
            int oldColor;
            if(holder.itemView.isSelected()){
                oldColor = 0;
                holder.itemView.setSelected(false);
                holder.itemView.setBackgroundColor(oldColor);
                itemsPagoSeleccionados.remove(itemPago);
                listener.onItemClick(itemPago);
                if (itemsPagoSeleccionados.isEmpty()) {
                    changeSelectMode(false);
                }
            }else {
                holder.itemView.setSelected(true);
                itemsPagoSeleccionados.add(itemPago);
                oldColor = holder.itemView.getDrawingCacheBackgroundColor();
                holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.grey, holder.itemView.getContext().getTheme()));
            }

            holder.itemView.setOnClickListener(v -> {
                if(isSelectedModeOn) {
                    if (holder.itemView.isSelected()) {
                        holder.itemView.setSelected(false);
                        holder.itemView.setBackgroundColor(oldColor);
                        itemsPagoSeleccionados.remove(itemPago);
                        listener.onItemClick(itemPago);
                        if (itemsPagoSeleccionados.isEmpty()) {
                            changeSelectMode(false);
                        }
                    } else {
                        holder.itemView.setSelected(true);
                        holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.grey, holder.itemView.getContext().getTheme()));
                        itemsPagoSeleccionados.add(itemPago);
                        listener.onItemClick(itemPago);
                    }
                }else{
                    listener.onItemClick(itemPago);
                }
            });

        }
    }
}
