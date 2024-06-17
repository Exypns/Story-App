package com.sulthan.storyapp.ui.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sulthan.storyapp.R
import com.sulthan.storyapp.databinding.ActivityHomeBinding
import com.sulthan.storyapp.ui.helper.ListStoryAdapter
import com.sulthan.storyapp.ui.helper.StoryViewModelFactory
import com.sulthan.storyapp.ui.helper.UserViewModelFactory
import com.sulthan.storyapp.ui.viewmodel.StoryViewModel
import com.sulthan.storyapp.ui.viewmodel.UserViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val userViewModel by viewModels<UserViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    private val storyViewModel by viewModels<StoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))

        userViewModel.getSession().observe(this) { result ->
            if (result?.isEmpty() == true) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                setStory()
            }
            }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        addStory()
    }

    override fun onResume() {
        super.onResume()
        setStory()
    }

    private fun setStory() {
        val adapter = ListStoryAdapter()
        binding.rvStory.adapter = adapter
        storyViewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_menu -> userViewModel.logout()
            R.id.language_menu -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.map_menu -> startActivity(Intent(this, MapsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        binding.addStoryButton.setOnClickListener {
            startActivity(intent)
        }
    }
}