package com.example.habitmate

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp

class HabitMateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
