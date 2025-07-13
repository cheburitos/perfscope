package com.perfscope.settings

import com.perfscope.SceneRegistry
import kotlin.collections.forEach
import kotlin.text.contains
import kotlin.text.endsWith
import kotlin.text.startsWith

class ThemeManager {

    private lateinit var sceneRegistry: SceneRegistry

    var currentTheme: Theme = Theme.DARK

    fun changeTheme(theme: Theme) {
        sceneRegistry.scenes().forEach { scene ->
            scene.stylesheets.removeIf {
                it.startsWith("themes/")
                        && it.endsWith("css")
                        && it.contains("perfscope", ignoreCase = true)
            }

            scene.stylesheets.removeIf { it.contains("perfscope") }
            scene.stylesheets.addAll(0, theme.cssPaths)
        }

        this.currentTheme = theme
    }

    fun setSceneRegistry(value: SceneRegistry) {
        this.sceneRegistry = value
    }
}