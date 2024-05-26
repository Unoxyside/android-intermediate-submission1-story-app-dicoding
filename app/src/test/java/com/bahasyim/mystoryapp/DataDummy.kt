package com.bahasyim.mystoryapp

import com.bahasyim.mystoryapp.data.api.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..50) {
            val story = ListStoryItem(
                "id $i",
                "name",
                "description",
                "https://img.freepik.com/free-vector/gradient-mountain-landscape_23-2149159772.jpg?w=1380&t=st=1716615493~exp=1716616093~hmac=17628c4c569b2a139b35dca306d35752e488b32b259e984e5ad632e59f9ccde2",
                "2024-01-22T22:22:22Z",
                null,
                null,
            )
            items.add(story)
        }
        return items
    }
}