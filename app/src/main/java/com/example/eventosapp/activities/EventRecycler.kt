package com.example.eventosapp.activities

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.eventosapp.Event
import com.example.eventosapp.R
import com.example.eventosapp.adapter.AdapterEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_event_recycler.*


class EventRecycler : AppCompatActivity() {
    private val TAG = "EventRecycler"
    private val auth = FirebaseAuth.getInstance()
    private val userUid = auth.currentUser!!.uid
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val eventRef = rootRef.child("events")
    private val userRef = rootRef.child("users").child(userUid)
    val REQUEST_CODE_NEW_EVENT = 123

    var event_list = mutableListOf<Event>()
    var createdEvents = ""


    private lateinit var adapterEventos: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_recycler)

        getUserInfo()
        listenForNewEvents()

        new_event_button.setOnClickListener {
            val i = Intent(this, NewEvent::class.java)
            startActivityForResult(i, REQUEST_CODE_NEW_EVENT)
        }
    }

    private fun listenForNewEvents() {
        progress_bar_event_recycler.visibility = View.VISIBLE
        eventRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val event: Event? = p0.getValue(Event::class.java)
                if (event != null) {
                    event_list.add(event)
                }
                runOnUiThread {
                    configureAdapter()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun getUserInfo() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "cancelado")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG, "nao cancelado")

                createdEvents = p0.child("numberOfCreatedEvents").value.toString()
            }
        })
    }

    private fun configureAdapter() {
        progress_bar_event_recycler.visibility = View.GONE

        adapterEventos = AdapterEvents(event_list, this)
        recycler_eventos.apply {
            adapter = adapterEventos
            layoutManager = LinearLayoutManager(this@EventRecycler, RecyclerView.VERTICAL, false)
        }

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    target: ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {

                    val eventToDelete = event_list[viewHolder.adapterPosition]
                    if (eventToDelete.creatorUid == auth.currentUser?.uid) {

//                        adapterEventos.notifyDataSetChanged()
                        deleteEvent(eventToDelete, viewHolder.adapterPosition)


                    } else {
                        Toast.makeText(
                            this@EventRecycler,
                            "Você não pode excluir eventos que outro usuário criou.",
                            Toast.LENGTH_SHORT
                        ).show()
                        adapterEventos.notifyDataSetChanged()

                        return
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val deleteIcon: Drawable =
                        ContextCompat.getDrawable(this@EventRecycler, R.drawable.trash_bin_icon)!!
                    val colorDrawableBackground: ColorDrawable = ColorDrawable(Color.RED)

                    val itemView = viewHolder.itemView
                    val iconMarginVertical =
                        (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2


                    if (dX > 0) {
                        colorDrawableBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.left + iconMarginVertical,
                            itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMarginVertical
                        )
                    } else {
                        colorDrawableBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                            itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical,
                            itemView.bottom - iconMarginVertical
                        )
                        deleteIcon.level = 0
                    }

                    colorDrawableBackground.draw(c)

                    c.save()

                    if (dX > 0)
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    else
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )

                    deleteIcon.draw(c)

                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_eventos)
    }

    private fun deleteEvent(event: Event, position: Int) {
        userRef.child("numberOfCreatedEvents").setValue((createdEvents.toInt() - 1).toString())
        eventRef.child(event.uid).removeValue()

//        Toast.makeText(this@EventRecycler, "hello ${event.name} deleted ${event_list.size}", Toast.LENGTH_SHORT).show()
        event_list.removeAt(position)

        adapterEventos.notifyItemRemoved(position)
    }
}
