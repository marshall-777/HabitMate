package com.example.habitmate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.habitmate.model.Habit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.snackbar.Snackbar

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var periodicitySpinner: AutoCompleteTextView
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var checkBoxes: List<CheckBox>
    private lateinit var layoutDays: LinearLayout
    private lateinit var btnViewProgress: Button


    private var selectedDates = mutableListOf<String>()
    private var habitId: String? = null // ID привычки в Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        titleEditText = findViewById(R.id.edit_habit_title)
        periodicitySpinner = findViewById(R.id.spinner_periodicity)
        dateEditText = findViewById(R.id.edit_date)
        timeEditText = findViewById(R.id.edit_time)
        saveButton = findViewById(R.id.btn_save)
        layoutDays = findViewById(R.id.layout_days)

        checkBoxes = listOf(
            findViewById(R.id.cb_mon), findViewById(R.id.cb_tue),
            findViewById(R.id.cb_wed), findViewById(R.id.cb_thu),
            findViewById(R.id.cb_fri), findViewById(R.id.cb_sat), findViewById(R.id.cb_sun)
        )

        val periodicityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.periodicity_array,
            android.R.layout.simple_dropdown_item_1line
        )
        periodicitySpinner.setAdapter(periodicityAdapter)

        // Слушатели для чекбоксов — обновляем даты при изменении
        checkBoxes.forEach { cb ->
            cb.setOnCheckedChangeListener { _, _ -> generateWeekDaysDates() }
        }

        periodicitySpinner.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> { // Ежедневно
                    layoutDays.visibility = LinearLayout.GONE
                    selectedDates.clear()
                    dateEditText.setText("")
                    dateEditText.setOnClickListener {
                        Toast.makeText(this, "Дата не требуется для ежедневных привычек", Toast.LENGTH_SHORT).show()
                    }
                }
                1 -> { // Через день
                    layoutDays.visibility = LinearLayout.GONE
                    generateAlternateDates()
                }
                else -> { // По дням недели
                    layoutDays.visibility = LinearLayout.VISIBLE
                    generateWeekDaysDates()
                }
            }
        }

        dateEditText.setOnClickListener {
            if (periodicitySpinner.text.toString() == "Ежедневно") {
                Toast.makeText(this, "Дата не требуется для ежедневных привычек", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showDatePicker()
        }

        timeEditText.setOnClickListener {
            showTimePicker()
        }

        // Проверка редактирования
        val habit = intent.getSerializableExtra("habit") as? Habit
        habit?.let {
            habitId = it.id
            titleEditText.setText(it.title)
            periodicitySpinner.setText(it.periodicity, false)
            timeEditText.setText(it.time)
            dateEditText.setText(it.startDate)
            it.days.forEach { dayIndex ->
                if (dayIndex in 1..7) checkBoxes[dayIndex - 1].isChecked = true
            }
        }

        saveButton.setOnClickListener {
            saveHabit()
        }
        btnViewProgress = findViewById(R.id.btn_view_progress)

        btnViewProgress.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val periodicity = periodicitySpinner.text.toString()
            val time = timeEditText.text.toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            val selectedDays = checkBoxes.mapIndexedNotNull { index, cb -> if (cb.isChecked) index + 1 else null }
            val startDate = if (periodicity == "Через день") {
                selectedDates.firstOrNull() ?: ""
            } else {
                dateEditText.text.toString()
            }

            val habit = Habit(
                id = habitId ?: UUID.randomUUID().toString(),
                title = title,
                description = "",
                periodicity = periodicity,
                days = selectedDays,
                startDate = startDate,
                time = time,
                userId = userId
            )

            val intent = Intent(this, HabitProgressActivity::class.java)
            intent.putExtra("habit", habit)
            intent.putExtra("habitId", habit.id)
            startActivity(intent)
        }
    }

    private fun generateAlternateDates() {
        selectedDates.clear()
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        for (i in 0..15) {
            selectedDates.add(format.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 2)
        }

        dateEditText.setText(selectedDates.joinToString(", "))
    }

    private fun generateWeekDaysDates() {
        val selectedDays = checkBoxes
            .mapIndexedNotNull { index, cb -> if (cb.isChecked) index + 1 else null }
        if (selectedDays.isEmpty()) {
            dateEditText.setText("")
            return
        }

        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dates = mutableListOf<String>()

        while (dates.size < 15) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // Воскресенье=1 ... Суббота=7
            val normalizedDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1

            if (selectedDays.contains(normalizedDay)) {
                dates.add(format.format(calendar.time))
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        dateEditText.setText(dates.joinToString(", "))
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance()
                selected.set(year, month, dayOfMonth)
                val dateStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selected.time)
                dateEditText.setText(dateStr)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                val timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                timeEditText.setText(timeStr)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun saveHabit() {
        val title = titleEditText.text.toString().trim()
        val periodicity = periodicitySpinner.text.toString()
        val time = timeEditText.text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val selectedDays = checkBoxes.mapIndexedNotNull { index, cb -> if (cb.isChecked) index + 1 else null }

        val startDate = if (periodicity == "Через день") {
            selectedDates.firstOrNull() ?: ""
        } else {
            dateEditText.text.toString()
        }

        val habit = Habit(
            id = habitId ?: UUID.randomUUID().toString(),
            title = title,
            description = "",
            periodicity = periodicity,
            days = selectedDays,
            startDate = startDate,
            time = time,
            userId = userId
        )

        val dbRef = FirebaseDatabase.getInstance().getReference("habits").child(userId)
        dbRef.child(habit.id).setValue(habit).addOnSuccessListener {
            Snackbar.make(saveButton, "Привычка сохранена", Snackbar.LENGTH_SHORT).show()
            MediaPlayer.create(this, R.raw.progress_sound).start()
            val intent = Intent(this, HabitProgressActivity::class.java)
            intent.putExtra("habit", habit)
            intent.putExtra("habitId", habit.id)
            startActivity(intent)
            finish()
        }
    }
}