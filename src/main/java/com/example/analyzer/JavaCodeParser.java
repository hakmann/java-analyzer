
package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class JavaCodeParser {

    public ClassInfo parseClass(File javaFile) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(javaFile);

        Optional<ClassOrInterfaceDeclaration> clazzOpt = cu.findFirst(ClassOrInterfaceDeclaration.class);
        if (clazzOpt.isEmpty()) return null;

        ClassOrInterfaceDeclaration clazz = clazzOpt.get();
        ClassInfo info = new ClassInfo();

        info.setClassName(clazz.getNameAsString());

        if (!clazz.getExtendedTypes().isEmpty()) {
            info.setSuperClass(clazz.getExtendedTypes(0).getNameAsString());
        }

        List<String> interfaces = new ArrayList<>();
        for (ClassOrInterfaceType t : clazz.getImplementedTypes()) {
            interfaces.add(t.getNameAsString());
        }
        info.setInterfaces(interfaces);

        info.setComponent(clazz.isAnnotationPresent("Service") ||
                          clazz.isAnnotationPresent("Repository") ||
                          clazz.isAnnotationPresent("Component"));

        List<String> imports = new ArrayList<>();
        for (ImportDeclaration imp : cu.getImports()) {
            imports.add(imp.getNameAsString());
        }
        info.setImports(imports);

        return info;
    }
}
