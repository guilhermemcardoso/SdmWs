package me.gmcardoso.sdmws.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import me.gmcardoso.sdmws.databinding.ActivityMainBinding
import me.gmcardoso.sdmws.model.Semestre
import me.gmcardoso.sdmws.viewmodel.SdmViewModel
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var sdmWsViewModel: SdmViewModel

    // Adapter do spinner de semestre
    private val listaSemestres = mutableListOf<Int>()
    private lateinit var semestreSpAdapter: ArrayAdapter<Int>

    // Adapter do spinner de disciplina
    private val listaDisciplinas = mutableListOf<String>()
    private lateinit var disciplinaSpAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        //Instanciando viewmodel e chamando getCurso
        sdmWsViewModel = ViewModelProvider
            .AndroidViewModelFactory(this.application)
            .create(SdmViewModel::class.java)

        sdmWsViewModel.cursoMdl.observe(this) { curso ->
            val cursoSb = StringBuilder()
            cursoSb.append("Nome: ${curso.nome}\n")
            cursoSb.append("Sigla: ${curso.sigla}\n")
            cursoSb.append("Quantidade de Semestres: ${curso.semestres}\n")
            cursoSb.append("Quantidade de Horas: ${curso.horas}\n")
            activityMainBinding.cursoTv.text = cursoSb.toString()

            // Preenchendo o spinner de semestres
            curso.semestres.also { semestres ->
                listaSemestres.clear()
                listaSemestres.addAll(1..semestres)
                semestreSpAdapter.notifyDataSetChanged()
            }
        }

        // Instanciando adapter do spinner semestre
        semestreSpAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaSemestres
        )
        activityMainBinding.semestreSp.adapter = semestreSpAdapter

        // Instanciando adapter do spinner disciplinas
        disciplinaSpAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaDisciplinas
        )
        activityMainBinding.disciplinaSp.adapter = disciplinaSpAdapter

        // Adicionar listener ao spinner de semestre para buscar um semestre
        activityMainBinding.semestreSp.onItemSelectedListener =
            object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicao: Int, p3: Long) {
                    val semestre = listaSemestres[posicao]
                    sdmWsViewModel.getSemestre(semestre)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // Não se aplica
                }
        }

        // Observando o mutableLiveData de semestre
        sdmWsViewModel.semestreMdl.observe(this) { semestre: Semestre ->
            listaDisciplinas.clear()
            semestre.forEach { disciplina ->
                disciplina.sigla.also { sigla ->
                    listaDisciplinas.add(sigla)
                }
            }
            disciplinaSpAdapter.notifyDataSetChanged()
            activityMainBinding.disciplinaSp.setSelection(0)
            sdmWsViewModel.getDisciplina(listaDisciplinas[0])
        }

        // listener para spinner de disciplinas
        activityMainBinding.disciplinaSp.onItemSelectedListener =
            object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicao: Int, p3: Long) {
                    val sigla = listaDisciplinas[posicao]
                    sdmWsViewModel.getDisciplina(sigla)

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // Não se aplica
                }
            }

        sdmWsViewModel.disciplinaMdl.observe(this) { disciplina ->
            val disciplinaSb = StringBuilder()
            disciplinaSb.append("Nome: ${disciplina.nome}\n")
            disciplinaSb.append("Sigla: ${disciplina.sigla}\n")
            disciplinaSb.append("Aulas: ${disciplina.aulas}\n")
            disciplinaSb.append("Horas: ${disciplina.horas}\n")
            activityMainBinding.disciplinaTv.text = disciplinaSb.toString()
        }

        sdmWsViewModel.getCurso()
    }
}