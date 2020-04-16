package io.beka.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;

@RestController
@RequestMapping(path = "/")
public class DashboardController {

    private MessageSource messageSource;

    public DashboardController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity index(){
        System.out.println("This is index");
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("bekaku", "Hello Springboot ccc");//this is main method if not define RequestionMapping value
        }});
    }


    @RequestMapping(value = "/dashboard/{username}")
    public ResponseEntity dashboard(@RequestHeader(value = "Accept-Language", required = false) Locale locale, @PathVariable("username") String username){
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("userName", username);
            put("dashboard",messageSource.getMessage("message", null, locale));
        }});
    }
}
