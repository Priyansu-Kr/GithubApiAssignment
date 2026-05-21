package com.example.githubassignment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.example.githubassignment.databinding.ActivityMainBinding
import com.example.githubassignment.ui.adapter.GithubAdapter
import com.example.githubassignment.ui.screens.UiState
import com.example.githubassignment.ui.vm.RepoViewModel
import com.example.githubassignment.utils.ConnectivityObserver
import com.example.githubassignment.utils.NetworkConnectivityObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityObserver: ConnectivityObserver
    private var lastRefreshTime = 0L
    private val refresh_difference = 30_000L
    
    private val githubAdapter by lazy {
        GithubAdapter { item ->
            viewModel.updateFavourite(item.id.toString())
            val message = if (item.isFavourite) "Removed from favorites" else "Added to favorites"
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private val viewModel: RepoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        observeConnectivity()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.btnRetry.setOnClickListener {
            viewModel.retry()
        }
        binding.swipeRefresh.setOnRefreshListener {

            val currentTime = System.currentTimeMillis()

            if (currentTime - lastRefreshTime < refresh_difference) {
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(
                    this,
                    "Please wait 30s before refreshing again",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnRefreshListener
            }
            lastRefreshTime = currentTime
            viewModel.manualRefresh()
        }
        binding.ivSearchIcon.setOnClickListener {
            binding.defaultToolbar.visibility = View.GONE
            binding.searchToolbar.visibility = View.VISIBLE
            binding.searchToolbar.requestFocus()
            binding.etSearch.text.clear()
        }
        binding.ivSearchBack.setOnClickListener {
            binding.defaultToolbar.visibility = View.VISIBLE
            binding.searchToolbar.visibility = View.GONE
            viewModel.closeSearch()

        }
        binding.etSearch.addTextChangedListener { text ->
            viewModel.filterList(text.toString())
        }
    }

    private fun setupRecyclerView() {
        binding.rvGithub.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = githubAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listData.collect { state ->
                    binding.swipeRefresh.isRefreshing = false
                    when (state) {
                        is UiState.Loading -> {
                            binding.shimmerLyt.visibility = View.VISIBLE
                            binding.shimmerLyt.startShimmer()
                            binding.rvGithub.visibility = View.GONE
                            binding.errorLayout.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.shimmerLyt.stopShimmer()
                            binding.shimmerLyt.visibility = View.GONE
                            binding.rvGithub.visibility = View.VISIBLE
                            binding.errorLayout.visibility = View.GONE
                            githubAdapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            binding.shimmerLyt.stopShimmer()
                            binding.shimmerLyt.visibility = View.GONE
                            binding.rvGithub.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = state.message
                        }
                    }
                }
            }
        }
    }

    private var previousStatus: ConnectivityObserver.Status? = null

    private fun observeConnectivity() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                connectivityObserver.observe().collect { status ->
                    if (previousStatus == null) {
                        if (status != ConnectivityObserver.Status.Available) {
                            showConnectivityBar("Looks like we're offline", R.color.red, false)
                        }
                    } else if (previousStatus != status) {
                        when (status) {
                            ConnectivityObserver.Status.Available -> {
                                showConnectivityBar("Connected", R.color.green, true)
                            }

                            ConnectivityObserver.Status.Losing ->{
                                showConnectivityBar("Connection Losing",R.color.yellow,true)
                            }
                            else -> {
                                showConnectivityBar("Looks like we're offline", R.color.red, false)
                            }
                        }
                    }
                    previousStatus = status
                }
            }
        }
    }

    private fun showConnectivityBar(message: String, colorRes: Int, autoHide: Boolean) {
        binding.tvConnectivityStatus.apply {
            text = message
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, colorRes))
            visibility = View.VISIBLE
        }
        if (autoHide) {
            lifecycleScope.launch {
                delay(3000)
                binding.tvConnectivityStatus.visibility = View.GONE
            }
        }
    }
}
