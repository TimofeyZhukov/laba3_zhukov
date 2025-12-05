package com.example.laba3_zhukov

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.laba3_zhukov.databinding.ActivityMainBinding
import com.example.laba3_zhukov.ui.screens.FavoritesFragment
import com.example.laba3_zhukov.ui.screens.HomeFragment
import com.example.laba3_zhukov.ui.screens.SettingsFragment
import com.example.laba3_zhukov.data.repository.SharedPrefManager
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context) {
        try {
            val prefs = SharedPrefManager(newBase)
            val lang = prefs.getLanguage()
            val locale = Locale(lang)
            Locale.setDefault(locale)

            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            val context = newBase.createConfigurationContext(config)
            super.attachBaseContext(context)
        } catch (e: Exception) {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MAIN_ACTIVITY", "MainActivity создан")

        if (savedInstanceState == null) {
            showHomeFragment()
        }
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    Log.d("NAVIGATION", "Выбрана Главная")
                    showHomeFragment()
                }
                R.id.favoritesFragment -> {
                    Log.d("NAVIGATION", "Выбрано Избранное")
                    showFavoritesFragment()
                }
                R.id.settingsFragment -> {
                    Log.d("NAVIGATION", "Выбраны Настройки")
                    showSettingsFragment()
                }
            }
            true
        }
    }

    internal fun showHomeFragment() {
        Log.d("MAIN_ACTIVITY", "Показываем HomeFragment")
        showFragment(HomeFragment())
    }

    internal fun showFavoritesFragment() {
        Log.d("MAIN_ACTIVITY", "Показываем FavoritesFragment")
        showFragment(FavoritesFragment())
    }

    internal fun showSettingsFragment() {
        Log.d("MAIN_ACTIVITY", "Показываем SettingsFragment")
        showFragment(SettingsFragment())
    }

    private fun showFragment(fragment: Fragment) {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit()
            Log.d("MAIN_ACTIVITY", "Фрагмент успешно показан")
        } catch (e: Exception) {
            Log.e("MAIN_ACTIVITY", "Ошибка при показе фрагмента: ${e.message}")
        }
    }
}