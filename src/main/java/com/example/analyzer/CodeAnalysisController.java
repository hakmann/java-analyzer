
package com.example.analyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.File;

@RestController
@RequestMapping("/api/analysis")
public class CodeAnalysisController {

    @Autowired
    private GitCloneService gitCloneService;

    @Autowired
    private Neo4jCodeAnalysisService neo4jService;

    @PostMapping("/analyze-repo")
    public ResponseEntity<String> analyzeRepo(@RequestParam String repoUrl,
                                              @RequestParam(defaultValue = "main") String branch) throws Exception {
        File repo = gitCloneService.cloneRepo(repoUrl, branch);
        neo4jService.analyzeAndStoreToNeo4j(repo);
        return ResponseEntity.ok("Neo4j 분석 완료");
    }

    @PostMapping("/analyze-existing-repo")
    public ResponseEntity<String> analyzeExistingRepo() throws Exception {
        //File repo = new File("/Users/leehakmin/workspace/java-analyzer");
        File repo = new File("/Users/leehakmin/workspace/demo");
        neo4jService.analyzeAndStoreToNeo4j(repo);
        return ResponseEntity.ok("Neo4j 분석 완료");
    }
}
