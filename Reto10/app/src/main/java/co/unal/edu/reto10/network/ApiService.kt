package co.unal.edu.reto10.network

import co.unal.edu.reto10.model.Concession
import co.unal.edu.reto10.network.ApiConstants.BASE_URL
import co.unal.edu.reto10.network.ApiConstants.ENDPOINT
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET(ENDPOINT)
    suspend fun getConcession(): List<Concession>

    @GET(ENDPOINT)
    suspend fun getConcessionbyTipo(@Query(value = "tipo") tipo: String): List<Concession>

    companion object {
        var apiService: ApiService? = null
        fun getInstance(): ApiService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}