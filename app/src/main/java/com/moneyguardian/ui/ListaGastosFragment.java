package com.moneyguardian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.FormularioGastoActivity;
import com.moneyguardian.R;
import com.moneyguardian.adapters.GastoListaAdapter;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.util.GastosUtil;

import java.util.List;

public class ListaGastosFragment extends Fragment {
    public static final String GASTO_CREADO = "GASTO_CREADO";
    public static final int GESTION_GASTO = 10;
    private RecyclerView recyclerView;
    private GastoListaAdapter adapter;

    // Base de datos
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Botones
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        View root = inflater.inflate(R.layout.fragment_lista_gastos, container, false);

        // Animaciones de botones
        rotateOpen = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getContext(), R.anim.to_bottom_amim);


        recyclerView = root.findViewById(R.id.recyclerGastos);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(root.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        // Recuperar gastos del usuario

        cargarDatos();

        adapter = new GastoListaAdapter(new GastoListaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Gasto gasto) {
                // TODO later
                // clickonItem(gasto);
            }
        });

        recyclerView.setAdapter(adapter);

        // Manejo del botón de añadir gasto

        FloatingActionButton buttonOpen = root.findViewById(R.id.buttonOpenMenuGastos);
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnimation(root);
                setVisibility(root);
            }
        });

        FloatingActionButton buttonAddIngreso = root.findViewById(R.id.buttonAddIngreso);
        buttonAddIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormularioGastoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Ingreso", true);
                intent.putExtras(bundle);
                startActivityForResult(intent, GESTION_GASTO);
            }
        });

        FloatingActionButton buttonAddGasto = root.findViewById(R.id.buttonAddGasto);
        buttonAddGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormularioGastoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Ingreso", false);
                intent.putExtras(bundle);
                startActivityForResult(intent, GESTION_GASTO);
            }
        });

        // Borrado y seleccionado de  gastos
        CheckBox checkBoxSelectAll = root.findViewById(R.id.cbSelectAllGastos);
        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.selectAll(isChecked);
                // TODO Va a funcionar?
                View recycler = root.findViewById(R.id.recyclerGastos);
                CheckBox cb = recycler.findViewById(R.id.checkBoxGasto);
                cb.setSelected(isChecked);
            }
        });

        Button buttonDelete = root.findViewById(R.id.btnEliminarGasto);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getCheckedGastos() != null)
                    GastosUtil.deleteGastos(adapter.getCheckedGastos());
                else
                    Toast.makeText(getContext(), getString(R.string.no_gasto_selected), Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

    private void setVisibility(View root) {
        FloatingActionButton buttonAddGasto = root.findViewById(R.id.buttonAddGasto);
        FloatingActionButton buttonAddIngreso = root.findViewById(R.id.buttonAddIngreso);
        if (buttonAddGasto.getVisibility() == View.VISIBLE) {
            buttonAddGasto.setVisibility(View.INVISIBLE);
            buttonAddIngreso.setVisibility(View.INVISIBLE);
            buttonAddGasto.setClickable(false);
            buttonAddIngreso.setClickable(false);
        } else {
            buttonAddGasto.setVisibility(View.VISIBLE);
            buttonAddIngreso.setVisibility(View.VISIBLE);
            buttonAddGasto.setClickable(true);
            buttonAddIngreso.setClickable(true);
        }
    }

    private void setAnimation(View root) {
        FloatingActionButton buttonAddGasto = root.findViewById(R.id.buttonAddGasto);
        FloatingActionButton buttonOpenMenuGastos = root.findViewById(R.id.buttonOpenMenuGastos);
        FloatingActionButton buttonAddIngreso = root.findViewById(R.id.buttonAddIngreso);
        if (buttonAddGasto.getVisibility() == View.INVISIBLE) {
            buttonAddGasto.startAnimation(fromBottom);
            buttonAddIngreso.startAnimation(fromBottom);
            buttonOpenMenuGastos.startAnimation(rotateOpen);
        } else {
            buttonAddGasto.startAnimation(toBottom);
            buttonAddIngreso.startAnimation(toBottom);
            buttonOpenMenuGastos.startAnimation(rotateClose);
        }
    }

    private void cargarDatos() {
        db.collection("users/").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = (DocumentSnapshot) task.getResult();
                    if (documentSnapshot.get("gastos") != null) {
                        List<DocumentReference> gastos = (List<DocumentReference>) documentSnapshot.get("gastos");
                        gastos.forEach(gasto -> {
                            gasto.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        // No podemos usarlo por culpa de la fecha :D
                                        // Gasto g = documentSnapshot.toObject(Gasto.class);
                                        String nombre = (String) documentSnapshot.get("nombre");
                                        double balance = (double) documentSnapshot.get("balance");
                                        String fecha = (String) documentSnapshot.get("fechaCreacion");
                                        String categoria = (String) documentSnapshot.get("categoria");
                                        Gasto g = new Gasto(nombre, (float) balance, categoria, fecha);
                                        g.setReference(gasto);
                                        adapter.add(g);
                                    }
                                }
                            });
                        });
                    }
                }
            }
        });
    }

    public void clickonItem(PagoConjunto pagoConjunto) {
        // TODO later
    }
}
