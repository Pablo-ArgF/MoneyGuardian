package com.moneyguardian.adapters;

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

import java.util.List;

public class UsersFormItemsListaAdapter extends
        RecyclerView.Adapter<UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder>{
    private List<UsuarioParaParcelable> usuariosDelPago;
    private double cantidad;

    public UsersFormItemsListaAdapter(List<UsuarioParaParcelable> usuariosDelPago, double cantidad) {
        this.usuariosDelPago = usuariosDelPago;
    }


    @NonNull
    @Override
    public UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recyler_form_items_pago_user_to_add, parent, false);
        return new UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder holder,
                                 int position) {
        UsuarioParaParcelable user= usuariosDelPago.get(position);
        holder.bindUser(user, cantidad);
    }

    @Override
    public int getItemCount() {
        if(usuariosDelPago == null){
            return 0;
        }
        return usuariosDelPago.size() ;
    }

    public static class UsersFormItemsListaViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView photo;
        private CheckBox participatesPayment;

        private EditText moneyToPay;

        public UsersFormItemsListaViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.textViewUserName);
            participatesPayment = (CheckBox) itemView.findViewById(R.id.checkBoxNeedToPay);
            moneyToPay =(EditText) itemView.findViewById(R.id.editTextNumberMoneyToPay);
            //photo= (ImageView)itemView.findViewById(R.id.imageViewAvatarIcon);
        }

        public void bindUser(final UsuarioParaParcelable user,double cantidad) {
            name.setText(user.getNombre());
            //photo.setImageIcon(user.getPhoto());
            if(participatesPayment.isChecked()){
                moneyToPay.setText(cantidad+"");
            }else{
                moneyToPay.setText("");
            }
        }
    }
}
