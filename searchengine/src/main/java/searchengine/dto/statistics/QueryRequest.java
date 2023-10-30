package searchengine.dto.statistics;

import org.springframework.boot.context.properties.ConstructorBinding;

public class QueryRequest {
    private String query;
    private String site;
    private int offset;
    private int limit;

    public QueryRequest() {
        this.offset = 0;
        this.limit = 20;
    }

    @ConstructorBinding
    public QueryRequest(String query, String site, int offset, int limit) {
        this.query = query;
        this.site = site;
        this.offset = offset;
        this.limit = limit;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "QueryRequest{" +
                "query='" + query + '\'' +
                ", site='" + site + '\'' +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
