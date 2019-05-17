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

    private int sort;
    private List<Department> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.name = Integer.toString(id);
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

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent_id=" + parent_id +
                ", instroduction='" + instroduction + '\'' +
                ", sort=" + sort +
                ", children=" + children +
                '}';
    }
}
