package com.soyle.stories.layout.config

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.config.fixed.CharacterList
import com.soyle.stories.layout.config.fixed.LocationList
import com.soyle.stories.layout.config.fixed.SceneList
import com.soyle.stories.layout.config.temporary.DeleteSceneRamifications
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.entities.layout

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:17 AM
 */

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
                        tool(Tool(SceneList))
                        tool(Tool(LocationList))
                        tool(Tool(CharacterList))
                    }
                }
                primaryStack(6) {}
                stack(2) {
                    //openTool(ToolType.NoteList)
                }
            }
            stack(2) {
                marker(DeleteSceneRamifications::class)
                //openTool(ToolType.Timeline)
                //openTool(ToolType.ContinuityErrors)
            }
        }
    }
}