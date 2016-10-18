package org.jahia.modules.pageimporter.tests;

import org.apache.commons.io.FileUtils;
import org.jahia.modules.pageimporter.tests.businessobjects.Area;
import org.jahia.modules.tests.core.ModuleTest;
import org.jahia.modules.tests.utils.CustomExpectedConditions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by sergey on 2016-08-30.
 */
public class PageImporterRepository extends ModuleTest {
    protected static final String SELECTED_AREA_MARK = "AreaSelection";
    protected static final String AREA_BORDER_TYPE_KEY = "borderType";
    protected static final String AREA_BORDER_COLOR_KEY = "borderColor";

    @BeforeSuite()
    protected void createSite() {
        // Creates site with form factory module
        initWithGroovyFile("createSiteWithAreas.groovy");
    }

    @AfterSuite()
    protected void deleteSite() {
        initWithGroovyFile("deleteSite.groovy");
    }

    /**
     * This method generates a random word for a given length.
     *
     * @param length Desired word length
     * @return String. word.
     */
    protected String randomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(26)));
        }
        return word.toString();
    }

    /**
     * Opens projects list and imports a new project, asserts that new project is listed in 'projects library'
     * @param locale Is used as part of url to projects list
     * @param projectName String. Name of the project you are creating
     * @param projectDescription String, Description of project you are creating
     * @param testProjectFileName String, name of .zip file to import. Zip file should be inside testData.testProjects folder.
     * @return 'Success' if form created successfully, otherwise null.
     */
    protected void importProject(String locale,
                                 String projectName,
                                 String projectDescription,
                                 String testProjectFileName){
        String zipFilePath = new File("src/test/resources/testData/testProjects/"+testProjectFileName).getAbsolutePath();
        String jsToEnableInput = "function getElementByXpath(path) {" +
                "return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                "}" +
                "fileInput = getElementByXpath(\"//label[input[@type='file']]\");" +
                "fileInput.setAttribute(\"style\", \"\");";

        goToProjectsList(locale);
        waitForGlobalSpinner(1, 45);
        WebElement importProjectButton = findByXpath("//button[@ng-click='projects.importProject($event)']");
        waitForElementToStopMoving(importProjectButton);
        clickOn(importProjectButton);
        WebElement projectNameField = findByXpath("//input[@name='projectName']");
        WebElement projectDescriptionField = findByXpath("//textarea[@ng-model='ipc.projectDescription']");
        WebElement projectZipFileField = findByXpath("//input[@type='file']");
        WebElement importButton = findByXpath("//button[@ng-click='ipc.submit()']");
        WebElement dialogueBox = findByXpath("//div[@class='md-dialog-container ng-scope']");
        try {
            createWaitDriver(5, 500).until(CustomExpectedConditions.javascriptWithoutException(jsToEnableInput));
        }catch(TimeoutException e){
            getLogger().error("Hidden Input field might be not activated. JavaScript execution still produces errors even after 5 - 7 attempts.");
        }
        typeInto(projectNameField, projectName);
        typeInto(projectDescriptionField, projectDescription);
        projectZipFileField.sendKeys(zipFilePath);

        waitForElementToBeEnabled(importButton, 7);
        Assert.assertEquals(
                importButton.isEnabled(),
                true,
                "All fields are filled, but 'Import' button is disabled. Cannot import a project. Check if project name is unique.");
        clickOn(importButton);
        //Increase second parameter here if import of large project fails
        waitForGlobalSpinner(1, 45);
        waitForElementToDisappear(dialogueBox, 7);
        waitForElementToDisappear(importButton, 7);
        Assert.assertEquals(
                isVisible(By.xpath("//md-card-title-text/span[contains(., '"+projectName+"')]"), 20),
                true,
                "New project name ("+projectName+")is not found in projects list.");
    }

    /**
     * Open list of projects. (Just iframe)
     * @param locale Is used as part of url to projects list.
     */
    protected void goToProjectsList(String locale){
        getDriver().get(getPath("/cms/adminframe/default/" + locale + "/sites/"+getPropertyValue("test.site.name", "page-importer-site")+".page-importer.html"));
        waitForGlobalSpinner(2, 45);
    }

    /**
     * Waits for several layers of global spinner to appear and than disappear
     * @param secondsToWaitSpinnerAppear int, amount of seconds to wait for global spinner elements to appear
     * @param secondsToWaitSpinnerDisappear int, amount of seconds to wait for global spinner elements to disappear
     */
    protected void waitForGlobalSpinner(int secondsToWaitSpinnerAppear,
                                        int secondsToWaitSpinnerDisappear) {
        List<WebElement> spinners = new LinkedList<WebElement>();

        try {
            WebElement spinner = null;
            WebElement tiOverlay;
            WebElement tiOverlayContent;
            try {
                spinner = createWaitDriver(secondsToWaitSpinnerAppear, 300).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ti-global-spinner']")));

            } catch (TimeoutException ee) {
            }
            tiOverlay = noWaitingFindBy(By.xpath("//div[@class='ti-overlay']"));
            tiOverlayContent = noWaitingFindBy(By.xpath("//div[@class='ti-overlay-content']"));
            spinners.add(spinner);
            spinners.add(tiOverlay);
            spinners.add(tiOverlayContent);

            for (WebElement elementToWait : spinners) {
                if (elementToWait != null) {
                    waitForElementToBeInvisible(elementToWait, secondsToWaitSpinnerDisappear);
                }
            }
        } catch (TimeoutException e) {
        }
    }

    /**
     * Delete all projects
     * @return Amount of deleted projects
     */
    protected int deleteAllProjects() {
        int projectsRemoved = 0;
        List<String> projectNames = new LinkedList<String>();

        try {
            List<WebElement> projectsBeforeDeletion = createWaitDriver(2, 300)
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//md-card-title-text/span[@ng-click='projects.seeProject($index)']")));
            for(WebElement projectTitle:projectsBeforeDeletion){
                projectNames.add(projectTitle.getText());
            }
        } catch (TimeoutException e) {
            return projectsRemoved;
        }

        for(String name:projectNames){
            if(deleteProject(name)){
                projectsRemoved++;
            }
        }

        return projectsRemoved;
    }

    /**
     * Deletes project with given name. Throws assertion error if you try to delete project that does not exist
     * @param projectName String, name of project to delete
     * @return True if project was deleted, false if still visible after deletion.
     */
    protected boolean deleteProject(String projectName){
        boolean isProjectDeleted;
        WebElement proectToDelete = findByXpath("//md-card[./*/md-card-title-text[contains(., '"+projectName+"')]]//button[@ng-click='projects.removeProject($event, p)']");

        Assert.assertNotNull(proectToDelete, "Remove button to delete a project '"+projectName+"' not found. Does project exist?");
        try{
            clickOn(proectToDelete);
        }catch (WebDriverException e){
            while(!isVisible(By.xpath("//button[@ng-click='dialog.hide()']"), 1)) {
                try {
                    new Actions(getDriver()).sendKeys(Keys.ARROW_UP).click(proectToDelete).build().perform();
                }catch(WebDriverException ee){}
            }
        }

        WebElement confirmRemovalBtn = findByXpath("//button[@ng-click='dialog.hide()']");
        waitForElementToStopMoving(confirmRemovalBtn);
        clickOn(confirmRemovalBtn);
        waitForElementToDisappear(confirmRemovalBtn, 10);
        waitForGlobalSpinner(1, 45);
        isProjectDeleted = waitForElementToBeInvisible(proectToDelete, 3);

        return isProjectDeleted;
    }

    protected void switchToProjectFrame(){
        createWaitDriver(10, 300).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[@id='tiProjectFrame']")));
    }

    protected void switchToDefaultContent(){
        getDriver().switchTo().defaultContent();
    }

    /**
     * Click on 'Open Project' button and select page for base template
     * @param projectName String, name of the project
     * @param baseTemplatePageName String, filename of desired page. Example: Index.html
     */
    protected void openProjectFirstTime(String  projectName,
                                        String  baseTemplatePageName){
        WebElement editProjectBtn = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//button[@ng-click='projects.seeProject($index)']");

        clickOn(editProjectBtn);
        waitForGlobalSpinner(1, 30);
        WebElement importBtn = findByXpath("//button[@ng-click='sbpc.submit()']");
        WebElement baseTemplateSelector = findByXpath("//md-select[@ng-model='sbpc.selectedPage']");
        waitForElementToStopMoving(baseTemplateSelector);
        clickOn(baseTemplateSelector);
        WebElement baseTemplateOption = findByXpath("//md-option[./div[normalize-space(text())='"+baseTemplatePageName+"']]");
        waitForElementToBeEnabled(baseTemplateOption, 3);
        waitForElementToStopMoving(baseTemplateOption);
        clickOn(baseTemplateOption);
        waitForElementToBeEnabled(importBtn, 7);
        clickOn(importBtn);
        waitForElementToBeInvisible(importBtn);
        waitForGlobalSpinner(2, 45);
        switchToProjectFrame();
//        Uncommenting this will cause chromedriver throwing ElementNotClickable exception, for some reason. Bug??
//        WebElement body = findByXpath("//body");
//        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }

    protected void openProject(String   projectName){
        WebElement editProjectBtn = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//button[@ng-click='projects.seeProject($index)']");

        clickOn(editProjectBtn);
        waitForGlobalSpinner(2, 45);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }

    /**
     * Select an area. If  ancestor selector does not work, make sure you have at least 1 area on template (at least 1 area is expanded on home or wherever)
     * @param areaName String, name of new area
     * @param xPath, String, xPath to click on to create new area
     * @param xOffset, int, Horizontal offsen in pixels, from top left corner of element. Pass 0 to click in the middle
     * @param yOffset int, Vertical offsen in pixels, from top left corner of element. Pass 0 to click in the middle
     * @param assignHtml True to assign selected html, node type and property to the area
     * @param nodeType String, node type to assign to the area
     * @param propertyType String, propetry type, pass empty string to avoid feature usage
     */
    protected void selectArea(String    areaName,
                              String    xPath,
                              int       xOffset,
                              int       yOffset,
                              boolean   assignHtml,
                              String    nodeType,
                              String    propertyType){
        rightMouseClick(xPath, xOffset, yOffset);
        WebElement menuAreaBtn = findByXpath("//div[@ng-click='rmc.showArea()']");
        waitForElementToStopMoving(menuAreaBtn);
        clickOn(menuAreaBtn);
        WebElement areaNameField = findByXpath("//input[@name='areaName']");
        WebElement okButton = findByXpath("//button[@ng-click='sac.area.ok()']");
        WebElement assignToPropertyCheckboxToCheck = findByXpath("//md-checkbox");
        WebElement assignToPropertyCheckboxToClick = findByXpath("//md-checkbox//div[@class='md-label']");
        waitForElementToStopMoving(areaNameField);
        //Defining area name
        typeInto(areaNameField, areaName);

        if (assignHtml) {
            //Ensure checkbox is on
            if (!assignToPropertyCheckboxToCheck.getAttribute("aria-checked").equals("true")) {
                waitForElementToStopMoving(assignToPropertyCheckboxToClick);
                clickOn(assignToPropertyCheckboxToClick);
            }
            if (!nodeType.isEmpty()) {
                WebElement nodeTypeField = findByName("nodeTypeSelection");
                if (typeInto(nodeTypeField, nodeType)) {
                    WebElement nodeTypeOption = findByXpath("//div[contains(text(), '" + nodeType + "')]");
                    waitForElementToStopMoving(nodeTypeOption);
                    clickOn(nodeTypeOption);
                    waitForElementToBeInvisible(nodeTypeOption);
                }
                if (!propertyType.isEmpty()) {
                    WebElement propertyTypeSelector = findByXpath("//md-select[@ng-model='sac.area.selectedPropertyForHtml.propertyName']");
                    clickOn(propertyTypeSelector);
                    WebElement propertyTypeOption = findByXpath("//md-option/div[contains(text(), '" + propertyType + "')]");
                    waitForElementToStopMoving(propertyTypeOption);
                    clickOn(propertyTypeOption);
                    waitForElementToBeInvisible(propertyTypeOption);
                }
            }
        }

        waitForElementToBeEnabled(okButton, 5);
        clickOn(okButton);
        waitForElementToBeInvisible(okButton);

        Assert.assertTrue(
                checkIfAreaSelected(xPath),
                "Area was not selected. Target element does not have '" + SELECTED_AREA_MARK + "' class." + " XPath: " + xPath);
    }

    protected void selectArea(Area area){
        selectArea(
                area.getName(),
                area.getXpath(),
                area.getxOffset(),
                area.getyOffset(),
                area.isHtmlAssigned(),
                area.getNodeType(),
                area.getPropertyType()
        );
    }

    /**
     * Check if area is selected (has AreaSelection class)
     * @param xPath String, XPath to the area
     * @param softAssert Instance of SoftAssert you are working with. Will fail if visibility result is not expected.
     * @param expectedResult boolean, your expectation if area should be selected.
     * @param errorMsg String, error massage that will be prepended to assert's error message
     * @return True if area selected, otherwise false
     */
    protected boolean checkIfAreaSelected(String        xPath,
                                          SoftAssert    softAssert,
                                          boolean       expectedResult,
                                          String        errorMsg) {
        switchToProjectFrame();
        WebElement area = findByXpath(xPath);
        softAssert.assertNotNull(area, errorMsg+". Cannot find an element that you are trying to check if selected as area. XPath: '" + xPath + "'.");

        boolean isAreaSelected = area.getAttribute("class").contains(SELECTED_AREA_MARK);
        switchToDefaultContent();
        softAssert.assertEquals(
                isAreaSelected,
                expectedResult,
                errorMsg+". Assertion if element: '" + xPath + "' has class '" + SELECTED_AREA_MARK + "' (is selected) Failed");
        return isAreaSelected;
    }

    /**
     * Check if area is selected (has ViewSelection class)
     * @param xPath String, XPath to the area
     * @return True if area selected, otherwise false
     */
    protected boolean checkIfAreaSelected(String xPath){
        return checkIfAreaSelected(xPath, new SoftAssert(), false, "");
    }

    /**
     * Performs right click on element with given xPath and offsets. Will do all the iFrame switching for you.
     * @param xPath String, xPath to your target element
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     */
    protected void rightMouseClick(String   xPath,
                                   int      xOffset,
                                   int      yOffset){
        switchToProjectFrame();
        WebElement area = findByXpath(xPath);
        Assert.assertNotNull(area, "Cannot find an element that you are trying to right click on. XPath: '"+xPath+"'.");

        if(xOffset == 0){
            xOffset = area.getSize().getWidth()/2;
        }
        if(yOffset == 0){
            yOffset =  area.getSize().getHeight()/2;
        }
        new Actions(getDriver()).moveToElement(area, xOffset, yOffset).contextClick().build().perform();
        switchToDefaultContent();
    }

    protected String generateRGB(){
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

    protected static int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    protected void mouseOver(String xPathToElement){
        switchToProjectFrame();
        WebElement el = findByXpath(xPathToElement);
        new Actions(getDriver()).moveToElement(el).build().perform();
        switchToDefaultContent();
    }

    protected Map<String, Map<String, String>> getBorderColors(Area[] areas){
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

    protected Map<String, String> changeColors(Area area){
        Map<String, String> newColors = new HashMap<String, String>();
        String newColor = generateRGB();

        WebElement menuBtn = findByXpath("//button[@aria-label='Settings']");
        clickOn(menuBtn);
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='pc.setUpColors($event)']");
        waitForElementToStopMoving(adjustColorsBtn);
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

    protected void clearSelections(){
        WebElement menuBtn = findByXpath("//button[@aria-label='Layout']");
        clickOn(menuBtn);
        WebElement clearBtn = findByXpath("//button[@ng-click='pc.clearSelections($event)']");
        waitForElementToStopMoving(clearBtn);
        clickOn(clearBtn);
        WebElement yesClearBtn = findByXpath("//button[@ng-click='dialog.hide()']");
        waitForElementToStopMoving(yesClearBtn);
        clickOn(yesClearBtn);
        waitForElementToBeInvisible(yesClearBtn);
    }

    /**
     * Click on field, clear it and type text into it.
     * @param field WebElement, filed to send keys into it
     * @param text String, text to send into input field
     */
    protected void typeIntoSlowly(WebElement  field,
                                  String      text){
        char[] textAsArray = text.toCharArray();
        clickOn(field);
        field.clear();

        for (int i = 0; i < textAsArray.length; i++){
            field.sendKeys(textAsArray[i]+"");
        }
    }

    protected void cleanDownloadsFolder() {
        String downloadsFolderPath = new File(getDownloadsFolder()).getAbsolutePath();

        try {
            FileUtils.cleanDirectory(new File(downloadsFolderPath));
        } catch (IOException e) {
            getLogger().error(e.getMessage());
        } catch (IllegalArgumentException ee){
            getLogger().error(ee.getMessage());
        }
    }

    private void deleteAllProjectsFast(){
        String jsToDeleteProjects = " function removeAllProjects() {\n" +
                "const trashcans = $('[aria-label=\"Remove\"]');\n" +
                "        for (let i = 0; i < trashcans.length; ++i) {\n" +
                "            trashcans[i].onclick = (event) => {\n" +
                "                $('span:contains(\"Remove\")').parent('button').click();\n" +
                "                if (trashcans[i + 1]) {\n" +
                "                    trashcans[i + 1].click();\n" +
                "                }\n" +
                "            };\n" +
                "        }\n" +
                "        if (trashcans.length > 0) {\n" +
                "            trashcans[0].click();   \n" +
                "        }\n" +
                "    }" +
                "removeAllProjects();";

        executeScriptWithJavascript(jsToDeleteProjects);
        waitForGlobalSpinner(2, 60);
    }

    /**
     * AfterClass method, deletes all projects, clean Downloads folder.
     */
    protected void customTestCleanUp(){
        goToProjectsList("en");
        deleteAllProjectsFast();
        cleanDownloadsFolder();
    }
}