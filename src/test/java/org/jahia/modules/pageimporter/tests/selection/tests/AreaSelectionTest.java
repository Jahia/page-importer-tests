package org.jahia.modules.pageimporter.tests.selection.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
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
        Area area = new Area(randomWord(5), "//body/div[1]", 1, 0, "", true, "jnt:bigText", "text");
        Area areaTwo = new Area(randomWord(5), "//body/div[2]", 1, 0, "", false, "", "");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(area);
        selectArea(areaTwo);
        checkIfAreaSelected(area.getXpath(), softAssert, true, "");
        checkIfAreaSelected(areaTwo.getXpath(), softAssert, true, "");

        softAssert.assertAll();
    }
}
