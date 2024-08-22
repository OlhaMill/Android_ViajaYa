package com.om.proyecto_olha

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.om.proyecto_olha.databinding.FragmentProximosBinding
import java.time.LocalDate


class FragmentProximos : Fragment() {
    private lateinit var binding: FragmentProximosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var viajesCollection: CollectionReference
    private val viajes = mutableListOf<Viaje>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentProximosBinding.inflate(layoutInflater)

        Log.d("Historico", "onCreateView")

        db = FirebaseFirestore.getInstance()
        setUpRecyclerView()

        binding.btnVolverProximos.setOnClickListener {
            requireActivity().finish()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    protected fun setUpRecyclerView() {
        binding.myRVTodos.setHasFixedSize(true)
        binding.myRVTodos.layoutManager = LinearLayoutManager(requireContext())
        viajesCollection = db.collection("viajes")

        viajesCollection.addSnapshotListener { querySnapshot, firestoreException ->
            if (firestoreException != null) {
                Log.w("addSnapshotListener", "Escucha fallida!.", firestoreException)
                return@addSnapshotListener
            }

            viajes.clear()



            //val fechaHoy = Calendar.getInstance()

            for (document in querySnapshot!!) {
                Log.d("DOC", "${document.id} => ${document.data}")

                val nombreMesString = (document["mes"].toString())
                val numeroMes = Meses.obtenerNumero(nombreMesString)
                val anyo = document["anyo"].toString().toInt()
                val dia = document["dia"].toString().toInt()

                val viajeFecha: LocalDate = LocalDate.of(anyo, numeroMes, dia)
                val fechaPosterior = viajeFecha.compareTo(LocalDate.now())

                if (fechaPosterior > 0) {
                    var viaje = Viaje(
                        document.id.toString(),
                        document["ciudadDestino"].toString(),
                        document["ciudadOrigen"].toString(),
                        document["dia"].toString(),
                        document["mes"].toString(),
                        document["anyo"].toString(),
                        document["imagen"].toString(),
                        document["notas"].toString()
                    )
                    viajes.add(viaje)
                }
            }
            val viajesAdapter = MyRecyclerViewAdapter(viajes, requireContext())
            binding.myRVTodos.adapter = viajesAdapter

        }
    }
}