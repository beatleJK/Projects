package searchengine.services;

import org.jsoup.Jsoup;
import searchengine.dto.statistics.QueryRequest;
import searchengine.dto.statistics.SearchData;
import searchengine.dto.statistics.SearchResponseSuccess;
import searchengine.model.*;

import java.io.IOException;
import java.util.*;

public class CoreEngine {
    private QueryRequest request;
    private SiteService siteService;
    private PageService pageService;
    private LemmaService lemmaService;
    private IndexService indexService;
    private MorphologyServiceImpl morphologyService;
    private int maxFrequency;

    public CoreEngine(QueryRequest request, SiteService siteService, PageService pageService, LemmaService lemmaService, IndexService indexService, int maxFrequency) {
        this.request = request;
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.morphologyService = new MorphologyServiceImpl();
        this.maxFrequency = maxFrequency;
    }

    public Set<Lemma> getLemmasFromRequest(QueryRequest request, int maxFrequency) {
        String text = request.getQuery();
        Set<Lemma> lemmaSet = new TreeSet<>();
        try {
            Set<String> titleSet = morphologyService.getWordsMap(text).keySet();
            for (String title : titleSet) {
                Iterator<Lemma> lemmaIterator = lemmaService.readAllLemmas(title).iterator();
                while (lemmaIterator.hasNext()) {
                    Lemma lemma = lemmaIterator.next();
                    if (lemma.getFrequency() <= maxFrequency) {
                        lemmaSet.add(lemma);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lemmaSet;
    }

    public Set<Page> getPagesFromLemma(Set<Page> pageSet, Lemma lemma) {
        Set<Page> newPage = new HashSet<>();
        List<Index> indexList = lemma.getIndexes();
        Iterator<Index> iterator = indexList.iterator();
        while (iterator.hasNext()) {
            Index index = iterator.next();
            Page page = index.getPage();
            if (pageSet.isEmpty()) {
                newPage.add(page);
            } else {
                if (pageSet.contains(page)) {
                    newPage.add(page);
                }
            }
        }
        return newPage;
    }

    public Set<Page> getSearchPages(Set<Lemma> lemmaSet) {
        Set<Page> currentPages = new HashSet<>();
        for (Lemma lemma : lemmaSet) {
            currentPages.addAll(getPagesFromLemma(currentPages, lemma));
        }
        return currentPages;
    }

    public SearchResponseSuccess getResponse(Set<Page> pageSet, QueryRequest queryRequest) {
        Set<SearchData> dataSet = new TreeSet<>();
        Map<Page, Double> mapRelevance = getRelevance(pageSet);
        for (Page page : pageSet) {
            String site = page.getSite().getUrl();
            String siteName = page.getSite().getName();
            String uri = page.getPath();
            String title = Jsoup.parse(page.getContent()).title();
            String snippet = getSnippet(page, queryRequest);
            double relevance = mapRelevance.get(page);
            SearchData searchData = new SearchData(site, siteName, uri, title, snippet, relevance);
            dataSet.add(searchData);
        }
        int count = dataSet.size();
        return new SearchResponseSuccess(count, dataSet);
    }

    private String getSnippet(Page page, QueryRequest queryRequest) {
        StringBuilder snippet = new StringBuilder();
        String[] searchWords = queryRequest.getQuery().split("\\s+");
        Set<String> normalSearchWords = new HashSet<>();
        for (String word : searchWords) {
            String normalWord = morphologyService.getNormalFormWord(word);
            normalSearchWords.add(normalWord);
        }
        String text = Jsoup.parse(page.getContent()).text();
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String normalWord = morphologyService.getNormalFormWord(words[i]);
            if (normalSearchWords.contains(normalWord)) {
                String partResult = getSelectedText(words, i);
                if (!partResult.isBlank()) {
                    snippet.append(partResult);
                }
            }
        }
        return snippet.toString();
    }

    private String getSelectedText(String[] words, int numberWord) {
        String oldKey = words[numberWord];
        String newKey = "<b>".concat(oldKey).concat("</b>");
        words[numberWord] = newKey;
        int step = 5;
        int right = Math.min(words.length - 1 - numberWord, step);
        int left = Math.min(numberWord, step);
        String[] fragments = Arrays.copyOfRange(words, numberWord - left, numberWord + right);
        return String.join("\s", fragments);
    }

    private Map<Page, Double> getRelevance(Set<Page> pageSet) {
        Map<Page, Double> map = new HashMap<>();
        double relMax = getMaxRelevance(pageSet);
        for (Page page : pageSet) {
            double relAbs = getAbsRelevance(page);
            double relevance = relAbs/relMax;
            map.put(page, relevance);
        }
        return map;
    }

    private double getMaxRelevance(Set<Page> pageSet) {
        double relMax = 0;
        for (Page page : pageSet) {
            double relAbs = getAbsRelevance(page);
            if (relMax == 0) {
                relMax = relAbs;
            } else {
                relMax = Math.max(relMax, relAbs);
            }
        }
        return relMax;
    }

    private double getAbsRelevance(Page page) {
        float rankAbs = 0;
        Site site = page.getSite();
        String[] searchWords = request.getQuery().split("\\s+");
        for (String searchWord : searchWords) {
            String normalWord = morphologyService.getNormalFormWord(searchWord);
            if (!normalWord.isBlank()) {
                Lemma lemma = lemmaService.read(normalWord, site);
                if (lemma != null) {
                    Index index = indexService.read(new IndexKey(page.getId(), lemma.getId())).get();
                    rankAbs += index.getRank();
                }
            }
        }
        return rankAbs;
    }
}
