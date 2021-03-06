package top.catoy.docmanagement.service.impl;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.DocLabel;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.domain.User;
import top.catoy.docmanagement.mapper.DocInfoAndDocLabelMapper;
import top.catoy.docmanagement.mapper.DocLabelMapper;
import top.catoy.docmanagement.service.DocLabelService;
import top.catoy.docmanagement.service.LogService;
import top.catoy.docmanagement.utils.JWTUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: xjn
 * @create: 2019-04-28 12:44
 **/
@Service
public class DocLabelServiceImpl implements DocLabelService {
    @Autowired
    private DocLabelMapper docLabelMapper;

    @Autowired
    private LogService logService;

    @Autowired
    private DocInfoAndDocLabelMapper docInfoAndDocLabelMapper;

    /**
     * 插入类别
     * @param docLabel
     * @return
     */
    @Override
    public ResponseBean insertDocLabel(DocLabel docLabel) {
        try {
            int sum = docLabelMapper.insertLabel(docLabel);
            if(sum > 0){
                User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
                logService.insertLog(u.getUserId(), "增加类别-"+docLabel.getDocLabelName(), "类别管理");
                return new ResponseBean(ResponseBean.SUCCESS,"类别插入成功",null);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"类别插入失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 删除根据Id类别
     * @param docLabel
     * @return
     */
    @Override
    public ResponseBean delDocLabel(DocLabel docLabel) {
        try {
            int sum = docLabelMapper.delDocLabel(docLabel);
            if(sum > 0){
                User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
                logService.insertLog(u.getUserId(), "删除类别-"+docLabel.getDocLabelName(), "类别管理");
                return new ResponseBean(ResponseBean.SUCCESS,"类别删除成功",null);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"类别删除失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 根据id更新类别
     * @param docLabel
     * @return
     */
    @Override
    public ResponseBean editDocLabel(DocLabel docLabel) {
        try {
            int sum = docLabelMapper.editDocLabel(docLabel);
            if(sum > 0){
                User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
                logService.insertLog(u.getUserId(), "修改类别-"+docLabel.getDocLabelName(), "类别管理");
                return new ResponseBean(ResponseBean.SUCCESS,"类别修改成功",null);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"类别修改失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 得到所有类别
     * @return
     */
    @Override
    public ResponseBean getAllDocLabels() {
        try {
            List<DocLabel> docLabels = docLabelMapper.getAllDocLabels();
            if(docLabels != null){
                return new ResponseBean(ResponseBean.SUCCESS,"查询成功",docLabels);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"插叙失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 得到分类树
     * @return
     */
    @Override
    public ResponseBean getDocLabelsTree() {
        try {
            List<DocLabel> docLabels = docLabelMapper.getAllDocLabels();
            List<DocLabel> fatherList = new ArrayList<>();
            if(docLabels != null){
                docLabels.forEach((docLabel) ->{
                    if (docLabel.getSuperId() == 0){
                        fatherList.add(docLabel);
                    }
                });
                for (DocLabel docLabel : fatherList) {
                    docLabel.setChildren(getChild(docLabel.getDocLabelId(), docLabels));
                    docLabel.setDocQuantity(docInfoAndDocLabelMapper.getDocQuantityByLabelId(docLabel.getDocLabelId()));
                    getdocTotalNum(docLabel);
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
    public List<DocLabel> getChild(int id, List<DocLabel> fatherList) {
        List<DocLabel> childList = new ArrayList<>();
        int count = 0;
        for (DocLabel docLabel : fatherList) {
            // 遍历所有节点，将父id与传过来的id比较
            if (docLabel.getSuperId()== id) {
                childList.add(docLabel);
            }
        }
        for (DocLabel docLabel : childList) {
            docLabel.setChildren(getChild(docLabel.getDocLabelId(), fatherList));
            docLabel.setDocQuantity(docInfoAndDocLabelMapper.getDocQuantityByLabelId(docLabel.getDocLabelId()));
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        System.out.println(childList.toString());
        return childList;
    }

    @Override
    public int getSuperIdByName(String name) {
        return docLabelMapper.getSuperIdByName(name);
    }

    @Override
    public DocLabel getDocLabelByName(String name) {
        return docLabelMapper.getDocLabelByName(name);
    }

    @Override
    public int getIdByName(String name) {
        return docLabelMapper.getIdByName(name);
    }

    /**
     * 得到所有子类别
     * @param superId
     * @return
     */
    @Override
    public ResponseBean getSonDocLabels(int superId) {
      try {
          List<DocLabel> docLabels = docLabelMapper.getDocLabelBySuperId(superId);
          if(docLabels != null){
              return new ResponseBean(ResponseBean.SUCCESS,"子标签查找成功",docLabels);
          }else{
              return new ResponseBean(ResponseBean.FAILURE,"子标签查找失败",null);
          }
      }catch (RuntimeException r){
          return new ResponseBean(ResponseBean.ERROR,"错误",null);
      }
    }

    /**
     * 通过Id查询类别
     * @param id
     * @return
     */
    @Override
    public ResponseBean getDocLabelById(int id) {
        try {
            DocLabel docLabel = docLabelMapper.getDocLabelById(id);
            if(docLabel != null){
                return new ResponseBean(ResponseBean.SUCCESS,"标签查询成功",docLabel);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"标签查询失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public List<DocLabel> getLabelByName(String[] labels) {
        List<DocLabel> docLabels = docLabelMapper.getLabelsByName(labels);
        return docLabels;
    }

    /**
     * 得到子标签的文件数
     * @param docLabel
     * @return
     */
    public int getdocTotalNum(DocLabel docLabel){
        List<DocLabel> childList = docLabel.getChildren();
        if(childList == null){
            //递归出口
            docLabel.setDocTotalQuantity(docLabel.getDocQuantity());
            return docLabel.getDocTotalQuantity();
        }else {
            for(DocLabel d:childList){
                docLabel.setDocTotalQuantity(docLabel.getDocTotalQuantity()+getdocTotalNum(d));
            }
            docLabel.setDocTotalQuantity(docLabel.getDocTotalQuantity() + docLabel.getDocQuantity());
            return docLabel.getDocQuantity();
        }
    }
}
