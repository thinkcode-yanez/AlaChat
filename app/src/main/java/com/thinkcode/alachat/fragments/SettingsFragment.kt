package com.thinkcode.alachat.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.thinkcode.alachat.R
import com.thinkcode.alachat.databinding.FragmentSearchBinding
import com.thinkcode.alachat.databinding.FragmentSettingsBinding
import com.thinkcode.alachat.models.Users


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    var userReference: DatabaseReference? = null
    var coverReferenceid: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val REQUESTCODE = 123
    private var imageUri: Uri? = null
    private var storageReference: StorageReference? = null
    private var coverChecker: String? = null
    private var socialChecker: String? = null


    var status:Boolean?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)


        storageReference = FirebaseStorage.getInstance().reference.child("User Images")


        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    val user: Users? = p0.getValue(Users::class.java)

                    if (context !== null) {

                        binding.usernameSettings.text = user!!.username
                        Picasso.get().load(user.profile).into(binding.profileImageSettings)
                        Picasso.get().load(user.cover).into(binding.coverImage)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })


           val getProfileImage = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback {
                    binding.profileImageSettings.setImageURI(it)
                    imageUri = it
                   // Toast.makeText(context,"Uploading image",Toast.LENGTH_SHORT).show()

                }

            )

         val getCoverImage = registerForActivityResult(
              ActivityResultContracts.GetContent(),
              ActivityResultCallback {
                  binding.coverImage.setImageURI(it)
                  imageUri = it
                  //Toast.makeText(context,"Uploading image",Toast.LENGTH_SHORT).show()
              }
          )



        binding.profileImageSettings.setOnClickListener {

            getProfileImage.launch("image/*")
            status=true
           // uploadImageToDatabase()
        }
        binding.coverImage.setOnClickListener {

             getCoverImage.launch("image/*")
             coverChecker = "cover"
           // uploadImageToDatabase()
            status=true
        }
        binding.setFacebook.setOnClickListener {
            socialChecker="facebook"
            setSocialLinks()
        }
        binding.setInstagram.setOnClickListener {
            socialChecker="instagram"
            setSocialLinks()
        }
        binding.setWebsite.setOnClickListener {
            socialChecker="website"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {

        val builder: AlertDialog.Builder=
            AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert)


        if(socialChecker=="website"){

            builder.setTitle("Write the URL:")
        }else{
            builder.setTitle("Write UserName")

        }

        val editText=EditText(context)

        if(socialChecker=="website"){

            editText.hint="e.g www.google.com"
        }else{

            builder.setTitle("e.g yanez1234")

        }

        builder.setView(editText)
        builder.setPositiveButton("Create",DialogInterface.OnClickListener{
            dialogInterface, i ->

            val str =editText.text.toString()

            if(str=="")
            {
                Toast.makeText(context,"Please write something...",Toast.LENGTH_SHORT).show()
            }else{

                saveSocialLink(str)
            }

        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener {
                dialogInterface, i ->

            dialogInterface.cancel()

        })
        val alertDialog:AlertDialog=builder.create()
        alertDialog.show()
        val nButton:Button= alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        nButton.setTextColor(Color.BLACK)
        val pButton:Button= alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        pButton.setTextColor(Color.BLACK)

       // builder.show()

    }

    private fun saveSocialLink(str: String) {

        val mapSocial=HashMap<String,Any>()

        when(socialChecker){

            "facebook"->{
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram"->{
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "website"->{
                mapSocial["website"] = "https://$str"
            }
            else->{}


        }

        userReference!!.updateChildren(mapSocial).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Updated Succesfully",Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(status==true){
            uploadImageToDatabase()
            status=false
        }
    }


     private fun uploadImageToDatabase() {
        val progresbar = ProgressDialog(context)
        progresbar.setMessage("Image is uploading, please wait..")
         progresbar.show()

        if (imageUri !== null) {
            //Guardamos la referencia unica puesto que si subimos una id puede que alguien ponga la misma
            val fileRefStore =
                storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRefStore.putFile(imageUri!!) //le agregamos la referencia al task
            //aqui lo hace url directo al storage


            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {

                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRefStore.downloadUrl
            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg) //Actialiazmos el campo cover con la url o imagen en base de datos
                        coverChecker = ""
                    } else {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        userReference!!.updateChildren(mapProfileImg) //Actialiazmos el campo cover con la url o imagen en base de datos

                    }
                     progresbar.dismiss()


                }
            }


        }


    }


}