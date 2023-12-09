package com.moneyguardian.adapters;

import android.net.Uri;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.R;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.DecimalFilterForInput;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersFormItemsListaAdapter extends RecyclerView.Adapter<UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder> {
    private static final DecimalFormat dfZero = new DecimalFormat("0.00");
    private final List<UsuarioParaParcelable> usuariosDelPago;
    private final HashMap<UsuarioParaParcelable, Double> usuariosSeleccionados;
    private final List<UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder> holders;
    private final boolean checkBoxesActivated;
    private UsuarioParaParcelable usuarioSeleccionado;
    private double cantidad;
    private double cantidadPorUser;
    private int totalUsuariosseleccionados;
    private boolean changeMoneyActive;
    private boolean dontCheck;

    public UsersFormItemsListaAdapter(List<UsuarioParaParcelable> usuariosDelPago,
                                      double cantidad, boolean activated,
                                      boolean changeMoneyActive,
                                      UsuarioParaParcelable usuarioSeleccionado,
                                      HashMap<UsuarioParaParcelable, Double> usuariosSeleccionados) {
        this.usuariosDelPago = usuariosDelPago;
        this.holders = new ArrayList<>();
        this.cantidad = cantidad;
        this.checkBoxesActivated = activated;
        this.changeMoneyActive = changeMoneyActive;
        this.usuarioSeleccionado = usuarioSeleccionado;
        this.usuariosSeleccionados = usuariosSeleccionados;
        if(!usuariosSeleccionados.containsKey(usuarioSeleccionado)){
            usuariosSeleccionados.put(usuarioSeleccionado, cantidad);
        }
    }

    @NonNull
    @Override
    public UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_recyler_form_items_pago_user_to_add, parent, false);
        return new UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder(itemView);
    }

    public void changeSelecctedUser(UsuarioParaParcelable usuario, HashMap<UsuarioParaParcelable,
            Double> usersSelected) {
        if (usuarioSeleccionado != null) {
            if(usersSelected.containsKey(usuarioSeleccionado)){
                double newCantidad = usersSelected.get(usuarioSeleccionado) - cantidad;

                if(newCantidad == 0.0){
                    usersSelected.remove(usuarioSeleccionado);
                }else{
                    usersSelected.put(usuarioSeleccionado, newCantidad);
                }
            }

            this.usuarioSeleccionado = usuario;

            if(usersSelected.containsKey(usuarioSeleccionado)){
                usersSelected.put(usuarioSeleccionado, usersSelected.get(usuarioSeleccionado) + cantidad);
            }else{
                usersSelected.put(usuarioSeleccionado, cantidad);
            }
        }
    }

    public void activateEditMoney(Boolean activate) {

        changeMoneyActive = activate;

        for (UsersFormItemsListaViewHolder h : holders) {
            if (h.participantesPayment.isChecked()) {
                h.moneyToPay.setEnabled(activate);
                h.isEdited = true;
            }
        }
    }

    public void changeCantidadTotal(Double cantidadNueva) {
        this.cantidad = cantidadNueva;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder holder, int position) {
        UsuarioParaParcelable user = usuariosDelPago.get(position);

        holder.bindUser(user);
        holder.participantesPayment.setOnClickListener(v -> {
            if (holder.participantesPayment.isChecked()) {
                totalUsuariosseleccionados++;
                if (changeMoneyActive) {
                    holder.isEdited = true;
                    double moneyLeftToPay = controlEqMoneyWhenEdited();
                    holder.moneyToPay.setText(dfZero.format(moneyLeftToPay).replace(',', '.'));
                    if (moneyLeftToPay != 0.0) {
                        if (user.equals(usuarioSeleccionado)) {
                            usuariosSeleccionados.put(user,
                                    Math.round((cantidad - moneyLeftToPay) * 100.0) / 100.0);
                        } else {
                            usuariosSeleccionados.put(user,
                                    Math.round(-moneyLeftToPay * 100.0) / 100.0);
                        }
                    }
                    holder.moneyToPay.setEnabled(true);
                } else {
                    cantidadPorUser = moneyToUser();
                    updateHolders();
                }
            } else {
                totalUsuariosseleccionados--;
                holder.isEdited = false;
                if (changeMoneyActive) {
                    holder.moneyToPay.setText("");
                    usuariosSeleccionados.remove(user);
                    holder.moneyToPay.setEnabled(false);
                } else {
                    cantidadPorUser = moneyToUser();
                    updateHolders();
                }
            }
        });

        holder.moneyToPay.setFilters(new InputFilter[]{new DecimalFilterForInput(2)});
        holder.moneyToPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty() && !user.equals(usuarioSeleccionado)) {
                    usuariosSeleccionados.remove(user);
                } else if (s.toString().isEmpty() && user.equals(usuarioSeleccionado)) {
                    usuariosSeleccionados.put(user, cantidad);
                } else {
                    double moneyToPay =
                            Double.parseDouble(holder.moneyToPay.getText().toString().replace(','
                                    , '.'));
                    if (moneyToPay != 0.0) {
                        if (user.equals(usuarioSeleccionado)) {
                            usuariosSeleccionados.put(user,
                                    Math.round((cantidad - moneyToPay) * 100.0) / 100.0);
                        } else {
                            usuariosSeleccionados.put(user,
                                    Math.round(-moneyToPay * 100.0) / 100.0);
                        }

                    }
                }
            }
        });

        if(usuariosSeleccionados.containsKey(user)){
            if(user.getId().equals(usuarioSeleccionado.getId()) && usuariosSeleccionados.get(user) != 0.0 &&  usuariosSeleccionados.get(user) != cantidad){
                holder.participantesPayment.setEnabled(true);
                holder.participantesPayment.setChecked(true);
                totalUsuariosseleccionados++;
                holder.moneyToPay.setText((Math.round((cantidad - usuariosSeleccionados.get(user))*100.0)/100.0)+"");
            }else if (!user.getId().equals(usuarioSeleccionado.getId())){
                holder.participantesPayment.setEnabled(true);
                holder.participantesPayment.setChecked(true);
                totalUsuariosseleccionados++;
                holder.moneyToPay.setText(Math.abs(usuariosSeleccionados.get(user))+"");
            }
            holder.participantesPayment.setEnabled(checkBoxesActivated);
        }else{
            holder.participantesPayment.setEnabled(checkBoxesActivated);
        }

        holders.add(holder);
    }

    public void activateAllCheckBox(Boolean activate) {
        for (UsersFormItemsListaViewHolder h : holders) {
            h.participantesPayment.setEnabled(activate);
        }
    }

    private double moneyToUser() {
        double totalPagado = 0.0, totalAPagar, editText = 0.0;
        for (UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder h : holders) {
            if (h.participantesPayment.isChecked() && !h.moneyToPay.getText().toString().isEmpty() && h.isEdited) {
                totalPagado += Double.parseDouble(h.moneyToPay.getText().toString());
                editText++;
            }
        }

        totalAPagar = cantidad - totalPagado;

        if (totalAPagar > 0.0) {
            return totalAPagar / (totalUsuariosseleccionados - editText);
        }

        return 0.0;
    }

    private double controlEqMoneyWhenEdited() {
        double totalPagado = 0.0, totalAPagar;
        for (UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder h : holders) {
            if (h.participantesPayment.isChecked() && !h.moneyToPay.getText().toString().isEmpty()) {
                totalPagado += Double.parseDouble(h.moneyToPay.getText().toString());
            }
        }

        totalAPagar = cantidad - totalPagado;

        if (totalAPagar > 0.0) {
            return totalAPagar;
        }

        return 0.0;

    }

    public boolean allMoneyIspaid() {
        double totalPagado = 0.0;

        for (UsersFormItemsListaViewHolder h : holders) {
            if (h.participantesPayment.isChecked()) {
                totalPagado += Math.round(Double.parseDouble(h.moneyToPay.getText().toString()) * 100.0) / 100.0;
            }
        }

        totalPagado = Math.round(totalPagado * 100.0) / 100.0 - cantidad;

        return totalPagado <= 0.1 && totalPagado >= -0.1;
    }

    private void updateHolders() {
        for (UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder h : holders) {
            if (!h.isEdited) {
                if (h.participantesPayment.isChecked()) {
                    h.moneyToPay.setText(dfZero.format(cantidadPorUser).replace(',', '.'));
                } else {
                    h.moneyToPay.setText("");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (usuariosDelPago == null) {
            return 0;
        }
        return usuariosDelPago.size();
    }

    public HashMap<UsuarioParaParcelable, Double> getUsersSelected() {
        return usuariosSeleccionados;
    }

    public static class UsersFormItemsListaViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final CircleImageView photo;
        CheckBox participantesPayment;
        EditText moneyToPay;
        boolean isEdited;

        public UsersFormItemsListaViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textViewUserName);
            participantesPayment = itemView.findViewById(R.id.checkBoxNeedToPay);
            moneyToPay = itemView.findViewById(R.id.editTextNumberMoneyToPay);
            isEdited = false;
            photo = (CircleImageView) itemView.findViewById(R.id.imageViewAvatarIcon);
        }

        public void bindUser(final UsuarioParaParcelable user) {
            name.setText(user.getNombre());
            Picasso.get().load(Uri.parse(user.getImageURI())).into(photo);
            participantesPayment.setEnabled(false);

        }
    }
}
