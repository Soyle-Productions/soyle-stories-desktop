package com.soyle.stories.layout.config.fixed

import com.soyle.stories.di.get
import com.soyle.stories.domain.project.Project
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import com.soyle.stories.storyevent.list.StoryEventListTool
import com.soyle.stories.storyevent.list.StoryEventListToolView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.add
import kotlin.reflect.KClass

object StoryEventList : ToolConfig<StoryEventList>, FixedTool() {

    override fun getFixedType(): FixedTool? = StoryEventList

    override fun getRegistration(): Pair<KClass<StoryEventList>, ToolConfig<StoryEventList>> {
        return StoryEventList::class to this
    }

    override fun getTabConfig(tool: ToolViewModel, type: StoryEventList): ToolTabConfig = object : ToolTabConfig {
        override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
            val list = projectScope.get<StoryEventListTool>().invoke(Project.Id(projectScope.projectId))
            tabPane.add(list.root)
            return tabPane.tabs.last()
        }
    }

    override fun getViewModelConfig(type: StoryEventList): ToolViewModelConfig = object : ToolViewModelConfig {
        override fun toolName(): String = "Story Events"
    }

    override fun toString(): String {
        return "StoryEventList"
    }

}