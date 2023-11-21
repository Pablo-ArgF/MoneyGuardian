package com.moneyguardian.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.moneyguardian.R;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsuarioArrayAdapter extends ArrayAdapter<UsuarioParaParcelable> {

    private boolean[] checked;

    public UsuarioArrayAdapter(@NonNull Context context, int resource, ArrayList<UsuarioParaParcelable> usuarios) {
        super(context, resource, usuarios);
        this.checked = new boolean[usuarios.toArray().length];
        for (int i = 0; i < this.checked.length; i++) {
            this.checked[i] = false;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItemView = convertView;

        // Inflamos el layout si la vista es null
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.linea_usuario_list_view, parent, false);
        }

        // Posición de la vista desde el array adapter
        UsuarioParaParcelable currentUser = getItem(position);

        if (currentUser != null) {
            TextView nombre = currentItemView.findViewById(R.id.nombreUsuario);
            TextView correo = currentItemView.findViewById(R.id.correoUsuario);
            ImageView image = currentItemView.findViewById(R.id.imagenUsuario);
            nombre.setText(currentUser.getNombre());
            correo.setText(currentUser.getEmail());
            if (currentUser.getImageURI() != null) {
                Picasso.get().load(currentUser.getImageURI()).into(image);
            }

            CheckBox cBox = (CheckBox) currentItemView.findViewById(R.id.checkBoxUsuarios);
            cBox.setTag(Integer.valueOf(position)); // set the tag so we can identify the correct row in the listener
            cBox.setChecked(checked[position]); // set the status as we stored it
            cBox.setOnCheckedChangeListener(mListener); // set the listener
        }

        return currentItemView;
    }

    /**
     * Listener para las checkbox
     */
    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checked[(Integer) buttonView.getTag()] = isChecked;

            Log.i("Checkbox: " + buttonView.getTag() + " status: ", String.valueOf(checked[(Integer) buttonView.getTag()]));
        }
    };

    public boolean isChecked(int i) {
        return checked[i];
    }

}
