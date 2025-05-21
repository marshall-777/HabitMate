package com.example.habitmate

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val nameEdit = findViewById<EditText>(R.id.edit_name)
        val emailEdit = findViewById<EditText>(R.id.edit_email)
        val passwordEdit = findViewById<EditText>(R.id.edit_password)
        val registerButton = findViewById<MaterialButton>(R.id.button_register)
        val loginRedirect = findViewById<TextView>(R.id.button_login_redirect)

        registerButton.setOnClickListener {
            val name = nameEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            when {
                name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Неверный email", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "Пароль должен быть от 6 символов", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = auth.currentUser?.uid
                                val userMap = mapOf("name" to name)
                                if (uid != null) {
                                    FirebaseDatabase.getInstance().getReference("users")
                                        .child(uid)
                                        .setValue(userMap)
                                }
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }

        loginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}