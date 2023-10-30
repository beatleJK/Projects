package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.config.ConnectionConfig;
import searchengine.config.Site;
import searchengine.dto.statistics.*;
import searchengine.dto.statistics.Error;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.services.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private SiteService siteService;
    @Autowired
    private PageService pageService;
    @Autowired
    private LemmaService lemmaService;
    @Autowired
    private IndexService indexService;
    private final StatisticsService statisticsService;
    private final ConfigProperties properties;
    private ConnectionConfig connectionConfig;
    private boolean isEndIndexed = true;
    private RecursiveTask recursiveTask;

    public ApiController(StatisticsService statisticsService, ConfigProperties configProperties, ConnectionConfig connectionConfig) {
        this.statisticsService = statisticsService;
        this.properties = configProperties;
        this.connectionConfig = connectionConfig;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity startIndexing() {
        String url = "https://www.google.ru/";
        List<Site> siteList = properties.getSitesList().getSites();
        url = siteList.get(2).getUrl();
        String siteName = siteList.get(2).getName();
        if (isEndIndexed) {
            isEndIndexed = false;
            try {
                recursiveTask = new RecursiveTask(url, siteName, connectionConfig, siteService, pageService, lemmaService, indexService);
            } catch (Exception e) {
                System.out.println("END");
            }
            isEndIndexed = true;
            return new ResponseEntity(new ResponseMethods(true), HttpStatus.OK);
        } else {
            return new ResponseEntity(new ResponseMethods(false, "Индексация уже запущена"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity stopIndexing() {
        if (isEndIndexed) {
            return new ResponseEntity(new ResponseMethods(false, "Индексация не запущена"), HttpStatus.BAD_REQUEST);
        } else {
            if (recursiveTask != null) {
                recursiveTask.stopRecursiveTask();
            }
            return new ResponseEntity(new ResponseMethods(true), HttpStatus.OK);
        }
    }

    @PostMapping("/indexPage")
    public ResponseEntity indexPage(@RequestParam String url, @RequestParam String name) {
        if (isEndIndexed) {
            isEndIndexed = false;
            List<String> urlList = new ArrayList<>();
            properties.getSitesList().getSites().forEach(site -> urlList.add(site.getUrl()));
            if (urlList.contains(url)) {
                recursiveTask = new RecursiveTask(url, name, connectionConfig, siteService, pageService, lemmaService, indexService);
                return new ResponseEntity(new ResponseMethods(true), HttpStatus.OK);
            } else {
                return new ResponseEntity(
                        new ResponseMethods(false, "Данная страница находится за пределами сайтов,указанных в конфигурационном файле"), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(new ResponseMethods(false, "Индексация уже запущена"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String query) {
        //TODO: Переделать queryRequest по другому задавать параметры
        QueryRequest queryRequest = new QueryRequest(query,"https://www.google.ru",0,10);
        int maxFrequencyLemmas = 20; //Настройка для исключения количества лемм, встречающихся слишком часто
        int frequencyLemmas = 0;
        List<SearchData> listData = new ArrayList<>();
        if (queryRequest.getQuery() != null) {
            CoreEngine core = new CoreEngine(queryRequest, siteService, pageService, lemmaService, indexService, maxFrequencyLemmas);
            Set<Lemma> lemmaSet = core.getLemmasFromRequest(queryRequest,maxFrequencyLemmas);
            Set<Page> searchPages = core.getSearchPages(lemmaSet);
            SearchResponseSuccess response = core.getResponse(searchPages,queryRequest);
        if (searchPages.isEmpty()) {
            return new ResponseEntity<>(new SearchResponseError(Error.ПОИСК_НЕ_ДАЛ_РЕЗУЛЬТАТОВ), HttpStatus.valueOf(404));
        } else {
            return new ResponseEntity<>(response,HttpStatus.valueOf(200));
        }
        } else {
            return new ResponseEntity<>(new SearchResponseError(Error.ЗАДАН_ПУСТОЙ_ПОИСКОВЫЙ_ЗАПРОС), HttpStatus.valueOf(404));
        }
    }
}
