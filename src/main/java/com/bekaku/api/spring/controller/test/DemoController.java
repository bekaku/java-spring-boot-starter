package com.bekaku.api.spring.controller.test;

import com.bekaku.api.spring.configuration.AppLogger;
import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.controller.api.BaseApiController;
import com.bekaku.api.spring.dto.Message;
import com.bekaku.api.spring.exception.ApiError;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.properties.LoggingFileProperties;
import com.bekaku.api.spring.service.AccessTokenService;
import com.bekaku.api.spring.service.UserService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.FileUtil;
import com.bekaku.api.spring.vo.LinkPreview;
import com.google.common.net.InternetDomainName;
import com.bekaku.api.spring.dto.UserRegisterRequest;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.queue.QueueSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping(path = "/test")
@RestController
@RequiredArgsConstructor
public class DemoController extends BaseApiController {

    Logger logger = LoggerFactory.getLogger(DemoController.class);
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

    @GetMapping("/testPercenRank")
    public ResponseEntity<Object> testPercenRank() {
        double[] values = {
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1
        };
        double targetValue = 1.0;

        double percentRank = AppUtil.calculatePercentRank(values, targetValue);
        System.out.println("The percent rank of " + targetValue + " in the values array is " + percentRank + "%");
        List<Double> doubles = new ArrayList<>();
        doubles.add(1.0);
        doubles.add(2.0);
        doubles.add(3.0);
        double[] nums = new double[doubles.size()];
        int i = 0;
        for (double v : doubles) {
            nums[i] = v;
            i++;
        }
//        for (int i = 0; i < nums.length; i++) {
//            nums[i] = i;
//        }

        throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, "message", "error"));
//        return this.responseEntity(new HashMap<String, Object>() {{
//            put("targetValue", targetValue);
//            put("percentRank", percentRank);
//            put("values", values);
//            put("nums", nums);
//
//        }}, HttpStatus.OK);
    }

    @GetMapping("/testArray2D")
    public ResponseEntity<Object> testArray2D(@RequestParam(name = "xLevel") int x, @RequestParam(name = "yLevel") int y) {
        return this.responseEntity(new HashMap<String, Object>() {{
            put("final", ConstantData.USER_LEVEL_CHART_TABLE[x][y]);
            put("myArray", ConstantData.USER_LEVEL_CHART_TABLE);
        }}, HttpStatus.OK);
    }

    @PostMapping("/testRequestBody")
    public void testRequestBody(@RequestBody() Map<String, String> body) {
        String name = body.get("name");
        logger.info("get body : {}", name);
    }

    @GetMapping("/currentUrlPath")
    public ResponseEntity<Object> currentUrlPath(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
        logger.info("DemoController > test/currentUrlPath");
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
                logger.warn("Unable to connect to extract domain name from : {}", url);
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

    @GetMapping("/testSendRabbitMq")
    public ResponseEntity<Object> testSendRabbitMq(@RequestParam(value = "message") String message) {
//        for (int i = 0; i < 100; i++) {
//            queueSender.calculateScore("calculateScore " + i);
//            queueSender.sendNotify("sendNotify " + i);
//            logger.info("calculateScore " + i);
//        }
//        queueSender.sendTopic(message);
//        queueSender.calculateScore(message);
//        queueSender.sendNotify(message);

//        queueSender.calculateScore();
//        queueSender.sendNotify();

//        queueSender.convertAndSend("teste-exchange", "routing-key-teste", "test message with teste-exchange");
        return this.responseEntity(HttpStatus.OK);
    }

    @GetMapping("/testReadJson")
    public ResponseEntity<Object> testReadJson() throws IOException {

        File file = jsonAcl.getFile();
        String content = new String(Files.readAllBytes(file.toPath()));
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            Object object = parser.parse(content);
            jsonArray = (JSONArray) object;
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        try {
//            File resource = new ClassPathResource("acl.json").getFile();
//            aclString = new String(Files.readAllBytes(resource.toPath()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        JSONArray finalJsonArray = jsonArray;
        return this.responseEntity(new HashMap<String, Object>() {{
            put("testReadJson", finalJsonArray);
        }}, HttpStatus.OK);
    }

    public String getJson(String path) {
        try {
            File file = ResourceUtils.getFile(path);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<Object> testGet(HttpServletRequest request,
                                          @RequestParam(name = "requestFrom", defaultValue = "", required = false) String requestFrom,
                                          @RequestHeader(value = ConstantData.USER_AGENT) String userAgent) {
        logger.info("Democontroller > testGet()");
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

    // broadcast websockect to client
    @GetMapping("/test-send-socket")
    public ResponseEntity<Object> testSendSocket() {
        this.broadcastChat(new Message("Bots", "Hello", ""));
        return this.responseEntity(HttpStatus.OK);
    }

    //    @MessageMapping(ConstantData.WEB_SOCKET_ENDPOINT_CHAT)
    public void broadcastChat(@Payload Message message) {
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
//        this.simpMessagingTemplate.convertAndSend("/topic/messagesWithSimpMessagingTemplate", new OutputMessage(message.getFrom(), message.getText(), time));
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

    //test upload file
    @PostMapping("/upload")
    public ResponseEntity<Object> singleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, "Please select a file to upload");
        }
        String yearMonthImages = FileUtil.getImagesYearMonthDirectory();
        String uploadPath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), yearMonthImages);
        String newName = FileUtil.generateFileName(FileUtil.getMultipartFileName(file));
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + newName);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }

        //resize image
        thumbnailatorResize(uploadPath, newName);

        //create thumbnail
        if (appProperties.getUploadImage().isCreateThumbnail()) {
            thumbnailatorCreateThumnail(uploadPath, newName);
        }

        String pathInDb = yearMonthImages + newName;
        return this.responseEntity(new HashMap<String, Object>() {{
            put("getFileSize", FileUtil.getFileSize(file));
            put("humanReadableByteCountSI", FileUtil.humanReadableByteCountSI(FileUtil.getFileSize(file)));
            put("getMimeType", FileUtil.getMimeType(file));
            put("originalName", FileUtil.getMultipartFileName(file));
            put("pathInDb", pathInDb);
            put("uploadPath", uploadPath);
            put("imageCdnUrl", FileUtil.generateCdnPath(appProperties.getCdnForPublic(), pathInDb, null));
            put("imageThumbnailCdnUrl", FileUtil.generateCdnPath(appProperties.getCdnForPublic(), FileUtil.generateThumbnailName(pathInDb, appProperties.getUploadImage().getThumbnailExname()), null));
        }}, HttpStatus.OK);
    }

    private void thumbnailatorResize(String uploadFile, String fileName) {
        try {
            int limitWidth = appProperties.getUploadImage().getLimitWidth();
            int limitHeight = appProperties.getUploadImage().getLimitHeight();
            BufferedImage originalImage = ImageIO.read(new File(uploadFile + fileName));

            //get width and height of image
            int imageWidth = originalImage.getWidth();
            int imageHeight = originalImage.getHeight();
            if (imageWidth > limitWidth || imageHeight > limitHeight) {
                BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getLimitWidth(), appProperties.getUploadImage().getLimitHeight(), 0.90);
                ImageIO.write(outputImage, "jpg", new File(uploadFile + fileName));
            }

        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }

    private void thumbnailatorCreateThumnail(String uploadFile, String fileName) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(uploadFile + fileName));
            BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getThumbnailWidth(), appProperties.getUploadImage().getThumbnailWidth(), 0.90);
            ImageIO.write(outputImage, "jpg", new File(uploadFile + FileUtil.generateThumbnailName(fileName, appProperties.getUploadImage().getThumbnailExname())));
        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }


}