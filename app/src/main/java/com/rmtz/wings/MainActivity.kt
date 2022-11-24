package com.rmtz.wings

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import com.rmtz.wings.data.AppDatabase
import com.rmtz.wings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Room.databaseBuilder(
            baseContext,
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        val user = database.UserDao().getListUser()
        val temp = database.TempDao().getTemp()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        Log.e("wLog", "DB User: $user")
        if (user.isEmpty()) {
            graph.setStartDestination(R.id.LoginFragment)
        } else {
            if (temp.isEmpty()) {
                graph.setStartDestination(R.id.LoginFragment)
            } else {
                graph.setStartDestination(R.id.ProductFragment)
            }
        }

        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_transaction_history -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}