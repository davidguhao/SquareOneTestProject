package com.example.squareonetestproject

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request


class MovieEntity(
    val title: String,
    val poster_path: String,
    val genre_ids: Array<Int>,
    val overview: String,
    val release_date: String,
)
class ResponseFromNetwork(
    val page: Int,
    val result: Array<MovieEntity>,
    val total_pages: Int,
    val total_results: Int
)
class MovieRepository {
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
            emit(Gson().fromJson(
                OkHttpClient().newCall(Request.Builder()
                    .url("https://api.themoviedb.org/3/discover/movie?language=en-US&page=1&sort_by=primary_release_date.desc")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()).execute().body!!.string(),
                ResponseFromNetwork::class.java).result.map { MovieDetail(
                name = it.title,
                poster = MovieDetail.ImageRequestFactory(
                    url = "https://api.themoviedb.org/3/discover/movie" + it.poster_path,
                    authToken = authToken
                ),
                genresList = it.genre_ids,
                overview = it.overview,
                releaseDate = it.release_date
            ) }.toList())
        }
    }
    fun getUpcomingMovies(): Flow<List<MovieDetail>> {
        return movies
    }
}