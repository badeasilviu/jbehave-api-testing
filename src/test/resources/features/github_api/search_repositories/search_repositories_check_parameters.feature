Feature: Basic checks for search/repositories parameters
  - required Parameters (**q**)
  - optional Parameters (**sort**, **order**)
  - **order** parameter is ignored if **sort** parameter is not present
  - default sorting is done on **best match** (no parameter present) by score value from response

  @automated @smoke
  Scenario Outline: Should receive response if q parameter is used in query and data
    Given the client uses the following url parameters:
      | q | <value> |
    When the client calls 'SEARCH_REPOSITORIES' endpoint
    Then the client should receive an HTTP 200 response code
    Then the client using jsonPath 'items' should see <numberItems> elements
    And the client using jsonPath 'total_count' should see having <numberTotal>
    And the client using jsonPath 'incomplete_results' should see having boolean false
    And the client using jsonPath 'items' should see having value '<language>' for all elements with key 'language'
    And the client using jsonPath 'items' should see containing value '<name>' for all elements with keys:
      | name        |
      | description |
    Examples:
      | value                                        | numberItems | numberTotal | language | name         |
      | tetris language:assembly created:<2013-12-31 | 30          | 65          | Assembly | tetris       |
      | serenity language:java created:<2018-12-31   | 30          | 713         | Java     | serenity |
      | behat language:ruby created:<2018-12-31      | 9           | 9           | Ruby     | behat        |
