package top.catoy.docmanagement.service;

import top.catoy.docmanagement.domain.Department;
import top.catoy.docmanagement.domain.ResponseBean;

import java.util.List;

public interface DepartmentService {

     String getDepartmentNameById(int id);

     String getDepartmentNumberById(int id);

     int getDepartmentIdByName(String name);

     ResponseBean getAllDepartments();

     ResponseBean getDepartmentsTree();

     public List<Department> getTopDepartment();

     List<Department> getChild(int id, List<Department> fatherList);

     ResponseBean delDepartmentById(int id);

     ResponseBean addDepartment(Department department);

     ResponseBean editDepartment(Department department);


}
