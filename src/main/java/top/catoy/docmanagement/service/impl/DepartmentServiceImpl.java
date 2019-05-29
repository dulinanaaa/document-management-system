package top.catoy.docmanagement.service.impl;

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

    @Override
    public String getDepartmentNameById(int id) {
        return departmentMapper.getDepartmentNameById(id);
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
                }
                return new ResponseBean(ResponseBean.SUCCESS,"查询成功",fatherList);

            }else {
                return new ResponseBean(ResponseBean.FAILURE,"查询失败",null);
            }

        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public List<Department> getTopDepartment() {
        return departmentMapper.getTopDepartmemt();
    }

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
        for (Department department : childList) {

                department.setChildren(getChild(department.getId(), fatherList));

        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }

        System.out.println(childList.toString());
        return childList;
    }

    @Override
    public ResponseBean delDepartmentById(int id) {
        try{
            List<Department> allList = departmentMapper.getAllDepartments();
            List<Department> childList = new ArrayList<>();
            getAllChildList(id,allList,childList);
            childList.add(departmentMapper.getDepartmentById(id));//当前部门和下级部门集合

            for(Department department:childList){
                if(department != null){
                    departmentMapper.deleteDepartmentById(department.getId());
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
           String departName = department.getName();
           Department dep = departmentMapper.getDepartmentByName(departName);
           if(dep == null){
               int sum = departmentMapper.insertDepartment(department);
               if("null".equals(department.getInstroduction())){
                   department.setInstroduction("");
               }
               if(sum > 0){
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
           int sum = departmentMapper.updateDepartment(department);
           System.out.println(sum);
           if(sum > 0){
               return new ResponseBean(ResponseBean.SUCCESS,"部门修改成功",null);
           }else{
               return new ResponseBean(ResponseBean.FAILURE,"部门修改失败",null);
           }
       }catch (Exception e){
           e.printStackTrace();
           return new ResponseBean(ResponseBean.ERROR,"失败",null);
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
