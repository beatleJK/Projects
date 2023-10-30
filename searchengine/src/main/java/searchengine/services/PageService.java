package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;
import searchengine.model.PageRepository;
@Component
public class PageService {
    @Autowired
    private PageRepository repository;
    @Transactional
    public Page create(Page page) {
        return repository.save(page);
    }
    @Transactional
    public boolean isExists(String path){
        return repository.existsByPath(path);
    }
    @Transactional
    public Page read(String path){
        return repository.findByPath(path);
    }

    @Transactional
    public long delete(String path){
        return repository.deleteByPath(path);
    }
}
