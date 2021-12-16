package com.soyle.stories.domain.character

import com.soyle.stories.domain.character.exceptions.CharacterException
import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import com.soyle.stories.domain.validation.ValidationException
import java.util.*


data class CharacterDoesNotHaveNameVariant(override val characterId: Character.Id, val name: String) : ValidationException(), CharacterException

class CharacterArcAlreadyContainsMaximumNumberOfTemplateSection(
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID,
) : DuplicateOperationException()

class TemplateSectionIsNotPartOfArcTemplate(
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID
) : EntityNotFoundException(templateSectionId)

data class CharacterArcSectionNotInMoralArgument(
    val characterArcSectionId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val arcId: UUID
) : EntityNotFoundException(characterArcSectionId)

data class ArcTemplateSectionIsNotMoral(
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID
) : ValidationException()