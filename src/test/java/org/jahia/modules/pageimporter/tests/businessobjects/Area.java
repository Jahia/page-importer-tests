package org.jahia.modules.pageimporter.tests.businessobjects;

/**
 * Created by sergey on 2016-09-12.
 */
public class Area {
    private String name;
    private String xPath;
    private int xOffset;
    private int yOffset;
    private boolean assignHtml;
    private String nodeType;
    private String propertyType;

    public Area(String name, String xPath, int xOffset, int yOffset, boolean assignHtml, String nodeType, String propertyType){
        this.name = name;
        this.xPath = xPath;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.assignHtml = assignHtml;
        this.nodeType = nodeType;
        this.propertyType = propertyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXpath() {
        return xPath;
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public boolean isHtmlAssigned() {
        return assignHtml;
    }

    public void setAssignHtml(boolean assignHtml) {
        this.assignHtml = assignHtml;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
