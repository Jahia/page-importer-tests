package org.jahia.modules.pageimporter.tests.export.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by sergey on 2016-09-14.
 */
public class ExportTest extends PageImporterRepository{
    @Test
    public void pageExportTest(){
        String projectName = randomWord(8);
        String newPageName = randomWord(9);
        String parentPageName = "Home";
        Area area = new Area(randomWord(5), "//body/div[1]", 1, 0, true, "jnt:bigText", "text");
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 1, 0, false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        selectArea(areaTwo);
        exportSeletions(newPageName, parentPageName);
        checkIfNewPageCreated(newPageName, true, new String[]{parentPageName});
    }

    @Test
    public void exportSeveralPagesTest(){
        String projectName = randomWord(8);
        String newPageName = randomWord(9);
        String anotherNewPageName = randomWord(10);
        String parentPageName = "Home";
        Area area = new Area(randomWord(5), "//body/div[1]", 1, 0, true, "jnt:bigText", "text");
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 1, 0, false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        selectArea(areaTwo);
        exportSeletions(newPageName, parentPageName);
        exportSelectionsUnderSecondLevel(anotherNewPageName, newPageName);
        checkIfNewPageCreated(newPageName, true, new String[]{parentPageName});
        checkIfNewPageCreated(anotherNewPageName, true, new String[]{parentPageName, newPageName});
    }

    protected void checkIfNewPageCreated(String     pageNameToLookFor,
                                         boolean    expectedResult,
                                         String[]   ancestorNames){
        String urlBase = getBaseURL()+"/cms/render/default/en/sites/"+getSiteName()+"/";
        for (String ancestor:ancestorNames){
            urlBase = urlBase+ancestor.toLowerCase()+"/";
        }
        String urlToPageToCheck = urlBase+pageNameToLookFor+".content-template.html";

        getDriver().get(urlToPageToCheck);
        Assert.assertNotEquals(
                isVisible(By.xpath("//h1[contains(., 'not found')]"), 3),
                expectedResult,
                pageNameToLookFor+" not found. 404 page is shown instead."
        );
    }

    protected void exportSelectionsUnderSecondLevel(String   newPAgeName,
                                                   String   parentLevelOnePageName){
        String xPathToSuccessfulToast = "//div[contains(@class, 'toast-title')][contains(., 'Created new page!')]";
        WebElement menuBtn = findByXpath("//button[@aria-label='Project']");
        clickOn(menuBtn);
        WebElement exportBtn = findByXpath("//button[@ng-click='pc.copySelections()']");
        waitForElementToStopMoving(exportBtn);
        clickOn(exportBtn);
        WebElement continueBtn = findByXpath("//button[@ng-click=\"awc.choice('generate')\"]");
        waitForElementToStopMoving(continueBtn);
        clickOn(continueBtn);
        WebElement newPageNameField = findByName("newPageName");
        WebElement exportAreaSelectionsBtn = findByXpath("//button[@ng-click='csc.copySelections()']");
        WebElement grandParentExpander = findByXpath("//i[@ng-click='npc.toggle(item)' and ../span[@ng-click='npc.select(item)' and contains(., 'Home')]]");

        waitForElementToStopMoving(newPageNameField);
        typeInto(newPageNameField, newPAgeName);
        clickOn(grandParentExpander);
        WebElement parentPage = findByXpath("//span[@ng-click='npc.select(item)' and contains(., '"+parentLevelOnePageName+"')]");
        clickOn(parentPage);
        Assert.assertTrue(
                parentPage.getAttribute("class").contains("selected"),
                "Parent page with name "+parentLevelOnePageName+" is not selected after click."
        );
        waitForElementToBeEnabled(exportAreaSelectionsBtn, 3);
        clickOn(exportAreaSelectionsBtn);
        Long start = new Date().getTime();
        waitForGlobalSpinner(2, 50);
        Long finish = new Date().getTime();
        waitForElementToBeInvisible(exportAreaSelectionsBtn);
        Assert.assertTrue(
                isVisible(By.xpath(xPathToSuccessfulToast), 2),
                "Success toast not found after selections export. " +
                        "Global spinner was visible for at least "+(finish-start)+" milliseconds. (Maximum is 50000)");
        clickOn(By.xpath(xPathToSuccessfulToast));

    }
    /**
     * Exports selections to top - level page.
     * @param newPAgeName Name of the new page
     * @param parentPageName Top - level page name, that will be selected as parent for new page.
     */
    protected void exportSeletions(String   newPAgeName,
                                   String   parentPageName){
        String xPathToSuccessfulToast = "//div[contains(@class, 'toast-title')][contains(., 'Created new page!')]";
        WebElement menuBtn = findByXpath("//button[@aria-label='Project']");
        clickOn(menuBtn);
        WebElement exportBtn = findByXpath("//button[@ng-click='pc.copySelections()']");
        waitForGlobalSpinner(1, 30);
        waitForElementToStopMoving(exportBtn);
        clickOn(exportBtn);
        WebElement continueBtn = findByXpath("//button[@ng-click=\"awc.choice('generate')\"]");
        waitForElementToStopMoving(continueBtn);
        clickOn(continueBtn);
        WebElement newPageNameField = findByName("newPageName");
        WebElement exportAreaSelectionsBtn = findByXpath("//button[@ng-click='csc.copySelections()']");
        WebElement parentPage = findByXpath("//span[@ng-click='npc.select(item)' and contains(., '"+parentPageName+"')]");

        waitForElementToStopMoving(newPageNameField);
        typeInto(newPageNameField, newPAgeName);
        clickOn(parentPage);
        Assert.assertTrue(
                parentPage.getAttribute("class").contains("selected"),
                "Parent page with name "+parentPageName+" is not selected after click."
        );
        waitForElementToBeEnabled(exportAreaSelectionsBtn, 3);
        clickOn(exportAreaSelectionsBtn);
        Long start = new Date().getTime();
        waitForGlobalSpinner(2, 50);
        Long finish = new Date().getTime();
        waitForElementToBeInvisible(exportAreaSelectionsBtn);
        Assert.assertTrue(
                isVisible(By.xpath(xPathToSuccessfulToast), 2),
                "Success toast not found after selections export. " +
                        "Global spinner was visible for at least "+(finish-start)+" milliseconds. (Maximum is 50000)");
        clickOn(By.xpath(xPathToSuccessfulToast));
    }
}
