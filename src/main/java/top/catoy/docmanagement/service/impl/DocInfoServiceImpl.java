package top.catoy.docmanagement.service.impl;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.*;
import top.catoy.docmanagement.mapper.AnnexMapper;
import top.catoy.docmanagement.mapper.DepartmentMapper;
import top.catoy.docmanagement.mapper.DocInfoMapper;
import top.catoy.docmanagement.service.DepartmentService;
import top.catoy.docmanagement.service.DocInfoService;
import top.catoy.docmanagement.utils.JWTUtil;

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
    public int getDocId(DocInfo docInfo) {
        return docInfoMapper.getDocId(docInfo);
    }
    @Override
    public ResponseBean getAllDocInfo(int currentPage, int pageSize) {
        try {
            List<DocInfo> docInfos = new ArrayList<>();
            List<Department> departmentList = departmentMapper.getAllDepartments();
            int departmentId = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal()).getDepartmentId();
            getChildDocInfo(departmentId,departmentList,docInfos);
            System.out.println("++++++++++++++++++++"+departmentId);
            System.out.println(docInfos.toString());


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
                PageInfo pageInfo = pageData(docInfos,pageSize,currentPage);
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

    public PageInfo pageData(List<DocInfo> list, Integer pagesize, Integer pageno){

        int totalcount=list.size();

        int pagecount=0;

        int m=totalcount%pagesize;

        List<DocInfo> subList;

        if  (m>0)
        {
            pagecount=totalcount/pagesize+1;
        }
        else
        {
            pagecount=totalcount/pagesize;
        }
        if (m==0)
        {
           subList= list.subList((pageno-1)*pagesize,pagesize*(pageno));
        }
        else
        {
            if (pageno==pagecount)
            {
                subList= list.subList((pageno-1)*pagesize,totalcount);
            }
            else
            {
                subList= list.subList((pageno-1)*pagesize,pagesize*(pageno));
            }
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotal(totalcount);
        pageInfo.setCurrentPage(pageno);
        pageInfo.setPageSize(pagesize);
        pageInfo.setList(subList);
        return pageInfo;
    }
}
