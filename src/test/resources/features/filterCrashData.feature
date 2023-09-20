Feature: Filtering crash data
  Scenario: User wants to filter crashes with only bicycle crashes
    Given the user wants to see crashes with a "bicycle_involved"
    When the user clicks the checkbox and changes the "transport_mode" filter
    Then the user will see less crashes than the size of the "crashes" table

  Scenario: User wants to filter crashes with only moped crashes
    Given the user wants to see crashes with a "moped_involved"
    When the user clicks the checkbox and changes the "transport_mode" filter
    Then the user will see less crashes than the size of the "crashes" table

  Scenario: User wants to filter crashes with only bus crashes
    Given the user wants to see crashes with a "bus_involved"
    When the user clicks the checkbox and changes the "transport_mode" filter
    Then the user will see less crashes than the size of the "crashes" table