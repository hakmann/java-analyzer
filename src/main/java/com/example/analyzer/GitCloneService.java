
package com.example.analyzer;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class GitCloneService {
    public File cloneRepo(String repoUrl, String branch) throws Exception {
        File localPath = File.createTempFile("repo", "");
        if (!localPath.delete()) throw new RuntimeException("Could not delete temp file");
        Git.cloneRepository()
                .setURI(repoUrl)
                .setBranch(branch)
                .setDirectory(localPath)
                .call();
        return localPath;
    }
}
