package searchengine.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "sites",schema = "search_engine")
public class Site {
    @Column(name = "id",nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status",columnDefinition = "enum('INDEXING','INDEXED','FAILED')")
    private Status status;
    @Column(name = "status_time",nullable = false)
    private LocalDateTime statusTime;
    @Column(name = "last_error")
    private String lastError;
    @Column(name = "url",nullable = false)
    private String url;
    @Column(name = "name",nullable = false)
    private String name;
    @OneToMany(mappedBy = "site",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Page> pages;

    @OneToMany(mappedBy = "site",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Lemma> lemmas;

    public Site() {
    }
    public Site(Status status, String lastError, String url, String name) {
        this.status = status;
        this.statusTime = LocalDateTime.now();
        this.lastError = lastError;
        this.url = url;
        this.name = name;
        this.pages = new ArrayList<>();
        this.lemmas = new ArrayList<>();
    }

    public Site(int id, Status status, LocalDateTime statusTime, String lastError, String url, String name) {
        this.id = id;
        this.status = status;
        this.statusTime = statusTime;
        this.lastError = lastError;
        this.url = url;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(LocalDateTime statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public List<Lemma> getLemmas() {
        return lemmas;
    }

    public void setLemmas(List<Lemma> lemmas) {
        this.lemmas.addAll(lemmas);
    }

    public void addPage(Page page){
        this.pages.add(page);
    }
    public void addLemma(Lemma lemma){
        this.lemmas.add(lemma);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return Objects.equals(url, site.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "Site{" +
                "id=" + id +
                ", status=" + status +
                ", statusTime=" + statusTime +
                ", lastError='" + lastError + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
