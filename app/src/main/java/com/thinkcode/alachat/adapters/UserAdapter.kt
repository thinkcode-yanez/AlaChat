package com.thinkcode.alachat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.thinkcode.alachat.R
import com.thinkcode.alachat.databinding.UserSearchItemLayoutBinding
import com.thinkcode.alachat.models.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    //private val mContext: Context,
    private val mUsers: List<Users>?,
    private var isChatChecked: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_search_item_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val item = mUsers?.get(position)
       // viewHolder.enlazarItem(item!!)
        viewHolder.userNameText.text= item!!.username
        Picasso.get().load(item.profile).into(viewHolder.profileImageView)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = mUsers!!.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = UserSearchItemLayoutBinding.bind(itemView)
        var context = itemView.context

        var userNameText: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var offlineImageView: CircleImageView
        var lastMessage: TextView


        init {
            userNameText = binding.usernameItem
            profileImageView = binding.profileImageItem
            onlineImageView = binding.imageOnline
            offlineImageView = binding.imageOffine
            lastMessage = binding.messageLast

        }


        }


    }