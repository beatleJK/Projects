package searchengine.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "search_index",schema = "search_engine")
public class Index {
    @EmbeddedId
    private IndexKey id;
    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("pageId")
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("lemmaId")
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    @Column(name = "rank_lemma")
    private float rank;

    public Index() {
    }
    public Index(IndexKey id, Page page, Lemma lemma, float rank) {
        this.id = id;
        this.page = page;
        this.lemma = lemma;
        this.rank = rank;
    }

    public IndexKey getId() {
        return id;
    }

    public void setId(IndexKey id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Lemma getLemma() {
        return lemma;
    }

    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return page.equals(index.page) && lemma.equals(index.lemma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, lemma);
    }
}
