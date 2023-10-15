Feature: Rating Area Management

  Scenario: Drawing and Rating a Circle
    Given the circle with centre at latitude "50.0" and longitude "30.0" and radius "5.0"
    When the circle is rated
    Then the rated area is calculated for the area

  Scenario: Drawing and Rating a Rectangle
    Given the bounding box with min point at latitude "20.0" and longitude "10.0" and max point at latitude "40.0" and longitude "50.0"
    When the rectangle is rated
    Then the rated area is calculated for the area
