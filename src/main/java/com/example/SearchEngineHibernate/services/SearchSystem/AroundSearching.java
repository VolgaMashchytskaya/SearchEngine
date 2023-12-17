package com.example.SearchEngineHibernate.services.SearchSystem;

import com.example.SearchEngineHibernate.dto.app.Lemma;
import com.example.SearchEngineHibernate.dto.app.SearchIndex;
import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.dto.search.SearchResult;
import com.example.SearchEngineHibernate.services.DataBaseCreation.LemmaServices;
import com.example.SearchEngineHibernate.services.Repositories.LemmaRepository;
import com.example.SearchEngineHibernate.services.Repositories.PageRepository;
import com.example.SearchEngineHibernate.services.Repositories.SearchIndexRepository;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class AroundSearching {
    LuceneMorphology luceneMorphology = new RussianLuceneMorphology();

    public ArrayList<SearchResult> finalSearchResult = new ArrayList<>();
    @Autowired
    private LemmaServices lemmaServicesItem;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SearchIndexRepository searchIndexRepository;

    private int limit = 50;
    private final Set<String> lemmas = new HashSet<>();
    HashMap<Integer, String[]> lemmaMap = new HashMap<>();

    public AroundSearching() throws IOException {
    }

    public ArrayList<SearchResult> searchSystem(String searchText) throws SQLException {
        ArrayList<Integer> pagesID = new ArrayList<>();

        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterableSite = siteRepository.findAll();
        iterableSite.forEach(sites::add);

        List<Lemma> lemmaArrayList = new ArrayList<>();
        Iterable<Lemma> iterableLemma = lemmaRepository.findAll();
        iterableLemma.forEach(lemmaArrayList::add);

        int totalFrequency = lemmaArrayList.stream().map(a -> a.getFrequency()).reduce(0, (x, y) -> x + y);
        int limitFrequency = totalFrequency * limit / 100;
        System.out.println(totalFrequency);
        System.out.println(limitFrequency);

        for (Site site : sites) {
            // ищем id леммы в только по выбранному сайту и формируем список лемм м карту лемм
            for (String word : lemmaServicesItem.textModify(searchText)) {
                List<Lemma> lemmaFilter = lemmaArrayList.stream().filter(lemma1 -> lemma1.getLemmaName().equals(word)).
                        filter(lemma1 -> lemma1.getSite().getUrl().equals(site.getUrl())).toList();

                int lemmaId = lemmaFilter.get(0).getId();
                String lemmaName = lemmaFilter.get(0).getLemmaName();
                System.out.println("имя леммы " + lemmaName);
                String frequency = String.valueOf(lemmaFilter.get(0).getFrequency());
                String[] lemmaInfoArray = {lemmaName, frequency};
                lemmaMap.put(lemmaId, lemmaInfoArray);
                lemmas.add(lemmaName);
            }


            if (lemmaMap.isEmpty() || lemmas.size() != lemmaServicesItem.textModify(searchText).size()) {
                SearchResult searchResult = new SearchResult();
                searchResult.setUrl(null);
                searchResult.setTitle(null);
                searchResult.setSnipper("Соответствие не найдено");
                searchResult.setRelevance(null);
                finalSearchResult.add(searchResult);
            } else {

                // фильтруем леммы по встречаемости
                Map<Integer, String[]> unSortedLemmasMap = lemmaMap.entrySet().stream()
                        .filter(n -> Integer.parseInt(n.getValue()[1]) < limitFrequency).
                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                // сортируем леммы по встречаемости

                Map<Integer, String[]> sortedLemmasMap = unSortedLemmasMap.entrySet().stream()
                        .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getValue()[1])))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> {
                                    throw new AssertionError();
                                },
                                LinkedHashMap::new));
                System.out.println("КартаЛемм- " + sortedLemmasMap);


                // по первой самой редкой лемме формируем список страниц из индекса

                int firstKey = sortedLemmasMap.entrySet().stream().findFirst().get().getKey();
                System.out.println("Самая редкая лемма --," + firstKey);


                List<SearchIndex> searchIndices = new ArrayList<>();
                Iterable<SearchIndex> searchIndexIterable = searchIndexRepository.findAll();
                searchIndexIterable.forEach(searchIndices::add);

                int pageID = searchIndices.stream().filter(a -> a.getLemma().getId() == firstKey).
                        map(a -> a.getPage().getId()).toList().get(0);

                pagesID.add(pageID);

                System.out.println("Список страниц - " + pagesID);

                // по списку лемм формируем список страниц и удаляем все которых нет в первом списке
                for (int lemmaId : sortedLemmasMap.keySet()) {

                    ArrayList<Integer> pagesIDcheck = new ArrayList<>();

                    pagesIDcheck = new ArrayList<>(searchIndices.stream().filter(a -> a.getLemma().getId() == lemmaId).map(a -> a.getPage().getId()).toList());


                    System.out.println("Перечень страниц " + pagesIDcheck);

                    Iterator<Integer> idIterator = pagesID.iterator();
                    while (idIterator.hasNext()) {
                        int nextId = idIterator.next();
                        if (!pagesIDcheck.contains(nextId)) {
                            idIterator.remove();
                        }
                    }
                    System.out.println("Перечень страниц " + pagesID);
                }


                for (int finalPageId : pagesID) {
                    float summRanks = 0;
                    String url = "";
                    String content = "";

                    url = pageRepository.findById(finalPageId).get().getPath();
                    content = pageRepository.findById(finalPageId).get().getContent();

                    String pageText = Jsoup.parse(content).text();

                    ArrayList<Integer> indexList = new ArrayList<>();
                    Set<String> lemmaReverseForms = new HashSet<>();

                    List<String> textSplit = List.of(content.replaceAll("[\\s<>]", " ").
                            replaceAll("[^А-Яа-я\\s+\\,\\.]", "").
                            replaceAll("\\+", "").replaceFirst("^[^А-Яа-я]+", "").
                            replaceAll("[<>]", ",").replaceAll("\\s+", ",").
                            replaceAll("\\.+", ",").replaceAll(",+", ",").split(","));

                    for (int finalLemmaId : sortedLemmasMap.keySet()) {

                        float rank = (searchIndices.stream().filter(a -> a.getLemma().getId() == finalLemmaId).
                                filter(x -> x.getPage().getId() == finalPageId)).toList().get(0).getRating();
                        summRanks += rank;
                    }


                    for (String finalLemmaName : lemmas) {
                        for (String lemmaReverseForm : textSplit) {
                            if (luceneMorphology.getNormalForms(lemmaReverseForm.toLowerCase()).
                                    get(0).equals(finalLemmaName)) {
                                lemmaReverseForms.add(lemmaReverseForm);
                                System.out.println("Слова для поиска -" + lemmaReverseForms);
                            }
                        }
                    }

                    for (String lemmaReverse : lemmaReverseForms) {
                        int indexStart = 0;
                        int indexFinish = 0;
                        Pattern pattern = Pattern.compile("[^а-яA-Я]{1}" + lemmaReverse + "[^а-яA-Я]{1}");
                        Matcher matcher = pattern.matcher(pageText);
                        while (matcher.find()) {
                            indexStart = matcher.start();
                            indexList.add(indexStart);
                            indexFinish = matcher.end();
                            indexList.add(indexFinish);
                            System.out.println("Слова -" + lemmaReverse + indexStart + indexFinish);
                        }
                    }
                    indexList.sort(Comparator.naturalOrder());
                    System.out.println("список индексов -" + indexList);

                    String snipper = new String();
                    int lag = 100;
                    for (int i = 0; i < indexList.size() - 1; i = i + 2) {

                        int start = indexList.get(i);
                        int limit = pageText.length();
                        int finish = ((indexList.get(i + 1) + lag) <= limit) ? indexList.get(i + 1) + lag : limit;

                        String partSnipper = pageText.substring(start, finish);
                        snipper = snipper + partSnipper;
                    }

                    for (String lemmaReverseOrigin : lemmaReverseForms) {
                        String lemmaReverseBold = "<b>" + lemmaReverseOrigin + "</b>";
                        snipper = snipper.replace(lemmaReverseOrigin, lemmaReverseBold);
                    }

                    Float absRelevance = summRanks;


                    String title = String.valueOf(Jsoup.parse(content).selectFirst("title"));

                    SearchResult searchResult = new SearchResult();
                    searchResult.setUrl(site.getUrl() + url);
                    System.out.println("Формируем объект поиска = " + url);
                    searchResult.setTitle(title);
                    System.out.println("Формируем объект поиска = " + title);
                    searchResult.setSnipper(snipper);
                    System.out.println("Формируем объект поиска = " + snipper);
                    searchResult.setRelevance(absRelevance);
                    finalSearchResult.add(searchResult);
                }
            }
        }
            return finalSearchResult;
    }
}





