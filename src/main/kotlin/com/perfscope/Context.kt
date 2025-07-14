package com.perfscope

import com.perfscope.settings.FontStyleUpdater
import com.perfscope.settings.ThreadListStyleUpdater
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
    val threadListStyleUpdater = ThreadListStyleUpdater()
    val settings: Settings

    init {
        settings = settingsFileStorage.read()
        sceneRegistry = SceneRegistry(themeManager, fontStyleUpdater, settings)
        themeManager.setSceneRegistry(sceneRegistry)
        bindSettings()
    }

    private fun bindSettings() {
        settings.systemFontName.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.systemFontSize.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.callTreeFontSpacing.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.callTreeFontName.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.callTreeFontSize.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.callTreeBoldElements.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.callTreeBoldElements.addListener { _, _, _ ->
            fontStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.threadListSpacing.addListener { _, _, _ ->
            threadListStyleUpdater.update(sceneRegistry.scenes(), settings)
        }
        settings.theme.addListener { _, _, theme ->
            themeManager.changeTheme(Theme.valueOf(theme))
        }
    }

    fun onAfterStart() {
        Platform.runLater {
            fontStyleUpdater.update(App.stage.scene, settings)
            threadListStyleUpdater.update(sceneRegistry.scenes(), settings)
        }

        themeManager.changeTheme(Theme.valueOf(settings.theme.get()))
    }
}