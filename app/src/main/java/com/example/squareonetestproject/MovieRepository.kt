package com.example.squareonetestproject

import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.NullPointerException


class MovieEntity(
    val title: String,
    val poster_path: String,
    val genre_ids: Array<Int>,
    val overview: String,
    val release_date: String,
)
class ResponseFromNetwork(
    val page: Int,
    val results: List<MovieEntity>,
    val total_pages: Int,
    val total_results: Int
)
class MovieRepository {
    @OptIn(DelicateCoroutinesApi::class)
    private val movies = flow {
        // <TMDB info>

        // username - squareOneTestProject
        // password - mYpzik-1zakne-xeqtag
        // email - davidguhao@icloud.com
        // API secret key - 7bd6d6c6d6d20ed3282ed8893fc4cbc4

        // <API read access token>
        //     eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YmQ2ZDZjNmQ2ZDIwZWQzMjgyZWQ4ODkzZmM0Y2JjNCIsInN1YiI6IjY1NDIwZjk5YTU4OTAyMDBhZDNlYmU3NyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.3Ajiics-oMoYqnzh61l-VLbchzfUrPXoXkRTYIhrD08
        // </API read access token>

        // </TMDB info>

        val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YmQ2ZDZjNmQ2ZDIwZWQzMjgyZWQ4ODkzZmM0Y2JjNCIsInN1YiI6IjY1NDIwZjk5YTU4OTAyMDBhZDNlYmU3NyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.3Ajiics-oMoYqnzh61l-VLbchzfUrPXoXkRTYIhrD08"


        while(true) {
            try {
                GlobalScope.async {
                    Gson().fromJson(
                        OkHttpClient().newCall(
                            Request.Builder()
                                .url("https://api.themoviedb.org/3/discover/movie?language=en-US&page=1&sort_by=primary_release_date.desc")
                                .get()
                                .addHeader("accept", "application/json")
                                .addHeader("Authorization", "Bearer $authToken")
                                .build()
                        ).execute().body!!.string(),
                        ResponseFromNetwork::class.java
                    )
                }.await()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }?.let { res ->
                try {
                    emit(res.results.map { MovieDetail(
                        name = it.title,
                        poster = MovieDetail.ImageRequestFactory(
                            url = "https://image.tmdb.org/t/p/original" + it.poster_path,
                            authToken = authToken
                        ),
                        genresList = it.genre_ids,
                        overview = it.overview,
                        releaseDate = it.release_date
                    ) })
                } catch(e: Exception) {
                    e.printStackTrace()

                    throw e
                }

            }

            delay(5000)
        }

    }
    fun getUpcomingMovies(): Flow<List<MovieDetail>> {
        return movies
    }
}