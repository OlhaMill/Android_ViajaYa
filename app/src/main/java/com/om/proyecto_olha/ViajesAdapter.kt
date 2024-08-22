package com.om.proyecto_olha

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.om.proyecto_olha.databinding.ItemViajeBinding

class MyRecyclerViewAdapter(listaViajes: MutableList<Viaje>, context: Context):
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>(){
    private lateinit var db: FirebaseFirestore
    private lateinit var viajesCollection: CollectionReference
    private lateinit var viajeEligir: Viaje
    var myListaViajes: MutableList<Viaje>
    var myContext: Context

    init {
        myListaViajes = listaViajes
        myContext = context
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d("RECYCLERVIEW", "onCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        db = FirebaseFirestore.getInstance()
        viajesCollection = db.collection("viajes")
        return ViewHolder(inflater.inflate(
            R.layout.item_viaje,
            parent, false))
    }

    private var actionMode: ActionMode? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = myListaViajes.get(position)
        holder.bind(item, myContext)
    }
    override fun getItemCount(): Int {
        return myListaViajes.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemViajeBinding.bind(view)

        fun bind(viaje: Viaje, context: Context) {
            Log.d("bind", viaje.ciudadDestino)

            val url = viaje.imagen
            Glide.with(context)
                .load(url)
                .into(binding.img)
            binding.txtOrigenDestino.text = "${viaje.ciudadOrigen} - ${viaje.ciudadDestino}"
            binding.txtFechaViaje.text = "${viaje.dia} de ${viaje.mes} ${viaje.anyo}"

            itemView.setOnClickListener{
                viajeEligir = viaje
                mostrarDetalle()
            }

            itemView.setOnLongClickListener {
                viajeEligir = viaje
                when (actionMode) {
                    null -> {
                        // Se lanza el ActionMode.
                        actionMode = it.startActionMode(actionModeCallback)
                        it.isSelected = true
                        true
                    }
                    else -> false
                }

                return@setOnLongClickListener true
            }
        }

        private val actionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu):
                    Boolean {
                val inflater = mode?.menuInflater
                inflater?.inflate(R.menu.menu, menu)
                return true
            }
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu):
                    Boolean {
                return false
            }
            override fun onActionItemClicked(mode: ActionMode, item:
            MenuItem
            ): Boolean {
                return when (item.itemId) {
                    R.id.editar -> {
                        val myIntent = Intent(myContext, NuevoViaje::class.java).apply {
                            putExtra(MainActivity.ARG_EXTRA_ID, viajeEligir.id)
                            putExtra(MainActivity.ARG_EXTRA_ORIGEN, viajeEligir.ciudadOrigen)
                            putExtra(MainActivity.ARG_EXTRA_DESTINO, viajeEligir.ciudadDestino)
                            putExtra(MainActivity.ARG_EXTRA_FECHA, "${viajeEligir.dia}.${viajeEligir.mes}.${viajeEligir.anyo}")
                            putExtra(MainActivity.EXTRA_SEARCH_RESULT_URL, viajeEligir.imagen)
                            putExtra(MainActivity.ARG_EXTRA_NOTAS, viajeEligir.nota)
                            putExtra(MainActivity.ARG_TAG_APP, "editando")
                        }
                        startActivity(myContext, myIntent, Bundle())
                        return true
                    }
                    R.id.borrar -> {
                        myAlertDialog()
                        mode.finish()
                        return true
                    }
                    R.id.detalles -> {
                        mostrarDetalle()
                        return true
                    }
                    else -> false
                }
            }
            override fun onDestroyActionMode(mode: ActionMode) {
                actionMode = null
            }
        }
        private fun myAlertDialog() {
            val builder = AlertDialog.Builder(myContext)
            // Se crea el AlertDialog.
            builder.apply {
                // Se asigna un título.
                setTitle("Eliminar viaje a ${viajeEligir.ciudadDestino}")
                // Se asgina el cuerpo del mensaje.
                setMessage("¿Quieres borrar el viaje?")
                // Se define el comportamiento de los botones.
                setPositiveButton(
                    ("Sí"),
                    DialogInterface.OnClickListener(function = actionButton)
                )
                setNegativeButton("Cancelar") { _, _ ->
                    Toast.makeText(context, android.R.string.no,
                        Toast.LENGTH_SHORT).show()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
        private val actionButton = { dialog: DialogInterface, which: Int ->
            val viajeBorrar: DocumentReference = viajesCollection.document(viajeEligir.id)

            viajeBorrar
                .delete()
                .addOnSuccessListener {
                    Log.d("DOC_UPD", "Documento eliminado correctamente")
                }
                .addOnFailureListener { e ->
                    Log.w("DOC_UPD", "Error al eliminar el documento", e)
                }
            Toast.makeText(myContext, "Se ha borrado correctamente", Toast.LENGTH_SHORT).show()
        }
    }
    private fun mostrarDetalle(){
        val myIntent = Intent(myContext, DetalleViaje::class.java).apply {
            putExtra(MainActivity.ARG_EXTRA_ID, viajeEligir.id)
            putExtra(MainActivity.ARG_EXTRA_ORIGEN, viajeEligir.ciudadOrigen)
            putExtra(MainActivity.ARG_EXTRA_DESTINO, viajeEligir.ciudadDestino)
            putExtra(MainActivity.ARG_EXTRA_FECHA, "${viajeEligir.dia}.${viajeEligir.mes}.${viajeEligir.anyo}")
            putExtra(MainActivity.EXTRA_SEARCH_RESULT_URL, viajeEligir.imagen)
            putExtra(MainActivity.ARG_EXTRA_NOTAS, viajeEligir.nota)
        }
        startActivity(myContext, myIntent, Bundle())
    }
}