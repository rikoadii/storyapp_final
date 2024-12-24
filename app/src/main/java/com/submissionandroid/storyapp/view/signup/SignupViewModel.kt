package com.submissionandroid.storyapp.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submissionandroid.storyapp.data.RegisterResponse
import com.submissionandroid.storyapp.data.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    fun register(name: String, email: String, password: String, onSuccess: (RegisterResponse) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                onSuccess(response)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
