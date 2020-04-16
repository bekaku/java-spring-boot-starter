package io.beka.controller;


import io.beka.model.Page;
import io.beka.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(path = "tags")
public class TagsController {
    private TagsService tagsService;

    @Autowired
    public TagsController(TagsService tagsQueryService) {
        this.tagsService = tagsQueryService;
    }

    @GetMapping
    public ResponseEntity getTags(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                  @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("tagsList", tagsService.allPaging(new Page(offset, limit)));
        }});
    }
}
