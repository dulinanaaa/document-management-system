package top.catoy.docmanagement.service;

import top.catoy.docmanagement.domain.*;

import javax.print.Doc;
import java.util.List;

public interface DocInfoService {


    int getDocId(DocInfo docInfo);
    int insertDocInfo(DocInfo docInfo);
    ResponseBean  editDoc(DocInfo docInfo);

    DocInfo getDocInfoById(DocInfo docInfo);

//    ResponseBean getAllDocInfo(int currentPage, int pageSize);

    ResponseBean getDocsBySearchParam(DocInfoSearchParams docInfoSearchParams);

    List<Department> getChildDocInfo(int id,
                                     List<Department> fatherList,
                                     List<DocInfo> docInfos,
                                     String docName,
                                     String docPostTime,
                                     List<Integer> docLabels,
                                     List<Integer> tags);

    PageInfo pageData(List<DocInfo> list, Integer pagesize, Integer pageno);

    DocInfo getDocInfoByName(String name);

    public int deleteDocInfo(DocInfo docInfo);

    int updateDocInfo(DocInfo docInfo);
}
