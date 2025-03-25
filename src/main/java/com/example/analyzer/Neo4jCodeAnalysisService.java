
package com.example.analyzer;

import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

@Service
public class Neo4jCodeAnalysisService {

    @Autowired
    private JavaCodeParser parser;

    @Autowired
    private Driver driver;

    public void analyzeAndStoreToNeo4j(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                analyzeAndStoreToNeo4j(file);
            } else if (file.getName().endsWith(".java")) {
                ClassInfo info = parser.parseClass(file);
                if (info != null) {
                    storeToNeo4j(info);
                }
            }
        }
    }

    private void storeToNeo4j(ClassInfo info) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (c:Class {name: $name}) SET c.isComponent = $component",
                        parameters("name", info.getClassName(), "component", info.isComponent()));

                if (info.getSuperClass() != null) {
                    tx.run("""
                        MERGE (super:Class {name: $superName})
                        MERGE (c:Class {name: $name})
                        MERGE (c)-[:EXTENDS]->(super)
                    """, parameters("name", info.getClassName(), "superName", info.getSuperClass()));
                }

                for (String iface : info.getInterfaces()) {
                    tx.run("""
                        MERGE (i:Interface {name: $iface})
                        MERGE (c:Class {name: $name})
                        MERGE (c)-[:IMPLEMENTS]->(i)
                    """, parameters("name", info.getClassName(), "iface", iface));
                }

                for (String imp : info.getImports()) {
                    tx.run("""
                        MERGE (i:Import {name: $imp})
                        MERGE (c:Class {name: $name})
                        MERGE (c)-[:IMPORTS]->(i)
                    """, parameters("name", info.getClassName(), "imp", imp));
                }

                return null;
            });
        }
    }
}
