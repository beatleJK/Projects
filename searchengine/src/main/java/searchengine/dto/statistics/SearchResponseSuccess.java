package searchengine.dto.statistics;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.services.*;

import java.util.*;

public class SearchResponseSuccess {
    @Autowired
    private static IndexService indexService;
    private final boolean result = true;
    private int count;
    private Set<SearchData> data;

    private static MorphologyServiceImpl morphologyService = new MorphologyServiceImpl();

    public SearchResponseSuccess() {
    }

    public SearchResponseSuccess(int count, Set<SearchData> data) {
        this.count = count;
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Set<SearchData> getData() {
        return data;
    }

    public void setData(Set<SearchData> data) {
        this.data = data;
    }

    public static SearchResponseSuccess getResponse(Set<Page> pageSet,QueryRequest queryRequest){
        Set<SearchData> dataSet = new TreeSet<>();
        for (Page page : pageSet){
            String site = page.getSite().getUrl();
            String siteName = page.getSite().getName();
            String uri = page.getPath();
            String title = Jsoup.parse(page.getContent()).title();
            String snippet = getSnippet(page,queryRequest);
            double relevance = 0.0;
            SearchData searchData = new SearchData(site,siteName,uri,title,snippet,relevance);
            dataSet.add(searchData);
        }
        int count = dataSet.size();
        return new SearchResponseSuccess(count,dataSet);
    }
    private static String getSnippet(Page page,QueryRequest queryRequest) {
        StringBuilder snippet = new StringBuilder();
        String[] searchWords = queryRequest.getQuery().split("\\s+");
        Set<String> normalSearchWords = new HashSet<>();
        for(String word : searchWords){
            String normalWord = morphologyService.getNormalFormWord(word);
            normalSearchWords.add(normalWord);
        }
        String text = Jsoup.parse(page.getContent()).text();
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++){
            String normalWord = morphologyService.getNormalFormWord(words[i]);
            if(normalSearchWords.contains(normalWord)){
                String partResult = getSelectedText(words,i);
                if(!partResult.isBlank()){
                    snippet.append(partResult);
                }
            }
        }
    return snippet.toString();
    }

    private static String getSelectedText(String[]words,int numberWord){
        String oldKey = words[numberWord];
        String newKey = "<b>".concat(oldKey).concat("</b>");
        words[numberWord] = newKey;
        int step = 5;
        int right = Math.min(words.length - 1 - numberWord,step);
        int left = Math.min(numberWord,step);
        String[] fragments = Arrays.copyOfRange(words,numberWord - left, numberWord + right);
        return String.join("\s",fragments);
    }
}
