package com.example.mviexample.ui

import com.example.mviexample.util.DataState

interface DataStateListener {

    fun onDataStateChange(dataState: DataState<*>?)
}