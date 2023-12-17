package com.example.SearchEngineHibernate.services.DataBaseCreation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class SiteMapper extends RecursiveTask<Set<String>> {

    private SiteNode parentNode;
    private Set<SiteNode> allSiteNodes;
    private Set<String> allSiteUrl;
    SiteMapper(SiteNode parentNode) {
        this.parentNode = parentNode;
        allSiteUrl = new HashSet<>();
        allSiteNodes = new HashSet<>();
    }

    public SiteMapper() {
    }

    public SiteNode getParentNode() {
        return parentNode;
    }
    public Set<SiteNode> getAllSiteNodes() {
        return allSiteNodes;
    }
    public Set<String> getAllSiteUrl() {
        return allSiteUrl;
    }

    @Override
    protected Set<String> compute() {

        allSiteNodes.add(parentNode);
        allSiteUrl.add(parentNode.getNodeUrl());

        Set<SiteMapper> taskList = new HashSet<>();

        try {
            Document document = Jsoup.connect(parentNode.getNodeUrl()).userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com").ignoreHttpErrors(true).get();
            Elements links = document.select("a[href]");
            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                SiteNode node = new SiteNode(absUrl);
                if (node.getNodeUrl().isBlank()) {
                    continue;
                }
                if (!node.getNodeUrl().equals(parentNode.getNodeUrl()) && !node.getNodeUrl().contains("#")
                        && !node.getNodeUrl().contains("?")
                        && !node.getNodeUrl().contains("'")
                        && !node.getNodeUrl().contains("pdf")
                        && !node.getNodeUrl().contains("jpg")
                        && !node.getNodeUrl().contains("png")
                        && !node.getNodeUrl().contains("xlsx")
                        && !node.getNodeUrl().contains("%")
                        && !node.getNodeUrl().contains("gif")
                        && node.getNodeUrl().startsWith(parentNode.getNodeUrl())) {
                    parentNode.getHeirNodes().add(node);
                    node.setParentNode(parentNode);
                    allSiteNodes.add(node);
                    allSiteUrl.add(node.getNodeUrl());
                }
            }

            for (SiteNode node : parentNode.getHeirNodes()) {
                SiteMapper task = new SiteMapper(node);
                task.fork();
                taskList.add(task);
            }

            for (SiteMapper task : taskList) {
                task.join();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return allSiteUrl;
    }
}




