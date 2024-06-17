package com.sulthan.storyapp.ui.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sulthan.storyapp.R
import com.sulthan.storyapp.data.ResultState
import com.sulthan.storyapp.databinding.ActivityDetailStoryBinding
import com.sulthan.storyapp.ui.helper.StoryViewModelFactory
import com.sulthan.storyapp.ui.viewmodel.StoryViewModel

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val storyViewModel by viewModels<StoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue)))
        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story_id = intent.getStringExtra(STORY_ID)

        story_id?.let { id ->
            storyViewModel.getDetailStory(id).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        showLoading(false)
                        val detailStory = result.data.story
                        Log.d("Detail Story Success: ", result.data.message.toString())
                        Glide.with(binding.storyImage.context).load(detailStory?.photoUrl).into(binding.storyImage)
                        binding.storyName.text = detailStory?.name
                        binding.storyDescription.text = detailStory?.description
                    }
                    is ResultState.Error -> {
                        Log.e("Detail Story Error:", result.error)
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val STORY_ID = "EXTRAS_ID"
    }
}