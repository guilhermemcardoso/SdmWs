package me.gmcardoso.sdmws.model

import retrofit2.Call
import retrofit2.http.*

interface SdmWsApi {
    @GET("curso")
    fun retrieveCurso(): Call<Curso>

    @GET("semestre/{sid}")
    fun retrieveSemestre(@Path("sid") sid: Int): Call<Semestre>

    @GET("disciplina/{sigla}")
    fun retrieveDisciplina(@Path("sigla") sigla: String): Call<Disciplina>

    @FormUrlEncoded
    @POST("disciplina")
    fun retrieveDisciplinaPost(@Field("sigla") sigla: String): Call<Disciplina>
}