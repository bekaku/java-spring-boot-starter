package com.bekaku.api.spring.controller.test;

import com.bekaku.api.spring.configuration.AppLogger;
import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.controller.api.BaseApiController;
import com.bekaku.api.spring.dto.UserRegisterRequest;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.LoggingFileProperties;
import com.bekaku.api.spring.queue.QueueSender;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.UserService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.vo.LinkPreview;
import com.google.common.net.InternetDomainName;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping(path = "/test")
@RestController
@RequiredArgsConstructor
public class DemoController extends BaseApiController {

    private static final Logger loginLog = LoggerFactory.getLogger("login-log");// config in log4j2.xml
    //    private final MailProperties mailProperties;
    private final AppProperties appProperties;
    private final LoggingFileProperties loggingFileProperties;
    private final ModelMapper modelMapper;
    private final AppLogger appLogger;
    private final I18n i18n;
    //    private final SimpMessagingTemplate simpMessagingTemplate;
    private final QueueSender queueSender;
//    private final RabbitTemplate queueSender;

    @Autowired
    private UserService userService;
    @Autowired
    private AccessTokenService accessTokenService;

    @Value("${logging.file.path}")
    String logingFilePath;

    @Value("${custom.config.file}")
    String testConfigFile;
    @Value("classpath:/acl.json")
    private Resource jsonAcl;

    private String getMetaTagContent(Document document, String cssQuery) {
        Element elm = document.select(cssQuery).first();
        if (elm != null) {
            return elm.attr("content");
        }
        return "";
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

    @GetMapping("/testGetOgMeta")
    public ResponseEntity<Object> testGetOgMeta(@RequestParam(value = "url") String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        Document document;
        LinkPreview preview;
        try {
            document = Jsoup.connect(url).get();
            String title = getMetaTagContent(document, "meta[name=title]");
            String desc = getMetaTagContent(document, "meta[name=description]");
            String ogUrl = getMetaTagContent(document, "meta[property=og:url]");
            String ogTitle = getMetaTagContent(document, "meta[property=og:title]");
            String ogDesc = getMetaTagContent(document, "meta[property=og:description]");
            String ogImage = getMetaTagContent(document, "meta[property=og:image]");
            String ogImageAlt = getMetaTagContent(document, "meta[property=og:image:alt]");
            String domain = ogUrl;
            try {
                domain = InternetDomainName.from(new URL(ogUrl).getHost()).topPrivateDomain().toString();
            } catch (Exception e) {
                log.warn("Unable to connect to extract domain name from : {}", url);
            }
            preview = new LinkPreview(domain, url,
                    !ObjectUtils.isEmpty(ogTitle) ? ogTitle : title,
                    !ObjectUtils.isEmpty(ogDesc) ? ogDesc : desc,
                    ogImage,
                    ogImageAlt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return this.responseEntity(preview, HttpStatus.OK);
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



}