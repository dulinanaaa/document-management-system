package top.catoy.docmanagement.service.impl;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.text.resources.sk.CollationData_sk;
import top.catoy.docmanagement.domain.*;
import top.catoy.docmanagement.mapper.*;
import top.catoy.docmanagement.service.DepartmentService;
import top.catoy.docmanagement.service.DocInfoService;
import top.catoy.docmanagement.service.LogService;
import top.catoy.docmanagement.utils.JWTUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    private DocInfoAndDocLabelMapper docInfoAndDocLabelMapper;

    @Autowired
    private DocInfoAndTagMapper docInfoAndTagMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private LogService logService;

    @Autowired
    private DocLabelMapper docLabelMapper;

    @Autowired
    private  TagMapper tagMapper;

    @Override
    public int insertDocInfo(DocInfo docInfo) {
        User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
        logService.insertLog(u.getUserId(), "上传文件-"+docInfo.getDocName(), "文件管理");
        return docInfoMapper.insertDocInfo(docInfo);
    }

    @Override
    public ResponseBean editDoc(DocInfo docInfo) {
        try {
            int sum =  docInfoMapper.updateDocInfo(docInfo);//更新文件信息
            //更新文件与分类关系
            docInfoAndDocLabelMapper.delByDocId(docInfo.getDocId());
            String[] docLabelList = docInfo.getDocLabelList().split(",");
            List<DocLabel> docLabels = docLabelMapper.getLabelsByName(docLabelList);
            docInfoAndDocLabelMapper.insertDocInfoAndDocLabels(docLabels,docInfo.getDocId());

            //更新文件与标签关系
            docInfoAndTagMapper.delByDocId(docInfo.getDocId());
            String[] docTagList = docInfo.getTagList().split(",");
            List<Tag> tags = tagMapper.getTagsByName(docTagList);
            docInfoAndTagMapper.insertDocInfoAndTags(tags,docInfo.getDocId());
            if(sum > 0){
                return new ResponseBean(ResponseBean.SUCCESS,"文件编辑成功",null);
            }else{
                return new ResponseBean(ResponseBean.FAILURE,"文件编辑失败",null);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public int getDocId(DocInfo docInfo) {
        return docInfoMapper.getDocId(docInfo);
    }
//    @Override
//    public ResponseBean getAllDocInfo(int currentPage, int pageSize) {
//        try {
//            List<DocInfo> docInfos = new ArrayList<>();
//            List<Department> departmentList = departmentMapper.getAllDepartments();
//            int departmentId = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal()).getDepartmentId();
//
//
//            if(docInfos!=null){
//                docInfos.forEach((docInfo) -> {
//                    List<Annex> annexes = annexMapper.getAnnexsByDocId(docInfo.getDocId());
//                    String departmentName = departmentMapper.getDepartmentNameById(docInfo.getDepartmentId());
//                    if(annexes !=null && annexes.size()>0){
//                        docInfo.setAnnexes(annexes);
//                    }
//                    if(departmentName !=null && departmentName.length()>0){
//                        docInfo.setDepartmentName(departmentName);
//                    }
//                });
//                PageInfo pageInfo = pageData(docInfos,pageSize,currentPage);
//                return new ResponseBean(ResponseBean.SUCCESS,"查询成功",pageInfo);
//            }else {
//                return new ResponseBean(ResponseBean.FAILURE,"查询失败",null);
//            }
//        }catch (RuntimeException r){
//            r.printStackTrace();
//            return new ResponseBean(ResponseBean.ERROR,"错误",null);
//        }
//    }

    @Override
    public ResponseBean getDocsBySearchParam(DocInfoSearchParams docInfoSearchParams) {
        try {
            List<DocInfo> docInfos = new ArrayList<>();
            List<DocInfo> docs;
            List<Department> departmentList = departmentMapper.getAllDepartments();
            List<Integer> docLabels;
            List<Integer> tags;
            int userGroupId = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal()).getGroupId();
            String userRole = userGroupMapper.getUserGroupById(userGroupId).getGroupName();
            int pageSize = docInfoSearchParams.getPageInfo().getPageSize();
            int currentPage = docInfoSearchParams.getPageInfo().getCurrentPage();
            int departmentId = -1;
            String docPostTime;

            if(docInfoSearchParams.getDocLabels()!=null && docInfoSearchParams.getDocLabels().size()  > 0){
                docLabels = docInfoAndDocLabelMapper.getDocInfoIdByLabelId(docInfoSearchParams.getDocLabels());
            }else {
                docLabels = null;
            }

            if(docInfoSearchParams.getTags()!=null && docInfoSearchParams.getTags().size()  > 0){
                tags = docInfoAndTagMapper.getDocIdByTagId(docInfoSearchParams.getTags());
            }else {
                tags = null;
            }

            System.out.println(tags+"------------------------------tags");

            if("Invalid date".equals(docInfoSearchParams.getSelectYear())){
                docPostTime = "";
            }else{
                docPostTime = docInfoSearchParams.getSelectYear();
            }
            if(docInfoSearchParams.getDepartmentId() != -1){
               if(docInfoSearchParams.getDepartmentId() == -2){//选择了未被管理的文件
                   departmentId = -1;//未被管理文件的部门id为-1
               }else{
                   departmentId = docInfoSearchParams.getDepartmentId();
               }
                docInfos = docInfoMapper.getDocByDepartmentIdAndSearchParam(departmentId,
                        docInfoSearchParams.getDocName(),
                        docPostTime,
                        docLabels,
                        tags);
            }else if(docInfoSearchParams.getDepartmentId() == -1) {//没有选择部门
                departmentId = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal()).getDepartmentId();
                docs = docInfoMapper.getDocByDepartmentIdAndSearchParam(departmentId,docInfoSearchParams.getDocName(),docPostTime,docLabels,tags);
                //获得未分配部门的文件
                if("管理员".equals(userRole)){
                    List<DocInfo> notTrackedDoc = docInfoMapper.getDocByDepartmentIdAndSearchParam(-1,docInfoSearchParams.getDocName(),docPostTime,docLabels,tags);
                    if (notTrackedDoc !=null && notTrackedDoc.size()>0){
                        docInfos.addAll(notTrackedDoc);
                    }
                }
                System.out.println(docs+"---------------------docs");
                docInfos.addAll(docs);//加入父部门所拥有的文档
                getChildDocInfo(departmentId,departmentList,docInfos,docInfoSearchParams.getDocName(),docPostTime,docLabels,tags);
            }

            //获得文件的附件信息、标签、分类和部门信息
            if(docInfos!=null){
                sort(docInfos);//发文按时间排序
                PageInfo pageInfo = pageData(docInfos,pageSize,currentPage);
                System.out.println("+++++++++++++++++++++++++++=");

                for(Object d:pageInfo.getList()){
                    System.out.println(((DocInfo)d).getDocPostTime());
                }
                pageInfo.getList().forEach((docInfo) -> {
                    DocInfo doc = (DocInfo) docInfo;
                    List<Annex> annexes = annexMapper.getAnnexsByDocId(doc.getDocId());
                    String departmentName = departmentMapper.getDepartmentNameById(doc.getDepartmentId());
                    List<Tag> tagList = docInfoAndTagMapper.getTagsByDocId(((DocInfo) docInfo).getDocId());
                    List<DocLabel> docLabelList = docInfoAndDocLabelMapper.getDocLabelByDocId(((DocInfo) docInfo).getDocId());
                    if(annexes !=null && annexes.size()>0){
                        doc.setAnnexes(annexes);
                    }
                    if(departmentName !=null && departmentName.length()>0){
                        doc.setDepartmentName(departmentName);
                    }
                    if(tagList != null && tagList.size()>0){
                        doc.setTagArrayList(tagList);
                    }
                    if(docLabelList !=null && docLabelList.size()>0){
                        doc.setDocLabelArrayList(docLabelList);
                    }
                });

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
    public List<Department> getChildDocInfo(int id,
                                            List<Department> fatherList,
                                            List<DocInfo> docInfos,
                                            String docName,
                                            String docPostTime,
                                            List<Integer> docLabels,
                                            List<Integer> tags) {
        List<Department> childList = new ArrayList<>();
        for (Department department : fatherList) {
            // 遍历所有节点，将父id与传过来的id比较
            if (department.getParent_id()== id) {
                List<DocInfo> list = docInfoMapper.getDocByDepartmentIdAndSearchParam(department.getId(),docName,docPostTime,docLabels,tags);
                if(list != null && list.size()>0){
                    docInfos.addAll(list);
                }
                childList.add(department);
            }
        }
        for (Department department : childList) {
            department.setChildren(getChildDocInfo(department.getId(), fatherList,docInfos,docName,docPostTime,docLabels,tags));
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    /**
     * 分页数据
     * @param list
     * @param pagesize
     * @param pageno
     * @return
     */
    @Override
    public PageInfo pageData(List<DocInfo> list, Integer pagesize, Integer pageno){
        int totalcount = 0;
        int pagecount = 0;
        int m = 0;
        List<DocInfo> subList = new ArrayList<>();
        if(list != null && list.size()>0){
            totalcount = list.size();
            m = totalcount%pagesize;
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
        }else {
            totalcount = 0;
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotal(totalcount);
        pageInfo.setCurrentPage(pageno);
        pageInfo.setPageSize(pagesize);
        pageInfo.setList(subList);
        return pageInfo;
    }

    @Override
    public DocInfo getDocInfoByName(String name) {
        return docInfoMapper.getDocInfoByName(name);
    }

    @Override
    public int deleteDocInfo(DocInfo docInfo) {
        return docInfoMapper.delDocInfo(docInfo);
    }

    public void sort(List<DocInfo> docInfos){
        Collections.sort(docInfos,(doc1,doc2)->{
            return -(doc1.getDocPostTime().compareTo(doc2.getDocPostTime()));
        });
    }
}
