package br.com.curso.listadetarefas.android
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 127.0.0.1 é o endereço de localhost para o emulador Android (após o adb reverse).
    private const val BASE_URL = "http://127.0.0.1:8080/api/"
    val instance: TarefaApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        retrofit.create(TarefaApiService::class.java)
    }
}