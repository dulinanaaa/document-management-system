package top.catoy.docmanagement.service;

import top.catoy.docmanagement.domain.Department;
import top.catoy.docmanagement.domain.DocInfo;
import top.catoy.docmanagement.domain.ResponseBean;

import javax.print.Doc;
import java.util.List;

public interface DocInfoService {

    int insertDocInfo(DocInfo docInfo);

    ResponseBean getAllDocInfo(int currentPage, int pageSize);

    List<Department> getChildDocInfo(int id, List<Department> fatherList,List<DocInfo> docInfos);
}
