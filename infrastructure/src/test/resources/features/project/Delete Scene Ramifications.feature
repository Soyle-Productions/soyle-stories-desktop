Feature: Delete Scene Ramifications

	Background: 
		Given 1 Scenes have been created
		And 1 Characters have been created

	@excluded
	Scenario: No Characters included in Scene
		When the Delete Scene Ramifications Tool is opened
		Then the Delete Scene Ramifications Tool should display an ok message
	
	@excluded
	Scenario: No motivations set in Scene
		Given 4 Characters have been created
		And all Characters have been included in the Scene
		When the Delete Scene Ramifications Tool is opened
		Then the Delete Scene Ramifications Tool should display an ok message
	
	@excluded
	Scenario: Motivation only set in this Scene
		Given 2 Scenes have been created
		And this is the first Scene in the story
		And the Character has been included in both Scenes
		And the Character Motivation has been set in this Scene
		When the Delete Scene Ramifications Tool is opened
		Then the other Scene should be listed in the Delete Scene Ramifications Tool
		And the Character should be listed in the Delete Scene Ramifications Tool
		And the Delete Scene Ramifications Current Motivation field for this Character should show the Motivation set in this Scene
		And the Delete Scene Ramifications Changed Motivation field for this Character should be empty
		
	@excluded
	Scenario: Motivation for one Character set in next Scene
		Given 2 Scenes have been created
		And this is the first Scene in the story
		And 2 Characters have been created
		And all Characters have been included in all Scenes
		And the Character Motivation has been set for all Characters in this Scene
		And the Character Motivation for one Character has been set in the next Scene
		When the Delete Scene Ramifications Tool is opened
		Then the other Scene should be listed in the Delete Scene Ramifications Tool
		And only the Character without their Motivation set in the next Scene should be listed in the Delete Scene Ramifications Tool
		And the Delete Scene Ramifications Current Motivation field for this Character should show the Motivation set in this Scene
		And the Delete Scene Ramifications Changed Motivation field for this Character should be empty
		
	@excluded
	Scenario: Show inherited motivations
		Given 3 Scenes have been created
		And this is the second Scene in the story
		And the Character has been included in all Scenes
		And the Character Motivation has been set in this Scene
		And the Character Motivation has been set in the previous Scene
		When the Delete Scene Ramifications Tool is opened
		Then the third Scene should be listed in the Delete Scene Ramifications Tool
		And the Character should be listed in the Delete Scene Ramifications Tool
		And the Delete Scene Ramifications Current Motivation field for this Character should show the Motivation set in this Scene
		And the Delete Scene Ramifications Changed Motivation field for this Character should show the Motivation set in the previous Scene
		
	@excluded
	Scenario: React to Scene Deleted
		Given the Delete Scene Ramifications Tool has been opened
		And 2 Scenes have been listed in the Delete Scene Ramifications Tool
		When one of the listed Scenes is deleted
		Then the deleted Scene should be removed from the Delete Scene Ramifications Tool
		
	@excluded
	Scenario: React to Character Deleted
		Given the Delete Scene Ramifications Tool has been opened
		And 2 Characters have been listed for a common Scene in the Delete Scene Ramifications Tool
		When one of the Characters is deleted
		Then the deleted Character should be removed from the Delete Scene Ramifications Tool
		
	@excluded
	Scenario: React to Last Affected Character in Scene Removed
		Given the Delete Scene Ramifications Tool has been opened
		And 1 Characters have been listed for a common Scene in the Delete Scene Ramifications Tool
		When the listed Character is removed from the Delete Scene Ramifications Tool
		Then the Scene listing that Character should be removed from the Delete Scene Ramifications Tool
		
	@excluded
	Scenario: React to Last Affected Scene Removed
		Given the Delete Scene Ramifications Tool has been opened
		And 1 Scenes have been listed in the Delete Scene Ramifications Tool
		When the listed Scene is removed from the Delete Scene Ramifications Tool
		Then the Delete Scene Ramifications Tool should display an ok message