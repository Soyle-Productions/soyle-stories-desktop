package com.soyle.stories

import com.soyle.stories.character.CharacterArcSteps
import com.soyle.stories.character.CharacterSteps
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationSteps
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En
import io.cucumber.java8.Scenario
import javafx.scene.input.KeyCode
import org.junit.jupiter.api.Assertions.*
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

class SoyleUATSteps : En, ApplicationTest() {

	private val double = SoyleStoriesTestDouble()

	private var targetLocation: Location? = null
	private var recentlyCreatedLocationName: String = ""
	private var deletedLocation: Location? = null

	init {
		Given("A project has been opened") {
			// ProjectDriver.givenHasBeenOpened(double)
			ProjectSteps.givenProjectHasBeenOpened(double)
		}
		Given("The Location List Tool has been opened") {
			// LocationListDriver.givenHasBeenOpened(double)
			LocationSteps.givenLocationListToolHasBeenOpened(double)
		}
		Given("A location has been created") {
			// LocationRepoDriver.givenNumberHaveBeenCreated(double, 1)
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, 1)
		}
		Given("The create new location dialog has been opened") {
			// CreateNewLocationDialogDriver.givenHasBeenOpened(double)
			LocationSteps.givenCreateNewLocationDialogHasBeenOpened(double)
		}
		Given("The user has entered an invalid location name") {
			// CreateNewLocationDialogDriver.givenInvalidNameHasBeenEntered(double)
			LocationSteps.givenUserHasEnteredInvalidLocationNameInCreateLocationDialog(double)
		}
		Given("The user has entered a valid location name") {
			// CreateNewLocationDialogDriver.givenValidNameHasBeenEntered(double)
			LocationSteps.givenUserHasEnteredValidLocationNameInCreateLocationDialog(double)
		}
		Given("{int} Locations have been created") { int1: Int ->
			// LocationRepoDriver.givenNumberHaveBeenCreated(double, count)
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, int1)
		}
		Given("a location has been selected") {
			// LocationListDriver.givenLocationHasBeenSelected(double)
			LocationSteps.givenLocationIsSelectedInLocationListTool(double)
		}
		Given("the location right-click menu is open") {
			// LocationListDriver.givenRightClickMenuHasBeenOpened(double)
			LocationSteps.givenLocationRightClickMenuIsOpenInLocationListTool(double)
			targetLocation = LocationSteps.getLocationsCreated(double).first()
		}
		Given("the delete location dialog has been opened") {
			// ConfirmDeleteLocationDialogDriver.givenHasBeenOpened(double)
			LocationSteps.givenConfirmDeleteDialogHasBeenOpened(double)
		}
		Given("the location rename input box is visible") {
			// LocationListDriver.givenRenameInputBoxIsVisible(double)
			LocationSteps.givenLocationRenameInputBoxIsVisible(double)
		}
		Given("the user has entered a valid name") {
			// LocationListDriver.givenValidNameHasBeenEnteredInRenameInputBox(double)
			LocationSteps.givenUserHasEnteredValidLocationNameInRenameInputBox(double)
		}
		Given("the Location Details Tool has been opened") {
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, 1)
			targetLocation = LocationSteps.getLocationsCreated(double).first()
			LocationSteps.givenLocationDetailsToolHasBeenOpened(double, targetLocation!!.id.uuid)
		}
		Given("the target Location has a description of {string}") { string: String ->
			LocationSteps.givenLocationHasDescription(double, targetLocation!!.id.uuid, string)
			targetLocation = LocationSteps.getLocationsCreated(double).first()
		}
		Given("the user has entered {string} into the description field") { string: String ->
			LocationSteps.givenLocationDetailsToolHasDescriptionOf(double, targetLocation!!.id.uuid, string)
		}
		Given("a Character has been created") {
			CharacterSteps.givenANumberOfCharactersHaveBeenCreated(double, 1)
		}
		Given("a Character Arc has been created") {
			CharacterArcSteps.givenANumberOfCharacterArcsHaveBeenCreated(double, 1)
		}
		Given("no Locations have been created") {
			LocationSteps.givenNoLocationsHaveBeenCreated(double)
		}
		Given("at least one Location has been created") {
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, 1)
		}
		Given("the Base Story Structure Tool has been opened") {
			CharacterArcSteps.givenANumberOfCharacterArcsHaveBeenCreated(double, 1)
			val arc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.givenBaseStoryStructureToolHasBeenOpened(double, arc.themeId, arc.characterId)
		}


		When("User selects the file->new->location menu option") {
			ProjectSteps.whenMenuItemIsSelected(double, "file", "file_new", "file_new_location")
		}
		When("User clicks the center create new location button") {
			LocationSteps.whenLocationListToolCenterButtonIsClicked(double)
		}
		When("User clicks the bottom create new location button") {
			LocationSteps.whenLocationListToolActionBarCreateButtonIsClicked(double)
		}
		When("The user presses the Enter key") {
			interact {
				press(KeyCode.ENTER).release(KeyCode.ENTER)
			}
		}
		When("The user clicks the Create button") {
			LocationSteps.whenCreateLocationDialogCreateButtonIsClicked(double)
		}
		When("The user presses the Esc key") {
			interact {
				press(KeyCode.ESCAPE).release(KeyCode.ESCAPE)
			}
		}
		When("The user clicks the Cancel button") {
			LocationSteps.whenCreateLocationDialogCancelButtonIsClicked(double)
		}
		When("The Location List Tool is opened") {
			LocationSteps.whenLocationListToolIsOpened(double)
		}
		When("A new Location is created") {
			recentlyCreatedLocationName = LocationSteps.whenLocationIsCreated(double)
		}
		When("A Location is deleted") {
			deletedLocation = LocationSteps.whenLocationIsDeleted(double)
		}
		When("the user clicks the location list tool right-click menu delete button") {
			LocationSteps.whenLocationListToolRightClickMenuButtonIsClicked(double, "delete")
		}
		When("the user clicks the location list tool right-click menu Rename button") {
			LocationSteps.whenLocationListToolRightClickMenuButtonIsClicked(double, "rename")
		}
		When("the user clicks the location list tool delete button") {
			LocationSteps.whenLocationListToolActionBarDeleteButtonIsClicked(double)
		}
		When("the user clicks the confirm delete location dialog delete button") {
			LocationSteps.whenConfirmDeleteDialogButtonIsClicked(double, true)
		}
		When("the user clicks the confirm delete location dialog cancel button") {
			LocationSteps.whenConfirmDeleteDialogButtonIsClicked(double, false)
		}
		When("The create new location dialog is reopened") {
			LocationSteps.whenCreateLocationDialogIsOpened(double)
		}
		When("the user clicks the location list tool right-click menu open button") {
			LocationSteps.whenLocationListToolRightClickMenuButtonIsClicked(double, "open")
		}
		When("the user closes the Location Details Tool") {
			LocationSteps.whenLocationDetailsToolIsClosed(double, targetLocation!!.id.uuid)
		}
		When("the Location Details Tool is reopened with the same Location") {
			LocationSteps.whenLocationDetailsToolIsOpened(double, targetLocation!!.id.uuid)
		}
		When("the Base Story Structure Tool is opened") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.whenBaseStoryStructureToolIsOpened(double, characterArc.themeId, characterArc.characterId)
		}
		When("the Character Arc Section Location dropdown is clicked") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.whenCharacterArcSectionLocationDropDownIsClicked(double, characterArc.themeId, characterArc.characterId)
		}


		Then("The Location List Tool should show a special empty message") {
			assertTrue(LocationSteps.locationListToolShowsEmptyMessage(double))
		}
		Then("The Location List Tool should show all {int} locations") { locationCount: Int ->
			assertTrue(LocationSteps.locationListToolShowsNumberOfLocations(double, locationCount))
		}
		Then("The Location List Tool should show the new Location") {
			assertTrue(LocationSteps.locationListToolShowsLocationWithName(double, recentlyCreatedLocationName))
		}
		Then("The Location List Tool should not show the deleted Location") {
			assertFalse(LocationSteps.locationListToolShowsLocationWithName(double, deletedLocation!!.name))
		}
		Then("The create new location dialog should be open") {
			assertTrue(LocationSteps.isCreateNewLocationDialogOpen(double))
		}
		Then("An error message should be visible in the create new location dialog") {
			assertTrue(LocationSteps.createNewLocationDialogShowsErrorMessage(double))
		}
		Then("The create new location dialog should be closed") {
			assertFalse(LocationSteps.isCreateNewLocationDialogOpen(double))
		}
		Then("the confirm delete location dialog should be opened") {
			assertTrue(LocationSteps.isConfirmDeleteLocationDialogOpen(double))
			targetLocation = LocationSteps.getLocationSelectedInLocationListTool(double)!!.let {
				Location(Location.Id(UUID.fromString(it.id)), Project.Id(UUID.randomUUID()), it.name, "")
			}
		}
		Then("the delete location dialog should be closed") {
			assertFalse(LocationSteps.isConfirmDeleteLocationDialogOpen(double))
		}
		Then("the location's name should be replaced by an input box") {
			assertTrue(LocationSteps.locationListToolShowsInputBoxForSelectedItem(double))
		}
		Then("the location rename input box should contain the location's name") {
			assertTrue(LocationSteps.locationListToolRenameInputBoxContainsSelectedItemName(double))
		}
		Then("the location rename input box should be replaced by the location name") {
			assertTrue(LocationSteps.locationListToolShowsLocationNameForSelectedItem(double))
		}
		Then("the location name should be the original name") {
			assertTrue(LocationSteps.locationListToolShowsOriginalLocationNameForSelectedItem(double))
		}
		Then("the location name should be the new name") {
			assertTrue(LocationSteps.locationListToolShowsChangedLocationNameForSelectedItem(double))
		}
		Then("the location name field should be blank") {
			assertTrue(LocationSteps.locationNameIsBlankInCreateLocationDialog(double))
		}
		Then("the Location Details Tool should be open") {
			assertTrue(LocationSteps.isLocationDetailsToolOpen(double, targetLocation!!.id.uuid))
		}
		Then("the description field text should be {string}") { string: String ->
			assertEquals(string, LocationSteps.getDescriptionInLocationDetailsTool(double, targetLocation!!.id.uuid))
		}
		Then("the confirm delete location dialog should show the location name") {
			assertTrue(LocationSteps.isConfirmDeleteLocationDialogLocationDisplayingNameOf(double, targetLocation!!))
		}
		Then("the Character Arc Section Location dropdown in the Base Story Structure Tool should be disabled") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			assertTrue(CharacterArcSteps.isLocationDropdownDisabledInBaseStoryStructureTool(double, characterArc.themeId, characterArc.characterId))
		}
		Then("the Character Arc Section Location dropdown in the Base Story Structure Tool should not be disabled") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			assertFalse(CharacterArcSteps.isLocationDropdownDisabledInBaseStoryStructureTool(double, characterArc.themeId, characterArc.characterId))
		}
		Then("all Locations should be listed in the Character Arc Section Location dropdown menu") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			val locations = LocationSteps.getLocationsCreated(double)
			CharacterArcSteps.isCharacterArcSectionLocationOpenWithAllLocations(double, characterArc.themeId, characterArc.characterId, locations)
			  .let(::assertTrue)
		}


		After { _: Scenario ->
			if (double.isStarted()) {
				FxToolkit.cleanupStages()
				FxToolkit.cleanupApplication(double.application)
			}
		}
	}

}