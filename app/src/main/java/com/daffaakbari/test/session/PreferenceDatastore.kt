package com.daffaakbari.test.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class PreferenceDatastore(context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
    var pref = context.dataStore

    companion object {
        var username = stringPreferencesKey("USERNAME")
        var email = stringPreferencesKey("EMAIL")
    }

    suspend fun setSession(sessionModel: SessionModel) {
        pref.edit {
            it[username] = sessionModel.username
            it[email] = sessionModel.email
        }
    }

    fun getSession() = pref.data.map {
        SessionModel(
            username = it[username]?:"",
            email = it[email]?:""
        )
    }
}