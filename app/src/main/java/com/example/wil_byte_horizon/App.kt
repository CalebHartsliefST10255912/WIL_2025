// app/src/main/java/com/example/wil_byte_horizon/App.kt
package com.example.wil_byte_horizon

import android.app.Application

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
