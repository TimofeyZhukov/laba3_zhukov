package com.example.laba3_zhukov.ui.screens

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.laba3_zhukov.R
import com.example.laba3_zhukov.data.repository.SharedPrefManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var languageSpinner: Spinner
    private lateinit var themeSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var prefs: SharedPrefManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        languageSpinner = view.findViewById(R.id.languageSpinner)
        themeSpinner = view.findViewById(R.id.themeSpinner)
        saveButton = view.findViewById(R.id.saveButton)
        prefs = SharedPrefManager(requireContext())
        val textColor = ContextCompat.getColor(requireContext(), R.color.genre_text)
        val languages = resources.getStringArray(R.array.languages_array).toList()
        val themes = resources.getStringArray(R.array.themes_array).toList()

        val langAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, languages) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as? TextView)?.setTextColor(textColor)
                return v
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                (v as? TextView)?.setTextColor(textColor)
                return v
            }
        }
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = langAdapter

        val themeAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, themes) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as? TextView)?.setTextColor(textColor)
                return v
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                (v as? TextView)?.setTextColor(textColor)
                return v
            }
        }
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = themeAdapter

        val langCode = prefs.getLanguageCode()
        languageSpinner.setSelection(if (langCode == "ru") 0 else 1)

        val isNight = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        themeSpinner.setSelection(if (isNight) 1 else 0)

        saveButton.setOnClickListener {
            saveSettings()
        }

        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            try {
                val mainActivity = activity as? com.example.laba3_zhukov.MainActivity
                if (mainActivity != null) {
                    mainActivity.showHomeFragment()
                    val bottomNav = mainActivity.findViewById<BottomNavigationView>(R.id.bottom_nav)
                    bottomNav.selectedItemId = R.id.homeFragment
                } else {
                    if (!requireActivity().supportFragmentManager.popBackStackImmediate()) {
                        requireActivity().onBackPressed()
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Ошибка при нажатии назад: ${e.message}")
            }
        }
    }

    private fun saveSettings() {
        val langPos = languageSpinner.selectedItemPosition
        val langCode = if (langPos == 0) "ru" else "en"
        prefs.setLanguage(langCode)
        applyLocale(langCode)

        val themePos = themeSpinner.selectedItemPosition
        if (themePos == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            prefs.setTheme("light")
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            prefs.setTheme("dark")
        }

        Toast.makeText(requireContext(), getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
    }

    private fun applyLocale(langCode: String) {
        try {
            val locale = Locale(langCode)
            Locale.setDefault(locale)
            val res = requireActivity().resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requireActivity().baseContext.createConfigurationContext(config)
            } else {
                @Suppress("DEPRECATION")
                res.updateConfiguration(config, res.displayMetrics)
            }
            requireActivity().recreate()
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Не удалось применить локаль: ${e.message}")
        }
    }

    private fun resolveAttrColor(context: Context, attr: Int): Int {
        val tv = TypedValue()
        val theme = context.theme
        val resolved = theme.resolveAttribute(attr, tv, true)
        return if (resolved) {
            if (tv.resourceId != 0) ContextCompat.getColor(context, tv.resourceId) else tv.data
        } else {
            ContextCompat.getColor(context, android.R.color.primary_text_dark)
        }
    }
}