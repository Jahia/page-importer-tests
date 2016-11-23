package org.jahia.modules.pageimporter.tests.selection.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sergey on 2016-09-22.
 */

/**
 * This test does not work in Chrome due to chromedriver bug. It thrown WebDriverException after opening the project
 */
public class SelectorConfigurationTest extends PageImporterRepository {
    private static final String SELECTABLE_HOVER_MARK = "DomOutlined";
    @Test
    public void selectAllTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectorConfigurationTest.selectAllTest");
        String projectName = randomWord(8);
        LinkedList<String> selectableByDefailt = createLinkedList(
                "/html/body/div",
                "/html/body/nav",
                "/html/body/header",
                "/html/body/summary",
                "/html/body/footer",
                "/html/body/section",
                "/html/body/article",
                "/html/body/aside",
                "/html/body/details"
        );
        LinkedList<String> selectableAfterEnablingAll = createLinkedList(
                "/html/body/p",
                "/html/body/a",
                "/html/body/div",
                "/html/body/nav",
                "/html/body/header",
                "/html/body/summary",
                "/html/body/footer",
                "/html/body/section",
                "/html/body/article",
                "/html/body/aside",
                "/html/body/details",
                "/html/body/button",
                "/html/body/h1",
                "/html/body/h2",
                "/html/body/h3",
                "/html/body/h4",
                "/html/body/h5",
                "/html/body/h6",
                "/html/body/ul",
                "/html/body/ol",
                "/html/body/dl",
                "/html/body/table",
                "/html/body/form",
                "/html/body/img"
        );
        LinkedList<String> notSelectableByDefault = new LinkedList<String>();
        //Initializing list of non selectables with values
        for(String all: selectableAfterEnablingAll){
            boolean isSelectable = false;
            for(String selectable:selectableByDefailt){
                if(all.equals(selectable)){
                    isSelectable = true;
                    break;
                }
            }
            if(!isSelectable){
                notSelectableByDefault.add(all);
            }
        }

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "configure-selector.html");
        hoverTest(selectableByDefailt, true, softAssert, "Initial hover test");
        hoverTest(notSelectableByDefault, false, softAssert, "Initial hover test");
        turnOnSelectabilityForAll();
        hoverTest(selectableAfterEnablingAll, true, softAssert, "Hover test after turning all ON");
        selectTest(selectableAfterEnablingAll, softAssert, "Selectability test after turning all ON");
        resetSelectability();
        hoverTest(selectableByDefailt, true, softAssert, "Hover test for default selectable elements after resetting settings");
        hoverTest(notSelectableByDefault, false, softAssert, "Hover test for non selectable by default after resetting settings");
        softAssert.assertAll();
    }

    private void resetSelectability(){
        WebElement menuBtn = findByXpath("//i[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement configureSelectorBtn = findByXpath("//button[@ng-click='pc.configureSelector($event)']");
        waitForElementToStopMoving(configureSelectorBtn);

        clickOn(configureSelectorBtn);
        WebElement resetBtn = findByXpath("//button[@ng-click='soc.reset()']");
        WebElement applyBtn = findByXpath("//button[@ng-click='soc.apply()']");

        waitForElementToStopMoving(resetBtn);
        clickOn(resetBtn);
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
    }

    private void turnOnSelectabilityForAll(){
        WebElement menuBtn = findByXpath("//i[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement configureSelectorBtn = findByXpath("//button[@ng-click='pc.configureSelector($event)']");
        waitForElementToStopMoving(configureSelectorBtn);
        clickOn(configureSelectorBtn);
        WebElement applyBtn = findByXpath("//button[@ng-click='soc.apply()']");
        waitForElementToStopMoving(applyBtn);

        List<WebElement> categories = findElementsByXpath("//md-checkbox[@ng-click='soc.selectCategory(category)']");
        for(WebElement checkbox:categories){
            if(checkbox.getAttribute("aria-checked").contains("false")){
                clickOn(checkbox);
            }
            Assert.assertTrue(
                    checkbox.getAttribute("aria-checked").contains("true"),
                    "Turning selectability of all elements On - failed. One or more caregory is not activated."
            );
        }
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
    }

    private void selectTest(Iterable<String>    xPathToSelect,
                            SoftAssert          softAssert,
                            String              errorMsg){
        for(String xPath:xPathToSelect){
            selectArea(new Area(randomWord(10), xPath, 0, 0, false, "", ""));
            checkIfAreaSelected(xPath, softAssert, true, errorMsg);
        }
        clearSelections();
    }

    private void hoverTest(Iterable<String>     xPathToHover,
                           boolean              expectedResult,
                           SoftAssert           softAssert,
                           String               errorMsg){
        String not;
        String has;

        if(expectedResult){
            not = "not ";
            has = "Does not have";
        }else{
            not = "";
            has = "Has";
        }
        switchToProjectFrame();
        for(String xPath:xPathToHover) {
            boolean actualResult;
            WebElement el = findByXpath(xPath);
            new Actions(getDriver()).moveToElement(el).build().perform();
            actualResult = el.getAttribute("class").contains(SELECTABLE_HOVER_MARK);
            softAssert.assertEquals(
                    actualResult,
                    expectedResult,
                    errorMsg+". "+xPath+" Element is "+not+"selectable. ("+has+" '"+SELECTABLE_HOVER_MARK+"' class after hovering."
            );
        }
        switchToDefaultContent();
    }

    static <T> LinkedList<T> createLinkedList(T...elements) {
        LinkedList<T> newList = new LinkedList<T>();
        for (T el : elements) {
            newList.add(el);
        }

        return newList;
    }
}
