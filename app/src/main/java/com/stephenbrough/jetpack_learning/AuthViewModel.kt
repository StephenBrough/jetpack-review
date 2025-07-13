package com.stephenbrough.jetpack_learning

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenbrough.jetpack_learning.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
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

    val authState = authRepository.isLoggedIn()
        .map { isLoggedIn ->
            println("Collecting flow, isLoggedIn: $isLoggedIn")
            if (isLoggedIn) AuthState.LoggedIn else AuthState.LoggedOut
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            AuthState.Loading
        )
}

@Parcelize
sealed class AuthState : Parcelable {
    @Parcelize
    data object Loading : AuthState()

    @Parcelize
    data object LoggedIn : AuthState()

    @Parcelize
    data object LoggedOut : AuthState()

    @Parcelize
    data object Authenticated : AuthState() // Bio auth
}
