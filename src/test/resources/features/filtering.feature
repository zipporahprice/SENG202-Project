Feature: Filtering crash data
  Scenario: User wants to filter crashes by weather.
    Given a user wants to filter crashes by "weather"
    When the user deselects the checkboxes "Fine, Light Rain"
    Then the user will see crashes without the filters deselected

  Scenario: User wants to filter crashes by region.
    Given a user wants to filter crashes by "region"
    When the user deselects the checkboxes "Auckland, Canterbury, Wellington"
    Then the user will see crashes without the filters deselected

  Scenario: User wants to filter crashes by holiday.
    Given a user wants to filter crashes by "holiday"
    When the user deselects the checkboxes "0"
    Then the user will see crashes without the filters deselected

  Scenario: User wants to filter crashes by severity.
    Given a user wants to filter crashes by "severity"
    When the user deselects the checkboxes "1,4"
    Then the user will see crashes without the filters deselected

  Scenario: User wants to filter crashes by transport mode.
    Given a user wants to filter crashes by "transport"
    When the user deselects the checkboxes "bicycle, bus"
    Then the user will see crashes without the filters deselected

  Scenario: User wants to filter crashes by year.
    Given a user wants to filter crashes by "year"
    When the user sets 2002 as the earliest year and 2020 as the latest year
    Then the user will see crashes without the filters deselected
