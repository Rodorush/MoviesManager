package br.com.rodorush.moviesmanager.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.rodorush.moviesmanager.R
import br.com.rodorush.moviesmanager.databinding.FragmentMovieBinding
import br.com.rodorush.moviesmanager.model.entity.Genre
import br.com.rodorush.moviesmanager.view.MainFragment.Companion.EXTRA_MOVIE
import br.com.rodorush.moviesmanager.view.MainFragment.Companion.MOVIE_FRAGMENT_REQUEST_KEY
import br.com.rodorush.moviesmanager.model.entity.Movie

class MovieFragment : Fragment() {
    private lateinit var ftb: FragmentMovieBinding
    private val navigationArgs: MovieFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            getString(R.string.movie_details)

        ftb = FragmentMovieBinding.inflate(inflater, container, false)

        val receivedMovie = navigationArgs.movie
        receivedMovie?.also { movie ->
            with(ftb) {
                nameEt.setText(movie.name)
                releaseYearEt.setText(movie.releaseYear.toString())
                studioEt.setText(movie.studio)
                durationEt.setText(movie.watched.toString())
                watchedCb.isChecked = movie.watched == true
                ratingEt.setText(movie.rating.toString())
                genreEt.setText(movie.genre.toString())
                navigationArgs.editMovie.also { editMovie ->
                    nameEt.isEnabled = editMovie
                    releaseYearEt.isEnabled = editMovie
                    studioEt.isEnabled = editMovie
                    durationEt.isEnabled = editMovie
                    watchedCb.isEnabled = editMovie
//                    ratingEt.isEnabled = editMovie
                    genreEt.isEnabled = editMovie
                    saveBt.visibility = if (editMovie) VISIBLE else GONE
                }
            }
        }

        ftb.run {
            saveBt.setOnClickListener {
                setFragmentResult(MOVIE_FRAGMENT_REQUEST_KEY, Bundle().apply {
                    putParcelable(
                        EXTRA_MOVIE, Movie(
                            nameEt.text.toString(),
                            Integer.parseInt(releaseYearEt.text.toString()),
                            studioEt.text.toString(),
                            Integer.parseInt(durationEt.text.toString()),
                            watchedCb.isChecked,
                            java.lang.Double.parseDouble(ratingEt.text.toString()),
                            genreEt.text.toString()
                        )
                    )
                })
                findNavController().navigateUp()
            }
        }

        return ftb.root
    }
}