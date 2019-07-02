package top.catoy.docmanagement.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.catoy.docmanagement.domain.FileSource;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.service.FileSourceService;

import java.util.List;

@RestController
public class FileSourceController {

//    public ResponseBean

    @Autowired
    private FileSourceService fileSourceService;

    @RequestMapping(value = "/addFileSource",method = RequestMethod.POST)
    public ResponseBean addFileSource(@RequestBody FileSource fileSource){
        try {
            System.out.println("fileSource"+fileSource);
            fileSourceService.insertFileSource(fileSource);
            return new ResponseBean(ResponseBean.SUCCESS,"添加成功!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(ResponseBean.FAILURE,"添加失败",null);
        }
    }


    @RequestMapping(value = "/deleteFileSource",method = RequestMethod.POST)
    public ResponseBean deleteFileSource(@RequestBody FileSource fileSource){
        try {
            fileSourceService.deleteFileSource(fileSource);
            return new ResponseBean(ResponseBean.SUCCESS,"删除成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(ResponseBean.FAILURE,"删除失败",null);
        }
    }


    @RequestMapping(value = "/getAllFileSource",method = RequestMethod.POST)
    public ResponseBean findAllFileSource(){
        try {
            List<FileSource> fileSources = fileSourceService.getAllFileSource();
            return new ResponseBean(ResponseBean.SUCCESS,"查找成功",fileSources);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(ResponseBean.SUCCESS,"查找失败!",null);
        }
    }

}
