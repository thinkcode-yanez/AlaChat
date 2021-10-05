package com.thinkcode.alachat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.thinkcode.alachat.databinding.ActivityMessageChatBinding
import com.thinkcode.alachat.models.Users

class MessageChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityMessageChatBinding

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString() //quitar el string

        firebaseUser = FirebaseAuth.getInstance().currentUser


        val reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user: Users? = snapshot.getValue(Users::class.java)
                binding.usernameMessageChat.text = user!!.username
                Picasso.get().load(user.profile).into(binding.profileImageMessageChat)
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
}