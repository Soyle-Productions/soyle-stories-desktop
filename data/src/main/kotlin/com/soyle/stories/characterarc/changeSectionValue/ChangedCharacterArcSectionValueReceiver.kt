package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue

interface ChangedCharacterArcSectionValueReceiver {

    suspend fun receiveChangedCharacterArcSectionValue(changedCharacterArcSectionValue: ChangedCharacterArcSectionValue)

}