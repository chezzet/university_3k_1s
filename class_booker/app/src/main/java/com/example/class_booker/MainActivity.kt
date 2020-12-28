package com.example.class_booker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.class_booker.data.UserHolder
import com.example.class_booker.data.model.UserCompanion.USER_ID
import com.example.class_booker.data.model.UserCompanion.USER_NAME
import com.example.class_booker.data.model.UserCompanion.USER_ROLE
import com.example.class_booker.login.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

const val SHARED_PREFERENCES_NAME = "user"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        if (!sharedPreferences.contains(USER_ID)) {
            startActivityForResult(Intent(this, LoginActivity::class.java), 3)
        } else {
            populateUserHolder(sharedPreferences)
            SyncIntentService.startSync(applicationContext)

            createView()
        }
    }

    private fun createView() {
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_overview, R.id.navigation_rooms
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sync_item -> {
                SyncIntentService.startSync(this)
                true
            }
            R.id.logout_item -> {
                logout()
                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && resultCode == RESULT_OK) {
            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

            val edit = sharedPreferences.edit()

            edit.putString(USER_ID, UserHolder.id.toString())
            edit.putString(USER_NAME, UserHolder.name)
            edit.putInt(USER_ROLE, UserHolder.role)

            edit.apply()

            createView()
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        val edit = sharedPreferences.edit()

        edit.remove(USER_ID)
        edit.remove(USER_NAME)
        edit.remove(USER_ROLE)

        edit.apply()
        finish()
    }

    private fun populateUserHolder(sharedPreferences: SharedPreferences) {
        try {
            UserHolder.id = UUID.fromString(sharedPreferences.getString(USER_ID, ""))
            UserHolder.name = sharedPreferences.getString(USER_NAME, "").toString()
            UserHolder.role = sharedPreferences.getInt(USER_ROLE, 0)
        } catch (e: Exception) {
            startActivityForResult(Intent(this, LoginActivity::class.java), 3)
        }
    }
}