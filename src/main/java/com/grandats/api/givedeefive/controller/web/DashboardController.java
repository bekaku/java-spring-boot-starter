package com.grandats.api.givedeefive.controller.web;

import com.grandats.api.givedeefive.configuration.I18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    I18n i18n;

    @Value("${custom.config.file}")
    String testConfigFile;

    private final MessageSource messageSource;

    public DashboardController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/welcome")
    public String welcome(ModelMap model) {
        model.addAttribute("testGetConfig", testConfigFile);
        return "welcomes";
    }

    @GetMapping("/theymeleaf")
    public String theymeleaf(ModelMap model) {
        System.out.println("DashboardController > theymeleaf 555" + ", Locale : " + LocaleContextHolder.getLocale());
//        Permission permission = new Permission();
//        permission.setCode("test");

        model.addAttribute("testHello", messageSource.getMessage("message.args", new Object[]{"Chanavee", "Bekaku"}, LocaleContextHolder.getLocale()));
        model.addAttribute("testMessageI18nUtil", i18n.getMessage("message.args", "Chanavee", "From i18n util"));
//        model.addAttribute("permission", permission);
        model.addAttribute("permission", null);
        return "theymeleaf";
    }

//
//    @RequestMapping(value = "/dashboard/{username}")
//    public ResponseEntity dashboard(@RequestHeader(value = "Accept-Language", required = false) Locale locale, @PathVariable("username") String username){
//        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("userName", username);
//            put("dashboard",messageSource.getMessage("message", null, locale));
//        }});
//    }

//    @RequestMapping(value = "/welcome")
//    public ResponseEntity welCome(@RequestHeader(value = "Accept-Language", required = false) Locale locale){
//        return ResponseEntity.ok(new HashMap<String, Object>() {{
//            put("welcome",messageSource.getMessage("message", null, locale));
//        }});
//    }
}
