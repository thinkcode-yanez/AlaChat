package com.thinkcode.alachat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thinkcode.alachat.R
import com.thinkcode.alachat.adapters.UserAdapter
import com.thinkcode.alachat.databinding.FragmentChatsBinding
import com.thinkcode.alachat.models.ChatList
import com.thinkcode.alachat.models.Users


class ChatsFragment : Fragment() {

    lateinit var binding:FragmentChatsBinding

    private var userAdapter: UserAdapter? = null

    private var mUsers: List<Users>? = null

    private var usersChatList: List<ChatList>? = null

    private var firebaseUser:FirebaseUser?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentChatsBinding.inflate(inflater,container,false)

        val view=binding.root

        binding.recyclerviewChatList.setHasFixedSize(true)
        binding.recyclerviewChatList.layoutManager=LinearLayoutManager(context)

        firebaseUser=FirebaseAuth.getInstance().currentUser

        usersChatList=ArrayList()

        val ref=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)

        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                (usersChatList as ArrayList).clear()
                for(dataSnapshot in snapshot.children){
                    val chatList = dataSnapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatList!!)

                }
                retrieveChatList()

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })



        return view
    }

    private fun retrieveChatList(){

        mUsers=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("Users")

        ref!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for(dataSnapshot in snapshot.children){

                    val user= dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in usersChatList!!){
                        if(user!!.uid.equals(eachChatList.id)){
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter= UserAdapter(mUsers as ArrayList<Users>,true)

                binding.recyclerviewChatList.adapter=userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })



    }


}