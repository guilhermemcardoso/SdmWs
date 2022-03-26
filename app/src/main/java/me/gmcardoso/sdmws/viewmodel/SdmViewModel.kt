package me.gmcardoso.sdmws.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.gmcardoso.sdmws.model.Curso
import me.gmcardoso.sdmws.model.Disciplina
import me.gmcardoso.sdmws.model.Semestre
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class SdmViewModel(application: Application): AndroidViewModel(application) {
    val cursoMdl: MutableLiveData<Curso> = MutableLiveData()
    val semestreMdl: MutableLiveData<Semestre> = MutableLiveData()
    val disciplinaMdl: MutableLiveData<Disciplina> = MutableLiveData()

    private val escopoCorrotinas = CoroutineScope(Dispatchers.IO + Job())

    private val filaRequisicoesVolley: RequestQueue =
        Volley.newRequestQueue(application.baseContext)

    init {
        exemplosReflexao()
    }

    private fun exemplosReflexao() {
        /* Tipo Classe */
        val classeResponseJava: Class<Response<*>> = Response::class.java
        val classeResponseKotlin: KClass<Response<*>> = (Response::class.java).kotlin
        Log.v("REFLEXÃO", "Classe Response Java: ${classeResponseJava.canonicalName}")
        Log.v("REFLEXÃO", "Classe Response Kotlin: ${classeResponseKotlin.qualifiedName}")

        /* Atributos e propriedades */
        classeResponseJava.declaredFields.forEach { atributo ->
            Log.v("REFLEXÃO", "Atributo Java: ${atributo}")
        }

        classeResponseKotlin.members.forEach { membro ->
            if(membro is KProperty) {
                Log.v("REFLEXÃO", "Membro Kotlin: ${membro}")
            }
        }
    }

    private val gson: Gson = Gson()

    companion object {
        val URL_BASE = "https://nobile.pro.br/sdm_ws"
        val ENDPOINT_CURSO = "/curso"
        val ENDPOINT_SEMESTRE = "/semestre"
        val ENDPOINT_DISCIPLINA = "/disciplina"
    }

    fun getCurso() {
        escopoCorrotinas.launch {
            val urlCurso = "${URL_BASE}${ENDPOINT_CURSO}"
            val requisicaoCursoJor = JsonObjectRequest(
                Request.Method.GET,
                urlCurso,
                null,
                { response ->
                    if (response != null) {
                        val curso: Curso = gson.fromJson(response.toString(), Curso::class.java)
                        cursoMdl.postValue(curso)
                    }
                },
                { error -> Log.e(urlCurso, error.toString())}
            )

            filaRequisicoesVolley.add(requisicaoCursoJor)
        }
    }

    fun getSemestre(sid: Int) {
        escopoCorrotinas.launch {
            val urlSemestre = "${URL_BASE}${ENDPOINT_SEMESTRE}/${sid}"
            val requisicaoSemestreJar = JsonArrayRequest(
                Request.Method.GET,
                urlSemestre,
                null,
                { response ->
                    response?.also { disciplinaJar ->
                        val semestre = gson.fromJson(response.toString(), Semestre::class.java)
                        semestreMdl.postValue(semestre)
                    }
                },
                { error -> Log.e(urlSemestre, error.toString())}
            )

            filaRequisicoesVolley.add(requisicaoSemestreJar)
        }
    }

    fun getDisciplina(sigla: String) {
        escopoCorrotinas.launch {
            val urlDisciplina = "${URL_BASE}${ENDPOINT_DISCIPLINA}"
            val requisicaoDisciplinaSr = object: StringRequest(
                Request.Method.POST,
                urlDisciplina,
                { response ->
                    if (response != null) {
                        val disciplina: Disciplina = gson.fromJson(response, Disciplina::class.java)
                        disciplinaMdl.postValue(disciplina)
                    }
                },
                { error -> Log.e(urlDisciplina, error.toString())}
            ) {
                override fun getParams(): MutableMap<String, String>? {
                    val params = mutableMapOf<String, String>()
                    params["sigla"] = sigla
                    return params
                }
            }

            filaRequisicoesVolley.add(requisicaoDisciplinaSr)
        }
    }
}