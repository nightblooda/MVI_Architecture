package com.example.mviexample.ui.main.state

import com.example.mviexample.model.BlogPost
import com.example.mviexample.model.User

data class MainViewState(

    var blogPosts: List<BlogPost>? = null,

    var user: User? = null

)