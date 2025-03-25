
package com.example.analyzer;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/query")
public class CodeAnalysisQueryController {

    @Autowired
    private Driver driver;

    // 1. 특정 클래스가 의존하거나 의존받는 클래스 조회
    @GetMapping("/class-impact")
    public Map<String, Object> getClassImpact(@RequestParam String className) {
        Map<String, Object> result = new HashMap<>();

        try (Session session = driver.session()) {
            // 의존하는 클래스
            List<String> dependsOn = session.readTransaction(tx -> {
                List<String> list = new ArrayList<>();
                Result res = tx.run("""
                    MATCH (a:Class {name: $name})-[:DEPENDS_ON]->(b:Class)
                    RETURN b.name AS name
                """, Values.parameters("name", className));
                while (res.hasNext()) list.add(res.next().get("name").asString());
                return list;
            });

            // 의존받는 클래스
            List<String> dependedBy = session.readTransaction(tx -> {
                List<String> list = new ArrayList<>();
                Result res = tx.run("""
                    MATCH (a:Class)-[:DEPENDS_ON]->(b:Class {name: $name})
                    RETURN a.name AS name
                """, Values.parameters("name", className));
                while (res.hasNext()) list.add(res.next().get("name").asString());
                return list;
            });

            result.put("dependsOn", dependsOn);
            result.put("dependedBy", dependedBy);
        }

        return result;
    }

    // 2. 특정 메서드가 변경되었을 때 영향을 받는 메서드 목록 (CALLS 역추적)
    @GetMapping("/method-impact")
    public List<Map<String, String>> getMethodImpact(@RequestParam String methodName) {
        List<Map<String, String>> result = new ArrayList<>();

        try (Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result res = tx.run("""
                    MATCH (caller:Method)-[:CALLS]->(callee:Method {name: $name})
                    RETURN caller.class AS class, caller.name AS name
                """, Values.parameters("name", methodName));

                while (res.hasNext()) {
                    Record r = res.next();
                    Map<String, String> item = new HashMap<>();
                    item.put("class", r.get("class").asString());
                    item.put("method", r.get("name").asString());
                    result.add(item);
                }
                return null;
            });
        }

        return result;
    }
}
