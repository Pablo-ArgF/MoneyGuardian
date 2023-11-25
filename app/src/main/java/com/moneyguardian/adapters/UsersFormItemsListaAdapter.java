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
import android.widget.ImageView;
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

public class UsersFormItemsListaAdapter extends
        RecyclerView.Adapter<UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder>{
    private final UsuarioParaParcelable usuarioSeleccionado;
    private List<UsuarioParaParcelable> usuariosDelPago;
    private double cantidad;
    private  double cantidadPorUser;
    private HashMap<UsuarioParaParcelable,Double> usuariosSeleccionados;
    private List<UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder> holders;
    private boolean checkBoxesActivated;
    private int totalUsuariosseleccionados;

    private static final DecimalFormat dfZero = new DecimalFormat("0.00");
    private boolean changeMoneyActive;
    private boolean dontCheck;

    public UsersFormItemsListaAdapter(List<UsuarioParaParcelable> usuariosDelPago,
                            double cantidad,boolean activated,UsuarioParaParcelable userSelected,
                                                                        boolean changeMoneyActive) {
        this.usuariosDelPago = usuariosDelPago;
        this.usuariosSeleccionados = new HashMap<>();
        this.holders = new ArrayList<>();
        this.cantidad = cantidad;
        this.checkBoxesActivated = activated;
        this.usuarioSeleccionado = userSelected;
        usuariosSeleccionados.put(usuarioSeleccionado,cantidad);
        this.changeMoneyActive = changeMoneyActive;
    }

    @NonNull
    @Override
    public UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recyler_form_items_pago_user_to_add, parent, false);
        return new UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder(itemView);
    }

    public void addUser(UsuarioParaParcelable user){
        usuariosDelPago.add(user);
        notifyItemInserted(usuariosDelPago.size()-1);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder holder,
                                 int position) {
        UsuarioParaParcelable user= usuariosDelPago.get(position);

        holder.bindUser(user);
        holder.participantesPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( holder.participantesPayment.isChecked()){
                    if(changeMoneyActive){
                        double moneyLeftToPay = controlEqMoneyWhenEdited();
                        holder.moneyToPay.setText(dfZero.format(moneyLeftToPay).replace(',','.'));
                        if(moneyLeftToPay != 0.0){
                            if(user.equals(usuarioSeleccionado)){
                                usuariosSeleccionados.put(user, Math.round((cantidad-moneyLeftToPay)*100.0)/100.0);
                            }else{
                                usuariosSeleccionados.put(user, Math.round(-moneyLeftToPay*100.0)/100.0);
                            }
                        }
                        holder.moneyToPay.setEnabled(true);
                    }else{
                        totalUsuariosseleccionados++;
                        cantidadPorUser = cantidad/totalUsuariosseleccionados;
                        updateHolders();
                    }
                }else{
                    if(changeMoneyActive){
                        holder.moneyToPay.setText("");
                        usuariosSeleccionados.remove(user);
                        holder.moneyToPay.setEnabled(false);
                    }else {
                        totalUsuariosseleccionados--;
                        cantidadPorUser = cantidad / totalUsuariosseleccionados;
                        updateHolders();
                    }
                }
            }
        });

        holder.participantesPayment.setEnabled(checkBoxesActivated);
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
                if(s.toString().isEmpty()){
                    usuariosSeleccionados.remove(user);
                }else{
                    double moneyToPay = Double.parseDouble(holder.moneyToPay.getText()
                            .toString().replace(',','.'));
                    if(moneyToPay != 0.0) {
                        if(user.equals(usuarioSeleccionado)){
                            usuariosSeleccionados.put(user, Math.round((cantidad-moneyToPay)*100.0)/100.0);
                        }else{
                            usuariosSeleccionados.put(user,Math.round(-moneyToPay*100.0)/100.0);
                        }

                    }
                }
            }
        });

        holders.add(holder);
    }

    private double controlEqMoneyWhenEdited() {
        double totalPagado = 0.0,totalAPagar = 0.0;
        for (UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder h : holders){
            if(h.participantesPayment.isChecked() && !h.moneyToPay.getText().toString().isEmpty()){
                totalPagado += Double.parseDouble(h.moneyToPay.getText().toString());
            }
        }

        totalAPagar = cantidad - totalPagado;

        if(totalAPagar > 0.0){
            return totalAPagar;
        }

        return 0.0;

    }

    public boolean allMoneyIspaid(){
        double totalPagado = 0.0;

        for(Map.Entry<UsuarioParaParcelable, Double> u : usuariosSeleccionados.entrySet()){
            totalPagado += Math.round(u.getValue() * 100.0)/100.0;
        }

        totalPagado =  Math.round(totalPagado * 100.0)/100.0;

        return  totalPagado <= 0.1 && totalPagado >= -0.1;
    }

    private void updateHolders(){
        for (UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder h : holders){
            if(h.participantesPayment.isChecked()){
                h.moneyToPay.setText(dfZero.format(cantidadPorUser).replace(',','.'));
            }else{
                h.moneyToPay.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        if(usuariosDelPago == null){
            return 0;
        }
        return usuariosDelPago.size() ;
    }

    public HashMap<UsuarioParaParcelable, Double> getUsersSelected(){
        return usuariosSeleccionados;
    }

    public static class UsersFormItemsListaViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private CircleImageView photo;
        CheckBox participantesPayment;

        EditText moneyToPay;
        boolean isEdited;

        public UsersFormItemsListaViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textViewUserName);
            participantesPayment = itemView.findViewById(R.id.checkBoxNeedToPay);
            moneyToPay = itemView.findViewById(R.id.editTextNumberMoneyToPay);
            isEdited = false;
            photo= (CircleImageView)itemView.findViewById(R.id.imageViewAvatarIcon);
        }

        public void bindUser(final UsuarioParaParcelable user) {
            name.setText(user.getNombre());
            Picasso.get().load(Uri.parse(user.getImageURI())).into(photo);
            participantesPayment.setEnabled(false);

        }
    }
}
