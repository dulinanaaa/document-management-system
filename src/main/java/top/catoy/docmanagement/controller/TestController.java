package top.catoy.docmanagement.controller;

import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.catoy.docmanagement.domain.ResponseBean;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Map;

@RestController
public class TestController {


    private String serverPath = "classpath:\\static\\";

    @RequestMapping(value = "/testapi",method = RequestMethod.POST)
    @RequiresRoles("admin")
    public ResponseBean TestApi(String user){
        return new ResponseBean(ResponseBean.SUCCESS,"成功",null);
    }


    @RequestMapping(value = "/permission",method = RequestMethod.GET)
    public void TestPermission(){
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        System.out.println(subject.hasRole("管理员"));
    }

    @RequestMapping("/getdemo")
    public void getdemo(){
        System.out.println(111111);
    }

    @RequestMapping(value = "/getRootPath",method = RequestMethod.GET)
    public void getRootPath(HttpServletRequest request){
//        System.out.println("path:"+request.getSession().getServletContext().getContextPath());
        File file = new File(serverPath);
        System.out.println(serverPath);
        System.out.println("isexists:"+file.isDirectory());
//        System.out.println();
    }


    @RequestMapping(value = "/moveFile",method = RequestMethod.GET)
    public void moveFile(){
        File soure = new File("C:\\Users\\hp\\Desktop\\赛题13_建达科技_投诉线索智能筛查系统\\13_S1_方案概要\\next.pdf");
        File target = new File("D:\\temp\\next.pdf");
        soure.renameTo(target);
    }

//    public String fileName = "";
//
//    @RequestMapping(value = "/getFileName",method = RequestMethod.GET)
//    public void getFileName(@RequestParam("fileName")String name){
//        System.out.println(name);
//        this.fileName = name;
//    }


//    @RequestMapping(value = "/pdfPreView",method = RequestMethod.GET)
//    public void pdfStreamHandler(@RequestParam("fileName") String fileName, HttpServletRequest request, HttpServletResponse response){
////        System.out.println(fileName+"-------------***-");
////        System.out.println(this.fileName);
////        File file = new File("C:\\Users\\hp\\Desktop\\赛题13_建达科技_投诉线索智能筛查系统\\13_S1_方案概要\\"+this.fileName);
//        System.out.println(file.exists());
//        if (file.exists()){
//            byte[] data = null;
//            try {
//                FileInputStream input = new FileInputStream(file);
//                data = new byte[input.available()];
//                input.read(data);
//                response.getOutputStream().write(data);
//                input.close();
//            } catch (Exception e) {
//
//            }
//        }else{
//            return;
//        }
//
//    }
}

