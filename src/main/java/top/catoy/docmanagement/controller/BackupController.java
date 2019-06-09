package top.catoy.docmanagement.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/admin/backup")
    public ResponseBean backupDatabase(){
        return backupService.backupDatabase();
    }

    @PostMapping("/admin/recover")
    public ResponseBean recover(@RequestBody RecordPoint recordPoint){
       return backupService.recover(recordPoint);
    }

    @GetMapping("/admin/getAllRecordPoint")
    public ResponseBean getAllRecordPoint(@RequestParam String currentPage,
                                          @RequestParam String pageSize){
        return backupService.getAllRecordPoint(Integer.parseInt(currentPage),Integer.parseInt(pageSize));
    }
}
