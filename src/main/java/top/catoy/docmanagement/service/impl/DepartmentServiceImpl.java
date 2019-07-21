package top.catoy.docmanagement.service.impl;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.Department;
import top.catoy.docmanagement.domain.DocInfo;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.domain.User;
import top.catoy.docmanagement.mapper.DepartmentMapper;
import top.catoy.docmanagement.mapper.DocInfoMapper;
import top.catoy.docmanagement.mapper.UserMapper;
import top.catoy.docmanagement.service.DepartmentService;
import top.catoy.docmanagement.service.LogService;
import top.catoy.docmanagement.utils.JWTUtil;

import java.util.ArrayList;
import java.util.List;


@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DocInfoMapper docInfoMapper;

    @Autowired
    private LogService logService;

    private int deptDocNum = 0;

    @Override
    public String getDepartmentNameById(int id) {
        return departmentMapper.getDepartmentNameById(id);
    }

    @Override
    public String getDepartmentNumberById(int id) {
        return departmentMapper.getDepartmentNumberById(id);
    }

    public int getDepartmentIdByName(String name){
        return departmentMapper.getDepartmentIdByName(name);
    }

    @Override
    public ResponseBean getAllDepartments() {
        try {
          List<Department> departments = departmentMapper.getAllDepartments();
          if(departments != null){
              return new ResponseBean(ResponseBean.SUCCESS,"查询成功",departments);
          }else{
              return new ResponseBean(ResponseBean.FAILURE,"查询失败",null);
          }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }



    @Override
    public ResponseBean getDepartmentsTree() {
        try {
            List<Department> departments = departmentMapper.getAllDepartments();
            List<Department> fatherList = new ArrayList<>();
            if(departments != null){
                departments.forEach((department) ->{
                    if (department.getParent_id() == 0){
                        fatherList.add(department);
                    }
                });
                for (Department department : fatherList) {
                    department.setChildren(getChild(department.getId(), departments));
                    department.setDocNum(docInfoMapper.getDocInfoNumByDepartmentId(department.getId()));
                    getdeptTotalNum(department);

                    List<DocInfo> docInfos = docInfoMapper.getDocByDepartmentId(department.getId());
                    int pageNum = 0;
                    if(docInfos != null && docInfos.size()>0){
                        for(DocInfo d:docInfos){
                            pageNum = pageNum + Integer.parseInt(d.getPageNum());
                        }
                    }
                    department.setDocPageNum(pageNum);
                    getdeptTotalPageNum(department);
                }
                return new ResponseBean(ResponseBean.SUCCESS,"查询成功",fatherList);

            }else {
                return new ResponseBean(ResponseBean.FAILURE,"查询失败",null);
            }

        }catch (RuntimeException r){
            r.printStackTrace();
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public List<Department> getTopDepartment() {
        return departmentMapper.getTopDepartmemt();
    }

    /**
     * 得到部门的子部门
     * @param id
     * @param fatherList
     * @return
     */
    @Override
    public List<Department> getChild(int id, List<Department> fatherList) {
        List<Department> childList = new ArrayList<>();
        int count = 0;
        for (Department department : fatherList) {
            // 遍历所有节点，将父id与传过来的id比较
            if (department.getParent_id()== id) {
                department.setSort(count);
                childList.add(department);
                count++;
            }
        }
        // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        for (Department department : childList) {
            department.setChildren(getChild(department.getId(), fatherList));
            department.setDocNum(docInfoMapper.getDocInfoNumByDepartmentId(department.getId()));
            List<DocInfo> docInfos = docInfoMapper.getDocByDepartmentId(department.getId());
            int pageNum = 0;
            if(docInfos != null && docInfos.size()>0){
                for(DocInfo d:docInfos){
                    pageNum = pageNum + Integer.parseInt(d.getPageNum());
                }
            }
            department.setDocPageNum(pageNum);
        }
        return childList;
    }

    @Override
    public ResponseBean delDepartmentById(int id) {
        try{
            User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
            List<Department> allList = departmentMapper.getAllDepartments();
            List<Department> childList = new ArrayList<>();
            getAllChildList(id,allList,childList);
            Department delDepartment = departmentMapper.getDepartmentById(id);
            childList.add(delDepartment);//当前部门和下级部门集合

            for(Department department:childList){
                if(department != null){
                    departmentMapper.deleteDepartmentById(department.getId());
                    logService.insertLog(u.getUserId(), "删除部门-"+department.getName(), "部门管理");
                    List<User> users = userMapper.getUsersByDepartmentId(department.getId());
                    List<DocInfo> docInfos = docInfoMapper.getDocByDepartmentId(department.getId());
                    for(User user:users){
                        if(user != null){
                            user.setDepartmentId(-1);
                            userMapper.updateUser(user);
                        }
                    }
                    for(DocInfo docInfo:docInfos){
                        if(docInfo != null){
                            docInfo.setDepartmentId(-1);
                            docInfoMapper.updateDocInfo(docInfo);
                        }
                    }
                }
            }
            return new ResponseBean(ResponseBean.SUCCESS,"删除部门成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(ResponseBean.FAILURE,"错误",null);
        }
    }

    @Override
    public ResponseBean addDepartment(Department department) {
       try {
           User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
           String departName = department.getName();
           Department dep = departmentMapper.getDepartmentByName(departName);
           if(dep == null){
               int sum = departmentMapper.insertDepartment(department);
               if("null".equals(department.getInstroduction())){
                   department.setInstroduction("");
               }
               if(sum > 0){
                   logService.insertLog(u.getUserId(), "增加部门-"+department.getName(), "部门管理");
                   return new ResponseBean(ResponseBean.SUCCESS,"部门添加成功",null);
               }else {
                   return new ResponseBean(ResponseBean.FAILURE,"部门添加失败",null);
               }

           }else{
               return new ResponseBean(ResponseBean.FAILURE,"部门已存在",null);
           }
       }catch (Exception e){
           e.printStackTrace();
           return new ResponseBean(ResponseBean.ERROR,"错误",null);
       }

    }

    @Override
    public ResponseBean editDepartment(Department department) {
       try{
           User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
           int sum = departmentMapper.updateDepartment(department);
           System.out.println(sum);
           if(sum > 0){
               logService.insertLog(u.getUserId(), "修改部门-"+department.getName(), "部门管理");
               return new ResponseBean(ResponseBean.SUCCESS,"部门修改成功",null);
           }else{
               return new ResponseBean(ResponseBean.FAILURE,"部门修改失败",null);
           }
       }catch (Exception e){
           e.printStackTrace();
           return new ResponseBean(ResponseBean.ERROR,"失败",null);
       }
    }

    /**
     * 得到子部门的文件数
     * @param department
     * @return
     */
    public int getdeptTotalNum(Department department){
        List<Department> childList = department.getChildren();
        if(childList == null){
            //递归出口
            department.setDocTotalNum(department.getDocNum());
            return department.getDocTotalNum();
        }else {
            for(Department d:childList){
                department.setDocTotalNum(department.getDocTotalNum()+getdeptTotalNum(d));
            }
            department.setDocTotalNum(department.getDocTotalNum() + department.getDocNum());
            return department.getDocTotalNum();
        }
    }

    /**
     * 得到子部门的页数
     * @param department
     * @return
     */
    public int getdeptTotalPageNum(Department department){
        List<Department> childList = department.getChildren();
        if(childList == null){
            //递归出口
            department.setDocTotalPagenum(department.getDocPageNum());
            System.out.println("docpageNum"+department.getDocTotalNum());
            return department.getDocTotalPagenum();
        }else {
            for(Department d:childList){
                department.setDocTotalPagenum(department.getDocTotalPagenum()+getdeptTotalPageNum(d));
            }
            department.setDocTotalPagenum(department.getDocTotalPagenum() + department.getDocPageNum());
            return department.getDocTotalPagenum();
        }
    }


    public void getAllChildList(int id,List<Department> allList,List<Department> childList){
        List<Department> list;
        list = getChild(id,allList);
        if(list != null){
            for(Department department:list){
                childList.add(department);
                if(department.getChildren() != null){
                    getAllChildList(department.getId(),allList,childList);
                }
            }
        }
    }
}
