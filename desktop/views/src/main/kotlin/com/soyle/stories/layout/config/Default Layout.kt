package com.soyle.stories.layout.config

import com.soyle.stories.domain.project.Project
import com.soyle.stories.layout.config.fixed.*
import com.soyle.stories.layout.config.temporary.ReorderSceneRamifications
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.entities.layout

fun defaultLayout(projectId: Project.Id, layoutId: Layout.Id): Layout = layout(projectId, layoutId) {
    window {
        horizontalStackSplitter {
            stackSplitter(4) {
                stackSplitter(2) {
                    stack(1) {
                        //openTool(ToolType.Properties)
                        //openTool(ToolType.CharacterDevelopment)
                        //openTool(ToolType.LocationTracking)
                    }
                    stack(1) {
                        tool(Tool(StoryEventList))
                        tool(Tool(LocationList))
                        tool(Tool(CharacterList))
                    }
                }
                primaryStack(6) {}
                stackSplitter(2) {
                    stack(2) {
                        tool(Tool(SceneList))
                        tool(Tool(ThemeList, isOpen = false))
                        //openTool(ToolType.NoteList)
                    }
                    stack(2) {
                        tool(Tool(SceneOutline, isOpen = false))
                        tool(Tool(SceneSymbols, isOpen = false))
                        tool(Tool(SceneSetting, isOpen = false))
                        tool(Tool(SceneCharacters, isOpen = false))
                    }
                }
            }
            stack(2) {
                tool(Tool(Ramifications, isOpen = false))
                marker(ReorderSceneRamifications::class)
                tool(Tool(Timeline, isOpen = false))
                //openTool(ToolType.ContinuityErrors)
            }
        }
    }
}