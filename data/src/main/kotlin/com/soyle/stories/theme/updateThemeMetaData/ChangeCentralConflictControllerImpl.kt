package com.soyle.stories.theme.updateThemeMetaData

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.updateThemeMetaData.ChangeCentralConflict
import java.util.*

class ChangeCentralConflictControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeCentralConflict: ChangeCentralConflict,
    private val changeCentralConflictOutputPort: ChangeCentralConflict.OutputPort
) : ChangeCentralConflictController {

    override fun changeCentralConflict(themeId: String, centralConflict: String) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            changeCentralConflict.invoke(
                preparedThemeId, centralConflict,
                changeCentralConflictOutputPort
            )
        }
    }

}