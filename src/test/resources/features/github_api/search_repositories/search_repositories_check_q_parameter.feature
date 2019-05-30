Feature: Check the order parameter for search/repositories endpoint
          - search just for keywords for q parameter: 1..N keywords where N is specified
          - search just for qualifiers for q parameter: 1..N qualifiers where N is specified and from this list https://help.github.com/en/articles/searching-for-repositories (qualifiers of same type can be used multiple times)
          - search for simple combination of keyword and qualifiers
          - search timeout - check for (incomplete_results property set to true in response)
          - Negative: search with empty keyword and valid qualifiers
          - Negative: search with valid keyword and empty qualifiers
          - Negative: search with empty query
          - Negative: search with invalid qualifiers

  Scenario: Search just for keywords for q parameter: 1..N keywords where N is specified


