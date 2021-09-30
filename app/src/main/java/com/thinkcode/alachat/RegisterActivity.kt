package com.thinkcode.alachat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thinkcode.alachat.databinding.ActivityRegisterBinding
import com.thinkcode.alachat.databinding.ActivityWelcomeActiviyBinding
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID:String=""


    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarRegister)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.toolbarRegister.setNavigationOnClickListener {
            backToWelcome()
        }

        mAuth= FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {
        val username=binding.etUsernameRegister.text.toString()
        val email=binding.etEmailRegister.text.toString()
        val password=binding.etPasswordRegister.text.toString()


        if(username==""){
            Toast.makeText(this,"Please write a username",Toast.LENGTH_SHORT).show()
        }else if(email=="") {
            Toast.makeText(this,"Please write a email",Toast.LENGTH_SHORT).show()
        }else if(password==""){
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_SHORT).show()
        }else{

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
                if(task.isSuccessful){
                    firebaseUserID= mAuth.currentUser!!.uid
                    refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    val userHashMap= HashMap<String,Any>()

                    userHashMap["uid"] = firebaseUserID
                    userHashMap["username"] = username
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/alachat-c6d6e.appspot.com/o/profile.png?alt=media&token=99fdf413-5874-43af-ba93-ea1c0e4202c1"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/alachat-c6d6e.appspot.com/o/cover.jpg?alt=media&token=a14a09f7-6400-457a-aaae-142feb8746a5"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = username.lowercase(Locale.getDefault())
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener {task->
                            if(task.isSuccessful){

                                val intent= Intent(this@RegisterActivity,MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }

                }else{
                    Toast.makeText(this,"Error message: " + task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()

                }

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToWelcome()
    }
    fun backToWelcome(){
        val intent= Intent(this@RegisterActivity,WelcomeActivity::class.java)
        startActivity(intent)
        finish()

    }
}