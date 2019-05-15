package top.catoy.docmanagement.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.*;
import top.catoy.docmanagement.mapper.AnnexMapper;
import top.catoy.docmanagement.mapper.DepartmentMapper;
import top.catoy.docmanagement.mapper.DocInfoMapper;
import top.catoy.docmanagement.service.DepartmentService;
import top.catoy.docmanagement.service.DocInfoService;
import top.catoy.docmanagement.utils.JWTUtil;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;


@Service
public class DocInfoServiceImpl implements DocInfoService {


    @Autowired
    private DocInfoMapper docInfoMapper;

    @Autowired
    private AnnexMapper annexMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public int insertDocInfo(DocInfo docInfo) {
        return docInfoMapper.insertDocInfo(docInfo);
    }

    @Override
    public ResponseBean getAllDocInfo(int currentPage, int pageSize) {
        try {
            List<DocInfo> docInfos = new ArrayList<>();
            int departmentId = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal()).getDepartmentId();
            List<Department> departmentList = departmentMapper.getAllDepartments();
            getChildDocInfo(2,departmentList,docInfos);
            System.out.println("++++++++++++++++++++"+departmentId);
            System.out.println(docInfos.toString());
//            for(int i=0;i<childList.size();i++){
//                System.out.println(childList.get(i).getName());
//            }
//            for(Department department:childList){
//                int id = department.getId();
//                List<DocInfo> list = docInfoMapper.getDocByDepartmentId(id);
//                if(list != null && list.size()>0){
//                    docInfos.addAll(list);
//                }
//            }
            PageHelper.startPage(currentPage, pageSize);

            if(docInfos!=null){
                docInfos.forEach((docInfo) -> {
                    List<Annex> annexes = annexMapper.getAnnexsByDocId(docInfo.getDocId());
                    String departmentName = departmentMapper.getDepartmentNameById(docInfo.getDepartmentId());
                    if(annexes !=null && annexes.size()>0){
                        docInfo.setAnnexes(annexes);
                    }
                    if(departmentName !=null && departmentName.length()>0){
                        docInfo.setDepartmentName(departmentName);
                    }
                });
                PageInfo<DocInfo> pageInfo = new PageInfo<>(docInfos);
                return new ResponseBean(ResponseBean.SUCCESS,"查询成功",pageInfo);
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"查询失败",null);
            }
        }catch (RuntimeException r){
            r.printStackTrace();
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public List<Department> getChildDocInfo(int id, List<Department> fatherList,List<DocInfo> docInfos) {
        List<Department> childList = new ArrayList<>();
        for (Department department : fatherList) {
            // 遍历所有节点，将父id与传过来的id比较
            if (department.getParent_id()== id) {
                List<DocInfo> list = docInfoMapper.getDocByDepartmentId(department.getId());
                if(list != null && list.size()>0){
                    docInfos.addAll(list);
                    System.out.println();
                    System.out.println(list.toString());
                }
                childList.add(department);
            }
        }
        for (Department department : childList) {
            department.setChildren(getChildDocInfo(department.getId(), fatherList,docInfos));
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        System.out.println(childList.toString());
        return childList;
    }
}
