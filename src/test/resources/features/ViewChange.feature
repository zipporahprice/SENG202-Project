Feature: View Change

  Scenario: Change the view of the map from None e.g. to heatmap
    Given the user has opened the app and the database is loaded
    When the user changes the view to 'Heatmap'
    Then the current view should be 'Heatmap'
