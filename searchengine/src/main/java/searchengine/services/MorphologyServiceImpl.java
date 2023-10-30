package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class MorphologyServiceImpl implements MorphologyService {
    private LuceneMorphology luceneMorph;

    public MorphologyServiceImpl() {
        try {
            this.luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Integer> getWordsMap(String text) throws IOException {
        SortedMap<String, Integer> lemmas = new TreeMap<>();
        String[] words = text.split("\\s+");
        for (String word : words) {
            word = word.trim().toLowerCase().replaceAll("[^А-ЯЁа-яё]", "");
            if (word.isBlank()) {
                continue;
            }
            List<String> normalForms = luceneMorph.getMorphInfo(word);
            if (isFunctionPart(word)) {
                continue;
            }
            if (normalForms.isEmpty()) {
                continue;
            }
            String normalWord = normalForms.toString().split("\\|")[0].replaceAll("\\]*\\[*", "");
            if (lemmas.containsKey(normalWord)) {
                lemmas.put(normalWord, lemmas.get(normalWord) + 1);
            } else {
                lemmas.put(normalWord, 1);
            }
        }
        return lemmas;
    }

    public boolean isFunctionPart(String word) throws IOException {
        if (!word.isBlank()) {
            List<String> wordBaseForms = luceneMorph.getMorphInfo(word);
            String[] infoArray = wordBaseForms.toString().split("\s");
            String info = infoArray[1].replaceAll("[,.!?\\]\\[]*", "");
            return info.equals("СОЮЗ") || info.equals("МЕЖД") || info.equals("ПРЕДЛ") || info.equals("ЧАСТ")
                     || info.equals("МС") || info.equals("МС-П") || info.equals("П");
        } else {
            return true;
        }
    }

    public String getNormalFormWord(String word) {
        word = word.toLowerCase().strip();
        if (!word.isBlank() && luceneMorph.checkString(word)) {
            List<String> wordBaseForms = luceneMorph.getNormalForms(word);
            return wordBaseForms.get(0);
        } else {
            return "";
        }
    }
}
