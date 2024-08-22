package com.om.proyecto_olha

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.om.proyecto_olha.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var ciudCollection: CollectionReference
    private val ciudades = mutableListOf<CiudadTop>()
        companion object {
        const val ARG_TAG_APP = "explicitIntent"
        const val ARG_EXTRA_FECHA = "myFecha"
        const val ARG_EXTRA_DIA = "myDia"
        const val ARG_EXTRA_MES = "myMes"
        const val ARG_EXTRA_ANYO = "myAnyo"
        const val ARG_EXTRA_ID = "myID"
        const val ARG_EXTRA_ORIGEN = "myOrigen"
        const val ARG_EXTRA_DESTINO = "myDestino"
        const val EXTRA_SEARCH_RESULT_URL = "myUrl"
        const val ARG_EXTRA_NOTAS = "Notas"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        setUpRecyclerView()

       binding.btnIntroducirDestino.setOnClickListener {
            val myIntent = Intent(this, NuevoViaje::class.java).apply {
                putExtra(ARG_TAG_APP, "añadiendo-sin-destino")
            }
            startActivity(myIntent)
        }

        binding.btnMisViajes.setOnClickListener {
            val myIntent = Intent(this, ListaViajes::class.java).apply {}
            startActivity(myIntent)
        }
    }

    protected fun setUpRecyclerView() {
        binding.recycleViewDestino.setHasFixedSize(true)
        binding.recycleViewDestino.layoutManager = GridLayoutManager(this, 3)
        ciudCollection = db.collection("cuidades")
        ciudCollection.addSnapshotListener { querySnapshot, firestoreException ->
            if (firestoreException != null) {
                Log.w("addSnapshotListener", "Escucha fallida!.", firestoreException)
                return@addSnapshotListener
            }

            ciudades.clear()

            for (document in querySnapshot!!) {
                Log.d("DOC", "${document.id} => ${document.data}")
                var ciudad = CiudadTop(
                    document!!["id"].toString(),
                    document["nombre"].toString(),
                    document["imagen"].toString()
                )
                ciudades.add(ciudad)
            }

            val destinosAdapter = DestinosAdapter(ciudades, this)
            binding.recycleViewDestino.adapter = destinosAdapter
        }
    }
}