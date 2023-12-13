package br.com.rodorush.moviesmanager.controller

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Room
import br.com.rodorush.moviesmanager.model.database.MoviesManagerDatabase
import br.com.rodorush.moviesmanager.model.entity.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MovieViewModel(application: Application): ViewModel() {
    private val movieDaoImpl = Room.databaseBuilder(
        application.applicationContext,
        MoviesManagerDatabase::class.java,
        MoviesManagerDatabase.MOVIES_MANAGER_DATABASE
    ).build().getMovieDao()

    val moviesMld = MutableLiveData<List<Movie>>()

    fun insertMovie(movie: Movie) {
        CoroutineScope(Dispatchers.IO).launch {
            movieDaoImpl.createMovie(movie)
        }
    }

    fun getMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = movieDaoImpl.retrieveMovies()
            moviesMld.postValue(movies)
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

    suspend fun isMovieNameUnique(movieName: String): Boolean {
        return CoroutineScope(Dispatchers.IO).run {
            val existingMovie = movieDaoImpl.retrieveMovieByName(movieName)
            existingMovie == null
        }
    }

    companion object {
        val MovieViewModelFactory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                MovieViewModel(checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])) as T
        }
    }
}