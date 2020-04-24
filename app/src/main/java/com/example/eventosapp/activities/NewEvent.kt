package com.example.eventosapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventosapp.Event
import com.example.eventosapp.R
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_new_event.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class NewEvent : AppCompatActivity() {
    private val GALLERY_REQUEST_CODE = 123
    private var selectedPhotoUri = Uri.EMPTY
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val userUid = auth.currentUser!!.uid
    private val userRef = rootRef.child("users").child(userUid)
    private val TAG = "NewEvent"


    var username = ""
    var createdEvents = "0"


    internal var placesClient: PlacesClient? = null

    var placeFields = Arrays.asList(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS
    )

    var eventLocation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        create_event_button.isEnabled = false

        getUserInfo()

        initplaces()
        setuPlacesAutoComplete()

        select_event_photo_button.setOnClickListener {
            pickImageFromGallery()
        }

        event_image_new_event.setOnClickListener {
            full_screen_image_new_event.setImageURI(selectedPhotoUri)
            blurry_layout_new_event_image.visibility = View.VISIBLE
            full_screen_image_new_event.visibility = View.VISIBLE
        }

        full_screen_image_new_event.setOnClickListener {
            it.visibility = View.GONE
            blurry_layout_new_event_image.visibility = View.GONE

        }

        create_event_button.setOnClickListener {
            createEvent()
        }

        new_event_begin_time_edittext.setInputType(InputType.TYPE_NULL)
        new_event_begin_time_edittext.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                pickTimeDate(new_event_begin_time_edittext, "##:##")
                new_event_begin_time_edittext.setOnClickListener {
                    pickTimeDate(new_event_begin_time_edittext, "##:##")
                }
            }
        }

        new_event_end_time_edittext.setInputType(InputType.TYPE_NULL)
        new_event_end_time_edittext.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                pickTimeDate(new_event_end_time_edittext, "##:##")

                new_event_begin_time_edittext.setOnClickListener {
                    pickTimeDate(new_event_end_time_edittext, "##:##")
                }
            }
        }

        new_event_date_edittext.setInputType(InputType.TYPE_NULL)
        new_event_date_edittext.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                pickTimeDate(new_event_date_edittext, "##/##/####")

                new_event_date_edittext.setOnClickListener {
                    pickTimeDate(new_event_date_edittext, "##/##/####")
                }
            }
        }
    }

    private fun pickTimeDate(editText: EditText, format: String) {
        val edit: WeakReference<EditText> = WeakReference<EditText>(editText)
        val editText = edit.get() ?: return

        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
        val dateFormat = SimpleDateFormat("dd/MM/YYYY", Locale("pt", "BR"))

        if (format.equals("##:##")) {
            val dialog = TimePickerDialog(
                this,
                AlertDialog.THEME_HOLO_DARK,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance(Locale("pt", "BR"))
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    editText.setText(timeFormat.format(selectedTime.time))
                },
                hour,
                minute,
                true
            )
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

        } else if (format.equals("##/##/####")) {
            val dialog = DatePickerDialog(
                this,
                AlertDialog.THEME_HOLO_DARK,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance(Locale("pt", "BR"))
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.YEAR, year)
                    editText.setText(dateFormat.format(selectedDate.time))
                },
                year,
                month,
                day
            )

            dialog.show()

        } else {
            Toast.makeText(this, "Formato não suportado.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setuPlacesAutoComplete() {
        val autocompletefragment = supportFragmentManager
            .findFragmentById(R.id.new_event_location_fragment) as AutocompleteSupportFragment

        autocompletefragment.setPlaceFields(placeFields)

        autocompletefragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Toast.makeText(this@NewEvent, "${p0.address}", Toast.LENGTH_SHORT).show()
                eventLocation = p0.address.toString()
            }

            override fun onError(p0: Status) {
                Toast.makeText(this@NewEvent, "${p0.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initplaces() {
        Places.initialize(this, getString(R.string.places_api))
        placesClient = Places.createClient(this)
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
            .into(event_image_new_event)
        select_event_photo_button.visibility = View.GONE
    }

    private fun launchImageCropper(uri: Uri?) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private fun createEvent() {
        progress_bar_new_event_creation.visibility = View.VISIBLE

        if (event_image_new_event.drawable != null) {

            if (new_event_name_edittext.text.isNotEmpty() ||
                new_event_date_edittext.text.isNotEmpty() ||
                new_event_begin_time_edittext.text.isNotEmpty() ||
                new_event_end_time_edittext.text.isNotEmpty() ||
                eventLocation.isNotEmpty() ||
                new_event_description_edittext.text.isNotEmpty()
            ) {

                uploadImageToFirebaseStorage()
            } else {
                Toast.makeText(
                    this,
                    "Todos os campos devem ser preenchidos",
                    Toast.LENGTH_SHORT
                )
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
                Log.d(TAG, "foto adicionada ao firebase com sucesso ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d(TAG, "Localização do arquivo: ${it}")

                    saveEventToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveEventToFirebaseDatabase(eventImageUrl: String) {

        val uid: String = rootRef.push().key.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/events/$uid")

        val event: Event = Event(
            uid,
            new_event_name_edittext.text.toString(),
            new_event_date_edittext.text.toString(),
            "${new_event_begin_time_edittext.text} até as ${new_event_end_time_edittext.text}",
            eventLocation,
            new_event_description_edittext.text.toString(),
            eventImageUrl,
            userUid,
            username
        )

        userRef.child("numberOfCreatedEvents").setValue((createdEvents.toInt() + 1).toString())

        ref.setValue(event).addOnSuccessListener {
            Toast.makeText(this, "Evento salvo", Toast.LENGTH_SHORT).show()
            progress_bar_new_event_creation.visibility = View.GONE
            finish()
        }
    }

    private fun getUserInfo() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "cancelado")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, "nao cancelado")

                username = p0.child("username").value.toString()
                createdEvents = p0.child("numberOfCreatedEvents").value.toString()
            }
        }).run {
            create_event_button.isEnabled = true
        }
    }
}
