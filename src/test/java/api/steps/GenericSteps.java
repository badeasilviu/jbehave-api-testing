package api.steps;

import api.WebServiceEndPoints;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Step;
import org.jruby.RubyProcess;

import java.util.Map;
import java.util.stream.Collectors;

public class GenericSteps {

    private String decorateUrl = "";

    @Step("Call web service endpoint")
    public  Response callEndpoint(String webServiceEndpoint) {
            return callEndpointHttp(webServiceEndpoint,"");
    }

    @Step("Call web service endpoint with body")
    public  Response callEndpoint(String webServiceEndpoint, String body) {
        return callEndpointHttp(webServiceEndpoint, body);
    }


    private String decorateUrl(Map<String,String> parameters) {

        return parameters.keySet().stream()
                .map(key -> key + "=" + parameters.get(key))
                .collect(Collectors.joining("&","?",""));
    }

    public void setDecorateUrl(Map<String,String> parameters) {
        this.decorateUrl = decorateUrl(parameters);
    }

    public void setDecorateUrl(String decorateUrl) {
        this.decorateUrl = decorateUrl;
    }


    private Response callEndpointHttp(String webServiceEndpoint, String body) {
        String url = WebServiceEndPoints.valueOf(webServiceEndpoint).getUrl() + this.decorateUrl;
        String method = WebServiceEndPoints.valueOf(webServiceEndpoint).getMethod();
        return callEndpointHttpMethod(url, method, body);

    }

    public Response callEndpointHttpWithUrl(String webServiceEndpoint,String url) {
        String method = WebServiceEndPoints.valueOf(webServiceEndpoint).getMethod();
        return callEndpointHttpMethod(url, method, "");
    }

    private Response callEndpointHttpMethod(String url, String method, String body) {
        switch (method) {
            case "GET":
                return callEndpointHttpMethodGet(url);
            case "POST":
                return callEndpointHttpMethodPost(url, body);
            default:
                throw new Error("Method" + method + "not supported");
        }
    }

    private Response callEndpointHttpMethodGet(String url) {
        Response response = SerenityRest.given()
                .contentType("application/json")
                .header("Content-Type", "application/json")
                .get(url);
        return response;

    }

    private Response callEndpointHttpMethodPost(String url, String body) {
        Response response = SerenityRest.given()
                .contentType("application/json")
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(url);
        return response;
    }


}
