Feature: Basic checks for search/repositories parameters
  AC1: required Parameters (**q**) DONE
  AC2: optional Parameters (**sort**, **order**) DONE
  AC3: **order** parameter is ignored if **sort** parameter is not present DONE
  AC4: default sorting is done on **best match** (no parameter present) by score value from response DONE

  @automated @smoke
  Scenario Outline: Should receive response if q parameter is used in query and data found
    Given the client uses the following url parameters:
      |q|<value>|
    When the client calls 'SEARCH_REPOSITORIES' endpoint
    Then the client should receive an HTTP 200 response code
    Then the client searching in response using jsonPath 'items' should see <numberItems> elements
    And the client searching in response using jsonPath 'total_count' should see <numberTotal>
    And the client searching in response using jsonPath 'items[0].language' should see '<language>'
    Examples:
      | value |numberItems|numberTotal|language|
      |tetris+language:assembly|30|232|Assembly|


  @automated @smoke
  Scenario Outline: Should receive response with if q parameter is used in query and no data found
    Given the client uses the following url parameters:
      |q|<value>|
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client searching in response using jsonPath 'items' should see <numberItems> elements
    And the client searching in response using jsonPath 'total_count' should see <numberItems>
    Examples:
      | value |numberItems|
      |cocojambo+language:assembly|0|

  @automated @smoke
  Scenario: Should not receive response with if q parameter is not used
    When the client calls 'SEARCH_REPOSITORIES' endpoint
    Then the client should receive an HTTP 422 response code

  @automated @smoke
  Scenario Outline: Should receive response if q and sort parameters are used in query and data found
    Given the client uses the following url parameters:
      |q|<value>|
      |sort|stars|
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client searching in response using jsonPath 'items' should see <numberItems> elements
    And the client searching in response using jsonPath 'total_count' should see <numberTotal>
    Examples:
      | value |numberItems|numberTotal|
      |tetris+language:assembly|30|232|

  @automated @smoke
  Scenario Outline: Should receive response if q, sort and order parameters are used in query and data found
    Given the client uses the following url parameters:
      |q|<value>|
      |sort|stars|
      |order|asc|
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client searching in response using jsonPath 'items' should see <numberItems> elements
    And the client searching in response using jsonPath 'total_count' should see <numberTotal>
    Examples:
      | value |numberItems|numberTotal|
      |tetris+language:assembly|30|232|

  @automated @smoke
  Scenario Outline: Order parameter is ignored if sort not present. Check that the results are the same when using and not using order param
    Given the client uses the following url parameters:
      |q|<value>|
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Given the client uses the following url parameters:
      |q|<value>|
      |order|asc|
    When the client calls 'SEARCH_REPOSITORIES' endpoint again with success
    Given the client uses the following url parameters:
      |q|<value>|
      |order|desc|
    When the client calls 'SEARCH_REPOSITORIES' endpoint again with success
    Then the client should see the same response with STRICT check
    Examples:
      | value |numberItems|numberTotal|
      |tetris+language:assembly|30|232|

  @automated @smoke
  Scenario Outline: Default sorting (no parameter present) is done on **best match** by score value from response with order desc
    Given the client uses the following url parameters:
      |q|<value>|
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client searching in response using jsonPath 'items' should see elements sorted by 'score' field in 'desc' order
    Examples:
      | value |numberItems|numberTotal|
      |tetris+language:python|30|232|

