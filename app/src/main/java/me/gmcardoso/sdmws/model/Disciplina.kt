package me.gmcardoso.sdmws.model

import com.google.gson.annotations.SerializedName

data class Disciplina(
    val aulas: Int,
    val horas: Int,
    val nome: String,
    val sigla: String
)