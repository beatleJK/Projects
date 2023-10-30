package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends CrudRepository<Page,Integer> {
    Page findByPath(String path);
    long deleteByPath(String path);
    boolean existsByPath(String path);
}
