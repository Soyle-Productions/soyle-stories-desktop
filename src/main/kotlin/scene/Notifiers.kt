package com.soyle.stories.desktop.config.scene

import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.invalidateRemovedMentions.DetectInvalidatedMentionsOutput
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneNotifier
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneNotifier
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.scene.includeCharacterInScene.IncludedCharacterInSceneNotifier
import com.soyle.stories.scene.includeCharacterInScene.IncludedCharacterInSceneReceiver

object Notifiers {

    init {
        scoped<ProjectScope> {
            provide(IncludedCharacterInSceneReceiver::class) {
                IncludedCharacterInSceneNotifier()
            }
            provide(CharacterArcSectionsCoveredBySceneReceiver::class) {
                CharacterArcSectionsCoveredBySceneNotifier()
            }
            provide(CharacterArcSectionUncoveredInSceneReceiver::class) {
                CharacterArcSectionUncoveredInSceneNotifier()
            }
            provide(DetectInvalidatedMentions.OutputPort::class) {
                DetectInvalidatedMentionsOutput()
            }
        }
    }

}