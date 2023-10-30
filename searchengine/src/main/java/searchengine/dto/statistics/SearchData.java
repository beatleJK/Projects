package searchengine.dto.statistics;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import searchengine.model.Page;
import searchengine.services.MorphologyServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchData implements Comparable<SearchData>{
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private double relevance;

    public SearchData() {
    }

    public SearchData(String site, String siteName, String uri, String title, String snippet, double relevance) {
        this.site = site;
        this.siteName = siteName;
        this.uri = uri;
        this.title = title;
        this.snippet = snippet;
        this.relevance = relevance;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    @Override
    public int compareTo(@NotNull SearchData o) {
        if(this.getRelevance() == o.getRelevance()){
            return this.getSiteName().compareTo(o.getSiteName());
        }else {
            return (int)(this.getRelevance() - o.getRelevance());
        }
    }
}
