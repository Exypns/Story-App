package com.sulthan.storyapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sulthan.storyapp.component.EmailEditText
import com.sulthan.storyapp.component.PasswordEditText
import com.sulthan.storyapp.component.SubmitButton
import com.sulthan.storyapp.data.ResultState
import com.sulthan.storyapp.data.remote.repository.UserRepository
import com.sulthan.storyapp.databinding.ActivitySignupBinding
import com.sulthan.storyapp.di.Injection

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private lateinit var signupButton: SubmitButton
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText

    private lateinit var userRepository : UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        signupButton = binding.signupButton
        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText

        setButtonEnable()

        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    setButtonEnable()
                } else {
                    signupButton.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    setButtonEnable()
                } else {
                    signupButton.isEnabled = false
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
                    signupButton.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        userRepository = Injection.provideUserRepository(this)

        signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            register(name, email, password)
        }
    }

    private fun register(name: String, email: String, password: String) {
       userRepository.register(name, email, password).observe(this) { result ->
           when (result) {
               is ResultState.Loading -> {
                    showLoading(true)
               }
               is ResultState.Success -> {
                   showLoading(false)
                   AlertDialog.Builder(this).apply {
                       setTitle("User Registration")
                       Log.d("User Registration Success: ", result.data.message.toString())
                       setMessage(result.data.message)
                       setPositiveButton("Continue") { _, _ ->
                           val intent = Intent(this@SignupActivity, LoginActivity::class.java)
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
                       setTitle("User Registration")
                       Log.e("User Registration Error: ", result.error)
                       setMessage("Registration Failed: ${result.error}")
                       setPositiveButton("Try Again") { _, _ -> }
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
        val name = binding.nameEditText.text
        val email = emailEditText.text
        signupButton.isEnabled = name != null && name.toString().isNotEmpty() && email != null && email.toString().isNotEmpty()
    }
}