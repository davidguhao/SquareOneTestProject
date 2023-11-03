package com.example.squareonetestproject

import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import javax.inject.Singleton

interface TMDBapiInterface {

    // <TMDB info>

    // username - squareOneTestProject
    // password - mYpzik-1zakne-xeqtag
    // email - davidguhao@icloud.com
    // API secret key - 7bd6d6c6d6d20ed3282ed8893fc4cbc4

    // <API read access token>
    //     eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YmQ2ZDZjNmQ2ZDIwZWQzMjgyZWQ4ODkzZmM0Y2JjNCIsInN1YiI6IjY1NDIwZjk5YTU4OTAyMDBhZDNlYmU3NyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.3Ajiics-oMoYqnzh61l-VLbchzfUrPXoXkRTYIhrD08
    // </API read access token>

    // </TMDB info>


    companion object {
        const val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YmQ2ZDZjNmQ2ZDIwZWQzMjgyZWQ4ODkzZmM0Y2JjNCIsInN1YiI6IjY1NDIwZjk5YTU4OTAyMDBhZDNlYmU3NyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.3Ajiics-oMoYqnzh61l-VLbchzfUrPXoXkRTYIhrD08"
    }

    @GET("3/movie/upcoming?language=en-US&page={page}")
    @Headers("Authorization: Bearer $authToken", "Custom-Header: Header-Value")
    suspend fun getUpcoming(@Path("page") page: Int): UpcomingMoviesResFromNetwork

    @GET("3/genre/movie/list?language=en")
    @Headers("Authorization: Bearer $authToken", "Custom-Header: Header-Value")
    suspend fun getGenreMap(): GenreMapFromNetwork

}

@Module
class TMDBapi {
    @Provides
    fun get(): TMDBapiInterface = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .addConverterFactory(GsonConverterFactory.create())

        .build().create(TMDBapiInterface::class.java)
}

@Singleton
@Component(modules = [ TMDBapi::class ])
interface MovieRepoComponent {
    fun inject(activity: MovieRepository)
}



