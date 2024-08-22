package com.om.proyecto_olha

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.om.proyecto_olha.databinding.FragmentTodosBinding


class FragmentTodos : Fragment() {
    private lateinit var binding: FragmentTodosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var viajesCollection: CollectionReference
    private val viajes = mutableListOf<Viaje>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentTodosBinding.inflate(layoutInflater)

        Log.d("Historico", "onCreateView")

        db = FirebaseFirestore.getInstance()
        setUpRecyclerView()

        binding.btnVolverTodos.setOnClickListener {
            requireActivity().finish()
        }

        return binding.root
    }

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

            for (document in querySnapshot!!) {
                Log.d("DOC", "${document.id} => ${document.data}")

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
            val viajesAdapter = MyRecyclerViewAdapter(viajes, requireContext())
            binding.myRVTodos.adapter = viajesAdapter

        }
    }
}