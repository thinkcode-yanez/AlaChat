package com.thinkcode.alachat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.thinkcode.alachat.databinding.ActivityWelcomeActiviyBinding

class WelcomeActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser?=null
    private lateinit var binding:ActivityWelcomeActiviyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeActiviyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegisterWelcome.setOnClickListener {
            val intent= Intent(this@WelcomeActivity,RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }
        binding.btnLoginWelcome.setOnClickListener {
            val intent= Intent(this@WelcomeActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()

        firebaseUser= FirebaseAuth.getInstance().currentUser
        if(firebaseUser!=null){

            val intent= Intent(this@WelcomeActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}