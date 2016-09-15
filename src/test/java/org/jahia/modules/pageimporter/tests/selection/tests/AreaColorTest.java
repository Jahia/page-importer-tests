package org.jahia.modules.pageimporter.tests.selection.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by sergey on 2016-09-15.
 */
public class AreaColorTest extends PageImporterRepository{
    private static final String AREA_BORDER_TYPE_KEY = "borderType";
    private static final String AREA_BORDER_COLOR_KEY = "borderColor";

    @Test
    public void colorSettingsTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaColorTest.colorSettingsTest");
        String projectName = randomWord(8);
        Area area = new Area(randomWord(5), "//body/div[1]", 1, 0, "", true, "jnt:bigText", "text");
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

    private void mouseOver(String xPathToElement){
        switchToProjectFrame();
        WebElement el = findByXpath(xPathToElement);
        new Actions(getDriver()).moveToElement(el).build().perform();
        switchToDefaultContent();
    }

    private Map<String, Map<String, String>> getBorderColors(Area[] areas){
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

        switchToProjectFrame();
        for(Area area:areas){
            WebElement selection = findByXpath(area.getXpath());
            Map<String, String> borderTypeAndColor = new HashMap<String, String>();

            if(getBrowser().equals(CHROME)) {
                String[] borderCssAttributes = selection.getCssValue("border").split(" ", 3);
                borderTypeAndColor.put(AREA_BORDER_TYPE_KEY, borderCssAttributes[1]);
                borderTypeAndColor.put(AREA_BORDER_COLOR_KEY, borderCssAttributes[2]);

            }else{
                borderTypeAndColor.put(AREA_BORDER_TYPE_KEY, selection.getCssValue("border-bottom-style"));
                borderTypeAndColor.put(AREA_BORDER_COLOR_KEY, selection.getCssValue("border-bottom-color"));
            }
            map.put(area.getName(), borderTypeAndColor);
        }
        switchToDefaultContent();

        return map;
    }

    private Map<String, String> changeColors(Area area){
        Map<String, String> newColors = new HashMap<String, String>();
        String newColor = generateRGB();

        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='pc.setUpColors($event)']");
        clickOn(adjustColorsBtn);
        WebElement applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        waitForElementToStopMoving(applyBtn);


        WebElement changerBtn = findByXpath("//md-dialog-content//div[contains(., 'Area')]/span[@ng-model='type.color']");
        WebElement colorInput = findByXpath("//input[@class='sp-input']");
        WebElement chooseBtn = findByXpath("//button[@class='sp-choose']");


        clickOn(changerBtn);

            for (int i = 0; i < 20; i++) {
                colorInput.sendKeys(Keys.BACK_SPACE);
            }
            colorInput.sendKeys(newColor);
            colorInput.sendKeys(Keys.ENTER);

        waitForElementToBeEnabled(chooseBtn, 3);
        clickOn(chooseBtn);
        waitForElementToBeInvisible(chooseBtn);
        newColors.put(area.getName(), newColor);


        applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();

        return newColors;
    }

    private void resetColors(){
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='pc.setUpColors($event)']");
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

    private String generateRGB(){
        int r = randInt(0, 255);
        int g = randInt(0, 255);
        int b = randInt(0, 255);
        String opacity = "0."+randInt(1, 9);

        if(getBrowser().equals(CHROME)){
            return "rgb("+r+", "+g+", "+b+")";
        }else{
            return "rgba("+r+", "+g+", "+b+", "+opacity+")";
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
