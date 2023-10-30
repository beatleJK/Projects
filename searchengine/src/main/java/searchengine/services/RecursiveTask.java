package searchengine.services;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import searchengine.config.ConnectionConfig;
import searchengine.model.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class RecursiveTask extends RecursiveAction {
    private Page page;
    private Site site;
    private Lemma lemma;
    private Index index;
    private IndexKey indexKey;
    private ConnectionConfig connectionConfig;
    private HttpConnection httpConnection;
    private SiteService siteService;
    private PageService pageService;
    private LemmaService lemmaService;
    private IndexService indexService;
    private ForkJoinPool pool;
    private MorphologyServiceImpl morphologyService;

    public RecursiveTask(String rootUrl, String siteName, ConnectionConfig connectionConfig, SiteService siteService, PageService pageService, LemmaService lemmaService, IndexService indexService) {
        this.indexService = indexService;
        this.lemmaService = lemmaService;
        this.connectionConfig = connectionConfig;
        this.httpConnection = new HttpConnection(connectionConfig);
        this.siteService = siteService;
        this.pageService = pageService;
        this.morphologyService = new MorphologyServiceImpl();
        this.site = siteService.read(rootUrl);
        if (site != null) {
            long countRow = siteService.delete(site.getUrl());
            if (countRow <= 0) {
                throw new RuntimeException("rows do not delete");
            }
        }
        this.site = new Site(Status.INDEXING, null, rootUrl, siteName);
        site = siteService.create(site);
        if(httpConnection.isExistsConnection(rootUrl)){
            try {
                String context = httpConnection.getConnection(rootUrl).get().html();
                page = new Page(site,rootUrl,200,context);
                page = pageService.create(page);
                site.addPage(page);
                site = siteService.create(site);
                processing();
                RecursiveTask task = new RecursiveTask(site,page,connectionConfig,siteService,pageService,lemmaService,indexService);
                task.invoke();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            site.setStatus(Status.FAILED);
            site = siteService.create(site);
        }
    }

    public RecursiveTask(Site site,Page page,ConnectionConfig connectionConfig, SiteService siteService, PageService pageService, LemmaService lemmaService, IndexService indexService) {
        this.morphologyService = new MorphologyServiceImpl();
        this.connectionConfig = connectionConfig;
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.site = site;
        this.page = page;
    }

    @Override
    protected void compute() {
        List<RecursiveTask> taskList = new ArrayList<>();
        List<Page> childrenPages = getChildrenPages(page);
        if (!childrenPages.isEmpty()) {
            Iterator<Page> iterator = childrenPages.iterator();
            while (iterator.hasNext()){
                page = iterator.next();
                page = pageService.create(page);
                site.addPage(page);
                site = siteService.create(site);
                processing();
                RecursiveTask task = new RecursiveTask(site,page,connectionConfig,siteService,pageService,lemmaService,indexService);
                taskList.add(task);
            }
            ForkJoinTask.invokeAll(taskList);
            pool = ForkJoinTask.getPool();
        }
        site.setStatus(Status.INDEXED);
        site = siteService.create(site);
    }
    private void processing(){
        if (page != null) {
            //Получение текста страницы без тэгов
            String text = getTextPage(page);
            try {
                //Составление карты ключ - перечень лемм на странице, значение количество лемм на странице
                Map<String, Integer> lemmasMap = morphologyService.getWordsMap(text);
                    List<Lemma> listLemma = new ArrayList<>();
                    List<Index> listIndex = new ArrayList<>();
                    if(!lemmasMap.isEmpty()){
                        lemmasMap.forEach((l,i)->{
                            lemma = lemmaService.read(l,site);
                            if(lemma != null){
                                Integer oldFrequency = lemma.getFrequency();
                                Integer newFrequency = oldFrequency + 1;
                                lemma.setFrequency(newFrequency);
                                lemma = lemmaService.create(lemma);
                            } else {
                                lemma = new Lemma(site,l,1);
                                lemma = lemmaService.create(lemma);
                            }
                            Integer pageId = page.getId();
                            Integer lemmaId = lemma.getId();
                            indexKey = new IndexKey(pageId, lemmaId);
                            float rank = i;
                            index = new Index(indexKey, page, lemma, rank);
                            index = indexService.create(index);
                            lemma.addIndex(index);
                            site.addLemma(lemma);
                            page.addIndex(index);
                            site.addPage(page);
                        });
                    }
                    page = pageService.create(page);
                    site.addPage(page);
                    site = siteService.create(site);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopRecursiveTask() {
        pool.shutdownNow();
    }

    //Connect to site
    public Connection getConnection(String url) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Jsoup.connect(url)
                .userAgent(connectionConfig.getUserAgent())
                .referrer(connectionConfig.getReferrer())
                .ignoreHttpErrors(true);
    }

    private CopyOnWriteArrayList<Page> getChildrenPages(Page page) {
        CopyOnWriteArrayList<Page> childrenPages = new CopyOnWriteArrayList<>();
        if (page != null) {
            String htmlPage = page.getContent();
            if (htmlPage != null) {
                Document document = Jsoup.parse(htmlPage);
                Elements elements = document.getElementsByTag("a");
                for (Element element : elements) {
                    String href = element.attr("href");
                    if (isValidationLink(href)) {
                        Page currentPage = getPage(href);
                        if (currentPage != null && !pageService.isExists(currentPage.getPath())) {
                            childrenPages.add(currentPage);
                        }
                    }
                }
            }
        }
        return childrenPages;
    }

    public Page getPage(String url) {
        if (url != null) {
            Connection connection = getConnection(url);
            try {
                int statusCode = connection.execute().statusCode();
                if (statusCode == 200) {
                    String html = connection.get().html();
                    return new Page(site, url, statusCode, html);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    public boolean isValidationLink(String link) {
        String regexRoot = site.getUrl().concat("[[/\\w]+/?]*");
        return link.matches(regexRoot);
    }
    public String getValidationLink(String link) {
        String rootUrl = site.getUrl().replaceAll("https?://", "");
        String regexRoot = "https?://".concat(rootUrl).concat("[\\w/]+");
        String regexChildLink = "[/?\\w+[\\.html]*/?]+";
        if (link.matches(regexRoot)) {
            return link;
        } else if (link.matches(regexChildLink)) {
            link = link.replaceAll("^/", "");
            return "https://".concat(rootUrl).concat("/").concat(link);
        }
        return null;
    }

    public String getTextPage(Page page) {
        String htmlPage = page.getContent();
        if (htmlPage != null) {
            String text = Jsoup.clean(htmlPage, Safelist.none());
            return text;
        } else {
            return null;
        }
    }
}
