package searchengine.dto.statistics;

public class ResponseMethods {
    private boolean result;
    private String error;

    public ResponseMethods(boolean result, String error) {
        this.result = result;
        this.error = error;
    }

    public ResponseMethods(boolean result) {
        this.result = result;
        this.error = "";
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
