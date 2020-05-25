package com.example.coronawatch.Interfaces

interface Commentable {

    fun getComments(itemId : Int)

    fun addComment(content : String, userId : Int, itemId : Int)
}