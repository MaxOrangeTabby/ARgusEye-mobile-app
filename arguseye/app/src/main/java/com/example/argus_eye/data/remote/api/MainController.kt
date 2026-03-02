package com.example.argus_eye.data.remote.api

import com.example.argus_eye.data.model.MainModel

class MainController(private val model: MainModel) {
    fun getAppName(): String {
        return model.title
    }

    fun getAppDescription(): String {
        return model.description
    }
}