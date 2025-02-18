import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("/machines")
    fun getMachines(
        @Header("X-API-Key") apiKey: String
    ): Call<List<Machine>>
}