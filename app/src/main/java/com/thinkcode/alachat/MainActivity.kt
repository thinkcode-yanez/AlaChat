package com.thinkcode.alachat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.thinkcode.alachat.databinding.ActivityMainBinding
import com.thinkcode.alachat.fragments.ChatsFragment
import com.thinkcode.alachat.fragments.SearchFragment
import com.thinkcode.alachat.fragments.SettingsFragment
import com.thinkcode.alachat.models.Chat
import com.thinkcode.alachat.models.Users

class MainActivity : AppCompatActivity() {

    var refUsers:DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        firebaseUser=FirebaseAuth.getInstance().currentUser //Obtiene el usuario individual
        refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)//Obetiene una referencia de algun usuario




        val toolbar = binding.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

     /*   val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(SearchFragment(),"Search")
        viewPagerAdapter.addFragment(SettingsFragment(),"Settings")

        binding.viewPager.adapter=viewPagerAdapter
        binding.tabLayoutid.setupWithViewPager(binding.viewPager)*/

        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)

                var countUnreadMessage=0
                for(dataSnapshot in snapshot.children){

                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(firebaseUser!!.uid) && !chat.isseen){

                        countUnreadMessage+=1
                    }
                }

                if(countUnreadMessage==0){

                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats")

                }else{

                    viewPagerAdapter.addFragment(ChatsFragment(),"($countUnreadMessage) Chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(),"Search")
                viewPagerAdapter.addFragment(SettingsFragment(),"Settings")
                binding.viewPager.adapter=viewPagerAdapter
                binding.tabLayoutid.setupWithViewPager(binding.viewPager)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        //Display username and profile pictures
        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val user: Users? =snapshot.getValue(Users::class.java)
                    binding.userName.text=user!!.username
                    Picasso.get().load(user.profile).into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })




    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         when (item.itemId) {
            R.id.action_logout -> {

                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this@MainActivity,WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }

        }
        return false
    }




    internal class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager)
    {

        private val fragments:ArrayList<Fragment>
        private val titles:ArrayList<String>

        init {
            fragments= ArrayList<Fragment>()
            titles=ArrayList<String>()

        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
           return fragments[position]
        }

        fun addFragment(fragment:Fragment,title:String){

            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }


    }
}