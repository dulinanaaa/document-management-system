package top.catoy.docmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.catoy.docmanagement.domain.ResponseBean;

import java.io.File;

/**
 * @description: 备份
 * @author: xjn
 * @create: 2019-06-05 22:23
 **/
@RestController
public class BackupController {
    @GetMapping("/backupSql")
    public ResponseBean backupSql(){
        String filePath="D:\\数据库文件\\";
        String dbName="documentManagementSystem";//备份的数据库名
        String username="root";//用户名
        String password="123456";//密码
        File uploadDir = new File(filePath);
        if (!uploadDir.exists())
            uploadDir.mkdirs();

        String cmd =  "mysqldump -u "+ username +"  -p"+password + " " + dbName + " -r "
                + filePath + "/" + dbName+new java.util.Date().getTime()+ ".sql";
        try {
            System.out.println(cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println("备份数据库成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseBean(ResponseBean.SUCCESS,"",null);
    }
}
