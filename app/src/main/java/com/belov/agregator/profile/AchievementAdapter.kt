package com.belov.agregator.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.belov.agregator.R
import com.belov.agregator.utilities.Achievement
import java.lang.reflect.Type


class AchievementAdapter(val ach: List<Achievement>) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val achName = view.findViewById<TextView>(R.id.ach_name)
        val achProgress = view.findViewById<ProgressBar>(R.id.ach_progress)
        val achText = view.findViewById<TextView>(R.id.ach_progress_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.achievement, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ach[position]
        holder.achName.text = item.name

        if (item.progress >= item.goal) {
            holder.achProgress.max = item.goal
            holder.achProgress.progress = item.goal
        } else holder.achProgress.progress = ((item.progress.toDouble()/item.goal.toDouble())*100).toInt()

        holder.achText.text = "${item.progress}/${item.goal}"

    }

    override fun getItemCount(): Int {
        return ach.size
    }

}