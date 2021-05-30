package com.alexandrat.todo_rtu

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.*


/* I followed this tutorial video series https://www.youtube.com/watch?v=zmrPTVR4jJE&list=PLlkSO32XQLGpF9HzRulWLpMbU3mWZYlJS
* Learned quite a lot from it, seemed very interesting, also got a chance to compare Java and Kotlin in action
* I mostly changed the design a little bit the way I like it, also added a function where a user
* can mark finished tasks and still see them, in case user doesn't want to delete the task */


class TaskActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var recyclerView: RecyclerView

    private lateinit var key: String
    private lateinit var task: String
    private lateinit var description: String
    private var status: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_tasks)

        var toolbar = findViewById<Toolbar>(R.id.homeToolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle("ToDO List App")
        mAuth = FirebaseAuth.getInstance()

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        mUser = mAuth.currentUser!!
        var onlineUserID = mUser!!.uid
        reference = FirebaseDatabase.getInstance().reference.child("tasks").child(
                onlineUserID
        );

        var floatingActionButton = findViewById<FloatingActionButton>(R.id.fab)
        floatingActionButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                addTask()
            }
        })

    }

    private fun addTask() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        var inflater = LayoutInflater.from(this)

        var myView = inflater.inflate(R.layout.input_file, null)
        alertDialogBuilder.setView(myView)

        var dialog = alertDialogBuilder.create()
        dialog.setCancelable(false)

        var task = myView.findViewById<EditText>(R.id.task)
        var description = myView.findViewById<EditText>(R.id.description)
        var saveBtn = myView.findViewById<Button>(R.id.saveBtn)
        var cancelBtn = myView.findViewById<Button>(R.id.cancelBtn)

        cancelBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }
        })

        saveBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var mTask = task.text.toString().trim()
                var mDescription = description.text.toString().trim()
                var id = reference.push().key
                var date = DateFormat.getDateInstance().format(Date())

                if (TextUtils.isEmpty(mTask)) {
                    task.error = "Task required!"
                    return;
                }
                if (TextUtils.isEmpty(mDescription)) {
                    task.error = "Description required!"
                    return;
                } else {
                    val model = Model(mTask, mDescription, id, date, false)
                    reference.child(id!!).setValue(model).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                    this@TaskActivity,
                                    "Task has been added successfully",
                                    Toast.LENGTH_LONG
                            ).show()
                        } else {
                            var error = task.exception.toString()
                            Toast.makeText(
                                    this@TaskActivity,
                                    "Failed $error",
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                dialog.dismiss()
            }

        })

        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        val options = FirebaseRecyclerOptions.Builder<Model>()
            .setQuery(reference, Model::class.java)
            .build()

        val adapter: FirebaseRecyclerAdapter<Model, MyViewHolder> =
            object : FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
                override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Model) {
                    holder.setDate(model.getDate())
                    holder.setTask(model.getTask())
                    holder.setDesc(model.getDescription())
                    holder.mView.setOnClickListener {
                        key = getRef(position).key!!
                        task = model.getTask()!!
                        description = model.getDescription()!!

                        updateTask()
                    }

                    status = model.getStatus()
                    if (status) {
                        var taskLayout = holder.mView.findViewById<LinearLayout>(R.id.taskLinearLayout)
                        taskLayout.alpha = 0.3F
                    }

                    var finishedBtn = holder.mView.findViewById<ImageButton>(R.id.btnFinished)
                    finishedBtn.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            var taskLayout = holder.mView.findViewById<LinearLayout>(R.id.taskLinearLayout)
                            taskLayout.alpha = 0.3F

                            var model = Model(model.getTask(), model.getDescription(), model.getId(), model.getDate(), true)
                            reference.child(model.getId().toString()).setValue(model)

                        }
                    })
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.retrieved_layout, parent, false)
                    return MyViewHolder(view)
                }

            }

        recyclerView.adapter = adapter
        adapter.startListening()
    }

    class MyViewHolder(var mView: View) : RecyclerView.ViewHolder(
            mView
    ) {
        fun setTask(task: String?) {
            val taskTextView = mView.findViewById<TextView>(R.id.taskTv)
            taskTextView.text = task
        }

        fun setDesc(desc: String?) {
            val descTextView = mView.findViewById<TextView>(R.id.descriptionTv)
            descTextView.text = desc
        }

        fun setDate(date: String?) {
            val dateTextView = mView.findViewById<TextView>(R.id.dateTv)
            dateTextView.text = date
        }
    }

    private fun updateTask() {
        var myDialog = AlertDialog.Builder(this);
        var inflater = LayoutInflater.from(this);
        var view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView(view);

        var dialog = myDialog.create();

        var editedTask = view.findViewById<EditText>(R.id.mEditTask);
        var editedDesc = view.findViewById<EditText>(R.id.mEditTaskDesc);

        editedTask.setText(task);
        editedTask.setSelection(task.length);

        editedDesc.setText(description);
        editedDesc.setSelection(description.length);

        var deleteBtn = view.findViewById<Button>(R.id.btnDelete);
        var updateBtn = view.findViewById<Button>(R.id.btnUpdate);

        updateBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                task = editedTask.text.toString().trim()
                description = editedDesc.text.toString().trim()
                var date = DateFormat.getDateInstance().format(Date())

                var model = Model(task, description, key, date, false)

                reference.child(key).setValue(model).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                                this@TaskActivity,
                                "Task has been updated successfully",
                                Toast.LENGTH_LONG
                        ).show()
                    } else {
                        var error = task.exception.toString()
                        Toast.makeText(
                                this@TaskActivity,
                                "Failed $error",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }

                dialog.dismiss()

            }
        })

        deleteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                reference.child(key).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                                this@TaskActivity,
                                "Task deleted successfully",
                                Toast.LENGTH_LONG
                        ).show()
                    } else {
                        var error = task.exception.toString()
                        Toast.makeText(
                                this@TaskActivity,
                                "Failed $error",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }

                dialog.dismiss()

            }
        })

        dialog.show();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                mAuth.signOut()
                val intent = Intent(this@TaskActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
