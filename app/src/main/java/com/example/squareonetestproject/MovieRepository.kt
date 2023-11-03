package com.example.squareonetestproject

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class GenreMapFromNetwork(
    val genres: List<GenreId2Name>
) {
    class GenreId2Name(
        val id: Int,
        val name: String
    )
}

class UpcomingMoviesResFromNetwork(
    val page: Int,
    val results: List<MovieEntity>,
    val total_pages: Int,
    val total_results: Int
) {
    class MovieEntity(
        val title: String,
        val poster_path: String,
        val genre_ids: List<Int>,
        val overview: String,
        val release_date: String,
        val backdrop_path: String
    )
}

class MovieRepository {
    @Inject
    lateinit var tmdb: TMDBapiInterface
    private suspend fun List<UpcomingMoviesResFromNetwork.MovieEntity>.entityToDetail(): List<MovieDetail> = map {
        MovieDetail(
            name = it.title,
            poster = MovieDetail.ImageRequestFactory(
                url = "https://image.tmdb.org/t/p/original" + it.poster_path,
            ),
            backdrop = MovieDetail.ImageRequestFactory(
                url = "https://image.tmdb.org/t/p/original" + it.backdrop_path,
            ),
            genresList = it.genre_ids.let { ids ->
                // Get genre map from network
                try {
                    CoroutineScope(Dispatchers.IO).async {
                        tmdb.getGenreMap()
                    }.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }?.let { res ->
                    ArrayList<String>().apply {
                        for(g in res.genres)
                            for(id in ids) {
                                if(g.id == id) add(g.name)
                            }
                    }
                } ?: ids.map { i ->  i.toString() }
            },
            overview = it.overview,
            releaseDate = it.release_date
        )
    }
    private val movies = flow {
        while(true) {
            // Get upcoming movies from network, first we need to get some basic info.
            val upComingMovies = try {
                CoroutineScope(Dispatchers.IO).async {
                    tmdb.getUpcoming(1)
                }.await()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            val result = ArrayList<MovieDetail>()

            upComingMovies?.let { res ->
                result.addAll(res.results.entityToDetail())

                var currentPage = 1
                while(currentPage < expectingDownloadedPageNum.coerceAtMost(res.total_pages)) {
                    ++currentPage

                    result.addAll(
                        try {
                            CoroutineScope(Dispatchers.IO).async {
                                tmdb.getUpcoming(currentPage)
                            }.await()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            UpcomingMoviesResFromNetwork(1, listOf(), 1, 0)
                        }.results.entityToDetail()
                    )
                }
                emit(result)
            }
        }

    }
    fun getUpcomingMovies(): Flow<List<MovieDetail>> {
        return movies
    }

    private var expectingDownloadedPageNum = 1
    fun downloadMoreMovies() {
        expectingDownloadedPageNum ++
    }
}