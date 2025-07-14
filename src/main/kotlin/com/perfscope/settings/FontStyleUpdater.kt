package com.perfscope.settings

import javafx.scene.Scene
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.collections.forEach
import kotlin.collections.indices
import kotlin.text.contains
import kotlin.text.toByteArray

class FontStyleUpdater {

    companion object {
        private const val STYLE_PREFIX = "call-tree-font-stylesheet"
    }

    private fun buildStyleSheet(settings: Settings): String {
        return """
        .root {
            -fx-font-family: ${settings.systemFontName.get()};
            -fx-font-size: ${settings.systemFontSize.get()}px;
        }
        .system-font-text {
            -fx-font-size: ${settings.systemFontSize.get()}px;
        }
        .perfscope-tooltip-text {
            -fx-font-size: ${settings.systemFontSize.get()}px;
        }
        .perfscope-call-tree {
            -fx-font-family: ${settings.callTreeFontName.get()};
            -fx-font-size: ${settings.callTreeFontSize.get()}px;
        }
        .perfscope-call-tree-call-node {
            -fx-min-height: -1;
            -fx-max-height: -1;
        }
        .perfscope-call-tree-view {
            -fx-fixed-cell-size: ${settings.callTreeFontSize.get() + settings.callTreeFontSpacing.get()}px;
        }
        .perfscope-smaller-text {
            -fx-font-size: ${(settings.callTreeFontSize.get() * 0.8).toInt()}px;
        }
        .perfscope-call-tree-bold {
            -fx-font-weight: ${(if (settings.callTreeBoldElements.get()) "bold" else "normal")};
        }
        """
    }

    fun update(scene: Scene, settings: Settings) {
        try {

            val path = Files.createTempFile(STYLE_PREFIX, null)
            path.toFile().deleteOnExit()
            Files.write(path, buildStyleSheet(settings).toByteArray(StandardCharsets.UTF_8), StandardOpenOption.WRITE)
            var index: Int? = null
            for (i in scene.stylesheets.indices) {
                if (scene.stylesheets[i].contains(STYLE_PREFIX)) {
                    index = i
                    break
                }
            }
            if (index != null) {
                scene.stylesheets[index] = path.toFile().toURI().toString()
            } else {
                scene.stylesheets.add(path.toFile().toURI().toString())
            }
        } catch (e: IOException) {
//            val errorPopup = applicationContext.getBean(
//                    ErrorModalView::class.java,
//                    "Could not change font size: " + e.message,
//                    ExceptionAsTextView(e)
//            )
//            errorPopup.show()
        }
    }

    fun update(scenes: List<Scene>, settings: Settings) {
        scenes.forEach { update(it, settings) }
    }
}