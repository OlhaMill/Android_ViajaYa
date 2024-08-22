package com.om.proyecto_olha

data class CiudadTop(
    val id: String,
    val nombre: String,
    val imagen: String
)

data class Viaje (
    val id: String,
    val ciudadDestino: String,
    val ciudadOrigen: String,
    val dia: String,
    val mes: String,
    val anyo: String,
    val imagen: String,
    val nota: String
)

enum class Meses() {
    Enero,
    Febrero,
    Marzo,
    Abril,
    Mayo,
    Junio,
    Julio,
    Agosto,
    Septiembre,
    Octubre,
    Noviembre,
    Diciembre;
    companion object {
        fun obtenerNumero(nombreMes: String): Int {
            return try {
                valueOf(nombreMes.capitalize()).ordinal + 1
            } catch (e: IllegalArgumentException) {
                -1
            }
        }
    }
}