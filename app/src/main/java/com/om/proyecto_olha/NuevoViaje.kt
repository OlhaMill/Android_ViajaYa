package com.om.proyecto_olha

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.om.proyecto_olha.databinding.ActivityMainBinding
import com.om.proyecto_olha.databinding.ActivityNuevoViajeBinding
import java.time.LocalDate
import java.util.Calendar

class NuevoViaje : AppCompatActivity() {
    private lateinit var binding: ActivityNuevoViajeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var ciudCollection: CollectionReference
    var tag = ""
    var dia = 0
    var mes = 0
    var anyo = 0
    var imagenAdd = false
    var ciudadBuscar = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoViajeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tag = intent.getStringExtra(MainActivity.ARG_TAG_APP) ?: ""

        if(tag == "añadiendo-con-destino"){
            val destino = intent.getStringExtra(MainActivity.ARG_EXTRA_DESTINO)
            val url2 = intent.getStringExtra(MainActivity.EXTRA_SEARCH_RESULT_URL)
            binding.etxtDestino.text = Editable.Factory.getInstance().newEditable(destino)
            binding.txtUrl.text = Editable.Factory.getInstance().newEditable(url2)
            imagenAdd = true

            if(url2 != null)
                showImg(url2)
            activarBoton()
        }else if (tag == "editando"){
            val destino = intent.getStringExtra(MainActivity.ARG_EXTRA_DESTINO)
            val origen = intent.getStringExtra(MainActivity.ARG_EXTRA_ORIGEN)
            val url2 = intent.getStringExtra(MainActivity.EXTRA_SEARCH_RESULT_URL)
            val fecha = intent.getStringExtra(MainActivity.ARG_EXTRA_FECHA)?.split(".")
            if(fecha != null) {
                dia = fecha.get(0).toInt()
                mes = Meses.obtenerNumero(fecha.get(1))
                anyo = fecha.get(2).toInt()
            }

            binding.etxtDestino.text = Editable.Factory.getInstance().newEditable(destino)
            binding.etxtOrigen.text = Editable.Factory.getInstance().newEditable(origen)
            binding.etxtFecha.text = Editable.Factory.getInstance().newEditable("$dia.$mes.$anyo")
            binding.txtUrl.text = Editable.Factory.getInstance().newEditable(url2)

            imagenAdd = true
            if(url2 != null)
                showImg(url2)
            activarBoton()
        }

        binding.etxtFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                dia = cal.get(Calendar.DAY_OF_MONTH)
                mes = cal.get(Calendar.MONTH) + 1
                anyo = cal.get(Calendar.YEAR)

                binding.etxtFecha.text = Editable.Factory.getInstance().newEditable("$dia.$mes.$anyo")
                activarBoton()
            }
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnCancelar.setOnClickListener {
            Log.d(MainActivity.ARG_TAG_APP, "Se devuelve Cancelar")
            // Finalizamos la actividad
            finish()
        }

        binding.imageCiudad.setOnClickListener {
            ciudadBuscar = binding.etxtDestino.text.toString()
            activarBoton()

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.es/search?sca_esv=588456036&q=$ciudadBuscar+ciudad&tbm=isch&source=lnms&sa=X&ved=2ahUKEwjO28mexPuCAxVqSaQEHReTDX0Q0pQJegQIDBAB&biw=606&bih=699&dpr=1.25")
            )
            imagenAdd = true
            startActivity(intent)
        }

        binding.txtUrl.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No necesitas hacer nada específico en este método, pero debes proporcionar la implementación.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val url = binding.txtUrl.text.toString()
                showImg(url)
                activarBoton()
            }
            override fun afterTextChanged(s: Editable?) {
                // No necesitas hacer nada específico en este método, pero debes proporcionar la implementación.
            }
        })

        binding.etxtOrigen.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No necesitas hacer nada específico en este método, pero debes proporcionar la implementación.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                activarBoton()
            }
            override fun afterTextChanged(s: Editable?) {
                // No necesitas hacer nada específico en este método, pero debes proporcionar la implementación.
            }
        })
        binding.txtDestino.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No necesitas hacer nada específico en este método, pero debes proporcionar la implementación.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                activarBoton()
            }
            override fun afterTextChanged(s: Editable?) {
                // No necesitas hacer nada específico en este método, pero debes proporcionar la implementación.
            }
        })

        binding.btnAnadirViaje.setOnClickListener {
            val viajeFecha: LocalDate = LocalDate.of(anyo, mes, dia)
            val fechaPosterior = viajeFecha.compareTo(LocalDate.now())

            if (fechaPosterior > 0) {
                if (!imagenAdd) {
                    myAlertDialog()
                } else {
                    anadirViaje()
                }
                activarBoton()
            }else{
                Toast.makeText(this, "La fecha debe ser posterior al día de hoy",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun myAlertDialog() {
        val builder = AlertDialog.Builder(this)
        // Se crea el AlertDialog.
        builder.apply {
            // Se asigna un título.
            setTitle("¿Seguir sin imagen?")
            // Se asgina el cuerpo del mensaje.
            setMessage("Vas a añadir un viaje sin imagen. \n¿Quieres seguir?")
            // Se define el comportamiento de los botones.
            setPositiveButton(
                android.R.string.ok,
                DialogInterface.OnClickListener(function = actionButton)
            )
            setNegativeButton(android.R.string.cancel) { _, _ ->
                Toast.makeText(context, android.R.string.no,
                    Toast.LENGTH_SHORT).show()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private val actionButton = { dialog: DialogInterface, which: Int ->
        anadirViaje()
    }
    private fun activarBoton(){
        if(!binding.etxtOrigen.text.isEmpty() && !binding.etxtDestino.text.isEmpty()
            && !binding.etxtFecha.text.isEmpty()){
            binding.btnAnadirViaje.isEnabled = true
        }
    }
    private fun anadirViaje() {
        db = FirebaseFirestore.getInstance()
        ciudCollection = db.collection("viajes")

        val mesString = Meses.values()[mes - 1]

        if(tag == "editando"){
            val nota = intent.getStringExtra(MainActivity.ARG_EXTRA_NOTAS)
            val id = intent.getStringExtra(MainActivity.ARG_EXTRA_ID)
            if(id != null) {
                val viaje = ciudCollection.document(id)
                val datos = mapOf(
                    "ciudadDestino" to binding.etxtDestino.text.toString(),
                    "ciudadOrigen" to binding.etxtOrigen.text.toString(),
                    "dia" to dia.toString(),
                    "mes" to mesString,
                    "anyo" to anyo.toString(),
                    "imagen" to binding.txtUrl.text.toString(),
                    "notas" to nota
                )

                viaje.update(datos)
                    .addOnSuccessListener {
                        Log.d("DOC_UPD", "Documento actualizado correctamente")
                    }
                    .addOnFailureListener { e ->
                        Log.w("DOC_UPD", "Error al actualizar el documento", e)
                    }
            }
        }else {
            val viaje = hashMapOf(
                "ciudadDestino" to binding.etxtDestino.text.toString(),
                "ciudadOrigen" to binding.etxtOrigen.text.toString(),
                "dia" to dia.toString(),
                "mes" to mesString,
                "anyo" to anyo.toString(),
                "imagen" to binding.txtUrl.text.toString(),
                "notas" to ""
            )
            ciudCollection.add(viaje)
                // Respuesta si ha sido correcto.
                .addOnSuccessListener {
                    Log.d("DOC_SET", "Documento añadido!")
                }
                // Respuesta si se produce un fallo.
                .addOnFailureListener { e ->
                    Log.w("DOC_SET", "Error en la escritura", e)
                }
        }
        Snackbar.make(
            binding.root,
            "El viaje se ha añadido correctamente",
            Snackbar.LENGTH_SHORT
        ).show()
        binding.etxtOrigen.text = null
        binding.etxtDestino.text = null
        binding.etxtFecha.text = null
        binding.txtUrl.text = null
        binding.btnAnadirViaje.isEnabled = false
        binding.imageCiudad.setImageResource(R.drawable.imagen)
    }
    private fun showImg (url:String){
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.imagen)
            .into(binding.imageCiudad)
    }
}
