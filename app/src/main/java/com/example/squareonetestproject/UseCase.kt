package com.example.squareonetestproject

import kotlinx.coroutines.flow.Flow


class UseCase {
    private val repo = MovieRepository()

    fun getMovies(): Flow<List<MovieDetail>> {
        return repo.getUpcomingMovies()
    }

}