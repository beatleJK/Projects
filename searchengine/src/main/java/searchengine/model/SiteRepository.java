package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends CrudRepository<Site,Integer> {
    Site findByUrl(String url);
    long deleteByUrl(String url);
    boolean existsByUrl(String url);
}
