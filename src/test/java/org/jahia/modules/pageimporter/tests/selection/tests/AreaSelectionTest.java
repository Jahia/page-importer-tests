package org.jahia.modules.pageimporter.tests.selection.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-07-06.
 */
public class AreaSelectionTest extends PageImporterRepository {

    @Test
    public void selectAreaTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectAreaTest");
        String projectName = randomWord(8);
        Area area = new Area(randomWord(5), "//body/div[1]", 2, 0, "", true, "jnt:bigText", "text");
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 2, 0, "", false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        selectArea(areaTwo);
        checkIfAreaSelected(area.getXpath(), softAssert, true, "");
        checkIfAreaSelected(areaTwo.getXpath(), softAssert, true, "");

        softAssert.assertAll();
    }

    @Test
    public void inheritAreaTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.inheritAreaTest");
        String projectName = randomWord(8);
        Area area = new Area(randomWord(5), "//body/div[1]", 2, 0, "pagecontent-side2", true, "jnt:bigText", "text");
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 2, 0, "pagecontent", false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        selectArea(areaTwo);
        checkIfAreaSelected(area.getXpath(), softAssert, true, "");
        checkIfAreaSelected(areaTwo.getXpath(), softAssert, true, "");

        softAssert.assertAll();
    }

    @Test
    public void clearSelectionsTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.clearSelectionsTest");
        String projectName = randomWord(8);
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 2, 0, "", false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaTwo);
        clearSelections();
        checkIfAreaSelected(areaTwo.getXpath(), softAssert, false, "Clear selections was clicked.");

        softAssert.assertAll();
    }

    @Test
    public void selectionRemovalTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectionRemovalTest");
        String projectName = randomWord(8);
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 2, 0, "", false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaTwo);
        removeArea(areaTwo, "Removing area");

        softAssert.assertAll();
    }

    protected void removeArea(Area area, String errorMsg){
        boolean isSelected = checkIfAreaSelected(area.getXpath(), new SoftAssert(), true, "");

        Assert.assertTrue(isSelected, errorMsg+". Area that you are trying to remove is not selected.");
        rightMouseClick(area.getXpath(), area.getxOffset(), area.getyOffset());
        WebElement menuAreaBtn = findByXpath("//div[@ng-click='rmc.removable && rmc.remove()']");
        waitForElementToStopMoving(menuAreaBtn);
        clickOn(menuAreaBtn);
        waitForElementToBeInvisible(menuAreaBtn);
        Assert.assertFalse(
                checkIfAreaSelected(area.getXpath()),
                errorMsg+". Area was not removed. XPath:'"+area.getXpath()+"'. Horizontal offset:"+area.getxOffset()+", Vertical offset:"+area.getyOffset());
    }
}
