package org.jahia.modules.pageimporter.tests.projectmanagement.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.tests.utils.CustomExpectedConditions;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
    @Test(dataProvider = "projectDetails", enabled = false)
    public void editProjectDescription(boolean isValidName, boolean isValidDescription, String modifiedName, String modifiedDescription) {
        String originalName = randomWord(9);
        String originalDescription = randomWord(15);
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "EditProjectTest.editProjectDescription");

        importProject("en", originalName, originalDescription, "AlexLevels.zip");
        WebElement editProjectBtn = findByXpath("//md-card-title-text[contains(., '" + originalName + "')]/ancestor::md-card//button[@ng-click='projects.editProject($event, p, $index)']");
        clickOn(editProjectBtn);

        WebElement projectNameField = findByXpath("//input[@name='projectName']");
        WebElement projectDescriptionField = findByXpath("//textarea[@ng-model='project.description']");
        WebElement saveChangesBtn = findByXpath("//button[@aria-label='Save Changes']");

        typeInto(projectNameField, modifiedName);
        typeInto(projectDescriptionField, modifiedDescription);
        boolean doesUIShowNameError = projectNameField.getAttribute("class").contains("ng-invalid-");
        boolean doesUIShowDescriptionError = projectDescriptionField.getAttribute("class").contains("ng-invalid-");

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
                        isVisible(By.xpath("//md-card-title-text/span[contains(text(), '" + modifiedName + "')]"), 7),
                        true,
                        "New project name is not visible after editing project. New name.: " + modifiedName + " Old name.:" + originalName +
                                ". New Desc.:"+modifiedDescription+". Old Desc.:"+originalDescription);
                softAssert.assertEquals(
                        isVisible(By.xpath("//md-card-title-text/span[contains(text(), '" + originalName + "')]"), 1),
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
                isVisible(By.xpath("//md-card-title-text/span[contains(text(), '" + originalName + "')]"), 7),
                true,
                "Cannot find project with original name after cancelling renaming. New name.: " + originalName + " Old name.:" + modifiedName);
        softAssert.assertEquals(
                isVisible(By.xpath("//md-card-title-text/span[contains(text(), '" + modifiedName + "')]"), 1),
                false,
                "Project with modified name is found after cancelling renaming. New name.: " + originalName + " Old name.:" + modifiedName);
        softAssert.assertAll();
    }

    //TI_S1C17
    @Test
    public void changeProjectPicture(){
        String projectName = randomWord(10);
        String projectDescription = randomWord(22);

        importProject("en", projectName, projectDescription, "AlexLevels.zip");
        changeProjectPicture(projectName, "JumpingCat.gif");
        changeProjectPicture(projectName, "FlowerCat.tif");
        changeProjectPicture(projectName, "RaccoonCat.jpg");
        changeProjectPicture(projectName, "TransparentBackgroundCat.png");
    }

    /**
     * Changes project picture. To use that method you have to be on the projects list page
     * @param projectName String, project name that you want to change picture for
     * @param pictureFileName String, picture filename. picture has to be under "src/test/resources/testData/pictures" folder
     */
    protected void changeProjectPicture(String projectName, String pictureFileName){
        Long maxMilliSecondsToWait = 5000L;
        Long start;
        String pictureFilePath = new File("src/test/resources/testData/pictures/"+pictureFileName).getAbsolutePath();
        String jsToEnableInput = "function getElementByXpath(path) {" +
                "return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                "}" +
                "fileInput = getElementByXpath(\"//label[input[@type='file']]\");" +
                "fileInput.setAttribute(\"style\", \"\");";

        WebElement editImageBtn = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//button[@ng-click='projects.editImage($event, p, $index)']");
        WebElement projectPictureElement = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//img[@alt='Project image']");
        String oldImageLocation = projectPictureElement.getAttribute("src");

        clickOn(editImageBtn);
        WebElement saveImageBtn = findByXpath("//button[@aria-label='Save image']");
        WebElement dialogueBox = findByXpath("//div[@class='md-dialog-container ng-scope']");
        WebElement projectImgFileField = findByXpath("//input[@type='file']");

        createWaitDriver(5, 500).until(CustomExpectedConditions.javascriptWithoutException(jsToEnableInput));
        projectImgFileField.sendKeys(pictureFilePath);
        waitForElementToBeEnabled(saveImageBtn, 7);
        Assert.assertEquals(
                saveImageBtn.isEnabled(),
                true,
                "Cannot save project image, because 'Save image' button disabled");
        clickOn(saveImageBtn);
        waitForElementToDisappear(dialogueBox, 7);
        waitForElementToDisappear(saveImageBtn, 7);
        waitForGlobalSpinner(1, 45);

        start = new Date().getTime();
        while (oldImageLocation.equals(
                findByXpath("//md-card-title-text[contains(., '" + projectName + "')]/ancestor::md-card//img[@alt='Project image']").getAttribute("src"))){
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {}
            if (new Date().getTime() - start >= maxMilliSecondsToWait) {
                Assert.fail("Changing project picture failed. picture location did not change for "+maxMilliSecondsToWait+" milliseconds and still remain the same:" + oldImageLocation);
                break;
            }
        }
    }

    @Test
    public void selectionTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "EditProjectTest.selectionTest");
        String projectNameOne = randomWord(8);
        String projectNameTwo = randomWord(8);
        String projectNameThree = randomWord(8);

        importProject("en", projectNameOne, "", "AlexLevels.zip");
        importProject("en", projectNameTwo, "", "AlexLevels.zip");
        importProject("en", projectNameThree, "", "AlexLevels.zip");

        //None is selected
        softAssert.assertFalse(
                isProjectSelected(projectNameOne),
                "Project is selected by default, we did not select that. Project name: "+projectNameOne);
        //Select projectOne. Only projectOne is selected
        softAssert.assertTrue(
                selectProject(projectNameOne),
                "Project is not selected after clicking on checkbox. Project name: "+projectNameOne);
        softAssert.assertFalse(
                isProjectSelected(projectNameTwo),
                "Project is selected after clicking on another project's checkbox. Project name: "+projectNameTwo);
        softAssert.assertFalse(
                isProjectSelected(projectNameThree),
                "Project is selected after clicking on another project's checkbox. Project name: "+projectNameThree);
        //Unselect projectOne, none is selected
        softAssert.assertFalse(
                unSelectProject(projectNameOne),
                "Project is still selected after clicking on checkbox. Project name: "+projectNameOne);
        softAssert.assertFalse(
                isProjectSelected(projectNameTwo),
                "Project is selected after unselecting another project. Project name: "+projectNameTwo);
        softAssert.assertFalse(
                isProjectSelected(projectNameThree),
                "Project is selected after unselecting another project. Project name: "+projectNameThree);
        //Select all, all selected
        clickSelectUnselectAll();
        softAssert.assertTrue(
                isProjectSelected(projectNameOne),
                "Project is not selected after clicking 'Select All'. Project name: "+projectNameOne);
        softAssert.assertTrue(
                isProjectSelected(projectNameTwo),
                "Project is not selected after clicking 'Select All'. Project name: "+projectNameTwo);
        softAssert.assertTrue(
                isProjectSelected(projectNameThree),
                "Project is not selected after clicking 'Select All'. Project name: "+projectNameThree);
        //Unselect projectOne, all but project one is selected
        softAssert.assertFalse(
                unSelectProject(projectNameOne),
                "Project is  selected after unselecting it. Project name: "+projectNameOne);
        softAssert.assertTrue(
                isProjectSelected(projectNameTwo),
                "Project is not selected after unselectiong another project 'Select All'. Project name: "+projectNameTwo);
        softAssert.assertTrue(
                isProjectSelected(projectNameThree),
                "Project is not selected after unselectiong another project. Project name: "+projectNameThree);
        //Select all again, all projects selected
        clickSelectUnselectAll();
        softAssert.assertTrue(
                isProjectSelected(projectNameOne),
                "Project is not selected after clicking 'Select All'. Project name: "+projectNameOne);
        softAssert.assertTrue(
                isProjectSelected(projectNameTwo),
                "Project is not selected after clicking 'Select All'. Project name: "+projectNameTwo);
        softAssert.assertTrue(
                isProjectSelected(projectNameThree),
                "Project is not selected after clicking 'Select All'. Project name: "+projectNameThree);

        softAssert.assertAll();
    }

    /**
     * Click 'Select All' Or 'Unselect All' checkbox
     */
    private void clickSelectUnselectAll(){
        WebElement selectAllCheckbox = findByXpath("//md-checkbox[@aria-label='Select all']");
        clickOn(selectAllCheckbox);
    }

    /**
     * Select project in projects list, if not already selected
     * @param projectName String, name of project to select
     * @return true if project is selected after all
     */
    private boolean selectProject(String projectName){
        WebElement checkbox = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//md-checkbox");
        boolean isSelected = isProjectSelected(projectName);

        if (!isSelected){
            clickOn(checkbox);
            checkbox = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//md-checkbox");
            isSelected = checkbox.getAttribute("aria-checked").contains("true");
        }
        return isSelected;
    }

    /**
     * Unselect project in projects list, if it is selected.
     * @param projectName String, name of project to unselect
     * @return true if project is still selected after all
     */
    private boolean unSelectProject(String projectName){
        WebElement checkbox = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//md-checkbox");
        boolean isSelected = isProjectSelected(projectName);

        if (isSelected){
            clickOn(checkbox);
            checkbox = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//md-checkbox");
            isSelected = checkbox.getAttribute("aria-checked").contains("true");
        }
        return isSelected;
    }

    /**
     * Checks if project selected
     * @param projectName String, project name
     * @return True if project is selected
     */
    private boolean isProjectSelected(String projectName){
        WebElement checkbox = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//md-checkbox");
        return checkbox.getAttribute("aria-checked").contains("true");
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
