package com.soyle.stories.character.renameCharacter

import com.soyle.stories.common.Notifier
import com.soyle.stories.entities.CharacterRenamed

class CharacterRenamedNotifier : CharacterRenamedReceiver, Notifier<CharacterRenamedReceiver>() {

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        notifyAll { it.receiveCharacterRenamed(characterRenamed) }
    }

}