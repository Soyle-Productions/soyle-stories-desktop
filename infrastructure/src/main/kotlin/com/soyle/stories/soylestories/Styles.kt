package com.soyle.stories.soylestories

import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.image.Image
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.importStylesheet
import tornadofx.loadFont

class Styles : Stylesheet() {

    companion object {

        val Purple = Color.web("#862F89")
        val Orange = Color.web("#D38147")
        val Blue = Color.web("#3A518E")

        val appIcon = Image("com/soyle/stories/soylestories/icon.png")
        val logo = Image("com/soyle/stories/soylestories/bronze logo.png")

        init {
            loadFont("/com/soyle/stories/soylestories/corbel/CORBEL.TTF", 14)!!
            loadFont("/com/soyle/stories/soylestories/corbel/CORBELB.TTF", 14)!!
        }

    }

    init {
        root {
            accentColor = Blue
            focusColor = Purple
        }
    }

}