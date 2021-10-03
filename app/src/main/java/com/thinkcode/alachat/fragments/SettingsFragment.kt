package com.thinkcode.alachat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.thinkcode.alachat.R
import com.thinkcode.alachat.databinding.FragmentSearchBinding
import com.thinkcode.alachat.databinding.FragmentSettingsBinding
import com.thinkcode.alachat.models.Users


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    var userReference: DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseUser=FirebaseAuth.getInstance().currentUser

        userReference= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)


        userReference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists()){

                    val user:Users?= p0.getValue(Users::class.java)

                   if(context!==null){

                       binding.usernameSettings.text= user!!.username
                       Picasso.get().load(user.profile).into(binding.profileImageSettings)
                       Picasso.get().load(user.cover).into(binding.coverImage)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        return view
    }


}