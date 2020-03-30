package com.soyle.stories.project

import com.soyle.stories.common.launchTask
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.project.projectList.ProjectFileViewModel
import javafx.concurrent.Task
import tornadofx.Scope
import tornadofx.find
import tornadofx.onChange

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 9:06 PM
 */
class ProjectScope(projectViewModel: ProjectFileViewModel) : Scope() {

    val projectId = projectViewModel.projectId
    val layoutViewListener = find<LayoutComponent>(scope = this).layoutViewListener

    private fun loadLayout(): Task<*>
    {
        return launchTask {
            println("async load layout")
            it.updateProgress(0.5, WorkBenchModel.MAX_LOADING_VALUE)
            println("progress updated")
            it.updateMessage("Loading Layout")
            println("message updated")
            layoutViewListener.loadLayoutForProject(projectId)
            it.updateProgress(WorkBenchModel.MAX_LOADING_VALUE, WorkBenchModel.MAX_LOADING_VALUE)
        }
    }

    init {

        println("get project loading dialog")
        find<ProjectLoadingDialog>(scope = this)

        println("create loading task")
        val loading = loadLayout()

        val model = find<WorkBenchModel>(scope = this)
        model.loadingProgress.bind(loading.progressProperty())
        model.loadingMessage.bind(loading.messageProperty())
        model.projectViewModel.set(projectViewModel)
        model.isValidLayout.onChange {
            if (it != true) { loadLayout() }
        }

        find<WorkBench>(scope = this)
    }

}