package com.example.jankenteamb.ui.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.R
import com.example.jankenteamb.ui.login.LoginActivity
import com.example.jankenteamb.utils.FirebaseHelper
import com.example.jankenteamb.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.android.ext.android.inject

class RegisterActivity : AppCompatActivity() {
    private val firebaseHelper by inject<FirebaseHelper>()

    private val factory by inject<RegisterViewModel.Factory>()
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)

        registerViewModel = ViewModelProvider(
            this,
            factory
        ).get(RegisterViewModel::class.java)

        registerViewModel.errorLiveData.observe(this, onError)

        registerViewModel.successLiveData.observe(this, onSuccess)

        btn_register.setOnClickListener {
            val username = et_username_register.text.toString()
            val email = et_email_register.text.toString()
            val password = et_password_register.text.toString()

            when {
                username.isEmpty() || username.length < 4 -> {
                    Toast.makeText(this, "Username minimal 4 karakter!", Toast.LENGTH_SHORT).show()
                }
                email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Isi Email Dengan Benar !", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() || password.length < 6 -> {
                    Toast.makeText(
                        this,
                        "Password tidak boleh kosong atau kurang dari 6 digit !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val uri: Uri = Uri.parse(
                        "android.resource://" + baseContext.packageName
                            .toString() + "/drawable/profilepict"
                    )
                    registerViewModel.registerToFirebase(email, password, username, uri)
                }
            }
        }

        tv_move_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    val onError = Observer<String>{msg ->
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private val onSuccess = Observer<String>{ msg ->
        firebaseHelper.signoutFirebase()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)

    }
}