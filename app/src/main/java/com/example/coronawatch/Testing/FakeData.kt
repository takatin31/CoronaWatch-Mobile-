package com.example.coronawatch.Testing

import com.example.coronawatch.DataClasses.Article
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.DataClasses.Comment

object FakeData {
    val thumbnailArticles = arrayOf(
        ArticleThumbnail(1, "www.image.com", "titre2", "sous_titre2", "2020-06-03 14:26",
        arrayListOf("flutter", "corona"), 0),
        ArticleThumbnail(2, "www.image.com", "titre1", "sous_titre1", "2020-06-03 14:26",
            arrayListOf("flutter", "corona"), 0)
    )

    val article = Article(2, "www.image.com", "", "titre1", "sous_titre1", "03-06-2020 14:26",
        arrayListOf("flutter", "corona"), 0)

    val comments = arrayOf(
        Comment(1, "this is test comment 1", "2020-06-01 14:00", "Ali Cherif", "www.image.com"),
        Comment(2, "this is test comment 2", "2020-06-03 14:24", "Ali Cherif", "www.image.com")
    )
}