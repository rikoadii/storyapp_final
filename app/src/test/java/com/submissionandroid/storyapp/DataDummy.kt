package com.submissionandroid.storyapp

import com.submissionandroid.storyapp.data.ListStoryItem
import com.submissionandroid.storyapp.data.StoryResponse

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                photoUrl = "https://picsum.photos/200/300",
                createdAt = "2021-08-15T00:00:00.000Z",
                name = "Story $i",
                description = "Description $i",
                lon = 0.0,
                lat = 0.0
            )
            items.add(story)
        }
        return items
    }
}