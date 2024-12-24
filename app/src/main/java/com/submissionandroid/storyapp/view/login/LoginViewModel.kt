package com.submissionandroid.storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submissionandroid.storyapp.data.LoginResponse
import com.submissionandroid.storyapp.data.UserRepository
import com.submissionandroid.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String, onSuccess: (LoginResponse) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                response.loginResult?.let {
                    repository.saveSession(UserModel(it.name ?: "", it.token ?: ""))
                }
                onSuccess(response)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}