package com.sulthan.storyapp.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sulthan.storyapp.databinding.ActivityMainBinding
import com.sulthan.storyapp.ui.helper.UserViewModelFactory
import com.sulthan.storyapp.ui.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<UserViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.getSession().observe(this) { result ->
            if (result?.isNotEmpty() == true) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            else {
                setContentView(binding.root)
            }
        }

        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
        binding.loginButton.setOnClickListener {
            startActivity(loginIntent)
        }

        val signupIntent = Intent(this@MainActivity, SignupActivity::class.java)
        binding.signupButton.setOnClickListener {
            startActivity(signupIntent)
        }
    }
}