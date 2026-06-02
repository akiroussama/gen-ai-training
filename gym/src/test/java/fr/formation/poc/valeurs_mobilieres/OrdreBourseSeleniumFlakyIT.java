package fr.formation.poc.valeurs_mobilieres;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * === DETTE TECHNIQUE INTENTIONNELLE — NE PAS IMITER ===
 *
 * Version VOLONTAIREMENT instable (« flaky ») du test e2e, support de
 * l'atelier J3 « Selenium -&gt; Playwright ». Lancée 5 fois, elle passe au
 * rouge/vert de façon ALÉATOIRE. Trois anti-patterns, à reconnaître :
 *
 * <ol>
 *   <li>SIN 1 — {@code Thread.sleep(...)} au lieu d'une attente conditionnelle :
 *       course avec le bootstrap Angular et l'appel HTTP. Trop court = l'élément
 *       n'est pas prêt = échec ; parfois prêt à temps = succès.</li>
 *   <li>SIN 2 — sélecteurs positionnels fragiles ({@code nth-child}, chaîne DOM) :
 *       cassent au moindre changement de structure du template.</li>
 *   <li>SIN 3 — état partagé + ordre d'exécution : driver statique non
 *       réinitialisé et champ statique qu'un test suppose rempli par un autre.
 *       Le test {@code filtre()} échoue s'il tourne seul ou avant {@code nominal()}.</li>
 * </ol>
 *
 * <p>{@code @Disabled} par défaut (ne casse pas {@code mvn verify}). Pour la
 * DÉMO : retirer {@code @Disabled}, démarrer backend :8080 + Angular :4200 +
 * Chrome, puis lancer 5 fois
 * {@code mvn verify -Dit.test=OrdreBourseSeleniumFlakyIT}.</p>
 *
 * <p>La version saine est {@link OrdreBourseSeleniumIT} ; l'équivalent
 * déterministe est dans {@code e2e/ordre-bourse.spec.ts} (Playwright).</p>
 */
@Disabled("DETTE INTENTIONNELLE (flaky) — démo atelier Selenium->Playwright. Retirer pour la démo.")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrdreBourseSeleniumFlakyIT {

    // SIN 3a : driver STATIQUE partagé entre les tests (jamais réinitialisé par test).
    private static WebDriver driver;
    // SIN 3b : état partagé entre tests -> dépendance à l'ordre d'exécution.
    private static int lignesVuesAuChargement = -1;

    // SIN 1 : attente fixe et marginale, calée trop court pour être fiable.
    private static final long SLEEP_MS = 400;

    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("FLAKY nominal : la liste se charge (sleep fixe au lieu d'attente)")
    void nominal() throws InterruptedException {
        driver.get("http://localhost:4200");
        Thread.sleep(SLEEP_MS); // SIN 1 : parfois Angular n'a pas fini de rendre la table.
        var lignes = driver.findElements(By.cssSelector("cat-liste-ordres tbody tr"));
        lignesVuesAuChargement = lignes.size(); // SIN 3 : on dépose un état pour l'autre test.
        assertThat(lignes).isNotEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("FLAKY filtre : dépend de l'ordre + sélecteur positionnel fragile")
    void filtre() throws InterruptedException {
        // SIN 3 : suppose que nominal() a tourné AVANT (sinon -1) -> rouge si lancé seul.
        assertThat(lignesVuesAuChargement)
                .as("ce test suppose que nominal() a déjà tourné")
                .isGreaterThan(0);

        driver.findElement(By.id("codeClient")).sendKeys("CLI-0001");
        // SIN 2 : le bouton ciblé par sa POSITION (3e enfant du .filtre) -> casse si le DOM bouge.
        driver.findElement(By.cssSelector("div.filtre > *:nth-child(3)")).click();
        Thread.sleep(SLEEP_MS); // SIN 1 : course avec l'appel HTTP de filtrage.

        var clients = driver.findElements(By.cssSelector("cat-liste-ordres tbody tr td:nth-child(2)"));
        assertThat(clients).isNotEmpty();
        assertThat(clients).allMatch(td -> td.getText().equals("CLI-0001"));
    }
}
