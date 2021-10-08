package com.thinkcode.alachat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.thinkcode.alachat.adapters.ChatsAdapter
import com.thinkcode.alachat.databinding.ActivityMessageChatBinding
import com.thinkcode.alachat.models.Chat
import com.thinkcode.alachat.models.Users
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityMessageChatBinding

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter:ChatsAdapter?=null
    var mChatList: List<Chat>?=null
    var linearLayout:LinearLayoutManager?=null

    var reference:DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMessageChat)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbarMessageChat.setNavigationOnClickListener {
            val intent= Intent(this@MessageChatActivity,WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString() //quitar el string

        firebaseUser = FirebaseAuth.getInstance().currentUser


        binding.recyclerviewChats.setHasFixedSize(true)
        linearLayout=LinearLayoutManager(applicationContext)
        binding.recyclerviewChats.layoutManager=linearLayout




        //CARGA LA INFOMRACION DE A QUIEN DIMOS CLICK Y CON QUIEN ESTAMOS CHATEANDO
         reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user: Users? = snapshot.getValue(Users::class.java)
                binding.usernameMessageChat.text = user!!.username
                Picasso.get().load(user.profile).into(binding.profileImageMessageChat)

                retrieveMessage(firebaseUser!!.uid,userIdVisit,user.profile)
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        binding.btnSendMessage.setOnClickListener {
            val message:String = binding.textMessageChat.text.toString()


            if (message == "") {
                Toast.makeText(this, "Please write a message first...", Toast.LENGTH_SHORT).show()

            } else {

                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)

            }
            binding.textMessageChat.setText("")

        }

        binding.btnAttachImageFile.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }

        seenMessage(userIdVisit)
    }



    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {

        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messegeId"] = messageKey

        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val chatListReference = FirebaseDatabase
                        .getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (!snapshot.exists()) {
                                chatListReference.child("id").setValue(userIdVisit)
                            }

                            val chatListReceiverReference = FirebaseDatabase
                                .getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)

                            chatListReceiverReference.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }


                    })



                    //Implements pushnotifiacions


                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser!!.uid)


                }
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data !== null && data!!.data != null) {

            val progresbar = ProgressDialog(this)
            progresbar.setMessage("Image is uploading, please wait..")
            progresbar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Photos Sent")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")


            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!) //le agregamos la referencia al task
            //aqui lo hace url directo al storage


            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {

                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messegeId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                    progresbar.dismiss()


                }
            }
        }
    }

    private fun retrieveMessage(senderId: String, receiverId: String, receiverImageUrl: String) {

        mChatList=ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for(snapshot in p0.children){

                    val chat = snapshot.getValue(Chat::class.java)

                    if(chat!!.receiver.equals(senderId) && chat.sender.equals(receiverId)
                        || chat.receiver.equals(receiverId) && chat.sender.equals(senderId)){

                        (mChatList as ArrayList<Chat>).add(chat)

                    }
                    chatsAdapter= ChatsAdapter(this@MessageChatActivity,(mChatList as ArrayList<Chat>),receiverImageUrl)

                    binding.recyclerviewChats.adapter=chatsAdapter
                   // linearLayout!!.scrollToPosition((mChatList as ArrayList<Chat>).size -1)
                    binding.recyclerviewChats.scrollToPosition(chatsAdapter!!.itemCount -1)
                   // binding.recyclerviewChats.scrollto

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    var seenListener:ValueEventListener?=null

    private fun seenMessage(userId:String,){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener=reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(dataSnapshot in snapshot.children){

                    val chat=dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(firebaseUser!!.uid) && chat.sender.equals(userId))
                    {
                        val hashmap=HashMap<String,Any>()
                        hashmap["isseen"]= true
                        dataSnapshot.ref.updateChildren(hashmap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)
    }
}