package searchengine.dto.statistics;

public class SearchResponseError {
    private boolean result;
    private Error error;

    public SearchResponseError(Error error) {
        this.result = false;
        this.error = error;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
