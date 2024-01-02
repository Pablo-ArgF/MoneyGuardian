package com.moneyguardian.util

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.moneyguardian.R
import java.util.concurrent.Callable

class BottomFilter(
    private val filtrosAplicados: MutableList<String>
) : BottomSheetDialogFragment() {


    var categoryLayout: FlexboxLayout? = null
    lateinit var categoryListener: View.OnClickListener
    lateinit var selectedCategories: ArrayList<String>
    lateinit var selectedCategoriesView: ArrayList<View>
    lateinit var callback: Callable<Void>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_filter, container, false)
        selectedCategories = ArrayList()
        selectedCategoriesView = ArrayList()

        // Manejo de base de datos
        var mAuth = FirebaseAuth.getInstance()
        var db = FirebaseFirestore.getInstance()

        //listener used when selecting a category
        categoryListener = View.OnClickListener { v ->
            val textView = v.findViewById<View>(R.id.name_category) as TextView
            if (selectedCategories.contains(textView.text.toString())) {
                //deselect it
                selectedCategories.remove(textView.text.toString())
                selectedCategoriesView.remove(v)
                val selectedColor: Int = resources.getColor(R.color.black)
                (v.findViewById<View>(R.id.name_category) as TextView).setTextColor(
                    selectedColor
                )
                v.findViewById<View>(R.id.image_category)
                    .setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black)));
            } else {
                //we select this one
                seleccionarCategoria(textView, v);
            }
            // Ejecutar el callback para el filtro
            callback.call();
        }

        val queryIngreso: Task<QuerySnapshot> =
            db.collection("categorias/").get();

        queryIngreso.addOnCompleteListener(OnCompleteListener { task ->

            var categorias = ArrayList<String>()
            task.result.documents.forEach { c ->
                categorias.add(c.get("nombre").toString())
            }

            categoryLayout = view.findViewById<FlexboxLayout>(R.id.categoryLayout)
            categoryLayout?.removeAllViews() //we clean the category container

            for (category in categorias) {
                // Inflate the LinearLayout from an XML layout file
                val view = LayoutInflater.from(context)
                    .inflate(
                        R.layout.category_card_for_filter,
                        categoryLayout, false
                    )
                //We add the click listener to the view
                view.setOnClickListener(categoryListener)
                // Get the TextView from the inflated layout
                val textView = view.findViewById<TextView>(R.id.name_category)
                // Set the category name as the text of the TextView
                textView.text = category
                //set the image of the category as the icon on the card
                view.findViewById<View>(R.id.image_category).setBackgroundDrawable(
                    context?.let { getDrawable(it, GastosUtil.getImageFor(category)) }
                )
                //We add the view to the layout
                categoryLayout?.addView(view)

                // Seleccionar las ya elegidas anteriormente
                Log.i("Lista", filtrosAplicados.toString());
                if (category in filtrosAplicados) {
                    seleccionarCategoria(textView, view);
                }
            }
        })

        return view
    }

    private fun seleccionarCategoria(textView: TextView, v: View) {
        selectedCategories.add(textView.text.toString())
        selectedCategoriesView.add(v)
        val selectedColor: Int = resources.getColor(R.color.blue)
        (v.findViewById<View>(R.id.name_category) as TextView).setTextColor(
            selectedColor
        )
        v.findViewById<View>(R.id.image_category).backgroundTintList =
            ColorStateList.valueOf(selectedColor)
    }

    fun getFiltros(): List<String> {
        return this.selectedCategories;
    }

}