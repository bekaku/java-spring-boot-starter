package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.vo.LinkPreview;
import com.google.common.net.InternetDomainName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

@Slf4j
@RequestMapping(path = "/api/public")
@RestController
@RequiredArgsConstructor
public class PublicController extends BaseApiController {
    private final I18n i18n;


    private String getMetaTagContent(Document document, String cssQuery) {
        Element elm = document.select(cssQuery).first();
        if (elm != null) {
            return elm.attr("content");
        }
        return "";
    }

    @GetMapping("/getOgMeta")
    public LinkPreview getOgMeta(@RequestParam(value = "url") String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        Document document;
        LinkPreview preview = null;
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
//                this.throwError(HttpStatus.OK, i18n.getMessage("error.error"), "Unable to connect to extract domain name from :" + url);
            }
            preview = new LinkPreview(domain, url,
                    !ObjectUtils.isEmpty(ogTitle) ? ogTitle : title,
                    !ObjectUtils.isEmpty(ogDesc) ? ogDesc : desc,
                    ogImage,
                    ogImageAlt);
        } catch (IOException e) {
//            this.throwError(HttpStatus.OK, i18n.getMessage("error.error"), e.getMessage());
        }

        return preview;
    }



}
