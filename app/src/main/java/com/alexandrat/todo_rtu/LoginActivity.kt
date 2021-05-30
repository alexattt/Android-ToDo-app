package com.alexandrat.todo_rtu

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener


class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        authStateListener = AuthStateListener {
            val user = mAuth.currentUser
            if (user != null) {
                val intent = Intent(this@LoginActivity, TaskActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        var toolbar = findViewById<Toolbar>(R.id.loginToolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Login"

        var loginEmail = findViewById<EditText>(R.id.loginEmail)
        var loginPwd = findViewById<EditText>(R.id.loginPassword)
        var loginBtn = findViewById<Button>(R.id.loginButton)
        var loginQn = findViewById<TextView>(R.id.loginPageQuestion)


        var progressBar = findViewById<ProgressBar>(R.id.progressBarLogin)
        progressBar.setVisibility(View.INVISIBLE);


        loginQn.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var email = loginEmail.text.toString().trim()
                var password = loginPwd.text.toString().trim()

                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Email is required!")
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    loginPwd.setError("Password is required!")
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this@LoginActivity, TaskActivity::class.java)
                                startActivity(intent)
                                finish()
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {
                                val exception = task.exception.toString()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login failed $exception",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                }


            }
        })
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(authStateListener)
    }

}