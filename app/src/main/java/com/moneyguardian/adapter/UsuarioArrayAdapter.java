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
import com.moneyguardian.modelo.Usuario;

import java.util.ArrayList;

public class UsuarioArrayAdapter extends ArrayAdapter<Usuario> {

    private boolean[] checked;

    public UsuarioArrayAdapter(@NonNull Context context, int resource, ArrayList<Usuario> usuarios) {
        super(context, resource, usuarios);
        this.checked = new boolean[usuarios.toArray().length];
        for (int i = 0; i < this.checked.length; i++) {
            this.checked[i] = false;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.usuario_list_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Usuario currentUser = getItem(position);

        if (currentUser != null) {
            TextView nombre = currentItemView.findViewById(R.id.nombreUsuario);
            TextView correo = currentItemView.findViewById(R.id.correoUsuario);
            ImageView imag = currentItemView.findViewById(R.id.imagenUsuario);
            nombre.setText(currentUser.getNombre());
            correo.setText(currentUser.getCorreo());
            // TODO Picasso.with(context)
            //.load(subjectData.Image)
            //.into(imag);
            // TODO numbersImage.setImageResource(currentUser.getImage());

            CheckBox cBox = (CheckBox) currentItemView.findViewById(R.id.checkBoxUsuarios);
            cBox.setTag(Integer.valueOf(position)); // set the tag so we can identify the correct row in the listener
            cBox.setChecked(checked[position]); // set the status as we stored it
            cBox.setOnCheckedChangeListener(mListener); // set the listener
        }

        return currentItemView;
    }

    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checked[(Integer) buttonView.getTag()] = isChecked; // get the tag so we know the row and store the status

            Log.i("Checkbox: ", String.valueOf(checked[(Integer) buttonView.getTag()]));
        }
    };

}
