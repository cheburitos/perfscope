package com.perfscope.settings

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.readText
import kotlin.io.writeText

class SettingsFileStorage {

    private val file = File("./settings.json")

    fun read(): Settings {
        val settings = if (file.exists()) {
            val settingsJson = file.readText(Charsets.UTF_8)
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString(settingsJson)
        } else {
            val settings = Settings()
            this.writeToFile(settings)
            settings
        }
        settings.addListener {
                _, _, _ -> writeToFile(settings)
        }
        return settings
    }

    private fun writeToFile(settings: Settings) {
        val encodeToString = Json.encodeToString(settings)
        file.writeText(encodeToString, Charsets.UTF_8)
    }
}