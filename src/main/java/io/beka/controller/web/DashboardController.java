package io.beka.controller.web;

import io.beka.model.entity.Permission;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {

    private final MessageSource messageSource;

    public DashboardController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping("/welcome")
    public String welcome(ModelMap model){
        return "welcomes";
    }

    @RequestMapping("/theymeleaf")
    public String theymeleaf(ModelMap model){
        System.out.println("DashboardController > theymeleaf 555");
        Permission permission = new Permission();
        permission.setName("test");

        model.addAttribute("testHello",messageSource.getMessage("message", null, LocaleContextHolder.getLocale()));
        model.addAttribute("permission", permission);
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
