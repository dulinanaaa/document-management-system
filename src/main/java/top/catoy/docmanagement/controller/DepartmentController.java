package top.catoy.docmanagement.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.catoy.docmanagement.domain.Department;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.mapper.DepartmentMapper;
import top.catoy.docmanagement.service.DepartmentService;
import top.catoy.docmanagement.utils.JWTUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: xjn
 * @create: 2019-05-06 18:31
 **/
@RestController
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentMapper departmentMapper;

    @GetMapping("/admin/getAllDepartments")
    public ResponseBean getDepartmentsTree(){
        ResponseBean result = departmentService.getDepartmentsTree();
        return result;
    }


    @RequestMapping(value = "/admin/getTopDepartment",method = RequestMethod.POST)
    public ResponseBean getTopDepartment(){
        List list = departmentService.getTopDepartment();
        if(list != null){
            return new ResponseBean(ResponseBean.SUCCESS,"查找成功!",list);
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"查找失败",null);
        }
    }

    @GetMapping(value = "/admin/getMyChildDepartments")
    public ResponseBean getMyChildDepartments(){
        List<Department> departments = departmentMapper.getAllDepartments();
        Department rootDepartment;
        int departmentId = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal()).getDepartmentId();
        List list = departmentService.getChild(departmentId,departments);
        rootDepartment = departmentMapper.getDepartmentById(departmentId);
        if(list != null){
            rootDepartment.setChildren(list);
        }
        List<Department> rootList = new ArrayList<>();
        rootList.add(rootDepartment);
        if(list != null){
            return new ResponseBean(ResponseBean.SUCCESS,"查找成功!",rootList);
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"查找失败",null);
        }
    }

    @RequestMapping(value = "/admin/addDepartment",method =RequestMethod.POST)
    public ResponseBean addDepartment(@RequestBody Department department ){
        System.out.println(department.toString());
        ResponseBean result = departmentService.addDepartment(department);
        return result;
    }



    @RequestMapping(value = "/admin/getDepartmentList",method = RequestMethod.POST)
    public ResponseBean getDepartmentList(){
        return null;
    }

    @RequestMapping(value = "/admin/delDepartmentById",method =RequestMethod.GET)
    public ResponseBean delDepartmentById(@RequestParam int departmentId){
        ResponseBean result = departmentService.delDepartmentById(departmentId);
        return result;
    }

    @RequestMapping(value = "/admin/editDepartment",method =RequestMethod.POST)
    public ResponseBean editDepartment(@RequestBody Department department){
        ResponseBean result = departmentService.editDepartment(department);
        return result;
    }

}
