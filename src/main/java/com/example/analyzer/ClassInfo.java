
package com.example.analyzer;

import java.util.List;

public class ClassInfo {
    private String className;
    private String superClass;
    private List<String> interfaces;
    private boolean isComponent;
    private List<String> imports;

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSuperClass() { return superClass; }
    public void setSuperClass(String superClass) { this.superClass = superClass; }

    public List<String> getInterfaces() { return interfaces; }
    public void setInterfaces(List<String> interfaces) { this.interfaces = interfaces; }

    public boolean isComponent() { return isComponent; }
    public void setComponent(boolean component) { isComponent = component; }

    public List<String> getImports() { return imports; }
    public void setImports(List<String> imports) { this.imports = imports; }
}
