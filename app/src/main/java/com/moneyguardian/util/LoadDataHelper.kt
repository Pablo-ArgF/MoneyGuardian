@file:JvmName("LoadDataHelper")

package com.moneyguardian.util

import com.google.firebase.firestore.DocumentReference
import com.moneyguardian.modelo.Gasto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoadDataHelper {


    /*suspend fun loadGastosData(lifecycleScope : CoroutineScope, refs : List<DocumentReference>): List<Gasto> {
        val gastosList = mutableListOf<Gasto>()
        lifecycleScope.launch(Dispatchers.IO) {
            refs.forEach { ref ->
                // Perform asynchronous operation to get data from Firestore
                val documentSnapshot = ref.get().await()

                // Map the document snapshot to a Gasto object
                val gasto = GastoMapper.map(documentSnapshot)

                // Add the Gasto object to the list
                gastosList.add(gasto)
            }
        }
        return gastosList
    }*/

    companion object {
        @JvmStatic
        suspend fun loadGastosData(lifecycleScope: CoroutineScope, refs: List<DocumentReference>): List<Gasto> {
            val gastosList = mutableListOf<Gasto>()
            lifecycleScope.launch(Dispatchers.IO) {
                // Create a list of deferred tasks
                val tasks = refs.map { ref ->
                    async {
                        // Perform asynchronous operation to get data from Firestore
                        val documentSnapshot = ref.get().await()

                        // Map the document snapshot to a Gasto object
                        val gasto = GastoMapper.map(documentSnapshot)

                        // Return the Gasto object
                        gasto
                    }
                }

                // Wait for all tasks to complete using awaitAll
                gastosList.addAll(awaitAll(*tasks.toTypedArray()))
            }.join()
            return gastosList
        }
    }
}