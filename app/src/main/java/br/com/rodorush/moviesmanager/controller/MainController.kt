package br.com.rodorush.moviesmanager.controller

import androidx.room.Room
import br.com.rodorush.moviesmanager.model.database.MoviesManagerDatabase
import br.com.rodorush.moviesmanager.model.entity.Movie
import br.com.rodorush.moviesmanager.view.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainController(private val mainFragment: MainFragment) {
    private val movieDaoImpl = Room.databaseBuilder(
        mainFragment.requireContext(),
        MoviesManagerDatabase::class.java,
        MoviesManagerDatabase.MOVIES_MANAGER_DATABASE
    ).build().getMovieDao()

    fun insertMovie(movie: Movie) {
        CoroutineScope(Dispatchers.IO).launch {
            movieDaoImpl.createMovie(movie)
        }
    }

    fun getMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = movieDaoImpl.retrieveMovies()
            mainFragment.updateMovieList(movies)
        }
    }

    fun editMovie(movie: Movie) {
        CoroutineScope(Dispatchers.IO).launch {
            movieDaoImpl.updateMovie(movie)
        }
    }

    fun removeMovie(movie: Movie) {
        CoroutineScope(Dispatchers.IO).launch {
            movieDaoImpl.deleteMovie(movie)
        }
    }
}