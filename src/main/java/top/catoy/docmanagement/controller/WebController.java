package top.catoy.docmanagement.controller;

import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.domain.User;
import top.catoy.docmanagement.service.UserService;
import top.catoy.docmanagement.utils.FileDownLoadUtil;
import top.catoy.docmanagement.utils.JWTUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@RestController
public class WebController {

    private static final Logger LOGGER = LogManager.getLogger(WebController.class);

    @Autowired
    private UserService userService;

    @Autowired
    public void setService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/public/login")
    public ResponseBean login(@RequestParam("username") String username,
                              @RequestParam("password")String password) {
        System.out.println("用户名:"+username+"密码:"+password);
        ResponseBean result = userService.Login(username,password);

        if(result.getMsg().equals("登录成功")){
                String token = JWTUtil.sign((User) result.getData(),password);
                return new ResponseBean(ResponseBean.SUCCESS, "登陆成功", JWTUtil.sign((User) result.getData(), password));
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"登录失败",null);
            }

    }

    @PostMapping("/public/getAllUsers")
    public ResponseBean getAllUsers(@Param("page") int page){
        ResponseBean result = userService.getAllUsers(page);
        if(result.getMsg().equals("查询成功")) {
            return new ResponseBean(ResponseBean.SUCCESS,"查询成功",result.getData());
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"查询失败",null);
        }
    }


    @PostMapping("/public/deleteUser")
//    @RequiresRoles("admin")
    public ResponseBean deleteUserById(@Param("userId") int userId) throws UnauthenticatedException {
        Subject subject = SecurityUtils.getSubject();
        if(subject.hasRole("admin")){
            int result = userService.deleteUserById(userId);
            if(result > 0 ){
                return new ResponseBean(ResponseBean.SUCCESS,"删除成功",null);
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"删除失败",null);
            }
        }else {
            return new ResponseBean(ResponseBean.ERROR,"你无权限进行此操作",null);
        }

    }

    @GetMapping("/article")
    public ResponseBean article() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return new ResponseBean(200, "You are already logged in", null);
        } else {
            return new ResponseBean(200, "You are guest", null);
        }
    }

    @GetMapping("/require_auth")
    @RequiresAuthentication
    public ResponseBean requireAuth() {
        return new ResponseBean(200, "You are authenticated", null);
    }

    @GetMapping("/require_role")
    @RequiresRoles("admin")
    public ResponseBean requireRole() {
        return new ResponseBean(200, "You are visiting require_role", null);
    }

    @GetMapping("/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public ResponseBean requirePermission() {
        return new ResponseBean(200, "You are visiting permission require edit,view", null);
    }

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseBean unauthorized() {
        return new ResponseBean(401, "Unauthorized", null);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void Download(HttpServletResponse res) {
        FileDownLoadUtil.Download(res);
    }
}
