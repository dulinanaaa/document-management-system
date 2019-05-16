package top.catoy.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.service.DocInfoService;

/**
 * @description:
 * @author: xjn
 * @create: 2019-05-15 20:29
 **/
@RestController
public class DocInfoController {

    @Autowired
    private DocInfoService docInfoService;

    @GetMapping("/public/getAllDocInfo")
    public ResponseBean getAllDocInfo(@RequestParam String currentPage,
                                   @RequestParam String pageSize){
        ResponseBean result = docInfoService.getAllDocInfo(Integer.parseInt(currentPage),Integer.parseInt(pageSize));
        return result;
    }
}
