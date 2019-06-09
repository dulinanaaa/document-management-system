package top.catoy.docmanagement.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.RecordPoint;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.domain.User;
import top.catoy.docmanagement.mapper.RecordPointMapper;
import top.catoy.docmanagement.service.BackupService;
import top.catoy.docmanagement.service.LogService;
import top.catoy.docmanagement.utils.JWTUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: xjn
 * @create: 2019-06-08 11:21
 **/
@Service
public class BackupServiceImpl implements BackupService {
    @Value("${com.sxito.custom.windows-backupPath}")
    private String winBackPath;

    @Value("${com.sxito.custom.linux-backupPath}")
    private String linuxBackPath;

    @Value("${com.sxito.custom.windows-path}")
    private String windowsuploadPath;

    @Value("${com.sxito.custom.linux-path}")
    private String linuxuploadPath;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Autowired
    private RecordPointMapper recordPointMapper;

    @Autowired
    private LogService logService;

    @Override
    public ResponseBean backupDatabase(){
        long time = new java.util.Date().getTime();
        String backupPath = "";//备份目的路径
        String username = mysqlUsername;//数据库用户名
        String password = mysqlPassword;//数据库密码
        String dbName="documentManagementSystem";//备份的数据库名
        String docDirPath="";//需要备份的文档目录路径

        if(System.getProperty("os.name").contains("Windows")){
            backupPath = winBackPath + time;
            docDirPath = windowsuploadPath;
            System.out.println(backupPath);
        }else if(System.getProperty("os.name").contains("Linux")){
            backupPath = linuxBackPath + time;
            docDirPath = linuxuploadPath;
            System.out.println(backupPath);
        }else {
            return new ResponseBean(ResponseBean.ERROR,"系统不支持",null);
        }

        File rootDir = new File(backupPath);
        if (!rootDir.exists())
            rootDir.mkdirs();

        String cmd =  "mysqldump -u"+ username +"  -p"+password + " " + dbName;
        try {
            System.out.println(cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream(); // 把进程执行中的控制台输出信息写入.sql文件，即生成了备份文件。注：如果不对控制台信息进行读出，则会导致进程堵塞无法运行
            InputStreamReader xx = new InputStreamReader(in, "utf-8");  // 设置输出流编码为utf-8。这里必须是utf-8，否则从流中读入的是乱码

            String inStr;
            StringBuffer sb = new StringBuffer("");
            String outStr;
            // 组合控制台输出信息字符串
            BufferedReader br = new BufferedReader(xx);
            while ((inStr = br.readLine()) != null) {
                sb.append(inStr + "\r\n");
            }
            outStr = sb.toString();
            FileOutputStream fout = new FileOutputStream(new File(backupPath + "/" + dbName + ".sql")); // 要用来做导入用的sql目标文件：
            System.out.println(new File(backupPath + "/" + dbName + ".sql").getName()+"sql");
            OutputStreamWriter writer = new OutputStreamWriter(fout, "utf-8");
            writer.write(outStr);
            writer.flush();
            in.close();
            xx.close();
            br.close();
            writer.close();
            fout.close();
            FileUtils.copyDirectoryToDirectory(new File(docDirPath),new File(backupPath));
            RecordPoint recordPoint = new RecordPoint();
            recordPoint.setSqlFileName(dbName);
            recordPoint.setTimestamp(String.valueOf(time));
            recordPoint.setDocDirName(new File(docDirPath).getName());
            recordPoint.setBackupPath(backupPath);
            recordPointMapper.addRecordPoint(recordPoint);
            User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
            logService.insertLog(u.getUserId(), "备份数据库-目录:"+backupPath, "文件管理");
            return new ResponseBean(ResponseBean.SUCCESS,"数据备份成功,所在目录为"+backupPath,recordPoint);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public ResponseBean recover(RecordPoint recordPoint){
        String backupPath = "";//存档目录
        String sqlFileName = "";//存档目录中的sql脚本名
        String docDirName = "";//存档目录中的文件根目录名
        String docDirPath="";//现文档根目录路径
        String dbName="documentManagementSystem";//备份的数据库名
        String username = mysqlUsername;//数据库用户名
        String password = mysqlPassword;//数据库密码


        if( recordPoint != null
                && recordPoint.getSqlFileName() != null
                && recordPoint.getDocDirName() != null){
            sqlFileName = recordPoint.getSqlFileName();
            docDirName = recordPoint.getDocDirName();
            if(System.getProperty("os.name").contains("Window")){
                backupPath = winBackPath + recordPoint.getTimestamp() + "\\";
                docDirPath = windowsuploadPath;
            }else if(System.getProperty("os.name").contains("Linux")){
                backupPath = linuxBackPath + recordPoint.getTimestamp() + "/";
                docDirPath = linuxuploadPath;
            }else {
                return new ResponseBean(ResponseBean.ERROR,"系统不支持",null);
            }

            String cmd =  "mysql -u"+ username +"  -p"+password + " " + dbName;
            try {
                System.out.println(cmd);
                Process process = Runtime.getRuntime().exec(cmd);
                OutputStream outputStream = process.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(backupPath + sqlFileName + ".sql"),
                        "utf-8"));
                String str = null;
                StringBuffer sb = new StringBuffer();
                while ((str = br.readLine()) != null) {
                    sb.append(str + "\r\n");
                }
                str = sb.toString();
                System.out.println(str);
                OutputStreamWriter writer = new OutputStreamWriter(outputStream,
                        "utf-8");
                writer.write(str);
                writer.flush();
                outputStream.close();
                br.close();
                writer.close();
                System.out.println(backupPath + docDirName);
                FileUtils.deleteDirectory(new File(docDirPath));
                FileUtils.copyDirectory(new File(backupPath + docDirName),new File(docDirPath));
                User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
                String res;
                Date date = new Date(new Long(recordPoint.getTimestamp()));
                res = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                logService.insertLog(u.getUserId(), "恢复数据-到:"+res, "文件管理");
                return new ResponseBean(ResponseBean.SUCCESS,"数据恢复成功",null);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseBean(ResponseBean.ERROR,"错误",null);
            }
        }else{
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public ResponseBean getAllRecordPoint(int currentPage, int pageSize) {
       try {
           PageHelper.startPage(currentPage, pageSize);
           List<RecordPoint> recordPoints = recordPointMapper.getAllRecordPoints();
           if(recordPoints != null){
               PageInfo<RecordPoint> pageInfo = new PageInfo<>(recordPoints);
               return new ResponseBean(ResponseBean.SUCCESS,"查询成功",pageInfo);
           }else {
               return new ResponseBean(ResponseBean.FAILURE,"查询失败",null);
           }
       }catch (Exception e){
           e.printStackTrace();
           return new ResponseBean(ResponseBean.ERROR,"错误",null);
       }
    }
}
