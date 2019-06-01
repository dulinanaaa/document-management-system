package top.catoy.docmanagement.service.impl;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.Log;
import top.catoy.docmanagement.domain.Tag;
import top.catoy.docmanagement.domain.User;
import top.catoy.docmanagement.mapper.TagMapper;
import top.catoy.docmanagement.service.LogService;
import top.catoy.docmanagement.service.TagService;
import top.catoy.docmanagement.utils.JWTUtil;

import java.util.List;


@Service
public class TagServiceImpl implements TagService {


    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private LogService logService;

    @Override
    public int insertTags(Tag tag) {

        return tagMapper.insertTag(tag);
    }

    @Override
    public int updateTags(Tag tag) {
        return tagMapper.updateTag(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.getAllTag();
    }

    @Override
    public int getIdByTagName(String name) {
        return tagMapper.getTagIdByName(name);
    }

    @Override
    public Tag getTagByName(String name) {
        return tagMapper.getTagByName(name);
    }

    @Override
    public Tag getTagById(int id) {
        return tagMapper.getTagById(id);
    }
}
