package top.catoy.docmanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.mapper.DepartmentMapper;
import top.catoy.docmanagement.service.DepartmentService;


@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public String getDepartmentNameById(int id) {
        return departmentMapper.getDepartmentNameById(id);
    }
}
