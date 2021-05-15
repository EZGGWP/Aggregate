package com.belov.agregator.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.belov.agregator.App
import com.belov.agregator.R
import com.belov.agregator.storage.GithubDataStorage
import com.belov.agregator.storage.SpotifyDataStorage
import com.belov.agregator.storage.SteamDataStorage
import com.belov.agregator.utilities.Achievement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type


class AchievementAdapter(val ach: List<Achievement>, val app: App) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {
    var isJsonUpdateNeeded = false

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

        holder.achProgress.progressDrawable = AppCompatResources.getDrawable(app.applicationContext, R.drawable.progress_style)



        if (item.progress >= item.goal) {
            holder.achProgress.max = item.goal
            holder.achProgress.progress = item.goal

            when (item::class.java) {
                SteamDataStorage.SteamAchievement::class.java -> {
                    val array = app.databaseManager.achJson.getAsJsonObject("achievements").getAsJsonArray("Steam")
                    if (!array.contains(JsonPrimitive(ach[position].id))) {
                        array.add(ach[position].id)
                        isJsonUpdateNeeded = true
                    }
                }
                GithubDataStorage.GithubAchievement::class.java -> {
                    val array = app.databaseManager.achJson.getAsJsonObject("achievements").getAsJsonArray("GitHub")
                    if (!array.contains(JsonPrimitive(ach[position].id))) {
                        array.add(ach[position].id)
                        isJsonUpdateNeeded = true
                    }
                }
                SpotifyDataStorage.SpotifyAchievement::class.java -> {
                    val array = app.databaseManager.achJson.getAsJsonObject("achievements").getAsJsonArray("Spotify")
                    if (!array.contains(JsonPrimitive(ach[position].id))) {
                        array.add(ach[position].id)
                        isJsonUpdateNeeded = true
                    }
                }
            }

        } else holder.achProgress.progress = ((item.progress.toDouble()/item.goal.toDouble())*100).toInt()

        holder.achText.text = "${item.progress}/${item.goal}"
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (isJsonUpdateNeeded) {
            app.databaseManager.saveUserAchievementsJson()
            isJsonUpdateNeeded = false
        }
    }

    override fun getItemCount(): Int {
        return ach.size
    }

}