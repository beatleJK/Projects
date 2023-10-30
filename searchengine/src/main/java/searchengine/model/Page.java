package searchengine.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pages",schema = "search_engine")
public class Page {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "site_id",nullable = false)
    private Site site;

    @Column(name = "path", columnDefinition = "TEXT",nullable = false)
    private String path;

    @Column(name = "code",nullable = false)
    private int code;

    @Column(name = "content",columnDefinition = "MEDIUMTEXT",nullable = false)
    private String content;

    @OneToMany(mappedBy = "page",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Index> indexes;

    public Page() {
    }

    public Page(Site site, String path, int code, String content) {
        this.indexes = new ArrayList<>();
        this.site = site;
        this.path = path;
        this.code = code;
        this.content = content;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        Page page = (Page) o;
        return site.equals(page.site) && path.equals(page.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(site, path);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Page{");
        sb.append("id=").append(id);
        sb.append(", site=").append(site);
        sb.append(", path='").append(path).append('\'');
        sb.append(", code=").append(code);
        sb.append(", content='").append(content).append('\'');
        sb.append(", indexes=").append(indexes);
        sb.append('}');
        return sb.toString();
    }
}
