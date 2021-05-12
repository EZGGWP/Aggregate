package com.belov.agregator.auth

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.belov.agregator.*
import com.belov.agregator.database.DatabaseManager
import com.belov.agregator.profile.ProfileBase
import com.google.android.material.tabs.TabLayout
import kotlin.properties.Delegates

class AuthFragment() : Fragment() {
    lateinit var button: Button
    lateinit var loginField: EditText
    lateinit var passwordField: EditText
    lateinit var proofField: EditText
    var regMode by Delegates.notNull<Boolean>()
    lateinit var db: DatabaseManager
    lateinit var parent: FragmentActivity

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        regMode = arguments?.getBoolean("regMode")!!
        db = ((activity as MainActivity).applicationContext as App).databaseManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return when (regMode) {
            false -> {                  // Авторизация
                val layout = inflater.inflate(R.layout.login_layout, container, false)
                loginField = layout.findViewById(R.id.login_field)
                passwordField = layout.findViewById(R.id.password_field)
                button = layout.findViewById(R.id.authButton)
                button.setOnClickListener {
                    db.setCurrentUserNameAndGetHash(loginField.text.toString())
                    if (db.getHash() != null) {
                        val result = verifyHash(passwordField.text.toString(), db.getHash()!!)
                        if (result.verified) {
                            Toast.makeText(context, "Добро пожаловать", Toast.LENGTH_LONG).show()

                            val sharedPref = activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)
                            sharedPref?.edit {
                                putString("authedUser", loginField.text.toString())
                                apply()
                            }

                            val intent = Intent(activity, ProfileBase::class.java)
                            startActivity(intent)

                            loginField.text.clear();
                            loginField.clearFocus();
                            passwordField.text.clear();
                            passwordField.clearFocus();
                        } else {
                            Toast.makeText(context, "Неверный пароль", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Пользователь не найден")
                                .setMessage("Пользователь с именем ${loginField.text} не найден. Хотите зарегистрироваться?")
                                .setPositiveButton("Да") { _: DialogInterface, _: Int -> activity?.findViewById<TabLayout>(R.id.tabLayout)?.getTabAt(1)?.select() }
                                .setNegativeButton("Нет", null).show()
                    }
                }

                layout
            }
            true -> {                   // Регистрация
                val layout = inflater.inflate(R.layout.register_layout, container, false)
                loginField = layout.findViewById(R.id.login_field)
                passwordField = layout.findViewById(R.id.password_field)
                proofField = layout.findViewById(R.id.proof_field)
                button = layout.findViewById(R.id.authButton)
                button.setOnClickListener {

                    if (loginField.text.length in 3..20) {
                        if (passwordField.text.toString().length in 5..30) {
                            if (passwordField.text.toString() == proofField.text.toString()) {
                                if (db.getNameCount(loginField.text.toString())) {
                                    db.addUser(
                                        loginField.text.toString(),
                                        createHash(passwordField.text.toString())
                                    )
                                    Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_LONG).show()

                                    val sharedPref = activity?.getSharedPreferences(getString(R.string.auth_prefs), Context.MODE_PRIVATE)
                                    sharedPref?.edit {
                                        putString("authedUser", loginField.text.toString())
                                        commit()
                                    }

                                    val intent = Intent(activity, ProfileBase::class.java)
                                    startActivity(intent)

                                    loginField.text.clear();
                                    loginField.clearFocus();
                                    passwordField.text.clear();
                                    passwordField.clearFocus();
                                    proofField.text.clear();
                                    proofField.clearFocus();
                                } else  {
                                    Toast.makeText(context, "Имя пользователя занято", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Пароль должен быть от 5 до 30 символов",Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Логин должен быть от 3 до 20 символов", Toast.LENGTH_LONG).show()
                    }
                }
                layout
            }
        }
    }
}