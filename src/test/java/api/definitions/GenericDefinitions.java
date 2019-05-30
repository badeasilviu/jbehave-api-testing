package api.definitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import api.steps.GenericSteps;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Steps;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.rest;
import static net.serenitybdd.rest.SerenityRest.restAssuredThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
public class GenericDefinitions {

    @Steps
    GenericSteps genericSteps;

    private Response response;

    @Given("the client uses the following url parameters:")
    public void theFollowingUrlParameters(Map<String,String> urlParameters) {
        genericSteps.setDecorateUrl(urlParameters);
    }

    @When("the client calls {string} endpoint")
    public void theUserCallsEndpoint(String webServiceEndpoint) {
        this.response = genericSteps.callEndpoint(webServiceEndpoint);
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

}
