package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Component
public class IndexService {

    @Autowired
    private IndexRepository repository;

    @Transactional
    public Index create(Index index){
        return repository.save(index);
    }

    @Transactional
    public List<Index> create(List<Index> indexList){
        return (List<Index>) repository.saveAll(indexList);
    }

    public Optional<Index> read(IndexKey indexKey){
        return repository.findById(indexKey);
    }
}
