package com.sulthan.storyapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sulthan.storyapp.component.EmailEditText
import com.sulthan.storyapp.component.PasswordEditText
import com.sulthan.storyapp.component.SubmitButton
import com.sulthan.storyapp.data.ResultState
import com.sulthan.storyapp.data.remote.repository.UserRepository
import com.sulthan.storyapp.databinding.ActivityLoginBinding
import com.sulthan.storyapp.di.Injection
import com.sulthan.storyapp.ui.helper.UserViewModelFactory
import com.sulthan.storyapp.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginButton: SubmitButton
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText

    private lateinit var userRepository: UserRepository

    private val viewModel by viewModels<LoginViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        loginButton = binding.loginButton
        emailEditText = binding.emailTextInput
        passwordEditText = binding.passwordTextInput

        setButtonEnable()

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    setButtonEnable()
                } else {
                    loginButton.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length >= 8) {
                    setButtonEnable()
                } else {
                    loginButton.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        userRepository = Injection.provideUserRepository(this)

        loginButton.setOnClickListener {
            val email = binding.emailTextInput.text.toString()
            val password = binding.passwordTextInput.text.toString()
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        userRepository.login(email, password).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    lifecycleScope.launch {
                        viewModel.saveSession(result.data.loginResult?.token.toString())
                    }
                    AlertDialog.Builder(this).apply {
                        setTitle("User Login")
                        Log.d("Login Success: ", result.data.message.toString())
                        setMessage(result.data.message)
                        setPositiveButton("Continue") { _, _ ->
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("User Login")
                        Log.e("Login Error: ", result.error)
                        setMessage("Login Failed: ${result.error}")
                        setPositiveButton("Try Again") { _, _ ->
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setButtonEnable() {
        val email = emailEditText.text
        loginButton.isEnabled = email != null && email.toString().isNotEmpty()
    }
}