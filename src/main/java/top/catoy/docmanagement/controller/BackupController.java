package top.catoy.docmanagement.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.catoy.docmanagement.domain.RecordPoint;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.service.BackupService;

import java.io.*;
/**
 * @description: 备份
 * @author: xjn
 * @create: 2019-06-05 22:23
 **/
@RestController
public class BackupController {
   @Autowired
   private BackupService backupService;

    @GetMapping("/backup")
    public ResponseBean backupDatabase(){
        return backupService.backupDatabase();
    }

    @GetMapping("/recover")
    public ResponseBean recover(RecordPoint recordPoint){
       return backupService.recover(recordPoint);
    }
}
