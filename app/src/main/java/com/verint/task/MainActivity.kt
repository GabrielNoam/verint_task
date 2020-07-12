package com.verint.task

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.verint.task.api.MovieApi.Companion.openMovie
import com.verint.task.api.Resource
import com.verint.task.database.MovieDatabase
import com.verint.task.model.Movie
import com.verint.task.model.Search
import com.verint.task.model.Status
import com.verint.task.presentation.MovieAdapter
import com.verint.task.presentation.MovieViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainActivity
 *
 * @author Gabriel Noam
 */
class MainActivity  : AppCompatActivity() {

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
    private var liveData: LiveData<Resource<List<Movie>>>? = null
    private lateinit var db: MovieDatabase
    private lateinit var viewModel: MovieViewModel
    private lateinit var gridViewAdapter: MovieAdapter
    private var movies: MutableList<Movie> = mutableListOf()
    private val search: Search = Search("", 0, System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.IO) {
            db = MovieDatabase.getInstance(this@MainActivity)
            withContext(Dispatchers.Main) {
                initView()
                initButtons()
            }
        }
    }

    private fun initButtons() {
        nextTextView.setOnClickListener {
            search.also {
                it.page++
                it.timestamp = System.currentTimeMillis()
            }

            submitSearch()
        }

        prevTextView.setOnClickListener {
            if(search.page > 1) {
                search.also {
                    it.page--
                    it.timestamp = System.currentTimeMillis()
                }

                submitSearch()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        initMenu(menu)

        return true
    }

    private fun initMenu(menu: Menu) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = "Search View"
            isSubmitButtonEnabled = query.length >= MIN_QUERY_LENGTH
            setIconifiedByDefault(true)
            setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if(query.length <= MIN_QUERY_LENGTH) showStatus(getString(R.string.short_query)) else {
                        @Suppress("DEPRECATION")
                        // would use onActionViewCollapsed() instead if it was working as should
                        MenuItemCompat.collapseActionView(menu.findItem(R.id.search))
                    }
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    isSubmitButtonEnabled = query.length > MIN_QUERY_LENGTH
                    if(query.length > MIN_QUERY_LENGTH) {
                        search.also {
                            it.key = query
                            it.page = 1
                            it.timestamp = System.currentTimeMillis()
                        }

                        submitSearch()
                     }
                    return true
                }
            })
        }
    }

    private fun submitSearch() {
        liveData?.removeObserver(observer)
        liveData = viewModel.getMovies(search)
        liveData?.observeOnce(this@MainActivity, observer)
    }

    private suspend fun initView() {
        gridViewAdapter = MovieAdapter(this, movies)

        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)

        gridView.apply {
            adapter = gridViewAdapter
            layoutAnimation = animation
            onItemClickListener =
                AdapterView.OnItemClickListener{ _, _, position, _ ->
                    openMovie(this@MainActivity, movies[position].imdbID) };
        }

        viewModel = ViewModelProviders.of(this).get(MovieViewModel::class.java)
        liveData = viewModel.retrieveMovies()
        liveData?.observeOnce(this@MainActivity, observer)
    }

    private val observer =
        Observer<Resource<List<Movie>>> { resource ->
            when (resource.status) {
                Status.LOADING -> showStatus(getString(R.string.loading))
                Status.LOCAL, Status.REMOTE -> {
                    resource.data?.let {
                        movies.clear()
                        movies.addAll(it)
                        if(it.isEmpty()) {
                            showStatus(getString(R.string.no_results))
                            setTitle(R.string.no_results)
                            pagingLayout.visibility = View.INVISIBLE
                        } else {
                            search.page = movies[0].page
                            search.key = movies[0].key
                            val message = "Results for '${search.key}'"
                            title = message
                            showStatus(message)
                            pageTextView.text = "Page ${search.page} out of ${Resource.total/10}"
                            pagingLayout.visibility = View.VISIBLE
                        }
                    }

                    gridViewAdapter.notifyDataSetChanged()
                }
                Status.ERROR -> showStatus(resource.message?: getString(R.string.error) )
            }
        }


    private fun showStatus(message: String) =
        Snackbar.make(pageTextView,
            "$message", Snackbar.LENGTH_SHORT).show()
}