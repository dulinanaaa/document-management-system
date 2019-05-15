package top.catoy.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.service.DepartmentService;

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





    @RequestMapping(value = "/admin/getDepartmentList",method = RequestMethod.POST)
    public ResponseBean getDepartmentList(){
        return null;
    }
}
