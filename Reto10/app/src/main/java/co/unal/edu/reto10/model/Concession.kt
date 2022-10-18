package co.unal.edu.reto10.model

data class Concession(
    var idproyecto : String,
    var codigo: String,
    var nombre: String,
    var nombrecorto: String,
    var idtipo: String = "1",
    var tipo: String = "Aeroportuario"
)
