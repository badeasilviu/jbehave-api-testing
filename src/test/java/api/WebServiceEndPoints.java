package api;

public enum WebServiceEndPoints {
    SEARCH_REPOSITORIES("https://api.github.com/search/repositories", "GET");

    private final String url;
    private final String method;

    WebServiceEndPoints(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }
    public String getMethod() {return method;}
}
