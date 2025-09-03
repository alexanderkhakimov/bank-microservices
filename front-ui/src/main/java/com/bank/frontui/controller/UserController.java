package com.bank.frontui.controller;

import com.bank.frontui.model.Currency;
import com.bank.frontui.model.UserAccount;
import com.bank.frontui.service.AccountClient;
import com.bank.frontui.service.KeycloakAdminService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Controller
@Slf4j
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private AccountClient accountClient;

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/keycloak";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("errors", null);
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String login,
                         @RequestParam String password,
                         @RequestParam String confirm_password,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String birthdate,
                         Model model) {
        if (!password.equals(confirm_password)) {
            model.addAttribute("errors", "Пароли не совпадают");
            return "signup";
        }
        var dob = LocalDate.parse(birthdate);
        if (Period.between(dob, LocalDate.now()).getYears() < 18) {
            model.addAttribute("errors", "Возраст должен быть старше 18");
            return "signup";
        }
        String keycloakId = keycloakAdminService.registerUser(login, password, name, email, dob);
        var account = accountClient.register(keycloakId, password, login, name, email, dob);
        return "redirect:/oauth2/authorization/keycloak";
    }

    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        String login = oidcUser.getPreferredUsername();
        //  UserAccount account = accountClient.getAccount(oidcUser);
        model.addAttribute("login", login);
//        model.addAttribute("name", account.name());
//        model.addAttribute("accounts", account.balances());
        model.addAttribute("currency", Arrays.asList(Currency.values()));
        return "main";
    }

    @PostMapping("/user/{login}/editPassword")
    public String editPassword(@PathVariable String login,
                               @RequestParam String password,
                               @RequestParam String confirm_password,
                               Model model) {
        if (!password.equals(confirm_password)) {
            model.addAttribute("passwordErrors", "Пароли не совпадают");
            return "main";
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLogin = null;
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            var oidcUser = (OidcUser) oAuth2AuthenticationToken.getPrincipal();
            currentUserLogin = oidcUser.getPreferredUsername();
        }

        if (currentUserLogin == null || !currentUserLogin.equals(login)) {
            model.addAttribute("unauthenticated", "Пользователь не авторизован!");
            return "main";
        }
        try {
            keycloakAdminService.updatePasswordKeycloak(login, password);
            accountClient.updatePassword(login, password);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("userAccountsErrors", e.getMessage());
            return "main";
        }
    }

    @PostMapping("/user/{login}/editUserAccounts")
    public String editUserAccounts(@PathVariable String login,
                                   @RequestParam String name,
                                   @RequestParam String birthdate,
                                   Model model) {
        LocalDate dob = LocalDate.parse(birthdate);
        if (Period.between(dob, LocalDate.now()).getYears() < 18) {
            model.addAttribute("userAccountsErrors", "Возраст должен быть старше 18");
            return "main";
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLogin = null;
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            var oidcUser = (OidcUser) oAuth2AuthenticationToken.getPrincipal();
            currentUserLogin = oidcUser.getPreferredUsername();
        }

        if (currentUserLogin == null || !currentUserLogin.equals(login)) {
            model.addAttribute("unauthenticated", "Пользователь не авторизован!");
            return "main";
        }
        try {
            keycloakAdminService.updateAccount(login,name,dob);
            accountClient.updateAccount(login,name,dob);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("userAccountsErrors", e.getMessage());
            return "main";
        }
    }

    @PostMapping("/user/{login}/addBalance")
    public String addBalance(@RequestParam Currency currency,
                             @RequestParam double initialBalance,
                             @AuthenticationPrincipal OidcUser oidcUser,
                             Model model) {
        try {
            //  accountClient.addBalance(oidcUser, currency, initialBalance);
        } catch (Exception e) {
            model.addAttribute("userAccountsErrors", e.getMessage());
            return "main";
        }
        return "redirect:/";
    }

    @PostMapping("/user/{login}/deleteBalance")
    public String deleteBalance(@RequestParam Currency currency,
                                @AuthenticationPrincipal OidcUser oidcUser,
                                Model model) {
        try {
            //accountClient.deleteBalance(oidcUser, currency);
        } catch (Exception e) {
            model.addAttribute("userAccountsErrors", e.getMessage());
            return "main";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout() {
        logger.info("Processing logout request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idToken = null;
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
            idToken = oidcUser.getIdToken().getTokenValue();
            logger.info("Found id_token for user: {}", oidcUser.getPreferredUsername());
        } else {
            logger.warn("No OAuth2 authentication found for logout");
        }

        // Очистка локальной сессии
        SecurityContextHolder.clearContext();

        // Формирование URL для Keycloak logout
        String redirectUri = "http://localhost:8081/login?logout";
        String logoutUrl = issuerUri + "/protocol/openid-connect/logout?post_logout_redirect_uri=" + redirectUri;
        if (idToken != null) {
            logoutUrl += "&id_token_hint=" + idToken;
        }
        logger.info("Redirecting to Keycloak logout URL: {}", logoutUrl);
        return "redirect:" + logoutUrl;
    }
}