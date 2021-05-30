package com.alexandrat.todo_rtu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

/* I followed this tutorial video series https://www.youtube.com/watch?v=zmrPTVR4jJE&list=PLlkSO32XQLGpF9HzRulWLpMbU3mWZYlJS
* Learned quite a lot from it, seemed very interesting, also got a chance to compare Java and Kotlin in action
* I mostly changed the design a little bit the way I like it, also added a function where a user
* can mark finished tasks and still see them, in case user doesn't want to delete the task */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val welcomeLogo = findViewById<ImageView>(R.id.welcomeLogo)
        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        val logoAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        welcomeLogo.startAnimation(logoAnimation)

        val nameAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.name_animation)
        welcomeText.startAnimation(nameAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2800)
    }
}