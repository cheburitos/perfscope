package com.perfscope.settings

enum class Theme(val cssPaths: List<String>) {

    LIGHT(listOf("themes/perfscope-base.css", "themes/light/perfscope-light.css")),
    DARK(listOf("themes/perfscope-base.css", "themes/dark/perfscope-dark.css"));
}