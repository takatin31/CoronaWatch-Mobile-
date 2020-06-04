package com.example.coronawatch.DataClasses

import android.net.Uri
import java.net.URI

class Article (val id : Int, val image : String, val video : String, val title : String, val description : String, val date : String,  val tags : ArrayList<String>, val nbrComments : Int){
}