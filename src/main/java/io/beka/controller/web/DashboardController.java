package io.beka.controller.web;

import io.beka.model.entity.Permissions;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
public class DashboardController {

    private MessageSource messageSource;

    public DashboardController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping("/welcome")
    public String welcome(ModelMap model){
        return "welcomes";
    }

    @RequestMapping("/theymeleaf")
    public String theymeleaf(ModelMap model){
        Permissions permissions = new Permissions();
        permissions.setName("test");

        model.addAttribute("testHello",messageSource.getMessage("message", null, LocaleContextHolder.getLocale()));
        model.addAttribute("permissions", permissions);
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
