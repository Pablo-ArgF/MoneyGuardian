package com.moneyguardian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.moneyguardian.R;
import com.moneyguardian.modelo.Usuario;

import java.util.ArrayList;

public class UsuarioArrayAdapter extends ArrayAdapter<Usuario> {
    public UsuarioArrayAdapter(@NonNull Context context, int resource, ArrayList<Usuario> usuarios) {
        super(context, resource, usuarios);
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

        TextView nombre = currentItemView.findViewById(R.id.nombreUsuario);
        TextView correo = currentItemView.findViewById(R.id.correoUsuario);
        ImageView imag = currentItemView.findViewById(R.id.imagenUsuario);
        nombre.setText(currentUser.getNombre());
        correo.setText(currentUser.getCorreo());
        // TODO Picasso.with(context)
        //.load(subjectData.Image)
        //.into(imag);
        // TODO numbersImage.setImageResource(currentUser.getImage());

        // then return the recyclable view
        return currentItemView;
    }


}
