package com.example.SearchEngineHibernate.services.DataBaseCreation;

import java.util.HashSet;
import java.util.Set;

public class SiteNode {

    private String nodeUrl;
    private volatile SiteNode parentNode;
    private Set<SiteNode> heirNodes;

    public SiteNode(String url) {
        this.nodeUrl = url;
        parentNode = null;
        heirNodes = new HashSet<>();
    }
    public SiteNode() {
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public SiteNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(SiteNode parentNode) {
        synchronized (this) {
            this.parentNode = parentNode;
        }
    }

    public Set<SiteNode> getHeirNodes() {
        return heirNodes;
    }

    public void setHeirNodes(Set<SiteNode> heirNodes) {
        this.heirNodes = heirNodes;
    }


    public synchronized void addHeirNodes(SiteNode subLink) {
        this.heirNodes.add(subLink);
    }
}
