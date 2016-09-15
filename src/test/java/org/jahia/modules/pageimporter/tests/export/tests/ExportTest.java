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
        Area area = new Area(randomWord(5), "//body/div[1]", 1, 0, "", true, "jnt:bigText", "text");
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 1, 0, "", false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        selectArea(areaTwo);
        exportSeletions(newPageName, parentPageName);
        checkIfNewPageCreated(newPageName, true);
    }

    @Test
    public void exportSeveralPagesTest(){
        String projectName = randomWord(8);
        String newPageName = randomWord(9);
        String anotherNewPageName = randomWord(10);
        String parentPageName = "Home";
        Area area = new Area(randomWord(5), "//body/div[1]", 1, 0, "", true, "jnt:bigText", "text");
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 1, 0, "", false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        selectArea(areaTwo);
        exportSeletions(newPageName, parentPageName);
        exportSeletions(anotherNewPageName, newPageName);
        checkIfNewPageCreated(newPageName, true);
        checkIfNewPageCreated(anotherNewPageName, true);
    }

    protected void checkIfNewPageCreated(String     pageNameToLookFor,
                                         boolean    expectedResult){
        WebElement exportBtn = findByXpath("//button[@ng-click='pc.copySelections()']");
        clickOn(exportBtn);
        WebElement parentpageField = findByName("path");
        WebElement closeBtn = findByXpath("//button[@ng-click='csc.cancel()']");

        waitForElementToStopMoving(parentpageField);
        typeIntoSlowly(parentpageField, pageNameToLookFor);
        Assert.assertEquals(
                isVisible(By.xpath("//div[@ng-click='tc.select(item)' and normalize-space(text())='"+pageNameToLookFor+"']"), 3),
                expectedResult,
                pageNameToLookFor+" - Page is not visible in the parent page selector after export."
        );
        clickOn(closeBtn);
        waitForElementToBeInvisible(closeBtn);
    }

    protected void exportSeletions(String   newPAgeName,
                                   String   parentPageName){
        String xPathToSuccessfulToast = "//div[contains(@class, 'toast-title')][contains(., 'Export successful!')]";
        WebElement exportBtn = findByXpath("//button[@ng-click='pc.copySelections()']");
        clickOn(exportBtn);
        WebElement newPageNameField = findByName("newPageName");
        WebElement parentpageField = findByName("path");
        WebElement exportAreaSelectionsBtn = findByXpath("//button[@ng-click='csc.copySelections()']");

        waitForElementToStopMoving(newPageNameField);
        typeInto(newPageNameField, newPAgeName);
        typeIntoSlowly(parentpageField, parentPageName);
        WebElement parentPageOption = findByXpath("//div[@ng-click='tc.select(item)' and normalize-space(text())='"+parentPageName+"']");
        waitForElementToStopMoving(parentPageOption);
        clickOn(parentPageOption);
        waitForElementToBeInvisible(parentPageOption);
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
