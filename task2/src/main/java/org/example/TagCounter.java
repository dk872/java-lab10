package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TagCounter {

    public static Map<String, Integer> countTags(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (Element element : document.getAllElements()) {
            String tagName = element.tagName().toLowerCase(Locale.ROOT);
            frequencyMap.put(tagName, frequencyMap.getOrDefault(tagName, 0) + 1);
        }

        return frequencyMap;
    }

    public static List<Map.Entry<String, Integer>> sortedByName(Map<String, Integer> frequencyMap) {
        return frequencyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    public static List<Map.Entry<String, Integer>> sortedByFrequency(Map<String, Integer> frequencyMap) {
        return frequencyMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue)
                        .thenComparing(Map.Entry::getKey))
                .collect(Collectors.toList());
    }
}
