package com.moneyguardian.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
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
import com.moneyguardian.util.Animations;
import com.moneyguardian.util.GastosUtil;


import java.util.Arrays;
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
    private Animations animations;

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
        animations = new Animations(root);

        //Botones
        FloatingActionButton buttonOpen = root.findViewById(R.id.buttonOpenMenuGastos);
        FloatingActionButton buttonAddIngreso = root.findViewById(R.id.buttonAddIngreso);
        FloatingActionButton buttonAddGasto = root.findViewById(R.id.buttonAddGasto);

        animations.setOnClickAnimationAndVisibility(buttonOpen, Arrays.asList(buttonAddIngreso,buttonAddGasto));

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
                // TODO No funciona
                View recycler = root.findViewById(R.id.recyclerGastos);
                CheckBox cb = recycler.findViewById(R.id.checkBoxGasto);
                if (cb != null)
                    cb.setSelected(isChecked);
            }
        });

        Button buttonDelete = root.findViewById(R.id.btnEliminarGasto);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBorrarGasto();
            }
        });

        return root;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle b = data.getExtras();
            if (b.get(GASTO_CREADO) != null) {
                Gasto gastoCreado = (Gasto) b.get(GASTO_CREADO);
                gastoCreado.setReference(db.document("/gastos/" + gastoCreado.getUUID()));
                this.adapter.add(gastoCreado);
            }
        }
    }

    private void alertBorrarGasto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.question_remove_gasto)
                .setPositiveButton(R.string.confirm_remove_friend, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                        List<Gasto> gastosList = new ArrayList<>();
                        // Si hay mapa y hay gastos seleccionados
                        if (adapter.getCheckedGastos() != null && adapter.getNumberOfChecked() > 0) {
                            gastosList = GastosUtil.deleteGastos(adapter.getCheckedGastos());
                            adapter.update(gastosList);
                            // Si no hay mapa, o no hay ninguno seleccionado
                        } else {
                            Toast.makeText(getContext(), getString(R.string.no_gasto_selected), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                }).create().show();
    }
}
