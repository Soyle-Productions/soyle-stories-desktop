@location
Feature: Location List Tool
  As a user working on a project
  I want to see the list of locations I've created
  So that I can prevent duplicates and remove unneeded locations

  Background:
    Given A project has been opened

  Scenario: Show special empty message when empty
    When The Location List Tool is opened
    Then The Location List Tool should show a special empty message

  Scenario Outline: Location List Tool shows correct number of locations
    Given <number> Locations have been created
    When The Location List Tool is opened
    Then The Location List Tool should show all <number> locations

    Examples:
      | number |
      | 1      |
      | 2      |
      | 3      |
      | 4      |
      | 5      |

  Scenario: Update when new locations created
    Given The Location List Tool has been opened
    When A new Location is created
    Then The Location List Tool should show the new Location

  Scenario Outline: Update when locations are deleted
    Given The Location List Tool has been opened
    And <number> Locations have been created
    When A Location is deleted
    Then The Location List Tool should not show the deleted Location

    Examples:
      | number |
      | 2      |

  Scenario: Click Away after Rename
    Given the Location rename input box is visible
    And the user has entered a valid Location name
    When The user clicks away from the input box
    Then the Location rename input box should be replaced by the Location name
    And the Location name should be the new name

  Scenario: Click Away without Rename
    Given the Location rename input box is visible
    When The user clicks away from the input box
    Then the Location rename input box should be replaced by the Location name
    And the Location name should be the original name

