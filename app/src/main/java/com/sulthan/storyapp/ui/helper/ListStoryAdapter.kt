package com.sulthan.storyapp.ui.helper

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sulthan.storyapp.data.remote.response.ListStoryItem
import com.sulthan.storyapp.databinding.ItemStoryBinding
import com.sulthan.storyapp.ui.view.DetailStoryActivity

class ListStoryAdapter : PagingDataAdapter<ListStoryItem, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    class MyViewHolder(val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide.with(binding.storyImage.context).load(story.photoUrl).into(binding.storyImage)
            binding.storyName.text = story.name
            binding.storyDescription.text = story.description
            binding.root.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.root.context as Activity,
                        Pair(binding.storyImage, "storyImage"),
                        Pair(binding.storyName, "name"),
                        Pair(binding.storyDescription, "description")
                    )
                val intent = Intent(binding.root.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.STORY_ID, story.id)
                binding.root.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}