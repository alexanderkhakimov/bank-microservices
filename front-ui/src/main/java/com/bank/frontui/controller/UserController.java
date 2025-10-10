//package com.bank.frontui.controller;
//
//import com.bank.frontui.model.AccountBalance;
//import com.bank.frontui.model.Currency;
//import com.bank.frontui.service.AccountClient;
//import com.bank.frontui.service.KeycloakAdminService;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDate;
//import java.time.Period;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Controller
//@Slf4j
//@RequestMapping("api/front-ui/v1")
//public class UserController {
//    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
//    @Autowired
//    private AccountClient accountClient;
//
//    @Autowired
//    private KeycloakAdminService keycloakAdminService;
//
//    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
//    private String issuerUri;
//
//    @PostMapping("/user/{login}/editPassword")
//    public String editPassword(@PathVariable String login,
//                               @RequestParam String password,
//                               @RequestParam String confirm_password,
//                               Model model) {
//        if (!password.equals(confirm_password)) {
//            model.addAttribute("passwordErrors", "Пароли не совпадают");
//            return "main";
//        }
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserLogin = null;
//        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
//            var oidcUser = (OidcUser) oAuth2AuthenticationToken.getPrincipal();
//            currentUserLogin = oidcUser.getPreferredUsername();
//        }
//
//        if (currentUserLogin == null || !currentUserLogin.equals(login)) {
//            model.addAttribute("unauthenticated", "Пользователь не авторизован!");
//            return "main";
//        }
//        try {
//            keycloakAdminService.updatePasswordKeycloak(login, password);
//            accountClient.updatePassword(login, password);
//            return "redirect:/";
//        } catch (Exception e) {
//            model.addAttribute("userAccountsErrors", e.getMessage());
//            return "main";
//        }
//    }
//
//    @PostMapping("/user/{login}/editUserAccounts")
//    public String editUserAccounts(@PathVariable String login,
//                                   @RequestParam (required = false) List<String> account ,
//                                   @RequestParam (required = false) String name,
//                                   @RequestParam (required = false) String birthdate,
//                                   Model model) {
//        LocalDate dob = LocalDate.parse(birthdate);
//        if (Period.between(dob, LocalDate.now()).getYears() < 18) {
//            model.addAttribute("userAccountsErrors", "Возраст должен быть старше 18");
//            return "main";
//        }
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserLogin = null;
//        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
//            var oidcUser = (OidcUser) oAuth2AuthenticationToken.getPrincipal();
//            currentUserLogin = oidcUser.getPreferredUsername();
//        }
//
//        if (currentUserLogin == null || !currentUserLogin.equals(login)) {
//            model.addAttribute("unauthenticated", "Пользователь не авторизован!");
//            return "main";
//        }
//        List<AccountBalance> newBalance = Arrays.stream(Currency.values())
//                .map(currency -> {
//                    var isExists = account != null && account.contains(currency.name());
//                    return AccountBalance.builder()
//                            .currency(currency)
//                            .balance(0.0)
//                            .isExists(isExists).build();
//                })
//                .toList();
//        try {
//            keycloakAdminService.updateAccount(login,name,dob);
//            accountClient.updateAccount(login,newBalance,name,dob);
//            return "redirect:/";
//        } catch (Exception e) {
//            model.addAttribute("userAccountsErrors", e.getMessage());
//            return "main";
//        }
//    }
//
//    @PostMapping("/user/{login}/addBalance")
//    public String addBalance(@RequestParam Currency currency,
//                             @RequestParam double initialBalance,
//                             @AuthenticationPrincipal OidcUser oidcUser,
//                             Model model) {
//        try {
//            //  accountClient.addBalance(oidcUser, currency, initialBalance);
//        } catch (Exception e) {
//            model.addAttribute("userAccountsErrors", e.getMessage());
//            return "main";
//        }
//        return "redirect:/";
//    }
//
//    @PostMapping("/user/{login}/deleteBalance")
//    public String deleteBalance(@RequestParam Currency currency,
//                                @AuthenticationPrincipal OidcUser oidcUser,
//                                Model model) {
//        try {
//            //accountClient.deleteBalance(oidcUser, currency);
//        } catch (Exception e) {
//            model.addAttribute("userAccountsErrors", e.getMessage());
//            return "main";
//        }
//        return "redirect:/";
//    }
//}