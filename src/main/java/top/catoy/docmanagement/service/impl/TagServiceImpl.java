package top.catoy.docmanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.Tag;
import top.catoy.docmanagement.mapper.TagMapper;
import top.catoy.docmanagement.service.TagService;


@Service
public class TagServiceImpl implements TagService {


    @Autowired
    private TagMapper tagMapper;

    @Override
    public int insertTags(Tag tag) {
        return tagMapper.insertTag(tag);
    }
}
