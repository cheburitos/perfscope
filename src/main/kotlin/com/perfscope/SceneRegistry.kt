package com.perfscope

import com.perfscope.settings.FontStyleUpdater
import com.perfscope.settings.Settings
import com.perfscope.settings.ThemeManager
import javafx.scene.Parent
import javafx.scene.Scene
import java.lang.ref.WeakReference
import kotlin.collections.forEach
import kotlin.collections.mapNotNull
import kotlin.collections.toList

open class SceneRegistry(
        private val themeManager: ThemeManager,
        private val fontStyleUpdater: FontStyleUpdater,
        private val settings: Settings) {

    private var scenes: MutableSet<WeakReference<Scene>> = mutableSetOf()

    fun newScene(parent: Parent, width: Double, height: Double): Scene {
        val scene = Scene(parent, width, height)

        scenes.add(WeakReference(scene))

        scene.stylesheets.addAll(themeManager.currentTheme.cssPaths)
        fontStyleUpdater.update(scene, settings)
        return scene
    }

    private fun cleanup() {
        val toRemove = mutableSetOf<WeakReference<Scene>>()

        scenes.forEach {
            if (it.get() == null) {
                toRemove.add(it)
            }
        }

        scenes.removeAll(toRemove)
    }

    fun scenes(): List<Scene> {
        cleanup()
        return scenes.mapNotNull { it.get() }.toList()
    }
}