package searchengine.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Site;
import searchengine.model.SiteRepository;

@Component
public class SiteService {
    @Autowired
    public SiteRepository repository;

    @Transactional
    public Site create(Site site){
        if(isExists(site.getUrl())){
            return read(site.getUrl());
        } else {
            return repository.save(site);
        }
    }
    @Transactional
    public boolean isExists( String url){
        return repository.existsByUrl(url);
    }
    @Transactional
    public Site read(String url){
        return repository.findByUrl(url);
    }

    @Transactional
    public long delete(String url){
        return repository.deleteByUrl(url);
    }
}
