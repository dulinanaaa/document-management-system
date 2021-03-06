package top.catoy.docmanagement.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 部门
 * @author: xjn
 * @create: 2019-04-26 08:50
 **/
public class Department {
    private int id;
    private String name;
    private int parent_id;
    private String instroduction;
    private String label;
    private String value;
    private String departmentNumber;//部门编号
    private int docTotalNum;//部门拥有总文件的数目，包含子部门
    private int docNum;//部门拥有文件数，不包含子部门
    private int docTotalPagenum;//部门拥有的总文件页数 ，包含子部门文件的页数
    private int docPageNum;//部门拥有文件的总页数之和，不包含子部门

    private int sort;
    private List<Department> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.value = Integer.toString(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.label = name;
    }



    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getInstroduction() {
        return instroduction;
    }

    public void setInstroduction(String instroduction) {
        this.instroduction = instroduction;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<Department> getChildren() {
        return children;
    }

    public void setChildren(List<Department> children) {
        this.children = children;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDocTotalNum() {
        return docTotalNum;
    }

    public void setDocTotalNum(int docTotalNum) {
        this.docTotalNum = docTotalNum;
    }

    public int getDocNum() {
        return docNum;
    }

    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    public int getDocTotalPagenum() {
        return docTotalPagenum;
    }

    public void setDocTotalPagenum(int docTotalPagenum) {
        this.docTotalPagenum = docTotalPagenum;
    }

    public int getDocPageNum() {
        return docPageNum;
    }

    public void setDocPageNum(int docPageNum) {
        this.docPageNum = docPageNum;
    }

    public String getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent_id=" + parent_id +
                ", instroduction='" + instroduction + '\'' +
                ", label='" + label + '\'' +
                ", value='" + value + '\'' +
                ", departmentNumber='" + departmentNumber + '\'' +
                ", docTotalNum=" + docTotalNum +
                ", docNum=" + docNum +
                ", docTotalPagenum=" + docTotalPagenum +
                ", docPageNum=" + docPageNum +
                ", sort=" + sort +
                ", children=" + children +
                '}';
    }
}
