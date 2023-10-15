Feature: Showing Pie Graph

  Scenario: Show a pie graph filtered e.g by severity
    Given the user has opened the app and has opened the pie chart
    When the user changes the filter to 'Severity'
    Then the graph updates to show data from 'Severity'
