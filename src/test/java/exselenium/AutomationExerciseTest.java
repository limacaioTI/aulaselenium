package exselenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AutomationExerciseTest {

	WebDriver driver;
	WebDriverWait wait;

	@BeforeEach
	void setUp() {
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	@AfterEach
	void tearDown() {
		driver.quit();
	}

	WebElement find(String css) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css)));
	}

	void type(String css, String text) {
		find(css).sendKeys(text);
	}

	// clique via JS porque anúncios do site cobrem os botões
	void click(String css) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", find(css));
	}

	void select(String css, String text) {
		new Select(find(css)).selectByVisibleText(text);
	}

	void abrirTelaDeLogin() {
		driver.get("http://automationexercise.com");
		assertTrue(driver.getTitle().contains("Automation Exercise"), "Home deve estar visível");
		click("a[href='/login']");
	}

	// Test Case 3
	// Classes de equivalência: e-mail válido não cadastrado; senha comum; senha com símbolos; e-mail com "+".
	// Valores limite: menor e-mail válido (a@b.c) e e-mail com 254 caracteres (máximo).
	@ParameterizedTest
	@ValueSource(strings = {
			"limacaio.teste@gmail.com|senhaErrada",
			"a@b.c|1",
			"limacaio.teste@gmail.com|!@#$%^&*()",
			"limacaio+teste@gmail.com|senhaErrada",
			"LONGO|senhaErrada" })
	void loginComEmailESenhaIncorretos(String entrada) {
		String[] p = entrada.split("\\|");
		String email = p[0].equals("LONGO") ? "a".repeat(6) + "@" + ("d".repeat(60) + ".").repeat(4) + "com" : p[0];

		abrirTelaDeLogin();
		assertEquals("Login to your account", find(".login-form h2").getText());
		type("[data-qa='login-email']", email);
		type("[data-qa='login-password']", p[1]);
		click("[data-qa='login-button']");
		assertEquals("Your email or password is incorrect!", find(".login-form p").getText());
	}

	// Test Case 1
	// Classes de equivalência para o nome: comum, com acentos/símbolos, só dígitos.
	// Valores limite: 1 caractere e 50 caracteres.
	@ParameterizedTest
	@ValueSource(strings = { "Caio Lima", "A", "NOME50", "Uff Praia vermelha", "123456" })
	void registrarUsuario(String nome) {
		if (nome.equals("NOME50")) {
			nome = "N".repeat(50);
		}
		abrirTelaDeLogin();
		assertEquals("New User Signup!", find(".signup-form h2").getText());
		type("[data-qa='signup-name']", nome);
		type("[data-qa='signup-email']", "limacaio" + System.currentTimeMillis() + "@gmail.com");
		click("[data-qa='signup-button']");

		assertEquals("ENTER ACCOUNT INFORMATION", find("h2.title.text-center").getText());
		click("#id_gender1");
		type("#password", "Senha@123");
		select("#days", "15");
		select("#months", "May");
		select("#years", "2003");
		click("#newsletter");
		click("#optin");
		type("#first_name", "Caio");
		type("#last_name", "Lima");
		type("#company", "UFF");
		type("#address1", "Rua A, 100");
		type("#address2", "Apto 1");
		select("#country", "Canada");
		type("#state", "Rio de Janeiro");
		type("#city", "Niteroi");
		type("#zipcode", "12345");
		type("#mobile_number", "21999999999");
		click("[data-qa='create-account']");

		assertEquals("ACCOUNT CREATED!", find("[data-qa='account-created']").getText());
		click("[data-qa='continue-button']");
		assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(
				By.xpath("//a[contains(.,'Logged in as')]"), nome)), "Deve mostrar 'Logged in as " + nome + "'");

		click("a[href='/delete_account']");
		assertEquals("ACCOUNT DELETED!", find("[data-qa='account-deleted']").getText());
		click("[data-qa='continue-button']");
	}
}
