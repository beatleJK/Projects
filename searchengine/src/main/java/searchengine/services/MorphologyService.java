package searchengine.services;

import java.io.IOException;
import java.util.Map;

public interface MorphologyService {
   Map<String,Integer> getWordsMap(String text) throws IOException;
}
