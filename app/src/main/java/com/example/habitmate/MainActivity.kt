package com.example.habitmate

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habitmate.adapter.HabitAdapter
import com.example.habitmate.model.Habit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitAdapter
    private val habitList = mutableListOf<Habit>()

    private lateinit var dbRef: DatabaseReference
    private lateinit var profileName: TextView

    private var backgroundMusic: MediaPlayer? = null
    private var progressSound: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar с именем
        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        profileName = findViewById(R.id.profileName)

        // Имя пользователя
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        dbRef.child("name").get().addOnSuccessListener {
            profileName.text = it.value?.toString() ?: "Профиль"
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = HabitAdapter(
            habitList,
            onItemClick = { habit ->
                val intent = Intent(this, HabitDetailActivity::class.java)
                intent.putExtra("habit", habit)
                startActivity(intent)
            },
            onEditClick = { habit ->
                val intent = Intent(this, HabitDetailActivity::class.java)
                intent.putExtra("habit", habit)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@HabitAdapter
                val dbRef = FirebaseDatabase.getInstance().getReference("habits").child(userId)
                dbRef.orderByChild("title").equalTo(habit.title).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            item.ref.removeValue()
                        }
                        Toast.makeText(this@MainActivity, "Удалено", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MainActivity, "Ошибка: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        )
        recyclerView.adapter = adapter

        val fabAdd: FloatingActionButton = findViewById(R.id.fabAddHabit)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, HabitDetailActivity::class.java))
        }

        val habitRef = FirebaseDatabase.getInstance().getReference("habits").child(userId)
        habitRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                habitList.clear()
                for (habitSnap in snapshot.children) {
                    val habit = habitSnap.getValue(Habit::class.java)
                    habit?.let { habitList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Ошибка: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        backgroundMusic = MediaPlayer.create(this, R.raw.background_music)
        backgroundMusic?.isLooping = true
        backgroundMusic?.start()

        progressSound = MediaPlayer.create(this, R.raw.progress_sound)
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic?.release()
        progressSound?.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_music -> {
                val isPlaying = backgroundMusic?.isPlaying == true
                if (isPlaying) {
                    backgroundMusic?.pause()
                    Toast.makeText(this, "Музыка выключена", Toast.LENGTH_SHORT).show()
                } else {
                    backgroundMusic?.start()
                    Toast.makeText(this, "Музыка включена", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}