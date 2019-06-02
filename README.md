##### Testing Endpoint: https://api.github.com/search/repositories
##### Documentation url: https://developer.github.com/v3/search/#search-repositories
##### Parameters:
- **q**	*string*	*Required* : The query contains one or more search keywords and qualifiers. Qualifiers allow you to limit your search to specific areas of GitHub. The REST API supports the same qualifiers as GitHub.com.
  - Documentation about format of query: https://developer.github.com/v3/search/#constructing-a-search-query
  - Documentation about qualifiers: https://help.github.com/en/articles/searching-for-repositories
- **sort**	*string* :	Sorts the results of your query by number of **stars**, **forks**, or **help-wanted-issues** or how recently the items were **updated**. **Default: best match**
- **order**	*string* : Determines whether the first search result returned is the highest number of matches (**desc**) or lowest number of matches (**asc**). This parameter is ignored unless you provide sort. **Default: desc**


##### Testcases

- Functional
  - required Parameters (**q**)
  - optional Parameters (**sort**, **order**)
  - **order** parameter is ignored if **sort** parameter is not present
  - default sorting is done on **best match** (no parameter present) by score value from response
  - **q** parameter (q=SEARCH_KEYWORD_1+SEARCH_KEYWORD_N+QUALIFIER_1+QUALIFIER_N)
      - search just for keywords for q parameter: 1..N keywords where N is specified
      - search just for qualifiers for q parameter: 1..N qualifiers where N is specified and from this list https://help.github.com/en/articles/searching-for-repositories (qualifiers of same type can be used multiple times)
      - search for simple combination of keyword and qualifiers
      - search timeout - check for (incomplete_results property set to true in response)
      - Negative: search with empty keyword and valid qualifiers
      - Negative: search with valid keyword and empty qualifiers
      - Negative: search with empty query
      - Negative: search with invalid qualifiers
  - **sort** parameter
      - sort values (**stars, forks, or help-wanted-issues, updated**)
      - Negative: sort empty value - **best match**
      - Negative: sort on invalid value
  - **order**
      - default order value is **desc**
      - order values (**asc** or **desc**) based on the sorting Parameters
      - check sorting scenarios that are default ordered desc
      - check sorting scenarios with desc order value (same as without parameter given)
      - check sorting scenarios with asc order value (reverse order)
  - **client use cases**
      - Search by repository name, description, or contents of the README file
      - Search based on the contents of a repository
      - Search within a user's or organisation's repositories
      - Search by repository size
      - Search by number of forks
      - Search by number of stars
      - Search by when a repository was created or last updated
      - Search by language
      - Search by topic
      - Search by number of topics
      - Search by license
      - Search by public or private repository
      - Search based on whether a repository is a mirror
      - Search based on whether a repository is archived
      - Search based on number of issues with good first issue or help wanted labels

- Non-Functional
 - Performance Testing
    - Load Testing (specific load below the maximum)
       - check number of concurrent requests
       - check response times for requests
    - Stress Testing (load above the maximum)
      - check number concurrent requests before it stops responding (certain threshold)
      - check response times
    - Soak Testing (continuous load for long amount of time)
      - check response times degradation over time
      - check number of concurrent response over time
    - Spike Testing (suddenly increase/decrease load)
      - check response times



##### Automated Regression
- Based on Serenity+Cucumber4
- Capabilities
  - run predefined suites (smoke,automated,complete feature)
  - do REST API (GET/POST)calls to predefined endpoints
  - check response code, headers,response body using jsonPath
  - check sorting and ordering of elements
  - check number of items returned
  - check pagination and walk pagination
  - check certain value is present for all fields from response (equals or contains)
  - check a list of keys in response
  - compare two jsons
- Structure
  - src/test/java/api - Test runners and supporting code
  - src/test/resources/features - Feature files
- How to run:
  - Prerequisites: maven3, java8 or greater
  - JUnit:
    - go to **src/test/java/api/** and run class **CucumberTestSuite.java** (will run all testcases with @automated tag by default)
    - you can modify the tags you want to run from @CucumberOptions inside the class
  - Maven:
    - run command from base project: **mvn clean verify** (will run all testcases with @automated tag by default)
    - if you want to run different tags: ** mvn clean verify -Dcucumber.options="--tags @test"**
    - html report is generated when running previous commands - open target/site/serenity/index.html after run
- Problems
  - two possible bugs found (tc marked with @bug tag)
  - github api has limit rate requests per hour per ip and return 403 if exceeded and because of this I couldn't have a full run without fails because of limits exceeded. If condition like this happen, those tests will be marked as pending in the report with the API limit exceeded message.