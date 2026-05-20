package com.example.githubassignment.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubassignment.data.local.room.GithubRepoEntity
import com.example.githubassignment.data.repository.GithubRepository
import com.example.githubassignment.ui.screens.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val repo : GithubRepository
): ViewModel() {

    private val _listData = MutableStateFlow<UiState>(UiState.Loading)
    val listData : StateFlow<UiState> = _listData

    private var originalList = emptyList<GithubRepoEntity>()

    init {
        observeRepos()
        initialFetch()
    }

    private fun observeRepos() {
        viewModelScope.launch {
            repo.getRepoFromRoom().collect { list ->
                if (list.isNotEmpty()) {
                    originalList = list
                    _listData.value = UiState.Success(list)
                }
            }
        }
    }

    private fun initialFetch() {
        viewModelScope.launch {
            try {
                Log.d("mainActivity", "${repo.isCacheExpired()}" )
                if (repo.isCacheExpired()) {
                    _listData.value = UiState.Loading
                    repo.refreshRepos()
                }
            } catch (e: Exception) {
                Log.e("RepoViewModel", "Error in initial fetch", e)
                if (_listData.value !is UiState.Success) {
                    _listData.value = UiState.Error(e.message ?: "Failed to load data")
                }
            }
        }
    }

    fun manualRefresh() {
        viewModelScope.launch {
            try {
                _listData.value = UiState.Loading
                repo.refreshRepos()
            } catch (e: Exception) {
                Log.e("RepoViewModel", "Error in manual refresh", e)
                _listData.value = UiState.Error(e.message ?: "Failed to refresh data")
            }
        }
    }

    fun updateFavourite(itemId: String) {
        viewModelScope.launch {
            try {
                repo.updateFavourite(itemId.toLong())
            } catch (e: Exception) {
                Log.e("RepoViewModel", "Error updating favourite", e)
            }
        }
    }

    fun filterList(str :String){
        val filteredList = if (str.isBlank()) {
            originalList

        } else {
            originalList.filter { repo ->
            repo.name.contains(str, ignoreCase = true) ||
            repo.description?.contains(str, ignoreCase = true) == true ||
            repo.language?.contains(str, ignoreCase = true) == true
            }
        }
        _listData.value = UiState.Success(filteredList)
    }


    fun closeSearch(){
        _listData.value = UiState.Success(originalList)
    }

    fun retry() {
    manualRefresh()    }
}
