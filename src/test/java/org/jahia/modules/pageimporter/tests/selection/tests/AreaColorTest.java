package org.jahia.modules.pageimporter.tests.selection.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Map;

/**
 * Created by sergey on 2016-09-15.
 */
public class AreaColorTest extends PageImporterRepository {

    @Test
    public void colorSettingsTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaColorTest.colorSettingsTest");
        String projectName = randomWord(8);
        Area area = new Area(randomWord(5), "//body/div[1]", 2, 0, true, "jnt:bigText", "text");
        String xPathToHover = "/html/body/div[4]";
        Map<String, Map<String, String>> originalBorderColors;
        Map<String, String> expectedNewBorderColors;
        Map<String, Map<String, String>> actualNewBorderColors;
        Map<String, Map<String, String>> resetedBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);

        Area[] allAreas = new Area[]{area};
        mouseOver(xPathToHover);
        originalBorderColors = getBorderColors(allAreas);
        mouseOver(xPathToHover);
        expectedNewBorderColors = changeColors(allAreas[0]);
        mouseOver(xPathToHover);
        actualNewBorderColors = getBorderColors(allAreas);
        resetColors();
        mouseOver(xPathToHover);
        resetedBorderColors = getBorderColors(allAreas);

        String areaName = area.getName();
        String expectedBorderType = "solid";
        String actualBorderType = actualNewBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
        String expectedNewBorderColor = expectedNewBorderColors.get(areaName);
        String actualNewBorderColor = actualNewBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
        String expectedBorderTypeAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
        String actualBorderTypeAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
        String expectedBorderColorAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
        String actualBorderColorAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);

        softAssert.assertEquals(
                actualBorderType,
                expectedBorderType,
                "Unexpected border type for selection '" + areaName + "' (" + area.getXpath() + ") after color change."
        );
        softAssert.assertEquals(
                actualNewBorderColor,
                expectedNewBorderColor,
                "Unexpected border color for selection '" + areaName + "' (" + area.getXpath() + ") after color change."
        );
        softAssert.assertEquals(
                actualBorderTypeAfterReset,
                expectedBorderTypeAfterReset,
                "Unexpected border type for selection '" + areaName + "' (" + area.getXpath() + ") after color reset."
        );
        softAssert.assertEquals(
                actualBorderColorAfterReset,
                expectedBorderColorAfterReset,
                "Unexpected border color for selection '" + areaName + "' (" + area.getXpath() + ") after color reset."
        );

        softAssert.assertAll();
    }

    @Test
    public void areaVisibilityTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaColorTest.areaVisibilityTest");
        String projectName = randomWord(8);
        Area area = new Area(randomWord(5), "//body/div[1]", 2, 0, true, "jnt:bigText", "text");
        String xPathToHover = "/html/body/div[4]";
        Map<String, Map<String, String>> originalBorderColors;
        Map<String, Map<String, String>> actualNewBorderColors;
        Map<String, Map<String, String>> resetedBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);

        Area[] allAreas = new Area[]{area};
        mouseOver(xPathToHover);
        originalBorderColors = getBorderColors(allAreas);
        turnOffVisibility();
        mouseOver(xPathToHover);
        actualNewBorderColors = getBorderColors(allAreas);
        resetColors();
        mouseOver(xPathToHover);
        resetedBorderColors = getBorderColors(allAreas);

        String areaName = area.getName();
        String expectedBorderType = "none";
        String actualBorderType = actualNewBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
        String expectedBorderTypeAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
        String actualBorderTypeAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
        String expectedBorderColorAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
        String actualBorderColorAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);

        softAssert.assertEquals(
                actualBorderType,
                expectedBorderType,
                "Unexpected border type for selection '" + areaName + "' (" + area.getXpath() + ") after visibility change."
        );
        softAssert.assertEquals(
                actualBorderTypeAfterReset,
                expectedBorderTypeAfterReset,
                "Unexpected border type for selection '" + areaName + "' (" + area.getXpath() + ") after visibility reset."
        );
        softAssert.assertEquals(
                actualBorderColorAfterReset,
                expectedBorderColorAfterReset,
                "Unexpected border color for selection '" + areaName + "' (" + area.getXpath() + ") after visibility reset."
        );

        softAssert.assertAll();
    }

    private void resetColors() {
        WebElement menuBtn = findByXpath("//button[@aria-label='Settings']");
        clickOn(menuBtn);
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='pc.setUpColors($event)']");
        waitForElementToStopMoving(adjustColorsBtn);
        clickOn(adjustColorsBtn);
        WebElement resetBtn = findByXpath("//button[@ng-click='sdoc.reset()']");
        waitForElementToStopMoving(resetBtn);
        clickOn(resetBtn);
        waitForElementToBeInvisible(resetBtn);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }

    private void turnOffVisibility() {
        WebElement menuBtn = findByXpath("//button[@aria-label='Settings']");
        clickOn(menuBtn);
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='pc.setUpColors($event)']");
        waitForElementToStopMoving(adjustColorsBtn);
        clickOn(adjustColorsBtn);
        WebElement applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        waitForElementToStopMoving(applyBtn);
        List<WebElement> toggles = findElementsByXpath("//md-dialog-content//md-switch");

        for (WebElement toggle : toggles) {
            clickOn(toggle);
            Assert.assertTrue(toggle.getAttribute("aria-checked").equals("false"));
        }
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }
}
