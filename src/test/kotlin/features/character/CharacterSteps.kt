package com.soyle.stories.desktop.config.features.character

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.desktop.config.drivers.character.*
import com.soyle.stories.desktop.config.drivers.soylestories.ScenarioContext
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.drivers.theme.givenMoralArgumentToolHasBeenOpenedForTheme
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.character.characterList.CharacterListAssertions
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.*
import java.util.*

class CharacterSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("the following characters have been created") { data: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            data.asList().forEach { characterName ->
                characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
            }
        }
        Given("I have created the following characters") { data: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            data.asList().forEach { characterName ->
                characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
            }
        }
        Given("a character named {string} has been created") { characterName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
        }
        Given("I have created a character named {string}")  { characterName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
        }
        Given("I have renamed the {character} to {string}") { character: Character, newName: String ->
            if (character.name.value != newName) {
                CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                    .givenCharacterRenamedTo(character.id, newName)
            }
        }
        Given("I have removed the {character} from the story") { character: Character ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterRemoved(character)
        }
        Given("I have created a character arc for the {character} in the {theme}") { character: Character, theme: Theme ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterIsIncludedInTheme(character.id, theme.id)
        }
        Given(
            "I have created the following character arcs for the {character}"
        ) { character: Character, dataTable: DataTable ->
            val driver = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
            dataTable.asList().forEach {
                driver.givenCharacterHasAnArcNamed(character, it)
            }
        }
        Given("I have created {int} character arcs for the {character}") { arcCount: Int, character: Character ->
            val driver = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
            repeat(arcCount) {
                driver.givenCharacterHasAnArcNamed(character, UUID.randomUUID().toString())
            }
        }
    }

    private fun whens() {
        When("I rename the {character} to {string}") { character: Character, newName: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .renameCharacterTo(character.id, newName)
        }
        When("I remove the {character} from the story") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenDeleteCharacterDialogHasBeenOpened(character.id)
                .confirmDelete()
        }
    }

    private fun thens() {
        Then(
            "the {character}'s character arc for the {theme} should have been renamed to {string}"
        ) { character: Character, theme: Theme, expectedName: String ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterDriver(workBench)
                .getCharacterArcByCharacterAndTheme(character, theme)!!
            assertEquals(expectedName, arc.name)

            CharacterListAssertions.assertThat(workBench.givenCharacterListToolHasBeenOpened()) {
                characterArcHasName(character.id, theme.id, arc.id, expectedName)
            }
        }
        Then(
            "all the character arcs in the theme originally named {string} should have been renamed to {string}"
        ) { originalThemeName: String, expectedArcName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(originalThemeName).second!!
            val arcs = CharacterArcDriver(workbench).getCharacterArcsForTheme(theme.id)

            arcs.onEach {
                assertEquals(expectedArcName, it.name) { "Character arc ${it.id} was not renamed to $expectedArcName" }
            }
        }
        Then(
            "all the character arcs in the theme named {string} should have been deleted"
        ) { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeId = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(themeName).first!!
            val arcs = CharacterArcDriver(workbench).getCharacterArcsForTheme(themeId)

            assertTrue(arcs.isEmpty()) { "Not all character arcs for $themeName theme deleted.  Still have ${arcs.size} left." }
        }
        Then(
            "all the character arcs in the theme named {string} should not have been deleted"
        ) { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeId = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(themeName).first!!
            val arcs = CharacterArcDriver(workbench).getCharacterArcsForTheme(themeId)

            assertFalse(arcs.isEmpty()) { "All character arcs for $themeName theme have been deleted." }
        }
        Then(
            "a new section should have been added to {string}s character arc in the {string} theme with that type"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val character = CharacterDriver(workbench).getCharacterByNameOrError(characterName)
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            val templateSectionToAdd = ScenarioContext(soyleStories).templateSectionToAdd!!

            arc.arcSections.find {
                it.template.id.uuid.toString() == templateSectionToAdd
            }!!

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            MoralArgumentViewAssert.assertThat(moralArgument) {
                onlyHasArcSections(
                    arc.moralArgument().arcSections.map { it.template.name }
                )
            }

            ScenarioContext(soyleStories).updatedCharacterArc = arc
        }
        Then("the character formerly named {string} should have the name {string}") { originalName: String, expectedName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val character = CharacterDriver(workbench)
                .getCharacterAtOnePointNamed(originalName)!!
            assertEquals(expectedName, character.name.value)

            CharacterListAssertions.assertThat(workbench.givenCharacterListToolHasBeenOpened()) {
                characterHasName(character.id, expectedName)
            }
        }
    }
}