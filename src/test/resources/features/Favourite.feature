Feature: Favourite
  Scenario: User wants to save the generated route as a favourite
    Given the user has a "Uni-Cycle Cycleway Canterbury" and "Main Street Canterbury" location entered on the routing menu
    When the user clicks save route and enters a unique "route1"
    Then the "route1" route is saved in the database

  Scenario: User wants to load a favourite route
    Given there is a route saved called "route2" with starting location "Uni-Cycle Cycleway Canterbury"
    When the user selects route "route2" and clicks "load" route
    Then the location "route2" has a start location matching "Uni-Cycle Cycleway Canterbury"

  Scenario: User wants to delete a favourite route
    Given there is a route saved called "route3" with starting location "Uni-Cycle Cycleway Canterbury"
    When the user selects route "route3" and clicks "delete" route
    Then the route "route3" is deleted from the database