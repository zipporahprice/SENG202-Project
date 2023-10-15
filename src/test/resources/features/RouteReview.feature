Feature: Route Review

  Scenario: Review a given route for its safety based on crash data
    Given the user has a set of coordinates, roads, and distances
    When the user generates a route
    Then the user should receive a review containing relevant metrics