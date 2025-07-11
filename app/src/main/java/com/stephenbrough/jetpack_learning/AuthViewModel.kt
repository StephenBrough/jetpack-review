package com.stephenbrough.jetpack_learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenbrough.jetpack_learning.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    val authState = flow {
        authRepository.isLoggedIn()
            .collect { isLoggedIn ->
                println("Collecting flow, isLoggedIn: $isLoggedIn")
                emit(if (isLoggedIn) AuthState.LoggedIn else AuthState.LoggedOut)
            }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        AuthState.Loading
    )
}

sealed class AuthState {
    object Loading : AuthState()
    object LoggedIn : AuthState()
    object LoggedOut : AuthState()

    object Authenticated : AuthState() // Bio auth
}