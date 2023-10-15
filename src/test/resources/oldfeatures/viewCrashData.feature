Feature: View crash data
  Scenario: User clicks on a crash point and sees crash information
    Given the user wants to see crash information
    When The user selects crash
    Then The user will see information on the crash
