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


class RegistrationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_registration)


        var toolbar = findViewById<Toolbar>(R.id.registrationToolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Registration"

        var regEmail = findViewById<EditText>(R.id.registrationEmail)
        var regPwd = findViewById<EditText>(R.id.registrationPassword)
        var regBtn = findViewById<Button>(R.id.registrationButton)
        var regQn = findViewById<TextView>(R.id.registrationPageQuestion)

        mAuth = FirebaseAuth.getInstance()
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.setVisibility(View.INVISIBLE);

        regQn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        regBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var email = regEmail.text.toString().trim()
                var password = regPwd.text.toString().trim()

                if (TextUtils.isEmpty(email)) {
                    regEmail.setError("Email is required!")
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    regEmail.setError("Password is required!")
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this@RegistrationActivity, TaskActivity::class.java)
                                startActivity(intent)
                                finish()
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {
                                val exception = task.exception.toString()
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Registration failed $exception",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                }
            }
        })
    }
}