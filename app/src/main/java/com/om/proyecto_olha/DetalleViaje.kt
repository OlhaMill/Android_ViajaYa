package com.om.proyecto_olha

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.om.proyecto_olha.databinding.ActivityDetalleViajeBinding
import com.om.proyecto_olha.databinding.DialogLayoutBinding
import java.time.LocalDate

class DetalleViaje : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleViajeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var viajesCollection: CollectionReference
    var nota:String? = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleViajeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        viajesCollection = db.collection("viajes")

        val id = intent.getStringExtra(MainActivity.ARG_EXTRA_ID)
        val origen = intent.getStringExtra(MainActivity.ARG_EXTRA_ORIGEN)
        val destino = intent.getStringExtra(MainActivity.ARG_EXTRA_DESTINO)
        val fecha = intent.getStringExtra(MainActivity.ARG_EXTRA_FECHA)
        val imagen = intent.getStringExtra(MainActivity.EXTRA_SEARCH_RESULT_URL)
        nota = intent.getStringExtra(MainActivity.ARG_EXTRA_NOTAS)

        val fechaSplit = fecha?.split(".")
        var mes = 0
        var dia = 0
        var anyo = 0
        if(fechaSplit != null) {
            mes = Meses.obtenerNumero(fechaSplit[1])
            dia = fechaSplit!![0]!!.toInt()
            anyo = fechaSplit!![2].toInt()
        }
        val viajeFecha: LocalDate = LocalDate.of(anyo, mes, dia)
        val fechaPosterior = viajeFecha.compareTo(LocalDate.now())

        if (fechaPosterior > 0) {
            val dias = LocalDate.now().until(viajeFecha).days
            binding.txtDias.text = dias.toString()
        }else{
            val dias = viajeFecha.until(LocalDate.now()).days
            binding.txtDias.text = dias.toString()
            binding.txtViajeEmpieza.text = "El viaje fue hace"
        }

        binding.txtNotas.text = nota
        binding.txtCiudadRes.text = destino

        binding.imgUbi.setOnClickListener {
            // Para abrir Google Maps, si no se requiere ubicación,
            // no es necesario pedir permiso
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=$destino ciudad")
            )
            startActivity(intent)
        }
        binding.imgBorrar.setOnClickListener {
            myAlertDialog()
        }
        binding.imgEditar.setOnClickListener {
            val myIntent = Intent(this, NuevoViaje::class.java).apply {
                putExtra(MainActivity.ARG_EXTRA_ID, id)
                putExtra(MainActivity.ARG_EXTRA_ORIGEN, origen)
                putExtra(MainActivity.ARG_EXTRA_DESTINO, destino)
                putExtra(MainActivity.ARG_EXTRA_FECHA, fecha)
                putExtra(MainActivity.EXTRA_SEARCH_RESULT_URL, imagen)
                putExtra(MainActivity.ARG_EXTRA_NOTAS, nota)
                putExtra(MainActivity.ARG_TAG_APP, "editando")
            }
            ContextCompat.startActivity(this, myIntent, Bundle())
        }
        binding.btnVolver.setOnClickListener {
                finish()
            }
        binding.btnAnadirNota.setOnClickListener {
            myCustomAlertDialog("", "añadiendo")
        }
        binding.btnEditarNota.setOnClickListener {
            myCustomAlertDialog(nota, "editando")
        }
    }
    private fun myAlertDialog() {
        val builder = AlertDialog.Builder(this)
        // Se crea el AlertDialog.
        builder.apply {
            // Se asigna un título.
            setTitle("Eliminar viaje")
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
        // Se muestra el AlertDialog.
        val dialog = builder.create()
        dialog.show()
    }
    private val actionButton = { dialog: DialogInterface, which: Int ->
        val id = intent.getStringExtra(MainActivity.ARG_EXTRA_ID)
        if (id != null) {
            val viajeBorrar: DocumentReference = viajesCollection.document(id)

            viajeBorrar
                .delete()
                .addOnSuccessListener {
                    Log.d("DOC_UPD", "Documento eliminado correctamente")
                }
                .addOnFailureListener { e ->
                    Log.w("DOC_UPD", "Error al eliminar el documento", e)
                }
            Toast.makeText(this, "Se ha borrado correctamente", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun myCustomAlertDialog(myNota: String?, contexto:String) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            // Así es con View Binding
            var bindingDialog: DialogLayoutBinding
            // Inflamos y asignamos el layout
            bindingDialog = DialogLayoutBinding.inflate(layoutInflater)
            setView(bindingDialog.root)

            bindingDialog.etxtNota.text = Editable.Factory.getInstance().newEditable(myNota)
            setPositiveButton("Guardar") { dialog, _ ->
                ActualizarNotas(bindingDialog, contexto)
                }
                setNegativeButton("Cancelar") { dialog, _ ->
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    private fun ActualizarNotas(bindingD: DialogLayoutBinding, contexto: String){
        if (contexto == "añadiendo")
            nota += "\n" + bindingD.etxtNota.text.toString()
        else
            nota = bindingD.etxtNota.text.toString()

        binding.txtNotas.text = nota

        val id = intent.getStringExtra(MainActivity.ARG_EXTRA_ID)
        if (id != null) {
            val viaje = viajesCollection.document(id)
            viaje
                .update("notas", nota)
                .addOnSuccessListener {
                    Log.d("DOC_UPD", "Documento actualizado correctamente")
                }
                .addOnFailureListener { e ->
                    Log.w("DOC_UPD", "Error al actualizar el documento", e)
                }
        }
    }


}