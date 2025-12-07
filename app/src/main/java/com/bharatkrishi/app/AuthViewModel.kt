package com.bharatkrishi.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        _user.value = auth.currentUser
        auth.addAuthStateListener { firebaseAuth ->
            _user.value = firebaseAuth.currentUser
        }
    }

    fun login(email: String, pass: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                _authState.value = AuthState.Success
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Login Failed")
            }
    }

    fun signup(email: String, pass: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                _authState.value = AuthState.Success
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Signup Failed")
            }
    }

    fun logout() {
        auth.signOut()
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
