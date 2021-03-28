package com.example.jankenteamb.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jankenteamb.R
import com.example.jankenteamb.ui.menu.MenuActivity
import com.example.jankenteamb.ui.register.RegisterActivity
import com.example.jankenteamb.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    //menggantikan presenter
    private val factory by inject<LoginViewModel.Factory>()
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)

        //inisialisasi ViewModel
        loginViewModel = ViewModelProvider(
            this,
            factory
        ).get(LoginViewModel::class.java)

        //INISIALISASI SEMUA OBSERVER
        //untuk toast error
        loginViewModel.errorLiveData.observe(this, onError)
        //untuk toast success
        loginViewModel.successLiveData.observe(this, onSuccess)

        btn_login.setOnClickListener {
            //menggantikan presenter.loginToFirebase di atas
            loginViewModel.loginToFirebase(
                et_email_login.text.toString(),
                et_password_login.text.toString()
            )
        }

        tv_move_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    //menggantikan onError di atas
    val onError = Observer<String> {
        runOnUiThread {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    //menggantikan onSucces di atas
    private val onSuccess = Observer<String> {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_activity,R.anim.exit_activity)
    }

}