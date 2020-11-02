package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ListAvailableArcSectionTypesToAddToMoralArgument
import com.soyle.stories.gui.View
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArcReceiver
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.outlineMoralArgument.GetMoralArgumentFrame
import com.soyle.stories.theme.usecases.outlineMoralArgument.OutlineMoralArgumentForCharacterInTheme
import java.util.*

class MoralArgumentPresenter(
    themeId: String,
    private val view: View.Nullable<MoralArgumentViewModel>
) :
    GetMoralArgumentFrame.OutputPort,
    OutlineMoralArgumentForCharacterInTheme.OutputPort,
    ListAvailableArcSectionTypesToAddToMoralArgument.OutputPort,
    ListAvailablePerspectiveCharacters.OutputPort,

    ArcSectionAddedToCharacterArcReceiver
{

    private val themeId = UUID.fromString(themeId)

    override suspend fun receiveMoralArgumentFrame(response: GetMoralArgumentFrame.ResponseModel) {
        view.update {
            MoralArgumentViewModel(
                moralProblemLabel = "Moral Problem",
                moralProblemValue = response.moralProblem,
                themeLineLabel = "Theme Line",
                themeLineValue = response.themeLine,
                thematicRevelationLabel = "Thematic Revelation",
                thematicRevelationValue = response.thematicRevelation,
                perspectiveCharacterLabel = "Perspective Character",
                noPerspectiveCharacterLabel = "-Select Perspective Character-",
                selectedPerspectiveCharacter = null,
                availablePerspectiveCharacters = null,
                loadingPerspectiveCharactersLabel = "Loading ...",
                loadingSectionTypesLabel = "Loading ...",
                createCharacterLabel = "Create Character",
                unavailableCharacterMessage = {
                    "${it.characterName} is a minor character.  By selecting them, they will " +
                            "gain a character arc for this theme and become a major character."
                },
                unavailableSectionTypeMessage = {
                    "${it.sectionTypeName} has already been used.  By selecting this, the section " +
                            "of this type will be moved to this position."
                },
                sections = null,
                availableSectionTypes = null
            )
        }
    }

    override suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters) {
        view.updateOrInvalidated {
            copy(
                availablePerspectiveCharacters = response.map {
                    AvailablePerspectiveCharacterViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.isMajorCharacter
                    )
                }
            )
        }
    }

    override suspend fun receiveMoralArgumentOutlineForCharacterInTheme(response: OutlineMoralArgumentForCharacterInTheme.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                selectedPerspectiveCharacter = CharacterItemViewModel(
                    response.characterId.toString(),
                    response.characterName,
                    ""
                ),
                sections = response.characterArcSections.map {
                    MoralArgumentSectionViewModel(
                        it.arcSectionId.toString(),
                        it.sectionTemplateName,
                        it.arcSectionValue
                    )
                }
            )
        }
    }

    override suspend fun receiveAvailableArcSectionTypesToAddToMoralArgument(response: ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != response.characterId.toString())
                return@updateOrInvalidated this
            copy(
                availableSectionTypes = response.map {
                    MoralArgumentSectionTypeViewModel(
                        it.sectionTemplateId.toString(),
                        it.sectionTemplateName,
                        it.canBeCreated
                    )
                }
            )
        }
    }

    override suspend fun receiveArcSectionAddedToCharacterArc(event: ArcSectionAddedToCharacterArc) {
        if (event.themeId != themeId) return
        if (event.indexInMoralArgument == null) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != event.characterId.toString())
                return@updateOrInvalidated this
            copy(
                sections = sections?.toMutableList()?.apply {
                    add(
                        event.indexInMoralArgument!!,
                        MoralArgumentSectionViewModel(
                            event.characterArcSectionId.toString(),
                            event.templateSectionName,
                            event.value
                        )
                    )
                }
            )
        }
    }

}