package com.soyle.stories.theme.doubles

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.ThemeRepository

class ThemeRepositoryDouble(
    private val onAddTheme: (Theme) -> Unit = {},
    private val onDeleteTheme: (Theme) -> Unit = {}
) : ThemeRepository
{
    val themes = mutableMapOf<Theme.Id, Theme>()

    override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
        return themes.values.filter { it.projectId == projectId }
    }

    override suspend fun getThemeById(id: Theme.Id): Theme? = themes[id]

    override suspend fun addTheme(theme: Theme) {
        themes[theme.id] = theme
        onAddTheme(theme)
    }

    override suspend fun updateTheme(theme: Theme) {
        themes[theme.id]= theme
    }

    override suspend fun deleteTheme(theme: Theme) {
        themes.remove(theme.id)
        onDeleteTheme(theme)
    }
}