package com.moneyguardian.adapters;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioArrayAdapter extends ArrayAdapter<UsuarioParaParcelable> {

    private Map<UsuarioParaParcelable, Boolean> checkboxMap;

    public UsuarioArrayAdapter(@NonNull Context context, int resource, ArrayList<UsuarioParaParcelable> usuarios) {
        super(context, resource, usuarios);
        checkboxMap = new HashMap<>();
        for (UsuarioParaParcelable user : usuarios) {
            checkboxMap.put(user, false);
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
            CircleImageView image = currentItemView.findViewById(R.id.imagenUsuario);
            nombre.setText(currentUser.getNombre());
            correo.setText(currentUser.getEmail());
            if (currentUser.getImageURI() != null) {
                Picasso.get().load(currentUser.getImageURI()).into(image);
            }

            CheckBox cBox = (CheckBox) currentItemView.findViewById(R.id.checkBoxUsuarios);
            cBox.setTag(currentUser); // set the tag so we can identify the correct row in the listener
            cBox.setChecked(false); // set the status as we stored it
            cBox.setOnCheckedChangeListener(mListener); // set the listener
        }

        return currentItemView;
    }

    /**
     * Listener para las checkbox
     */
    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //checked[(Integer) buttonView.getTag()] = isChecked;
            checkboxMap.put((UsuarioParaParcelable) buttonView.getTag(), isChecked);
            //Log.i("Checkbox: " + buttonView.getTag() + " status: ", String.valueOf(checked[(Integer) buttonView.getTag()]));
        }
    };

    public boolean isChecked(int i) {
        //return checked[i];
        return false;
    }

    public List<UsuarioParaParcelable> getChecked() {
        List<UsuarioParaParcelable> lista = new ArrayList<>();
        for (Map.Entry<UsuarioParaParcelable, Boolean> entry : checkboxMap.entrySet()) {
            if (entry.getValue()) {
                lista.add(entry.getKey());
            }
        }



        return lista;
    }

    public boolean atLeastOneUserSelected() {
        for (Map.Entry<UsuarioParaParcelable, Boolean> entry : checkboxMap.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public void update(ArrayList<UsuarioParaParcelable> usuarios) {
        checkboxMap = new HashMap<>();
        for (UsuarioParaParcelable user : usuarios) {
            checkboxMap.put(user, false);
        }
        notifyDataSetChanged();
    }
}
