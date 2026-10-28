package com.example.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthenticationManager"

enum class UserRole(val label: String, val badge: String) {
    CONSUMER("Consumer / Buyer", "🍏"),
    GROWER("Local Grower / Farmer", "🚜")
}

data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val role: UserRole
)

sealed interface AuthState {
    data object Initializing : AuthState
    data object Unauthenticated : AuthState
    data object Loading : AuthState
    data class Authenticated(val profile: UserProfile) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthenticationManager(
    private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val credentialManager = CredentialManager.create(context)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var currentRole: UserRole = UserRole.CONSUMER

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            _authState.value = AuthState.Authenticated(
                UserProfile(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName ?: if (currentRole == UserRole.GROWER) "Highland Grower" else "Local Consumer",
                    photoUrl = user.photoUrl?.toString(),
                    role = currentRole
                )
            )
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun getRole(): UserRole = currentRole

    fun switchRole(newRole: UserRole) {
        currentRole = newRole
        val user = auth.currentUser
        if (user != null) {
            _authState.value = AuthState.Authenticated(
                UserProfile(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName ?: if (newRole == UserRole.GROWER) "Highland Grower" else "Local Consumer",
                    photoUrl = user.photoUrl?.toString(),
                    role = newRole
                )
            )
        }
    }

    /**
     * Interactive Google Sign-In via Jetpack Credential Manager.
     */
    fun signInWithGoogle(
        activity: Activity,
        role: UserRole,
        onSuccess: (UserProfile) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        currentRole = role
        _authState.value = AuthState.Loading

        coroutineScope.launch {
            try {
                val serverClientId = context.getString(R.string.default_web_client_id)
                val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = activity,
                    request = request
                )

                val customCredential = result.credential as? CustomCredential
                if (customCredential?.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.data)
                    val idToken = googleIdTokenCredential.idToken
                    signInWithGoogleIdToken(idToken, role, onSuccess, onError)
                } else {
                    val errorMsg = "Unsupported credential format"
                    Log.e(TAG, errorMsg)
                    _authState.value = AuthState.Error(errorMsg)
                    onError(errorMsg)
                }
            } catch (e: GetCredentialCancellationException) {
                Log.d(TAG, "Sign-in cancelled by user")
                _authState.value = AuthState.Unauthenticated
            } catch (e: GetCredentialException) {
                val errorMsg = "Credential Manager error: ${e.message}"
                Log.e(TAG, errorMsg, e)
                _authState.value = AuthState.Error(errorMsg)
                onError(errorMsg)
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Unexpected sign-in error"
                Log.e(TAG, "Sign in failed", e)
                _authState.value = AuthState.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    /**
     * Firebase Auth sign-in with Google Id Token.
     */
    fun signInWithGoogleIdToken(
        idToken: String,
        role: UserRole,
        onSuccess: (UserProfile) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        currentRole = role
        _authState.value = AuthState.Loading

        coroutineScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user
                if (user != null) {
                    val profile = UserProfile(
                        uid = user.uid,
                        email = user.email,
                        displayName = user.displayName ?: if (role == UserRole.GROWER) "Verified Farm Grower" else "Conscious Consumer",
                        photoUrl = user.photoUrl?.toString(),
                        role = role
                    )
                    _authState.value = AuthState.Authenticated(profile)
                    onSuccess(profile)
                } else {
                    val errorMsg = "Firebase user is null after sign in"
                    _authState.value = AuthState.Error(errorMsg)
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Firebase credential authentication failed"
                Log.e(TAG, "Firebase auth error", e)
                _authState.value = AuthState.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    /**
     * Sign out current user.
     */
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
