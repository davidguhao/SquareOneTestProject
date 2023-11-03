package com.example.squareonetestproject

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UseCase @Inject constructor() {
    private val repo = MovieRepository()

    fun getMovies(): Flow<List<MovieDetail>> {
        return repo.getUpcomingMovies()
    }

    fun loadMoreMovies() {
        repo.downloadMoreMovies()
    }

}