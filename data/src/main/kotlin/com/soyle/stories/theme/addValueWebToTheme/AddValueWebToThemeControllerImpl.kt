package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import java.util.*

class AddValueWebToThemeControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addValueWebToTheme: AddValueWebToTheme,
    private val addValueWebToThemeOutputPort: AddValueWebToTheme.OutputPort
) : AddValueWebToThemeController {

    override fun addValueWebToTheme(themeId: String, name: String, onError: (Throwable) -> Unit) {
        val preparedThemeId = UUID.fromString(themeId)
        threadTransformer.async {
            try {
                addValueWebToTheme.invoke(
                    preparedThemeId,
                    name,
                    addValueWebToThemeOutputPort
                )
            } catch (t: Throwable) { onError(t) }
        }
    }

}