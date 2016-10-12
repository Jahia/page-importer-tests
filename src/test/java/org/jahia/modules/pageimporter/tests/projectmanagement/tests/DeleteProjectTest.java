package org.jahia.modules.pageimporter.tests.projectmanagement.tests;

import org.jahia.modules.pageimporter.tests.PageImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergey on 2016-06-29.
 */
public class DeleteProjectTest extends PageImporterRepository {
    @Test
    public void deleteProjectTest(){
        String projectToDeleteName = randomWord(7);
        String projectToKeepName = randomWord(5);
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "DeleteProjectTest.deleteProjectTest");

        importProject("en", projectToDeleteName, "", "AlexLevels.zip");
        importProject("en", projectToKeepName, "", "AlexLevels.zip");

        softAssert.assertEquals(deleteProject(projectToDeleteName),
                true,
                "Project with name '"+projectToDeleteName+" is still visible after deletion.");
        softAssert.assertEquals(isVisible(By.xpath("//md-card-title-text[contains(., '"+projectToKeepName+"')]/ancestor::md-card"), 10),
                true,
                "Project with name '"+projectToKeepName+"' should not be deleted, but become invisible after another project deletion.");

        softAssert.assertAll();
    }

    @Test
    public void deleteAllProjectsTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "DeleteProjectTest.deleteAllProjectsTest");
        int projectsToImport = 5;
        Map<String, String> projectsInfo = new HashMap<String, String>();

        for(int i = 0; i < projectsToImport; i++) {
            String projectName = randomWord(10);
            String projectDescription = randomWord(1) + " " + randomWord(2) + " " + randomWord(3);

            projectsInfo.put(projectName, projectDescription);

            importProject("en", projectName, projectDescription, "AlexLevels.zip");
        }

        int projectsDeleted = deleteAllProjects();

        for(String projectName: projectsInfo.keySet()){
            softAssert.assertEquals(
                    isVisible(By.xpath("//md-card-title-text/span[contains(., '"+projectName+"')]"), 1),
                    false,
                    "After removing several projects, one of them is still visible:"+ projectName);
        }
        softAssert.assertEquals(projectsDeleted, projectsToImport, "Amount of imported and deleted projects does not mach");
        softAssert.assertAll();
    }
}
