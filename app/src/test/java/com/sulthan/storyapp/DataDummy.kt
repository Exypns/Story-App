package com.sulthan.storyapp

import com.sulthan.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val item: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                i.toFloat(),
                i.toFloat()
            )
            item.add(story)
        }
        return item
    }
}