package com.example.habitmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habitmate.R
import com.example.habitmate.model.Habit

class HabitAdapter(
    private val habitList: List<Habit>,
    private val onItemClick: (Habit) -> Unit,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHabitTitle: TextView = itemView.findViewById(R.id.textHabitTitle)
        val textPeriodicity: TextView = itemView.findViewById(R.id.textPeriodicity)
        val textStartDate: TextView = itemView.findViewById(R.id.textStartDate)
        val textProgress: TextView = itemView.findViewById(R.id.textProgress)
        val btnEditHabit: Button = itemView.findViewById(R.id.btnEditHabit)
        val btnDeleteHabit: Button = itemView.findViewById(R.id.btnDeleteHabit)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(habitList[position])
                }
            }

            btnEditHabit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(habitList[position])
                }
            }

            btnDeleteHabit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(habitList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitList[position]
        holder.textHabitTitle.text = habit.title
        holder.textPeriodicity.text = "Периодичность: ${habit.periodicity}"
        holder.textStartDate.text = "Начало: ${habit.startDate.ifEmpty { "не указано" }}"

        // Прогресс привычки
        val completed = habit.progress.count { it.value }
        val total = habit.progress.size
        if (total > 0) {
            holder.textProgress.text = "Прогресс: $completed / $total"
        } else {
            holder.textProgress.text = "Прогресс: пока нет данных"
        }
    }

    override fun getItemCount(): Int = habitList.size
}