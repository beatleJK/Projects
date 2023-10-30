package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma,Integer> {
    Lemma findByLemma(String lemma);

    Iterable<Lemma> findAllByLemma(String lemma);
    long deleteByLemma(String lemma);
}
