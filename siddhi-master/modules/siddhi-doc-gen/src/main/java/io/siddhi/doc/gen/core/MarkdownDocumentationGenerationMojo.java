/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.siddhi.doc.gen.core;

import io.siddhi.doc.gen.core.utils.Constants;
import io.siddhi.doc.gen.core.utils.DocumentationUtils;
import io.siddhi.doc.gen.metadata.NamespaceMetaData;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * Mojo for generating Siddhi Documentation
 */
@Mojo(
        name = "generate-md-docs",
        aggregator = true,
        defaultPhase = LifecyclePhase.INSTALL,
        requiresDependencyResolution = ResolutionScope.TEST
)
public class MarkdownDocumentationGenerationMojo extends AbstractMojo {
    /**
     * The maven project object
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    /**
     * The target module for which the files will be generated
     * Optional
     */
    @Parameter(property = "module.target.directory")
    private File moduleTargetDirectory;

//    /**
//     * The path of the readme file in the base directory
//     * Optional
//     */
//    @Parameter(property = "home.page.template.file")
//    private File homePageTemplateFile;
//
//    /**
//     * The name of the index file
//     * Optional
//     */
//    @Parameter(property = "home.page.file.name")
//    private String homePageFileName;

    /**
     * The path of the mkdocs.yml file in the base directory
     * Optional
     */
    @Parameter(property = "mkdocs.config.file")
    private File mkdocsConfigFile;

    /**
     * The directory in which the docs will be generated
     * Optional
     */
    @Parameter(property = "doc.gen.base.directory")
    private File docGenBaseDirectory;

    /**
     * The readme file
     * Optional
     */
    @Parameter(property = "readme.file")
    private File readmeFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // Finding the root maven project
        MavenProject rootMavenProject = mavenProject;
        while (rootMavenProject.getParent() != null &&
                rootMavenProject.getParent().getBasedir() != null) {
            rootMavenProject = rootMavenProject.getParent();
        }

        // Setting the relevant modules target directory if not set by user
        String moduleTargetPath;
        if (moduleTargetDirectory != null) {
            moduleTargetPath = moduleTargetDirectory.getAbsolutePath();
        } else {
            moduleTargetPath = mavenProject.getBuild().getDirectory();
        }

        // Setting the documentation output directory if not set by user
        String docGenBasePath;
        if (docGenBaseDirectory != null) {
            docGenBasePath = docGenBaseDirectory.getAbsolutePath();
        } else {
            docGenBasePath = rootMavenProject.getBasedir() + File.separator + Constants.DOCS_DIRECTORY;
        }

//        // Setting the home page template file path if not set by user
//        if (homePageTemplateFile == null) {
//            homePageTemplateFile = new File(rootMavenProject.getBasedir() + File.separator
//                    + Constants.README_FILE_NAME + Constants.MARKDOWN_FILE_EXTENSION);
//        }

        // Setting the mkdocs config file path if not set by user
        if (mkdocsConfigFile == null) {
            mkdocsConfigFile = new File(rootMavenProject.getBasedir() + File.separator
                    + Constants.MKDOCS_CONFIG_FILE_NAME + Constants.YAML_FILE_EXTENSION);
        }
//
//        // Setting the home page file name if not set by user
//        File homePageFile;
//        if (homePageFileName == null) {
//            homePageFile = new File(docGenBasePath + File.separator
//                    + Constants.HOMEPAGE_FILE_NAME + Constants.MARKDOWN_FILE_EXTENSION);
//        } else {
//            homePageFile = new File(docGenBasePath + File.separator + homePageFileName);
//        }

        // Setting the readme file name if not set by user
        if (readmeFile == null) {
            readmeFile = new File(rootMavenProject.getBasedir() + File.separator
                    + Constants.README_FILE_NAME + Constants.MARKDOWN_FILE_EXTENSION);
        }

        // Retrieving metadata
        List<NamespaceMetaData> namespaceMetaDataList;
        try {
            namespaceMetaDataList = DocumentationUtils.getExtensionMetaData(
                    moduleTargetPath,
                    mavenProject.getRuntimeClasspathElements(),
                    getLog()
            );
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Unable to resolve dependencies of the project", e);
        }

        // Generating the documentation
        if (namespaceMetaDataList.size() > 0) {
            DocumentationUtils.generateDocumentation(namespaceMetaDataList, docGenBasePath, mavenProject.getVersion(),
                    getLog());
//            DocumentationUtils.updateHeadingsInMarkdownFile(homePageTemplateFile, homePageFile,
//                    rootMavenProject.getArtifactId(), mavenProject.getVersion(), namespaceMetaDataList);
//            DocumentationUtils.updateHeadingsInMarkdownFile(readmeFile, readmeFile, rootMavenProject.getArtifactId(),
//                    mavenProject.getVersion(), namespaceMetaDataList);
        }
    }
}
