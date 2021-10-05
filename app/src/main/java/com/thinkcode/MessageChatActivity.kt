package com.thinkcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thinkcode.alachat.databinding.ActivityMessageChatBinding

class MessageChatActivity : AppCompatActivity() {

    lateinit var binding:ActivityMessageChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}