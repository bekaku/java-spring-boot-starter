package io.beka.service;

import io.beka.dao.TagsMapper;
import io.beka.model.Page;
import io.beka.model.data.Tags;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagsService {
    private TagsMapper tagsMapper;

    public TagsService(TagsMapper tagsMapper) {
        this.tagsMapper = tagsMapper;
    }

    public List<Tags> allPaging(Page page) {
        return tagsMapper.allPaging(page);
    }
}
