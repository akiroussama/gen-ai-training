package fr.formation.poc.valeurs_mobilieres;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test e2e Selenium WebDriver sur le parcours « lister + exécuter
 * un ordre de bourse » dans le front Angular.
 *
 * <p>DESACTIVE par défaut. Pré-requis pour activer :</p>
 * <ol>
 *   <li>Backend Spring Boot tourne sur :8080
 *       ({@code mvn spring-boot:run} dans poc-titres/)</li>
 *   <li>Front Angular tourne sur :4200 avec proxy /api/*
 *       ({@code npm start} dans angular-front/)</li>
 *   <li>Google Chrome installé (WebDriverManager gère le binaire driver)</li>
 *   <li>Retirer l'annotation {@code @Disabled} ci-dessous</li>
 *   <li>Lancer : {@code mvn verify -Dit.test=OrdreBourseSeleniumIT}</li>
 * </ol>
 *
 * Sert l'atelier M6 Selenium e2e (J3 PM v2).
 */
@Disabled("Pré-requis : Chrome + backend Spring sur :8080 + Angular sur :4200. " +
        "Voir JavaDoc pour activation. Volontairement désactivé pour ne pas casser mvn verify.")
class OrdreBourseSeleniumIT {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Parcours nominal : la liste des ordres s'affiche au chargement")
    void parcoursNominal_listeOrdres_seCharge() {
        driver.get("http://localhost:4200");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("cat-liste-ordres table")));

        var lignes = driver.findElements(By.cssSelector("cat-liste-ordres tbody tr"));
        assertThat(lignes).isNotEmpty();
    }

    @Test
    @DisplayName("Parcours filtre : recherche par code client filtre les résultats")
    void parcoursFiltre_clientFiltre_resultats() {
        driver.get("http://localhost:4200");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("codeClient")));

        driver.findElement(By.id("codeClient")).sendKeys("CLI-0001");
        driver.findElement(By.cssSelector(".filtre button")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("cat-liste-ordres tbody tr")));

        var lignes = driver.findElements(By.cssSelector("cat-liste-ordres tbody tr"));
        assertThat(lignes).allMatch(l ->
                l.findElement(By.cssSelector("td:nth-child(2)")).getText().equals("CLI-0001"));
    }
}
