package org.jahia.modules.pageimporter.tests.selection.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

/**
 * Created by sergey on 2016-09-15.
 */
public class ProjectSavingTest extends PageImporterRepository {
    @Test
    public void saveProjectTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "ProjectSavingTest.saveProjectTest");
        String projectName = randomWord(8);
        Area area = new Area(randomWord(5), "//body/div[1]", 1, 0, false, "", "");
        String xPathToHover = "/html/body/div[4]";
        Map<String, String> expectedBorderColors;
        Map<String, Map<String, String>> actualBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        mouseOver(xPathToHover);
        expectedBorderColors = changeColors(area);
        saveProject();
        goBackToProjects();
        openProject(projectName);
        checkIfAreaSelected(area.getXpath(), softAssert, true, "After saving project and opening it back");
        mouseOver(xPathToHover);
        actualBorderColors = getBorderColors(new Area[]{area});

        String areaName = area.getName();
        String expectedBorderType = "solid";
        String actualBorderType = actualBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
        String expectedNewBorderColor = expectedBorderColors.get(areaName);
        String actualNewBorderColor = actualBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);

        softAssert.assertEquals(
                actualBorderType,
                expectedBorderType,
                "Unexpected border type for selection '" + areaName + "' (" + area.getXpath() + ") after saving project and opening it back."
        );
        softAssert.assertEquals(
                actualNewBorderColor,
                expectedNewBorderColor,
                "Unexpected border color for selection '" + areaName + "' (" + area.getXpath() + ") after saving project and opening it back."
        );

        softAssert.assertAll();
    }

    private void goBackToProjects(){
        WebElement goBackBtn = findByXpath("//button[@ng-click='pc.backToList()']");
        clickOn(goBackBtn);
        waitForGlobalSpinner(2, 30);
    }

    private void saveProject(){
        WebElement menuBtn = findByXpath("//button[@aria-label='Project']");
        clickOn(menuBtn);
        WebElement saveBtn = findByXpath("//button[@ng-click='pc.saveProject($event)']");
        waitForElementToStopMoving(saveBtn);
        clickOn(saveBtn);
        WebElement successToast = findByXpath("//div[contains(@class, 'toast-title')][contains(., 'Project Saved!')]");
        Assert.assertNotNull(
                successToast,
                "Toast that project saved not found after clicking 'Save project.'"
        );
        Assert.assertTrue(
                isVisible(successToast, 3),
                "Toast that project saved is not visible after clicking 'Save project.'"
        );
        waitForElementToStopMoving(successToast);
        clickOn(successToast);
        waitForElementToBeInvisible(successToast);
    }
}
