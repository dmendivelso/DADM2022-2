package co.unal.edu.reto10.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.unal.edu.reto10.model.Concession
import co.unal.edu.reto10.network.ApiService
import kotlinx.coroutines.launch

class ConcessionViewModel: ViewModel() {
    var concessionListResponse: List<Concession> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")
    var selectedFilter by mutableStateOf("")

    fun getConcessionList(){
        viewModelScope.launch {
            val apiService = ApiService.getInstance()
            try {
                val concessionList = apiService.getConcession()
                concessionListResponse = concessionList
            }
            catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }
    }

    fun getConcessionbyTipoList(tipo : String){
        viewModelScope.launch {
            val apiService = ApiService.getInstance()
            try {
                val concessionList = apiService.getConcessionbyTipo(tipo)
                concessionListResponse = concessionList
            }
            catch (e: Exception){
                errorMessage = e.message.toString()
            }
        }
    }

}