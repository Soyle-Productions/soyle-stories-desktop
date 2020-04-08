package com.soyle.stories.project.layout

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 2:49 PM
 */
data class LayoutViewModel(
    val staticTools: List<StaticToolViewModel> = emptyList(),
    val primaryWindow: WindowViewModel? = null,
    val secondaryWindows: List<WindowViewModel> = emptyList(),
    val isValid: Boolean = false
)

class WindowViewModel(val id: String, val child: WindowChildViewModel)
sealed class WindowChildViewModel {
    abstract val id: String
}
class GroupSplitterViewModel(val splitterId: String, val orientation: Boolean, val children: List<Pair<Int, WindowChildViewModel>>) : WindowChildViewModel() {
    override val id: String
        get() = splitterId
}
class ToolGroupViewModel(val groupId: String, val focusedToolId: String?, val tools: List<ToolViewModel>) : WindowChildViewModel() {
    override val id: String
        get() = groupId
}
class StaticToolViewModel(val toolId: String, val isOpen: Boolean, val name: String)
sealed class ToolViewModel {
    abstract val toolId: String
}
class CharacterListToolViewModel(override val toolId: String) : ToolViewModel()
class BaseStoryStructureToolViewModel(override val toolId: String, val characterId: String, val themeId: String) : ToolViewModel()
class CharacterComparisonToolViewModel(override val toolId: String, val themeId: String, val characterId: String) : ToolViewModel()