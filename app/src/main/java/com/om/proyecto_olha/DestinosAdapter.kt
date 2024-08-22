package com.om.proyecto_olha

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.om.proyecto_olha.databinding.ItemCiudadBinding

class DestinosAdapter(listaDestinos: MutableList<CiudadTop>, context: Context):
    RecyclerView.Adapter<DestinosAdapter.ViewHolder>(){
    var myListaDestinos: MutableList<CiudadTop>
    var myContext: Context

    init {
        myListaDestinos = listaDestinos
        myContext = context
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d("RECYCLERVIEW", "onCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(
            R.layout.item_ciudad,
            parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = myListaDestinos.get(position)
        holder.bind(item, myContext)
    }
    override fun getItemCount(): Int {
        return myListaDestinos.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemCiudadBinding.bind(view)

        fun bind(ciudad: CiudadTop, context: Context) {

            val url = ciudad.imagen
            Glide.with(context)
                .load(url)
                .into(binding.imgCiudad)
            binding.txtCiudad.text = ciudad.nombre
            itemView.setOnClickListener{
                val myIntent = Intent(context, NuevoViaje::class.java).apply {
                    putExtra(MainActivity.ARG_EXTRA_DESTINO, ciudad.nombre)
                    putExtra(MainActivity.EXTRA_SEARCH_RESULT_URL, ciudad.imagen)
                    putExtra(MainActivity.ARG_TAG_APP, "añadiendo-con-destino")
                }
                startActivity(context, myIntent, Bundle())
            }
        }
    }
}