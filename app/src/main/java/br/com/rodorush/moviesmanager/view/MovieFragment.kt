package br.com.rodorush.moviesmanager.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.rodorush.moviesmanager.R
import br.com.rodorush.moviesmanager.controller.MovieViewModel
import br.com.rodorush.moviesmanager.databinding.FragmentMovieBinding
import br.com.rodorush.moviesmanager.model.entity.Genre
import br.com.rodorush.moviesmanager.view.MainFragment.Companion.EXTRA_MOVIE
import br.com.rodorush.moviesmanager.view.MainFragment.Companion.MOVIE_FRAGMENT_REQUEST_KEY
import br.com.rodorush.moviesmanager.model.entity.Movie
import br.com.rodorush.moviesmanager.model.enumerator.ActionType
import kotlinx.coroutines.launch

class MovieFragment : Fragment() {
    private lateinit var ftb: FragmentMovieBinding
    private val navigationArgs: MovieFragmentArgs by navArgs()

    // ViewModel
    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModel.MovieViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            getString(R.string.movie_details)

        ftb = FragmentMovieBinding.inflate(inflater, container, false)

        val genreAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Genre.values())
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ftb.genreSpinner.adapter = genreAdapter

        val receivedActionType = navigationArgs.actionType
        receivedActionType.also { actionType ->
            with(ftb) {
                navigationArgs.movie?.also { movie ->
                    nameEt.setText(movie.name)
                    releaseYearEt.setText(movie.releaseYear.toString())
                    studioEt.setText(movie.studio)
                    durationEt.setText(movie.duration.toString())
                    watchedCb.isChecked = movie.watched
                    ratingBar.rating = (movie.rating / 2.0).toFloat()
                    val genreList = Genre.entries.map { it.name }
                    val genrePosition = genreList.indexOf(movie.genre)
                    genreSpinner.setSelection(genrePosition)                }
                nameTv.visibility = if (actionType != ActionType.INSERT) VISIBLE else GONE
                nameEt.isEnabled = actionType == ActionType.INSERT
                releaseYearTv.visibility = if (actionType != ActionType.INSERT) VISIBLE else GONE
                releaseYearEt.isEnabled = actionType != ActionType.VIEW
                studioTv.visibility = if (actionType != ActionType.INSERT) VISIBLE else GONE
                studioEt.isEnabled = actionType != ActionType.VIEW
                durationTv.visibility = if (actionType != ActionType.INSERT) VISIBLE else GONE
                durationEt.isEnabled = actionType != ActionType.VIEW
                watchedCb.isEnabled = actionType != ActionType.VIEW
                ratingTv.visibility = if (actionType != ActionType.INSERT) VISIBLE else GONE
                ratingBar.isEnabled = actionType != ActionType.VIEW
                genreTv.visibility = if (actionType != ActionType.INSERT) VISIBLE else GONE
                genreSpinner.isEnabled = actionType != ActionType.VIEW
                saveBt.visibility = if (actionType != ActionType.VIEW) VISIBLE else GONE
            }
        }

        ftb.run {
            saveBt.setOnClickListener {
                val movieName = nameEt.text.toString().trim()
                val releaseYearText = releaseYearEt.text.toString().trim()
                val selectedGenre = (genreSpinner.selectedItem as Genre).name

                movieName.takeIf { it.isEmpty() }?.let {
                    Toast.makeText(requireContext(), "Movie Name is required!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                releaseYearText.takeIf { it.isEmpty() }?.let {
                    Toast.makeText(
                        requireContext(),
                        "Release Year is required!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

//                genre.takeIf { it.isEmpty() }?.let {
//                    Toast.makeText(requireContext(), "Genre is required!", Toast.LENGTH_SHORT)
//                        .show()
//                    return@setOnClickListener
//                }

                lifecycleScope.launch {
                    if (!movieViewModel.isMovieNameUnique(movieName) && navigationArgs.actionType == ActionType.INSERT) {
                        Toast.makeText(
                            requireContext(),
                            "Movie with the same name already exists!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    setFragmentResult(MOVIE_FRAGMENT_REQUEST_KEY, Bundle().apply {
                        putParcelable(
                            EXTRA_MOVIE, Movie(
                                nameEt.text.toString(),
                                Integer.parseInt(releaseYearEt.text.toString()),
                                studioEt.text.toString(),
                                durationEt.text.toString().toIntOrNull() ?: 0,
                                watchedCb.isChecked,
                                ratingBar.rating.toDouble() * 2.0,
                                selectedGenre
                            )
                        )
                    })
                    findNavController().navigateUp()
                }
            }
        }

        return ftb.root
    }
}