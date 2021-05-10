package com.belov.agregator.profile

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.belov.agregator.App
import com.belov.agregator.R
import com.belov.agregator.utilities.Friendship
import com.belov.agregator.utilities.User
import kotlinx.android.synthetic.main.friend.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Friends : Fragment() {
    lateinit var currentUser: String
    lateinit var friendships: ArrayList<Friendship>
    lateinit var recycler: RecyclerView
    lateinit var app: App
    var users = arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = (activity as ProfileBase).app
        currentUser = arguments?.getString("user")!!
        friendships = (activity as ProfileBase).app.databaseManager.getFriends()
        users = (activity as ProfileBase).app.databaseManager.users

        addNamesToIDs()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.friends_layout, container, false)
        val addButton = layout.findViewById<Button>(R.id.add_friend_btn)
        val refresher = layout.findViewById<ImageView>(R.id.refresh)

        refresher.setImageResource(R.drawable.outline_refresh_24)
        refresher.setOnClickListener {
            var colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), resources.getColor(R.color.grey), resources.getColor(R.color.purple_500))
            colorAnimator.duration = 100
            colorAnimator.addUpdateListener {
                refresher.setColorFilter(it.animatedValue as Int)
            }
            colorAnimator.start()

            val adapter = (recycler.adapter as FriendsAdapter)

            val prevSize = adapter.friendsList.size
            val prevAccepts = countAcceptedFriendships(adapter.friendsList)

            friendships = app.databaseManager.getFriends()
            val newAccepts = countAcceptedFriendships(friendships)
            if (prevSize != friendships.size || (newAccepts != prevAccepts)) {
                addNamesToIDs()
                adapter.friendsList = friendships
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Нет новых запросов", Toast.LENGTH_LONG).show()
            }

            colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), resources.getColor(R.color.purple_500), resources.getColor(R.color.grey))
            colorAnimator.duration = 4000
            colorAnimator.addUpdateListener {
                refresher.setColorFilter(it.animatedValue as Int)
            }
            colorAnimator.start()
        }



        recycler = layout.findViewById(R.id.friends_recycler)
        recycler.adapter = FriendsAdapter(friendships, currentUser, requireContext(), app)
        recycler.layoutManager = LinearLayoutManager(context)
        addButton.setOnClickListener {
            val dialog = MaterialDialog(requireContext()).title(R.string.friend_search)
            dialog.show {

                input(hint = "Введите имя пользователя") { _, text ->
                    val friend = text.toString()
                    if (friend != currentUser) {
                        var isOkay = false
                        for (friendship in friendships) {
                            if (friendship.rUsername == friend && friendship.sUsername == currentUser) {
                                Toast.makeText(context, "Вы уже отправили заявку этому пользователю", Toast.LENGTH_LONG).show()
                            } else if (friendship.sUsername == friend && friendship.rUsername == currentUser) {
                                Toast.makeText(context, "Пользователь уже дружит с вами", Toast.LENGTH_LONG).show()
                            } else isOkay = true
                        }
                        if (friendships.size == 0) isOkay = true
                        if (isOkay) {
                            val id = users.find {
                                it.username == friend
                            }?.id

                            if (id != null) {
                                app.databaseManager.addFriendRequest(id)

                                friendships.add(Friendship(false, -1, id, currentUser, friend))

                                (recycler.adapter as FriendsAdapter).friendsList = friendships
                                (recycler.adapter as FriendsAdapter).notifyDataSetChanged()
                                Toast.makeText(context, "Запрос успешно отправлен", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Пользователь не найден", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Нельзя подружиться с собой", Toast.LENGTH_LONG).show()
                    }
                }
                dialog.getInputField().setBackgroundColor(Color.WHITE)
                positiveButton(null, "Добавить")
            }
        }

        return layout
    }

    fun addNamesToIDs() {
        for (friend in friendships) {
            friend.sUsername = users.find {
                it.id == friend.sId
            }!!.username

            friend.rUsername = users.find {
                it.id == friend.rId
            }!!.username
        }
    }

    fun countAcceptedFriendships(friendships: ArrayList<Friendship>): Int {
        var counter = 0
        for (friendship in friendships) {
            if (friendship.status) {
                counter++
            }
        }
        return counter
    }
}