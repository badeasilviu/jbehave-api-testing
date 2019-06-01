Feature: Check the most common use cases for search repositories
          - Search by repository name, description, or contents of the README file DONE
          - Search based on the contents of a repository DONE
          - Search within a user's or organisation's repositories DONE
          - Search by repository size DONE
          - Search by number of forks DONE
          - Search by number of stars DONE
          - Search by when a repository was created or last updated
          - Search by language
          - Search by topic
          - Search by number of topics
          - Search by license
          - Search by public or private repository
          - Search based on whether a repository is a mirror
          - Search based on whether a repository is archived
          - Search based on number of issues with good first issue or help wanted labels

  @automated @uat
  Scenario: Search by repository name, description, or contents of the README file
    Given the client uses the following url parameters:
      |q |java in:name,description,readme |
      |sort|stars                         |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items[0].name' should see containing 'tensorflow'

  @automated @uat
  Scenario: Search based on the contents of a repository
    Given the client uses the following url parameters:
      |q |org:apache |
      |sort|forks                         |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items[0].name' should see having 'spark'
     And the client using jsonPath 'items[0].license.name' should see containing 'Apache License 2.0'
     And the client using jsonPath 'items[1].name' should see having 'kubernetes'
     And the client using jsonPath 'items[1].language' should see having 'Go'

  @automated @uat
  Scenario: Search within a user's or organisation's repositories
    Given the client uses the following url parameters:
      |q |org:apache |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'total_count' should see having 1790

  @automated @uat
  Scenario: Search by repository size
    Given the client uses the following url parameters:
      |q |size:100020 |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items' should see having value 'Python' for all elements with key 'language'

  @automated @uat
  Scenario: Search by number of forks
    Given the client uses the following url parameters:
      |q |forks:>10000 |
      |sort|stars      |
      |order|desc      |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items[0].stargazers_count' should see having 303161

  @automated @uat
  Scenario: Search by number of stars
    Given the client uses the following url parameters:
      |q |stars:99..100|
      |sort|forks      |
      |order|asc       |
    When the client calls 'SEARCH_REPOSITORIES' endpoint with success
    Then the client using jsonPath 'items[0].forks' should see having 0
