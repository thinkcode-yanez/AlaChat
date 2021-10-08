package com.thinkcode.alachat.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thinkcode.alachat.R
import com.thinkcode.alachat.adapters.UserAdapter
import com.thinkcode.alachat.databinding.FragmentSearchBinding
import com.thinkcode.alachat.models.Users
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null

    private var mUsers: List<Users>? = null

    private var recyclerView: RecyclerView? = null

    lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAdapter = UserAdapter( mUsers!!, false)
        binding.rvSearhList.adapter=userAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        //val view:View= inflater.inflate(R.layout.fragment_search, container, false)

        binding.rvSearhList.setHasFixedSize(true)
        binding.rvSearhList.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        retrieveAllUsers()

        binding.etSearchUsers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(p0.toString().lowercase(Locale.getDefault()))
            }

            override fun afterTextChanged(p0: Editable?) {

            }


        })

        return view
    }

    private fun retrieveAllUsers() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid //cambiar a var
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

                if (binding.etSearchUsers!!.text.toString() == "") {
                    for (snapshot in p0.children) {


                        val user: Users? = snapshot.getValue(Users::class.java)
                        if (!(user!!.uid).equals(firebaseUserID)) {

                            (mUsers as ArrayList<Users>).add(user)
                        }

                    }
                    userAdapter = UserAdapter(mUsers!!, false)
                    binding.rvSearhList.adapter = userAdapter
                   // binding.rvSearhList.smoothScrollToPosition(userAdapter!!.itemCount-1)


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun searchForUsers(str: String) {

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {

                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.uid).equals(firebaseUserID)) {

                        (mUsers as ArrayList<Users>).add(user)
                    }

                }
                userAdapter = UserAdapter(mUsers!!, false)
                binding.rvSearhList.adapter = userAdapter

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

}