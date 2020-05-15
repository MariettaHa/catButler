package catbutler.logic;

import java.util.ArrayList;

public class BooleanTreeNode {

    int treeType = 0; // 0: leaf node; 1: and-Node; 2: or-Node
    private ArrayList<BooleanTreeNode> children = new ArrayList<>();
    private int level = -1;
    private String annotation = "";
    private BooleanTreeNode parent = null;
    private boolean isRoot = true;
    private String dnf = "";
    private String dnfWithCoeff = "";

    public BooleanTreeNode() {
    }

    public BooleanTreeNode(int level, String string, int treeType, BooleanTreeNode parent) {
        this.level = level;
        this.annotation = string;
        this.treeType = treeType;
        this.parent = parent;
    }

    public BooleanTreeNode(boolean isRoot) {
        if (isRoot) {
            this.level = -1;
            this.annotation = "root";
            this.parent = this;
        }
    }

    public void addChild(BooleanTreeNode child) {
        children.add(child);
    }

    public ArrayList<BooleanTreeNode> getChildren() {
        return children;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getDnf() {
        return dnf;
    }

    public void setDnf(String dnf) {
        this.dnf = dnf;
    }

    public String getDnfWithCoeff() {
        return dnfWithCoeff;
    }

    public void setDnfWithCoeff(String dnfStrWithCoefficients) {
        this.dnfWithCoeff = dnfStrWithCoefficients;
    }
}
