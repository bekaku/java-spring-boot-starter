package com.bekaku.api.spring.controller.test;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.controller.api.BaseApiController;
import com.bekaku.api.spring.dto.UserRegisterRequest;
import com.bekaku.api.spring.logger.AppLogger;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.LoggingFileProperties;
import com.bekaku.api.spring.queue.QueueSender;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.UserService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@RequestMapping(path = "/test")
@RestController
@RequiredArgsConstructor
public class TestController extends BaseApiController {

    private static final Logger loginLog = LoggerFactory.getLogger("login-log");// config in log4j2.xml
    //    private final MailProperties mailProperties;
    private final AppProperties appProperties;
    private final LoggingFileProperties loggingFileProperties;
    private final AppLogger appLogger;
    private final I18n i18n;
    //    private final SimpMessagingTemplate simpMessagingTemplate;
    private final QueueSender queueSender;
//    private final RabbitTemplate queueSender;

    private final ServletContext servletContext;

    private final UserService userService;
    private final AccessTokenService accessTokenService;

    private final Executor asyncExecutor;

    @Value("${logging.file.path}")
    String logingFilePath;

    @Value("${custom.config.file}")
    String testConfigFile;

//    private final KafkaProducerService kafkaProducerService;

    private String getMetaTagContent(Document document, String cssQuery) {
        Element elm = document.select(cssQuery).first();
        if (elm != null) {
            return elm.attr("content");
        }
        return "";
    }

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

//    @GetMapping("/kafkaSend")
//    public ResponseEntity<Object> kafkaSend() {
//        log.info("kafkaSend");
//        kafkaProducerService.send("response-message", new ResponseMessage(HttpStatus.OK, "Test send KafKa"));
//
//        for (int i = 0; i < 15; i++) {
//            log.info("kafkaSend NO.:{}", i);
//            kafkaProducerService.send("test-thread", "Test send NO. " + i);
//        }
//        return this.responseEntity(HttpStatus.OK);
//    }

    @GetMapping("/check-virtual-thread")
    public CompletableFuture<String> getData() {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate long-running task
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Processed with virtual threads!";
        });
    }

    @PostMapping("/testRequestBody")
    public void testRequestBody(@RequestBody() Map<String, String> body) {
        String name = body.get("name");
        log.info("get body : {}", name);
    }

    @GetMapping("/currentUrlPath")
    public ResponseEntity<Object> currentUrlPath(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
        log.info("DemoController > test/currentUrlPath");
        return responseEntity(new HashMap<String, Object>() {{
            put("baseURL", baseURL);
            put(ConstantData.SERVER_TIMESTAMP, DateUtil.getLocalDateTimeNow());
        }}, HttpStatus.OK);
    }

    @GetMapping("/server-info")
    public String getServerInfo() {

        StringBuilder info = new StringBuilder();

        // Get server info
        info.append("Server Info:").append(servletContext.getServerInfo()).append("\n");

        // Check if Undertow classes are available
        boolean isUndertow = false;
        try {
            Class.forName("io.undertow.Undertow");
            isUndertow = true;
        } catch (ClassNotFoundException e) {
            isUndertow = false;
        }

        info.append("Undertow Available: ").append(isUndertow).append("\n");

        // Get JVM arguments
        info.append("JVM Arguments: ").append(System.getProperty("java.vm.args", "Not Available")).append("\n");

        // Get available processors
        info.append("Available Processors: ").append(Runtime.getRuntime().availableProcessors()).append("\n");

        return info.toString();
    }

    @GetMapping
    public ResponseEntity<Object> testGet(HttpServletRequest request,
                                          @RequestParam(name = "requestFrom", defaultValue = "", required = false) String requestFrom,
                                          @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {
        log.info("Democontroller > testGet()");
        return this.responseEntity(new HashMap<String, Object>() {{
            put("camelToSnake", AppUtil.camelToSnake("ApiClient"));
            put("i18nMessage", i18n.getMessage("message.args", "Chanavee", "From i18n util"));
            put("AUTHORIZATION", request.getHeader(ConstantData.AUTHORIZATION));
            put("ACCEPT_LANGUGE", request.getHeader(ConstantData.ACCEPT_LANGUGE));
            put("requestFrom", requestFrom);
            put("userAgent", userAgent);
            put("testConfigFile", testConfigFile);
        }}, HttpStatus.OK);
    }

    @PostMapping("/test-post")
    public ResponseEntity<Object> testPost(@Valid @RequestBody UserRegisterRequest registerDto) {
        return this.responseEntity(registerDto, HttpStatus.OK);
    }


    @GetMapping("/properties")
    public ResponseEntity<Object> testProperties() {
        return this.responseEntity(new HashMap<String, Object>() {{
            put("normal-properties", appProperties != null ? appProperties.getVersion() : null);
            put("object-properties", appProperties != null ? appProperties.getMailConfig() : null);
            put("list-properties", appProperties != null ? appProperties.getDefaultRecipients() : null);
            put("map-Properties", appProperties != null ? appProperties.getAdditionalHeaders() : null);
            put("object-list-properties", appProperties != null ? appProperties.getMenus() : null);
        }}, HttpStatus.OK);
    }

    @GetMapping("/test-page-loop")
    public void testLoopPaging(){
        Page<User> page;
        int pageNumber = 0;
        do {
            log.info("page:{}", pageNumber);
            page = userService.findAllPageBy(PageRequest.of(pageNumber++, 20));
            page.forEach(user -> {
                log.info("id:{}, email:{}", user.getId(), user.getEmail());
            });
        } while(!page.isEmpty());
    }
    @GetMapping("/async-process")
    public CompletableFuture<String> triggerAsync() {
        return userService.processAsyncTask()
                .thenApply(result -> "Controller got: " + result);
    }

    @GetMapping("/get-user-async")
    public CompletableFuture<List<User>> getAsyncUser() {
        return CompletableFuture.supplyAsync(userService::findAll, asyncExecutor);
//        return CompletableFuture.supplyAsync(()->userService.findAll(), asyncExecutor);
//        return CompletableFuture.supplyAsync(()->{
//            return userService.findAll();
//        }, asyncExecutor);
    }

    @GetMapping("/get-user-async2")
    public CompletableFuture<List<User>> getAsyncUser2() {
        return CompletableFuture.supplyAsync(userService::findAll, asyncExecutor)
                .exceptionally(ex -> {
                    throw new RuntimeException("Error fetching users", ex);
                });
    }

    /*
    @GetMapping
    public CompletableFuture<List<User>> getAllUsers() {
        return CompletableFuture.supplyAsync(userService::findAll, asyncExecutor);
    }

    @GetMapping("/{id}")
    public CompletableFuture<User> getUserById(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> userService.findById(id).orElse(null), asyncExecutor);
    }

    @PostMapping
    public CompletableFuture<User> createUser(@RequestBody User user) {
        return CompletableFuture.supplyAsync(() -> userService.save(user), asyncExecutor);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<Void> deleteUser(@PathVariable Long id) {
        return CompletableFuture.runAsync(() -> userService.deleteById(id), asyncExecutor);
    }
     */
}