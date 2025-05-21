package com.example.habitmate

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habitmate.adapter.DateProgressAdapter
import com.example.habitmate.model.Habit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HabitProgressActivity : AppCompatActivity() {

    private lateinit var textProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerDates: RecyclerView
    private lateinit var btnSaveProgress: Button
    private lateinit var habitTitleText: TextView
    private lateinit var periodicityText: TextView

    private lateinit var habit: Habit
    private var habitId: String? = null
    private lateinit var userId: String

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_progress)

        textProgress = findViewById(R.id.textProgress)
        progressBar = findViewById(R.id.progressBar)
        recyclerDates = findViewById(R.id.recyclerDates)
        btnSaveProgress = findViewById(R.id.btnSaveProgress)
        habitTitleText = findViewById(R.id.textHabitTitle)
        periodicityText = findViewById(R.id.textPeriodicity)

        habit = intent.getSerializableExtra("habit") as? Habit ?: return
        habitId = intent.getStringExtra("habitId") ?: return
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        habitTitleText.text = "Привычка: ${habit.title}"
        periodicityText.text = "Периодичность: ${habit.periodicity}"

        val upcomingDates = generateNextNDates(14)
        recyclerDates.layoutManager = LinearLayoutManager(this)
        recyclerDates.adapter = DateProgressAdapter(upcomingDates, habit.progress)

        updateProgressBar()

        btnSaveProgress.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance()
                .getReference("habits")
                .child(userId)
                .child(habitId!!)
                .child("progress")

            dbRef.setValue(habit.progress).addOnSuccessListener {
                Toast.makeText(this, "Прогресс сохранён", Toast.LENGTH_SHORT).show()
                updateProgressBar()
            }
        }
    }

    private fun updateProgressBar() {
        val completed = habit.progress.count { it.value }
        val total = habit.progress.size
        val percent = if (total > 0) (completed * 100 / total) else 0

        progressBar.progress = percent
        textProgress.text = "Выполнено: $percent%"
    }

    private fun generateNextNDates(n: Int): List<String> {
        val calendar = Calendar.getInstance()
        return List(n) {
            val date = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            date
        }
    }
}