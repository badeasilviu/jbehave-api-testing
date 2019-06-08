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