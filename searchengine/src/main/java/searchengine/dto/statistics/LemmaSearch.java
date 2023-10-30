package searchengine.dto.statistics;

import org.jetbrains.annotations.NotNull;

public class LemmaSearch implements Comparable<LemmaSearch>{
    private String lemma;
    private int frequency;

    public LemmaSearch(String lemma, int frequency) {
        this.lemma = lemma;
        this.frequency = frequency;
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

    @Override
    public String toString() {
        return "LemmaSearch{" +
                "lemma='" + lemma + '\'' +
                ", frequency=" + frequency +
                '}';
    }

    @Override
    public int compareTo(@NotNull LemmaSearch o) {
        return this.frequency - o.getFrequency();
    }
}
