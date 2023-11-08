package com.moneyguardian;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moneyguardian.modelo.Usuario;

import org.w3c.dom.Text;

import java.util.List;

public class UsersFormItemsListaAdapter extends
        RecyclerView.Adapter<UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder>{
    private List<Usuario> usuariosDelPago;
    private View.OnClickListener listener;

    public UsersFormItemsListaAdapter(List<Usuario> usuariosDelPago, View.OnClickListener listener) {
        this.usuariosDelPago = usuariosDelPago;
        this.listener = listener;
    }


    @NonNull
    @Override
    public UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pago_lista_recycler, parent, false);
        return new UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersFormItemsListaAdapter.UsersFormItemsListaViewHolder holder,
                                 int position) {
        Usuario user= usuariosDelPago.get(position);
        holder.bindUser(user, listener);
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

        public void bindUser(final Usuario user,View.OnClickListener checkBonOnCLick) {
            name.setText(user.getNombre());
            //photo.setImageIcon(user.getPhoto());
            participatesPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(participatesPayment.isChecked()){

                    }
                }
            });
        }
    }
}
