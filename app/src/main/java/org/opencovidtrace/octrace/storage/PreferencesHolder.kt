package org.opencovidtrace.octrace.storage

import android.content.Context
import android.content.SharedPreferences
import org.opencovidtrace.octrace.di.ContextProvider

open class PreferencesHolder(private val preferencesName: String) {

    protected val context by ContextProvider()

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }


    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun setString(key: String, value: String?) {
        withEditor { editor -> editor.putString(key, value) }
    }

    fun getInt(key: String): Int {
        return preferences.getInt(key, 0)
    }

    fun setInt(key: String, value: Int) {
        withEditor { editor -> editor.putInt(key, value) }
    }

    fun setBoolean(key: String, value: Boolean) {
        withEditor { editor -> editor.putBoolean(key, value) }
    }

    fun remove(key: String) {
        withEditor { editor -> editor.remove(key) }
    }

    private fun withEditor(handler: (SharedPreferences.Editor) -> Unit) {
        val editor = preferences.edit()
        handler(editor)
        editor.apply()
    }


}
