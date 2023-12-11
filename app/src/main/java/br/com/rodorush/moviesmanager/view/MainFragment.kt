package br.com.rodorush.moviesmanager.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.rodorush.moviesmanager.R
import br.com.rodorush.moviesmanager.databinding.FragmentMainBinding
import br.com.rodorush.moviesmanager.view.adapter.MovieAdapter
import br.com.rodorush.moviesmanager.view.adapter.OnMovieClickListener
import br.com.rodorush.moviesmanager.model.entity.Movie

class MainFragment : Fragment(), OnMovieClickListener {
    private lateinit var fmb: FragmentMainBinding

    // Data source
    private val movieList: MutableList<Movie> = mutableListOf()

    // Adapter
    private val moviesAdapter: MovieAdapter by lazy {
        MovieAdapter(movieList, this)
    }

    // Navigation controller
    private val navController: NavController by lazy {
        findNavController()
    }

    // Communication constants
    companion object {
        const val EXTRA_MOVIE = "EXTRA_MOVIE"
        const val MOVIE_FRAGMENT_REQUEST_KEY = "MOVIE_FRAGMENT_REQUEST_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(MOVIE_FRAGMENT_REQUEST_KEY) { requestKey, bundle ->
            if (requestKey == MOVIE_FRAGMENT_REQUEST_KEY) {
                val movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable(EXTRA_MOVIE, Movie::class.java)
                } else {
                    bundle.getParcelable(EXTRA_MOVIE)
                }
                movie?.also { receivedMovie ->
                    movieList.indexOfFirst { it.name == receivedMovie.name }.also { position ->
                        if (position != -1) {
                            movieList[position] = receivedMovie
                            moviesAdapter.notifyItemChanged(position)
                        } else {
                            movieList.add(receivedMovie)
                            moviesAdapter.notifyItemInserted(movieList.lastIndex)
                        }
                    }
                }

                // Hiding soft keyboard
                (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    fmb.root.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = getString(R.string.movie_list)

        fmb = FragmentMainBinding.inflate(inflater, container, false).apply {
            moviesRv.layoutManager = LinearLayoutManager(context)
            moviesRv.adapter = moviesAdapter

            addMovieFab.setOnClickListener {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToMovieFragment(null, editMovie = false)
                )
            }
        }

        return fmb.root
    }

    override fun onMovieClick(position: Int) = navigateToMovieFragment(position, false)

    override fun onRemoveMovieMenuItemClick(position: Int) {
        movieList.removeAt(position)
        moviesAdapter.notifyItemRemoved(position)
    }

    override fun onEditMovieMenuItemClick(position: Int) = navigateToMovieFragment(position, true)

    override fun onWatchedCheckBoxClick(position: Int, checked: Boolean) {
        movieList[position].apply {
            watched = checked
        }
    }

    private fun navigateToMovieFragment(position: Int, editMovie: Boolean) {
        movieList[position].also {
            navController.navigate(
                MainFragmentDirections.actionMainFragmentToMovieFragment(it, editMovie)
            )
        }
    }
}