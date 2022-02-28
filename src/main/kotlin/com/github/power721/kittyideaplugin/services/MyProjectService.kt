package com.github.power721.kittyideaplugin.services

import com.intellij.openapi.project.Project
import com.github.power721.kittyideaplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
