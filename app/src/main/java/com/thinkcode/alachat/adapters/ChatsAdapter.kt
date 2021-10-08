package com.thinkcode.alachat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import com.thinkcode.alachat.R
import com.thinkcode.alachat.databinding.MessageItemLeftBinding
import com.thinkcode.alachat.models.Chat
import de.hdodenhof.circleimageview.CircleImageView


class ChatsAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String

) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private val mContext: Context?
    private val mChatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mContext = mContext
        this.mChatList = mChatList
        this.imageUrl = imageUrl
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {

        return if (position == 1) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(viewHolder.profile_image)


        //IMAGES MESSAGES
        if (chat.message == "sent you an image." && chat.url != "") {

            //Image message - right side (sender)
            if (chat.sender == firebaseUser!!.uid) {
                viewHolder.show_text_message!!.visibility = View.GONE
                viewHolder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(viewHolder.right_image_view)

            }
            //Image message - left side (sender)
            else if (chat.sender != firebaseUser!!.uid) {
                viewHolder.show_text_message!!.visibility = View.GONE
                viewHolder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(viewHolder.left_image_view)

            }

        }
        //TEXT MESSAGES
        else if(chat.url.equals("")){
            viewHolder.show_text_message!!.text=chat.message


        }

        //Sente and seen message
        if(position==mChatList.size-1){

            if(chat.isseen){
                viewHolder.text_seen!!.text="Seen"
                if(chat.message == "sent you an image." && chat.url != ""){
                    val lp:RelativeLayout.LayoutParams?= viewHolder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    viewHolder.text_seen!!.layoutParams=lp

                }
            }else{
                viewHolder.text_seen!!.text="Sent"
                if(chat.message == "sent you an image." && chat.url != ""){
                    val lp:RelativeLayout.LayoutParams?= viewHolder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    viewHolder.text_seen!!.layoutParams=lp

                }
            }

        }else{

            viewHolder.text_seen!!.visibility=View.GONE
        }





    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = mChatList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       // var binding = MessageItemLeftBinding.bind(itemView)
        var context = itemView.context


        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)


        }


    }

    override fun getItemViewType(position: Int): Int {

        return if (mChatList[position].sender.equals(firebaseUser!!.uid)) {
            1
        } else {
            0
        }

    }


}