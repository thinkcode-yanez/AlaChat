package com.thinkcode.alachat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.thinkcode.alachat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbarLogin)
        supportActionBar!!.title="Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.toolbarLogin.setNavigationOnClickListener {

            backToWelcome()
        }

        mAuth= FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            loginUser()

        }
    }

    private fun loginUser() {
        val email=binding.etEmailLogin.text.toString()
        val password=binding.etPasswordLogin.text.toString()

        if(email=="") {
            Toast.makeText(this@LoginActivity,"Please write a email", Toast.LENGTH_SHORT).show()
        }else if(password=="") {
            Toast.makeText(this@LoginActivity, "Please enter a password", Toast.LENGTH_SHORT).show()
        }else{

            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent= Intent(this@LoginActivity,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    }else{
                        Toast.makeText(this@LoginActivity,"Error message: " + it.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                }


        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToWelcome()
    }

    fun backToWelcome(){
        val intent= Intent(this@LoginActivity,WelcomeActivity::class.java)
        startActivity(intent)
        finish()

    }
}