package top.catoy.docmanagement.controller;


import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.domain.Tag;
import top.catoy.docmanagement.domain.User;
import top.catoy.docmanagement.service.LogService;
import top.catoy.docmanagement.service.TagService;
import top.catoy.docmanagement.utils.JWTUtil;

import java.util.List;

@RestController
public class TagController {


    @Autowired
    private TagService tagService;

    @Autowired
    private LogService logService;

    @RequestMapping(value = "/public/createTags",method = RequestMethod.POST)
    public ResponseBean createTag(@RequestBody Tag tag){
        return new ResponseBean(ResponseBean.SUCCESS,"创建标签成功!",null);
    }

    @RequestMapping(value = "/public/getAllTags",method = RequestMethod.POST)
    public ResponseBean getAllTags(){
        List<Tag> tags = tagService.getAllTags();
        if(tags != null){
            return new ResponseBean(ResponseBean.SUCCESS,"查找成功!",tags);
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"查找失败!",null);
        }
    }


    @RequestMapping(value = "/public/updateTag",method = RequestMethod.POST)
    public ResponseBean updateTag(@RequestBody Tag tag){
        User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
        Tag tg = tagService.getTagByName(tag.getTagName());
        System.out.println(tg);
        if(tg.getIsuse() == 0){
            tg.setIsuse(1);
        }else {
            tg.setIsuse(0);
        }
        int result = tagService.updateTags(tg);
        if(result > 0 ){
            logService.insertLog(u.getUserId(), tg.getIsuse()==1?"恢复标签-"+tg.getTagName():"废弃标签-"+tg.getTagName(), "标签管理");
            return new ResponseBean(ResponseBean.SUCCESS,"修改成功",null);
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"修改失败",null);
        }
    }
}
