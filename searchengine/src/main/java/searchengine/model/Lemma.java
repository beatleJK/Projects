package searchengine.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lemma",schema = "search_engine")
public class Lemma implements Comparable<Lemma>{
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "site_id")
    private Site site;
    @Column(name = "lemma", nullable = false)
    private String lemma;
    @Column(name = "frequency",nullable = false)
    private int frequency;
    @OneToMany(mappedBy = "lemma",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Index> indexes;
    public Lemma() {
    }

    public Lemma(Site site, String lemma, int frequency) {
        this.indexes = new ArrayList<>();
        this.site = site;
        this.lemma = lemma;
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public List<Index> getIndexes() {
        return indexes;
    }
    public void setIndexes(List<Index> indexes) {
        this.indexes.addAll(indexes);
    }
    public void addIndex(Index index){
        indexes.add(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lemma lemma1 = (Lemma) o;
        return site.equals(lemma1.getSite()) && lemma.equals(lemma1.getLemma());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemma);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Lemma{");
        sb.append("id=").append(id);
        sb.append(", site=").append(site);
        sb.append(", lemma='").append(lemma).append('\'');
        sb.append(", frequency=").append(frequency);
        sb.append(", indexes=").append(indexes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull Lemma o) {
        if(this.getFrequency() != o.getFrequency()){
            return this.getFrequency() - o.getFrequency();
        } else {
            return this.getLemma().compareTo(o.getLemma());
        }
    }
}
