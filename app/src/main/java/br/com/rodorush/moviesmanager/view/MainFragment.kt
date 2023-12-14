package br.com.rodorush.moviesmanager.view

import MovieDiffCallback
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.rodorush.moviesmanager.controller.MovieViewModel
import br.com.rodorush.moviesmanager.R
import br.com.rodorush.moviesmanager.databinding.FragmentMainBinding
import br.com.rodorush.moviesmanager.view.adapter.MovieAdapter
import br.com.rodorush.moviesmanager.view.adapter.OnMovieClickListener
import br.com.rodorush.moviesmanager.model.entity.Movie
import br.com.rodorush.moviesmanager.model.enumerator.ActionType
import br.com.rodorush.moviesmanager.model.enumerator.SortType

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

    // ViewModel
    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModel.MovieViewModelFactory
    }

    private var currentSortType: SortType = SortType.BY_NAME

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        movieViewModel.moviesMld.observe(requireActivity()) { movies ->
            movieList.clear()
            movies.forEachIndexed { index, movie ->
                movieList.add(movie)
                moviesAdapter.notifyItemChanged(index)
            }
        }

//        movieViewModel.moviesMld.observe(requireActivity()) { movies ->
//            movieList.clear()
//            movieList.addAll(getSortedMovies())
//            moviesAdapter.notifyDataSetChanged()
//        }

        movieViewModel.getMovies()
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
                    MainFragmentDirections.actionMainFragmentToMovieFragment(ActionType.INSERT, null)
                )
            }

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
                                movieViewModel.editMovie(receivedMovie)
                                movieList[position] = receivedMovie
                                moviesAdapter.notifyItemChanged(position)
                            } else {
                                movieViewModel.insertMovie(receivedMovie)
                                movieList.add(receivedMovie)
                                moviesAdapter.notifyItemInserted(movieList.lastIndex)
                            }
                        }
                    }

                    // Hiding soft keyboard
                    (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        fmb.root.windowToken,
                        HIDE_NOT_ALWAYS
                    )
                }
            }
        }

        return fmb.root
    }

    override fun onMovieClick(position: Int) = navigateToMovieFragment(position, ActionType.VIEW)

    override fun onRemoveMovieMenuItemClick(position: Int) {
        movieViewModel.removeMovie(movieList[position])
        movieList.removeAt(position)
        moviesAdapter.notifyItemRemoved(position)
    }

    override fun onEditMovieMenuItemClick(position: Int) = navigateToMovieFragment(position, ActionType.EDIT)

    override fun onWatchedCheckBoxClick(position: Int, checked: Boolean) {
        movieList[position].apply {
            watched = checked
            movieViewModel.editMovie(this)
        }
    }

//    override fun onRatingBarClick(position: Int, rating: Float) {
//        val convertedRating = rating * 5.0
//        Toast.makeText(requireContext(), "Rating: $convertedRating", Toast.LENGTH_SHORT).show()
//    }

    private fun navigateToMovieFragment(position: Int, actionType: ActionType) {
        movieList[position].also {
            navController.navigate(
                MainFragmentDirections.actionMainFragmentToMovieFragment(actionType, it)
            )
        }
    }

    private fun getSortedMovies(): List<Movie> {
        return when (currentSortType) {
            SortType.BY_NAME -> movieList.sortedBy { it.name }
            SortType.BY_RATING -> movieList.sortedByDescending { it.rating }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sortByName -> {
                currentSortType = SortType.BY_NAME
                val sortedMovies = getSortedMovies()
                val diffResult = DiffUtil.calculateDiff(MovieDiffCallback(movieList, sortedMovies))
                movieList.clear()
                movieList.addAll(sortedMovies)
                diffResult.dispatchUpdatesTo(moviesAdapter)
                true
            }
            R.id.sortByRating -> {
                currentSortType = SortType.BY_RATING
                val sortedMovies = getSortedMovies()
                val diffResult = DiffUtil.calculateDiff(MovieDiffCallback(movieList, sortedMovies))
                movieList.clear()
                movieList.addAll(sortedMovies)
                diffResult.dispatchUpdatesTo(moviesAdapter)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}