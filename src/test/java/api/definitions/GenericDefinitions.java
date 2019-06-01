package api.definitions;

import api.steps.GenericSteps;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.thucydides.core.annotations.Steps;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

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
    public void theFollowingUrlParameters(Map<String,String> urlParameters) {
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

    @Then("the client searching in response using jsonPath {string} should see {string}")
    public void theClientShouldSeeString(String jsonPath, String expected) {
        String result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result,containsString(expected));
    }

    @Then("the client searching in response using jsonPath {string} should see boolean {word}")
    public void theClientShouldSeeBoolean(String jsonPath, String expected) {
        boolean result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result,equalTo(Boolean.parseBoolean(expected)));
    }

    @Then("the client searching in response using jsonPath {string} should see {int}")
    public void theClientShouldSeeInt(String jsonPath, int expected) {
        int result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result,equalTo(expected));
    }

    @Then("the client searching in response using jsonPath {string} should see {float}")
    public void theClientShouldSeeInt(String jsonPath, double expected) {
        double result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result,equalTo(expected));
    }

    @Then("the client searching in response using jsonPath {string} should see:")
    public void theClientShouldSeeList(String jsonPath, Map<String,String> expected) {
        Map<String,String> result = JsonPath.from(response.asString()).get(jsonPath);
        expected.keySet().stream().forEach(key -> assertThat(result,hasEntry(key,expected.get(key))));
    }

    @Then("the client searching in response using jsonPath {string} should see {int} elements")
    public void theClientShouldSeeList(String jsonPath, int no) {
        ArrayList<String> result = JsonPath.from(response.asString()).get(jsonPath);
        assertThat(result.size(),equalTo(no));
    }

    @Then("the client searching in response using jsonPath {string} should not be empty")
    public void theClientShouldNotSeeEmpty(String jsonPath) {
        Map<String,String> result = JsonPath.from(response.asString()).get(jsonPath);
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
        JSONAssert.assertEquals(this.responseNext.asString(),this.response.asString(),
                new CustomComparator(mode,
                        new Customization("items[*].score", (o1, o2) -> true)
                )
        );
    }

    @Then("the client searching in response using jsonPath {string} should see elements sorted by {string} field in {word} order")
    public void theClientShouldSeeSortedElementsOrder(String jsonPath, String jsonKey, String order) throws Exception {
        ArrayList<Map<String,? extends Number>> result = JsonPath.from(response.asString()).get(jsonPath);
        List<Float> list = new LinkedList<>();
        for (Map<String,? extends Number> map : result) {
            list.add((Float)(((Number) map.get(jsonKey)).floatValue()));
        }

        List<Float> listSorted = list.stream().collect(Collectors.toList());
        switch (order.replaceAll("'","")) {
            case "desc":
                listSorted.sort(Comparator.reverseOrder());
                break;
            case "asc":
                listSorted.sort(Comparator.naturalOrder());
                break;
            default:
                throw new Error("Order by " + order + " is not supported");
        }

        assertThat(list,equalTo(listSorted));
    }


}
