package com.example.argus_eye.controller

import com.example.argus_eye.model.MainModel

class MainController(private val model: MainModel) {
    fun getAppName(): String {
        return model.title
    }

    fun getAppDescription(): String {
        return model.description
    }
}
