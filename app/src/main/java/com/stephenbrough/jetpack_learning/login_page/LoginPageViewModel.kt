package com.stephenbrough.jetpack_learning.login_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenbrough.jetpack_learning.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginFormViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _state = MutableStateFlow(LoginFormState())
    val state = _state.asStateFlow()

    fun login(email: LoginForm.Email, password: LoginForm.Password) {
        _state.update { it.copy(error = null) } // Clear any login errors
        if (!email.isValid()) {
            println("Invalid email")
            _state.update {
                it.copy(emailValidationError = "Invalid email", emailError = true)
            }
        }

        if (!password.isValid()) {
            println("Invalid password")
            _state.update {
                it.copy(passwordValidationError = "Invalid password")
            }
        }

        if (email.isValid() && password.isValid()) {
            println("Logging in...")
            // No errors, make the request
            viewModelScope.launch {
                val loginResult = authRepository.login(email.email, password.password)
                if (loginResult.isSuccess) {
                    println("Success")
                    _state.update { it.copy(isLoggedIn = true) }
                } else {
                    _state.update { it.copy(error = loginResult.exceptionOrNull()?.message) }
                }
            }
        }
    }

    fun clearPasswordError() {
        if (_state.value.passwordValidationError != null)
            _state.update { it.copy(passwordValidationError = null) }
    }

    fun checkEmail(email: LoginForm.Email) {
        if (!_state.value.emailError) return
        when {
            email.isValid() && _state.value.emailValidationError != null -> {
                _state.update { it.copy(emailValidationError = null) }
            }

            !email.isValid() && _state.value.emailValidationError == null -> {
                _state.update { it.copy(emailValidationError = "Invalid email") }
            }
        }
    }
}

data class LoginFormState(
    val email: LoginForm.Email? = null,
    val password: LoginForm.Password? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailValidationError: String? = null,
    // After first failed attempt this will always be true; user should know that a proper email is
    // required at that point so we show the error as long as the entered email is invalid
    val emailError: Boolean = false,
    val passwordValidationError: String? = null,
    val isLoggedIn: Boolean = false
)


object LoginForm {
    @JvmInline
    value class Email(val email: String) {
        fun isValid(): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }
    }

    @JvmInline
    value class Password(val password: String) {
        fun isValid(): Boolean {
            return password.length >= 8
        }
    }
}