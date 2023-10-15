Feature: Database Importing

  Scenario: Import a CSV file to the app
    Given the user has a CSV data file saved on the device running the app
    When the user imports the CSV file
    Then the database should be populated with data from the CSV file