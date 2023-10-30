package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;
import searchengine.model.LemmaRepository;
import searchengine.model.Site;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class LemmaService {
    @Autowired
    private LemmaRepository repository;

    @Transactional
    public Lemma create(Lemma lemma){
        return repository.save(lemma);
    }

    @Transactional
    public List<Lemma> create(List<Lemma> lemmaList){
        List<Lemma> newList = new ArrayList<>();
        for (Lemma lemma : lemmaList){
            Lemma newLemma = read(lemma.getLemma(),lemma.getSite());
            if(newLemma != null){
                newLemma.setFrequency(newLemma.getFrequency() + lemma.getFrequency());
                newLemma = repository.save(newLemma);
            } else {
                newLemma = repository.save(lemma);
            }
            newList.add(newLemma);
        }
    return newList;
    }

    @Transactional
    public Lemma read(String lemma, Site site){
        Iterator<Lemma> iterator = repository.findAllByLemma(lemma).iterator();
        while (iterator.hasNext()){
            Lemma currentLemma = iterator.next();
            if(currentLemma.getSite().equals(site)){
                return currentLemma;
            }
        }
    return null;
    }

    @Transactional
    public Iterable<Lemma> readAllLemmas(String lemma){
        return repository.findAllByLemma(lemma);
    }
    @Transactional
    public long delete(String lemma){
        return repository.deleteByLemma(lemma);
    }
}
