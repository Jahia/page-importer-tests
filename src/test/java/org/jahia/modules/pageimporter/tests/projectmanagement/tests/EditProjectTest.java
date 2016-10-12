package org.jahia.modules.pageimporter.tests.projectmanagement.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.tests.utils.CustomExpectedConditions;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.Date;

/**
 * Created by sergey on 2016-07-04.
 */

public class EditProjectTest extends PageImporterRepository {
    /**
     * Tests for editing the project details
     * @param isValidName boolean, true if passing a valid project name
     * @param isValidDescription boolean, true if passing a valid project description
     * @param modifiedName String, new project name
     * @param modifiedDescription String, new project description
     */
    @Test(dataProvider = "projectDetails")
    public void editProjectDescription(boolean isValidName, boolean isValidDescription, String modifiedName, String modifiedDescription) {
        String originalName = randomWord(9);
        String originalDescription = randomWord(15);
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "EditProjectTest.editProjectDescription");

        importProject("en", originalName, originalDescription, "AlexLevels.zip");
        WebElement editProjectBtn = findByXpath("//md-card-title-text[contains(., '" + originalName + "')]/ancestor::md-card//button[@ng-click='projects.editProject($event, p, $index)']");
        clickOn(editProjectBtn);

        WebElement projectNameField = findByXpath("//input[@name='projectName']");
        WebElement projectDescriptionField = findByXpath("//textarea[@ng-model='project.description']");

        typeInto(projectNameField, modifiedName);
        typeInto(projectDescriptionField, modifiedDescription);
        boolean doesUIShowNameError = projectNameField.getAttribute("class").contains("ng-invalid-");
        boolean doesUIShowDescriptionError = projectDescriptionField.getAttribute("class").contains("ng-invalid-");
        WebElement saveChangesBtn = findByXpath("//button[@ng-click='edit()']");

        if (isValidName) {
            if (isValidDescription) {
                //Project name and description are valid here
                Assert.assertEquals(
                        saveChangesBtn.isEnabled(),
                        true,
                        "Renaming failed. 'Save Changes' - button is disabled with valid name: " + modifiedName);
                softAssert.assertNotEquals(doesUIShowNameError, isValidName, "Project name is valid, but an error message is shown.");
                softAssert.assertNotEquals(doesUIShowDescriptionError, isValidDescription, "Project description is valid, but an error message is shown. Description is " +
                        modifiedDescription.length() + " characters long.");
                clickOn(saveChangesBtn);
                waitForElementToBeInvisible(saveChangesBtn, 7);
                softAssert.assertEquals(
                        isVisible(By.xpath("//md-card-title-text/span[contains(., '" + modifiedName + "')]"), 7),
                        true,
                        "New project name is not visible after editing project. New name.: " + modifiedName + " Old name.:" + originalName +
                                ". New Desc.:"+modifiedDescription+". Old Desc.:"+originalDescription);
                softAssert.assertEquals(
                        isVisible(By.xpath("//md-card-title-text/span[contains(., '" + originalName + "')]"), 1),
                        false,
                        "Old project name is still visible after editing project. New name.: " + modifiedName + " Old name.:" + originalName +
                                ". New Desc.:"+modifiedDescription+". Old Desc.:"+originalDescription);
                if (!modifiedDescription.equals("")) {
                    //Special case, descriptions is empty string
                    softAssert.assertEquals(
                            isVisible(By.xpath("//*[@ng-if='p.description'][contains(., '" + modifiedDescription + "')]"), 7),
                            true,
                            "New project descriptions is not visible after editing project description.  New name.: " + modifiedName + " Old name.:" + originalName +
                                    ". New Desc.:"+modifiedDescription+". Old Desc.:"+originalDescription);
                }
                softAssert.assertEquals(
                        isVisible(By.xpath("//*[@ng-if='p.description'][contains(., '" + originalDescription + "')]"), 1),
                        false,
                        "Old project descriptions is still visible after editing project description.  New name.: " + modifiedName + " Old name.:" + originalName +
                                ". New Desc.:"+modifiedDescription+". Old Desc.:"+originalDescription);
            } else {
                //Project name is valid, but description is not.
                softAssert.assertEquals(
                        saveChangesBtn.isEnabled(),
                        false,
                        "Renaming failed. 'Save Changes' - button is enabled with invalid description: Description length:" + modifiedDescription.length());
                softAssert.assertNotEquals(doesUIShowDescriptionError, isValidDescription, "Project description is not valid, but an error message is not shown. Description is " +
                        modifiedDescription.length() + " characters long.");
            }
        } else {
            //Project name is not valid
            softAssert.assertEquals(
                    saveChangesBtn.isEnabled(),
                    false,
                    "Project name validation failed. 'Save changes' - button is enabled with invalid project name: " + modifiedName);
            softAssert.assertNotEquals(doesUIShowNameError, isValidName, "Project name is not valid, but an error message is not shown.");
            softAssert.assertNotEquals(doesUIShowDescriptionError, isValidDescription, "Project description is not valid, but an error message is not shown. Description is " +
                    modifiedDescription.length() + " characters long.");
        }

        softAssert.assertAll();
    }

    @Test
    public void editAndCancel(){
        String originalName = randomWord(9);
        String originalDescription = randomWord(15);
        String modifiedName = randomWord(7);
        String modifiedDescription = randomWord(18);
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "EditProjectTest.editAndCancel");

        importProject("en", originalName, originalDescription, "AlexLevels.zip");
        WebElement editProjectBtn = findByXpath("//md-card-title-text[contains(., '" + originalName + "')]/ancestor::md-card//button[@ng-click='projects.editProject($event, p, $index)']");
        clickOn(editProjectBtn);

        WebElement projectNameField = findByXpath("//input[@name='projectName']");
        WebElement projectDescriptionField = findByXpath("//textarea[@ng-model='project.description']");
        WebElement cancelButton = findByXpath("//button[@ng-click='cancel()']");

        typeInto(projectNameField, modifiedName);
        typeInto(projectDescriptionField, modifiedDescription);
        clickOn(cancelButton);
        waitForElementToBeInvisible(cancelButton, 7);

        softAssert.assertEquals(
                isVisible(By.xpath("//md-card-title-text/span[contains(., '" + originalName + "')]"), 7),
                true,
                "Cannot find project with original name after cancelling renaming. New name.: " + originalName + " Old name.:" + modifiedName);
        softAssert.assertEquals(
                isVisible(By.xpath("//md-card-title-text/span[contains(., '" + modifiedName + "')]"), 1),
                false,
                "Project with modified name is found after cancelling renaming. New name.: " + originalName + " Old name.:" + modifiedName);
        softAssert.assertAll();
    }


    @DataProvider(name = "projectDetails")
    public Object[][] createProjectDetails() {
        return new Object[][]{
                {true, true, "Name" + randomWord(3), "Description"}, // Case TI_S1C5
                {true, true, "400", "400"}, //From here and further: Case  TI_S1C12, Bug https://jira.jahia.org/browse/MOD-881
                {true, true, "Q400", randomWord(300)},
                {true, true, "400" + randomWord(3), "Digit before letter"},
                {true, true, "L1234567890" + randomWord(3), "Digits between letters"},
                {true, true, "Name_Underscore" + randomWord(3), "Name with underscore"},
                {true, true, "Name With space" + randomWord(3), "Description"},
                {true, true, "Error 404 page" + randomWord(3), "Spaces and digits"},
                {true, true, "All_in-1 name ЫЫЫ" + randomWord(3), "Name with all allowed characters"},
                {true, true, "334a_-_c--cc__v", "Mix of allowed characters"},
                {true, false, "Magic", randomWord(300) + "22"},

                {false, true, "-StartsWithDash" + randomWord(3), "Starts with dash"},
                {false, true, randomWord(3) + "EndsWithDash-", "Ends with dash"},
                {false, true, "~", "Just special character alone"},
//                {false, true, "sometext&$$%tohide" + randomWord(3), "Specials under letters sauce"}, // Disabled, will not fix.
                {false, true, "12'", "Twelve inch case"},
                {false, true, "", ""},
                {false, true, "@mail", ""},
                {false, true, randomWord(3) + "text,", "Text and comma"},
                {false, false, "DescriptionIsToLong!!!!!*&^%", "A" + randomWord(300)}
        };
    }
}
