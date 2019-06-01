@complete
Feature: Basic checks for search repositories parameters
  AC1: required Parameters (**q**) DONE
  AC2: optional Parameters (**sort**, **order**) DONE
  AC3: **order** parameter is ignored if **sort** parameter is not present DONE
  AC4: default sorting is done on **best match** (no parameter present) by score value from response DONE
  AC5: Requests that return multiple items will be paginated to 30 items by default DONE
  AC5: Pagination with limiting number of elements per page DONE
  AC6: incomplete results TODO

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


  @automated @smoke
  Scenario Outline: Should receive response with if q parameter is used in query and no data found
    Given the client uses the following url parameters:
      | q | <value> |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items' should see <numberItems> elements
     And the client using jsonPath 'total_count' should see having <numberItems>
     And the client using jsonPath 'incomplete_results' should see having boolean false
    Examples:
      | value                       | numberItems |
      | cocojambo language:assembly | 0           |

  @automated @smoke
  Scenario: Should not receive response with if q parameter is not used
    When the client calls 'SEARCH_REPOSITORIES' endpoint
    Then the client should receive an HTTP 422 response code

  @automated @smoke @bug
    #There is a possible bug here. When sorting using forks field it doesn't sort correctly. See assertError of testcase run
  Scenario: Should receive response if q and sort parameters are used in query and data found
    Given the client uses the following url parameters:
      | q    | tetris language:assembly created:<2013-12-31 |
      | sort | forks   |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items' should see elements sorted by 'forks' field in 'desc' order

  @automated @smoke
  Scenario Outline: Should receive response if q, sort and order parameters are used in query and data found
    Given the client uses the following url parameters:
      | q     | <value> |
      | sort  | forks   |
      | order | asc     |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items' should see elements sorted by 'forks' field in 'asc' order
    Examples:
      |value|
      |tetris language:assembly created:<2013-12-31|

  @automated @smoke @bug
    #Possible bug here. When doing two consecutive requests with same parameters, the score fields gives different approximations. That's why I had to skip that field when comparing two complete responses. See method behind "Then the client should see the same response with STRICT check"
  Scenario Outline: Order parameter is ignored if sort not present. Check that the results are the same when using and not using order param
    Given the client uses the following url parameters:
      | q | <value> |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Given the client uses the following url parameters:
      | q     | <value> |
      | order | asc     |
    When the client calls 'SEARCH_REPOSITORIES' endpoint again with success
    Given the client uses the following url parameters:
      | q     | <value> |
      | order | desc    |
    When the client calls 'SEARCH_REPOSITORIES' endpoint again with success
    Then the client should see the same response with STRICT check
    Examples:
      | value                    |
      | tetris+language:assembly |

  @automated @smoke
  Scenario Outline: Default sorting (no parameter present) is done on **best match** by score value from response with order desc
    Given the client uses the following url parameters:
      |q | <value> |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items' should see elements sorted by 'score' field in 'desc' order
    Examples:
      |value|
      |tetris language:assembly created:<2013-12-31|

  @automated @smoke
  Scenario Outline: Pagination  - check results are paginated and can be accessed when > 30 results returned
    Given the client uses the following url parameters:
      | q | <value> |
    When the client calls 'SEARCH_REPOSITORIES' endpoint
    Then the client using jsonPath 'incomplete_results' should see having boolean false
     And the client should see 3 pages with responses
     And the client should see paginated links in header:
      |next|
      |last|
    When the client walks pagination to step 'next' for 'SEARCH_REPOSITORIES' endpoint
    Then the client should see paginated links in header:
      |prev|
      |next|
      |last|
      |first|
     And the client using jsonPath 'incomplete_results' should see having boolean false
     And the client using jsonPath 'items' should see <numberItems> elements
     And the client using jsonPath 'total_count' should see having <numberTotal>
     And the client using jsonPath 'items' should see having value '<language>' for all elements with key 'language'
     And the client using jsonPath 'items' should see containing value '<name>' for all elements with keys:
      | name        |
      | description |
    When the client walks pagination to step 'next' for 'SEARCH_REPOSITORIES' endpoint
    Then the client should see paginated links in header:
      |prev|
      |first|
     And the client using jsonPath 'incomplete_results' should see having boolean false
     And the client using jsonPath 'items' should see 5 elements
     And the client using jsonPath 'total_count' should see having <numberTotal>
     And the client using jsonPath 'items' should see having value '<language>' for all elements with key 'language'
     And the client using jsonPath 'items' should see containing value '<name>' for all elements with keys:
      | name        |
      | description |
    Examples:
      | value                                        | numberItems | numberTotal | language | name         |
      | tetris language:assembly created:<2013-12-31 | 30          | 65          | Assembly | tetris       |

  @automated @smoke
  Scenario: Pagination  - check results are not paginated when less than 30 results returned
    Given the client uses the following url parameters:
      | q | behat language:ruby created:<2018-12-31  |
    When the client calls 'SEARCH_REPOSITORIES' endpoint
    Then the client should not see paginated links in header