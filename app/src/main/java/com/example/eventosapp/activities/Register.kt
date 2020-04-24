package com.example.eventosapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventosapp.R
import com.example.eventosapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class Register : AppCompatActivity() {
    val GALLERY_REQUEST_CODE = 123
    var selectedPhotoUri = Uri.EMPTY
    val TAG = "Register"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        already_have_an_account_text_view.setOnClickListener {
            finish()
        }

        register_button.setOnClickListener {
            performRegister()
        }

        select_user_photo_register_button.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        i.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
        i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(i, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println("debug: hello")
        println("debug: ${requestCode == GALLERY_REQUEST_CODE}")
        println("debug: ${resultCode}")
        println("debug: ${Activity.RESULT_OK}")
        println("debug: ${requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null}")

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCropper(uri)
                    }
                } else {
                    Log.d(TAG, "falha ao selecionar a imagem")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)

                if (resultCode == Activity.RESULT_OK) {
                    result.uri?.let { uri ->
                        selectedPhotoUri = uri
                        setImage(uri)
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.d(TAG, "${result.error}")
                }
            }
        }
    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(select_user_photo_register_button)
        select_user_photo_text_register_text_view.visibility = View.GONE
    }

    private fun launchImageCropper(uri: Uri?) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private fun clearFields() {
        user_register_edittext.text.clear()
        email_register_edittext.text.clear()
        password_register_edittext.text.clear()
        select_user_photo_register_button.setImageDrawable(null)
        select_user_photo_text_register_text_view.visibility = View.VISIBLE
    }

    private fun performRegister() {
        progress_bar_register.visibility = View.VISIBLE
        val username = user_register_edittext.text.toString()
        val email = email_register_edittext.text.toString()
        val password = password_register_edittext.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Porfavor, preencha todos os campos de email e senha,",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "Usuario criado com uid: ${it.result?.user?.uid}")
                Toast.makeText(
                    this,
                    "Email cadastrado com sucesso, dirija-se à tela de Login",
                    Toast.LENGTH_SHORT
                ).show()

                progress_bar_register.visibility = View.GONE
                uploadImageToFirebaseStorage()

            }.addOnFailureListener {
                Log.d(TAG, "Falha ao criar usuario: ${it.message}")
                Toast.makeText(
                    this,
                    "Email não cadastrado, corrija o campo 'email' e tente novamente",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/users/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "foto adicionada ao firebase com sucesso ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d(TAG, "Localização do arquivo: ${it}")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val username = user_register_edittext.text.toString()
        val password = password_register_edittext.text.toString()
        val email = email_register_edittext.text.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user: User =
            User(uid, username, password,email, profileImageUrl,"0")

        ref.setValue(user).addOnSuccessListener {
            Log.d(TAG, "Usuario salvo no banco de dados")
            clearFields()
        }
    }
}

