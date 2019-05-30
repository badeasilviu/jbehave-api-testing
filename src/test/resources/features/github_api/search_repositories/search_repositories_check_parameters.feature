Feature: Basic checks for search/repositories parameters
  - required Parameters (**q**)
  - optional Parameters (**sort**, **order**)
  - **order** parameter is ignored if **sort** parameter is not present
  - default sorting is done on **best match** (no parameter present) by score value from response

    @automated
    Scenario: Should receive response if q parameter is used in query
        Given the client uses the following url parameters:
         |q|tetris+language:assembly|
        When the client calls 'SEARCH_REPOSITORIES' endpoint
        Then the client should receive an HTTP 200 response code
         And the client searching in response using jsonPath 'items[0]' should see:
         |language|Assembly|
