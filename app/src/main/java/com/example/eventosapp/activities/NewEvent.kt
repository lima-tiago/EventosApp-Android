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
import com.example.eventosapp.Event
import com.example.eventosapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_new_event.*
import java.util.*


class NewEvent : AppCompatActivity() {
    val GALLERY_REQUEST_CODE = 123
    var selectedPhotoUri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

//        val dimensionInDp = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            image_new_event.layoutParams.width.toFloat(),
//            resources.displayMetrics
//        ).toInt()
//        image_new_event.layoutParams.height = dimensionInDp
//        var hi = image_new_event.layoutParams.width
//        var he = dimensionInDp

        select_event_photo_button.setOnClickListener {
            pickImageFromGallery()
        }

        image_new_event.setOnClickListener {
            full_screen_image.setImageURI(selectedPhotoUri)
            full_screen_image.visibility = View.VISIBLE
        }

        full_screen_image.setOnClickListener {
            full_screen_image.visibility = View.GONE
        }

        create_event_button.setOnClickListener {
            createEvent()

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

//        println("debug: hello")
//        println("debug: ${requestCode == GALLERY_REQUEST_CODE}")
//        println("debug: ${resultCode}")
//        println("debug: ${Activity.RESULT_OK}")
//        println("debug: ${requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null}")

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCropper(uri)
                    }
                } else {
                    Log.d("Register", "falha ao selecionar a imagem")
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
                    Log.d("Register", "${result.error}")
                }
            }
        }
    }


    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(image_new_event)
        select_event_photo_button.visibility = View.GONE
    }

    private fun launchImageCropper(uri: Uri?) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private fun createEvent() {
        if (image_new_event.drawable != null) {

            println("debug: ${new_event_name_edittext.text.isNotEmpty()}")
            println("debug: ${new_event_date_edittext.text.isNotEmpty()}")
            println("debug: ${new_event_begin_time_edittext.text.isNotEmpty()}")
            println("debug: ${new_event_end_time_edittext.text.isNotEmpty()}")
            println("debug: ${new_event_location_edittext.text.isNotEmpty()}")
            println("debug: ${new_event_description_edittext.text.isNotEmpty()}")
            if (new_event_name_edittext.text.isNotEmpty() ||
                new_event_date_edittext.text.isNotEmpty() ||
                new_event_begin_time_edittext.text.isNotEmpty() ||
                new_event_end_time_edittext.text.isNotEmpty() ||
                new_event_location_edittext.text.isNotEmpty() ||
                new_event_description_edittext.text.isNotEmpty()
            ) {

                uploadImageToFirebaseStorage()
            } else {
                Toast.makeText(this, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "Escolha uma foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/events/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "foto adicionada ao firebase com sucesso ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("Register", "Localização do arquivo: ${it}")

                    saveEventToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveEventToFirebaseDatabase(eventImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/events/$uid")

        val event: Event = Event(
            uid,
            new_event_name_edittext.text.toString(),
            new_event_date_edittext.text.toString(),
            "${new_event_begin_time_edittext.text.toString()} até as ${new_event_end_time_edittext.text.toString()}",
            new_event_location_edittext.text.toString(),
            new_event_description_edittext.text.toString(),
            eventImageUrl,
            "nome do criador"
        )
        ref.setValue(event).addOnSuccessListener {
            Toast.makeText(this, "Evento salvo", Toast.LENGTH_SHORT).show()
            clearFields()
        }
    }

    private fun clearFields() {
        TODO("Not yet implemented")
    }
}
