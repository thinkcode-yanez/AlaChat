package com.thinkcode.alachat

import android.content.Intent
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
import com.thinkcode.alachat.databinding.ActivityMainBinding
import com.thinkcode.alachat.fragments.ChatsFragment
import com.thinkcode.alachat.fragments.SearchFragment
import com.thinkcode.alachat.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        val toolbar = binding.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(SearchFragment(),"Search")
        viewPagerAdapter.addFragment(SettingsFragment(),"Settings")

        binding.viewPager.adapter=viewPagerAdapter
        binding.tabLayoutid.setupWithViewPager(binding.viewPager)





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