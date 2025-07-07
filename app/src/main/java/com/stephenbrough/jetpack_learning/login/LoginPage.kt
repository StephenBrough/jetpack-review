package com.stephenbrough.jetpack_learning.login

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope
import com.stephenbrough.jetpack_learning.util.AnimatedTextField
import kotlinx.coroutines.flow.takeWhile

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginPage(
    modifier: Modifier,
    viewModel: LoginFormViewModel = hiltViewModel(),
    onLoginClicked: () -> Unit
) {
    val pageState = viewModel.state.collectAsState()
    val sharedScope = LocalNavSharedTransitionScope.current

    val emailValue = rememberTextFieldState(initialText = "")
    val passwordValue = rememberTextFieldState()
    var showPassword by remember { mutableStateOf(false) }

    // Email error handling
    LaunchedEffect(emailValue.text) {
        snapshotFlow { emailValue.text.toString() }.takeWhile { it.isNotBlank() }
            .collect { viewModel.checkEmail(LoginForm.Email(it)) }
    }

    // Password error handling
    LaunchedEffect(passwordValue.text) {
        snapshotFlow { passwordValue.text.toString() }
            .collect { viewModel.clearPasswordError() }
    }

    Column(
        modifier = modifier
            .padding(64.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.weight(1f))

        with(sharedScope) {
            Text(
                "Jetpack Compose Playground",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.sharedElement(
                    sharedScope.rememberSharedContentState("title"),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            state = emailValue,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = pageState.value.emailValidationError?.isNotBlank() ?: false,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        )
        AnimatedTextField(
            errorMessage = pageState.value.emailValidationError,
        )
        OutlinedSecureTextField(
            state = passwordValue,
            textObfuscationMode = if (showPassword) TextObfuscationMode.Visible else TextObfuscationMode.Hidden,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            isError = pageState.value.passwordValidationError?.isNotBlank() ?: false,
            leadingIcon = { Icon(Icons.Default.Password, contentDescription = null) },
            trailingIcon = {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        onClick = { showPassword = !showPassword }
                    )
                )
            }

        )
        AnimatedTextField(
            errorMessage = pageState.value.passwordValidationError,
        )
        Spacer(modifier = Modifier.height(50.dp))

        OutlinedButton(
            onClick = {
//                onLoginClicked()
                viewModel.login(
                    email = LoginForm.Email(emailValue.text.toString()),
                    password = LoginForm.Password(passwordValue.text.toString())
                )
            },
            modifier = Modifier.fillMaxWidth(),
            content = { Text("Login") },
            contentPadding = PaddingValues(15.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(4.dp),
        )

        Spacer(modifier = Modifier.weight(2f))

    }
}

//
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    JetpacklearningTheme {
//        Greeting("Android")
//    }
//}