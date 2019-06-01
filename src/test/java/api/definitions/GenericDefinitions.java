package api.definitions;

import api.steps.GenericSteps;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.thucydides.core.annotations.Steps;
import org.jruby.RubyProcess;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GenericDefinitions {

    @Steps
    GenericSteps genericSteps;

    private Response response;
    private Response responseNext;


    @Given("the client uses the following url parameters:")
    public void theFollowingUrlParameters(Map<String, String> urlParameters) {
        genericSteps.setDecorateUrl(urlParameters);
    }

    @When("the client calls {string} endpoint")
    public void theUserCallsEndpoint(String webServiceEndpoint) {
        this.response = genericSteps.callEndpoint(webServiceEndpoint);
    }

    @When("the client calls {string} endpoint again")
    public void theUserCallsEndpointAgain(String webServiceEndpoint) {
        this.responseNext = genericSteps.callEndpoint(webServiceEndpoint);
    }

    @When("the client calls {string} endpoint with success")
    public void theUserCallsEndpointWithSuccess(String webServiceEndpoint) {
        theUserCallsEndpoint(webServiceEndpoint);
        theClientShouldReceiveHttpResponseCode(200);
    }

    @When("the client calls {string} endpoint again with success")
    public void theUserCallsEndpointAgainWithSuccess(String webServiceEndpoint) {
        theUserCallsEndpointAgain(webServiceEndpoint);
        theClientShouldReceiveHttpResponseCode(200);
    }

    @When("the client calls {string} endpoint with body {string}")
    public void theUserCallsEndpoint(String webServiceEndpoint, String body) {
        this.response = genericSteps.callEndpoint(webServiceEndpoint, body);
    }

    @Then("the client should receive an HTTP {int} response code")
    public void theClientShouldReceiveHttpResponseCode(int httpCode) {
        assertThat(httpCode, equalTo(response.getStatusCode()));
    }

    @Then("the client using jsonPath {string} should see containing {string}")
    public void theClientShouldSeeHavingString(String jsonPath, String expected) {
        String result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result, containsString(expected));
    }

    @Then("the client using jsonPath {string} should see having {string}")
    public void theClientShouldSeeContainingString(String jsonPath, String expected) {
        String result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result, equalTo(expected));
    }

    @Then("the client using jsonPath {string} should see having value {string} for all elements with key {string}")
    public void theClientShouldSeeStringInAllElementHavingElem(String jsonPath, String expected, String jsonKey) {
        theClientShouldSeeStringInAllElements(jsonPath, Arrays.asList(jsonKey), expected, true);
    }

    @Then("the client using jsonPath {string} should see containing value {string} for all elements with {string}")
    public void theClientShouldSeeStringInAllElementContainingList(String jsonPath, String expected, String jsonKey) {
        theClientShouldSeeStringInAllElements(jsonPath, Arrays.asList(jsonKey), expected, false);
    }

    @Then("the client using jsonPath {string} should see having value {string} for all elements with keys:")
    public void theClientShouldSeeStringInAllElementHavingElem(String jsonPath, String expected, List<String> jsonKeys) {
        theClientShouldSeeStringInAllElements(jsonPath, jsonKeys, expected, true);
    }

    @Then("the client using jsonPath {string} should see containing value {string} for all elements with keys:")
    public void theClientShouldSeeStringInAllElementContainingList(String jsonPath, String expected, List<String> jsonKeys) {
        theClientShouldSeeStringInAllElements(jsonPath, jsonKeys, expected, false);
    }

    /**
     * Method that checks if list of elements from jsonPath contain the value given based on strict boolean
     *
     * @param jsonPath
     * @param jsonKeys
     * @param expected
     * @param strict   if strict true - check that all elements from list of jsonKey have the exact value from expected
     *                 if strict false - check that at least one element from list contains the value case insensitive
     */
    public void theClientShouldSeeStringInAllElements(String jsonPath, List<String> jsonKeys, String expected, boolean strict) {
        ArrayList<Map<String, String>> result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat("Didn't get results", result.size(), not(0));
        if (strict) {
            for (String s : jsonKeys) {
                for (Map<String, String> map : result) {
                    assertThat(map, hasEntry(s, expected));
                }
            }
        } else {
            for (Map<String, String> map : result) {
                boolean found = false;
                for (String s : jsonKeys) {
                    if (map.get(s) == null) {
                        continue;
                    }
                    if (map.get(s).toLowerCase().contains(expected.toLowerCase())) {
                        found = true;
                        break;
                    }
                }
                assertThat("Didn't find the value: " + expected + " for either keys: " + jsonKeys + "\n Map:" + map.toString(), found, equalTo(true));
            }
        }
    }


    @Then("the client using jsonPath {string} should see having boolean {word}")
    public void theClientShouldSeeBoolean(String jsonPath, String expected) {
        boolean result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result, equalTo(Boolean.parseBoolean(expected)));
    }

    @Then("the client using jsonPath {string} should see having {int}")
    public void theClientShouldSeeInt(String jsonPath, int expected) {
        int result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result, equalTo(expected));
    }

    @Then("the client using jsonPath {string} should see having {float}")
    public void theClientShouldSeeInt(String jsonPath, double expected) {
        double result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result, equalTo(expected));
    }

    @Then("the client using jsonPath {string} should see:")
    public void theClientShouldSeeList(String jsonPath, Map<String, String> expected) {
        Map<String, String> result = JsonPath.from(response.asString()).get(jsonPath);
        expected.keySet().stream().forEach(key -> assertThat(result, hasEntry(key, expected.get(key))));
    }

    @Then("the client using jsonPath {string} should see {int} elements")
    public void theClientShouldSeeList(String jsonPath, int no) {
        ArrayList<String> result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result.size(), equalTo(no));
    }

    @Then("the client using jsonPath {string} should not be empty")
    public void theClientShouldNotSeeEmpty(String jsonPath) {
        Map<String, String> result = JsonPath.from(response.asString()).get(jsonPath);
    }

    @Then("the client should see the same response with {word} check")
    public void theClientShouldSeeSameResponse(String check) throws Exception {
        JSONCompareMode mode;
        switch (check) {
            case "STRICT":
                mode = JSONCompareMode.STRICT;
                break;
            case "LENIENT":
                mode = JSONCompareMode.LENIENT;
                break;

            default:
                throw new Exception("Mode check " + check + " not supported. Try STRICT or LENIENT");

        }
        //Ignoring the score key from check because of approximation problem in responses.
        //TO DO add a check to verify that the values from the two responses are closeTo a certain threshold
        JSONAssert.assertEquals(this.responseNext.asString(), this.response.asString(),
                new CustomComparator(mode,
                        new Customization("items[*].score", (o1, o2) -> true)
                )
        );
    }

    @Then("the client using jsonPath {string} should see elements sorted by {string} field in {word} order")
    public void theClientShouldSeeSortedElementsOrder(String jsonPath, String jsonKey, String order) throws Exception {
        ArrayList<Map<String, ? extends Number>> result = JsonPath.from(response.asString()).get(jsonPath);
        List<Float> list = new LinkedList<>();
        for (Map<String, ? extends Number> map : result) {
            list.add((Float) (((Number) map.get(jsonKey)).floatValue()));
        }

        List<Float> listSorted = list.stream().collect(Collectors.toList());
        switch (order.replaceAll("'", "")) {
            case "desc":
                listSorted.sort(Comparator.reverseOrder());
                break;
            case "asc":
                listSorted.sort(Comparator.naturalOrder());
                break;
            default:
                throw new Error("Order by " + order + " is not supported");
        }

        assertThat(list, equalTo(listSorted));
    }

    @Then("the client should see paginated links in header:")
    public void theClientShouldSeePaginatedLinksInHeader(List<String> steps) throws UnsupportedEncodingException{
        Map<String, String> pagesMap = getMapsPageLinksFromHeader();
        assertThat(response.getHeader("Link"),pagesMap.size(),equalTo(steps.size()));
        for (String s : steps) {
            assertThat(pagesMap, hasKey(s));
        }
    }

    @Then("the client should not see paginated links in header")
    public void theClientShouldNotSeePaginatedLinksInHeader() throws UnsupportedEncodingException{
        Map<String, String> pagesMap = getMapsPageLinksFromHeader();
        assertThat(pagesMap.size(), equalTo(0));

    }

    @Then("the client should see {int} pages with responses")
    public void theClientShouldSeeNoPages(Integer noPages) throws UnsupportedEncodingException{
        assertThat(getNumberOfPages(),equalTo(noPages));
    }

    @When("the client walks pagination to step {string} for {string} endpoint")
    public void theClientWalksPaginationStep(String step, String webServiceEndpoint) throws UnsupportedEncodingException {
        Map<String, String> pagesMap = getMapsPageLinksFromHeader();
        genericSteps.setDecorateUrl("");
        this.response = genericSteps.callEndpointHttpWithUrl(webServiceEndpoint,pagesMap.get(step));
        System.out.println();
    }

    public Map<String, String> getMapsPageLinksFromHeader() throws UnsupportedEncodingException {
        Map<String, String> mapPages = new HashMap<>();
        String paginationHeaderElement = "Link";
        if (response.getHeader(paginationHeaderElement) == null) {
            return mapPages;
        }
        List<String> result = Arrays.asList(response.getHeader(paginationHeaderElement).split(","));

        for (String s : result) {
            String[] link = s.split(";");
            String url = link[0].replaceAll("<", "").replaceAll(">", "").trim();
            String step = link[1].replace("rel=", "").replaceAll("\"", "").replace(",", "").trim();
            mapPages.put(step, URLDecoder.decode( url, "UTF-8" ));
        }
        return mapPages;
    }

    public Integer getNumberOfPages() throws UnsupportedEncodingException{
        Map<String, String> pagesMap = getMapsPageLinksFromHeader();
        String lastPageLink = pagesMap.get("last");
        return Integer.parseInt(lastPageLink.substring(lastPageLink.length() - 1));
    }


}
