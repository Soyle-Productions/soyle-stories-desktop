package com.soyle.stories.characterarc.characterList

import com.soyle.stories.di.characterarc.CharacterArcComponent
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:44 AM
 */
class CharacterList : View("Characters") {


    override val root = stackpane {
        hgrow = Priority.SOMETIMES
        vgrow = Priority.ALWAYS
        this += find<PopulatedDisplay>()
        this += find<EmptyDisplay>()
    }

    init {
        val characterListViewListener = find<CharacterArcComponent>().characterListViewListener
        val model = find<CharacterListModel>()
        model.invalid.onChange {
            if (it) characterListViewListener.getList()
        }
    }
}