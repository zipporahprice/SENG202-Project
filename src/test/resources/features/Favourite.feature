Feature: Favourite
  Scenario: User wants to save the generated route as a favourite
    Given the user has a start and end location entered on the routing menu
    When the user clicks save route and enters a unique "name"
    Then the "name" route is saved in the database

  Scenario: User wants to load a favourite route
    Given there is a route saved called "name"
    When the user selects route "name" and clicks load route
    Then the "start_location" in the route menu is matches the favourite in the database

  Scenario: User wants to delete a favourite route
    Given there is a route saved called "name"
    When the user selects route "name" and clicks delete route
    Then the route "name" is deleted from the database