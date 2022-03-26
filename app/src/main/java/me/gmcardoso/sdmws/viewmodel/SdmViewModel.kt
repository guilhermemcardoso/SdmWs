package me.gmcardoso.sdmws.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.gmcardoso.sdmws.model.Curso
import me.gmcardoso.sdmws.model.Disciplina
import me.gmcardoso.sdmws.model.SdmWsApi
import me.gmcardoso.sdmws.model.Semestre
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SdmViewModel(application: Application): AndroidViewModel(application) {
    val cursoMdl: MutableLiveData<Curso> = MutableLiveData()
    val semestreMdl: MutableLiveData<Semestre> = MutableLiveData()
    val disciplinaMdl: MutableLiveData<Disciplina> = MutableLiveData()

    private val escopoCorrotinas = CoroutineScope(Dispatchers.IO + Job())

    // Retrofit
    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl("${URL_BASE}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val sdmWsApi: SdmWsApi = retrofit.create(SdmWsApi::class.java)

    companion object {
        val URL_BASE = "https://nobile.pro.br/sdm_ws"
    }

    fun getCurso() {
        escopoCorrotinas.launch {
            sdmWsApi.retrieveCurso().enqueue(object: Callback<Curso> {
                override fun onResponse(call: Call<Curso>, response: retrofit2.Response<Curso>) {
                    cursoMdl.postValue(response.body())
                }

                override fun onFailure(call: Call<Curso>, t: Throwable) {
                    Log.e("$URL_BASE", t.message.toString())
                }
            })
        }
    }

    fun getSemestre(sid: Int) {
        escopoCorrotinas.launch {
            sdmWsApi.retrieveSemestre(sid).enqueue(object: Callback<Semestre> {
                override fun onResponse(
                    call: Call<Semestre>,
                    response: retrofit2.Response<Semestre>
                ) {
                    semestreMdl.postValue(response.body())
                }

                override fun onFailure(call: Call<Semestre>, t: Throwable) {
                    Log.e("$URL_BASE", t.message.toString())
                }
            })
        }
    }

    fun getDisciplina(sigla: String) {
        escopoCorrotinas.launch {
            sdmWsApi.retrieveDisciplinaPost(sigla).enqueue(object: Callback<Disciplina> {
                override fun onResponse(
                    call: Call<Disciplina>,
                    response: retrofit2.Response<Disciplina>
                ) {
                    disciplinaMdl.postValue(response.body())
                }

                override fun onFailure(call: Call<Disciplina>, t: Throwable) {
                    Log.e("$URL_BASE", t.message.toString())
                }
            })
        }
    }
}