package com.example.habitmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.habitmate.R

class DateProgressAdapter(
    private val dates: List<String>,
    private val progress: MutableMap<String, Boolean>
) : RecyclerView.Adapter<DateProgressAdapter.DateViewHolder>() {

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date_checkbox, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]
        holder.checkBox.text = date
        holder.checkBox.setOnCheckedChangeListener(null) // предотвращаем переиспользование
        holder.checkBox.isChecked = progress[date] == true

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            progress[date] = isChecked
        }
    }

    override fun getItemCount(): Int = dates.size
}