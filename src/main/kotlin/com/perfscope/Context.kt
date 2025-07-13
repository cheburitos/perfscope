package com.perfscope

import com.perfscope.settings.FontStyleUpdater
import com.perfscope.settings.RecordingListStyleUpdater
import com.perfscope.settings.Settings
import com.perfscope.settings.SettingsFileStorage
import com.perfscope.settings.Theme
import com.perfscope.settings.ThemeManager;
import javafx.application.Platform;

class Context {

    val themeManager = ThemeManager()
    val settingsFileStorage = SettingsFileStorage()
    val sceneRegistry: SceneRegistry
    val fontStyleUpdater = FontStyleUpdater()
    val recordingListStyleUpdater = RecordingListStyleUpdater()
    val settings: Settings

    init {
        settings = settingsFileStorage.read()
        sceneRegistry = SceneRegistry(themeManager, fontStyleUpdater, settings)
        themeManager.setSceneRegistry(sceneRegistry)
        bindSettings()
    }

    fun bindSettings() {
        settings.systemFontName.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.systemFontSize.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.recordingTreeFontSpacing.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.recordingTreeFontName.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.recordingTreeFontSize.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.recordingTreeBoldElements.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.recordingTreeBoldElements.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.recordingListSpacing.addListener { _, _, _ ->
            recordingListStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.theme.addListener { _, _, theme ->
            themeManager.changeTheme(Theme.valueOf(theme))
        }
    }

    fun onAfterStart() {
        Platform.runLater {
            fontStyleUpdater.update(App.stage.scene, settings)
            recordingListStyleUpdater.update(sceneRegistry.scenes(), settings)
        }

        themeManager.changeTheme(Theme.valueOf(settings.theme.get()))
    }
}