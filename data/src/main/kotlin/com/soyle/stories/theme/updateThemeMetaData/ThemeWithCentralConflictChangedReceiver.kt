package com.soyle.stories.theme.updateThemeMetaData

import com.soyle.stories.theme.usecases.updateThemeMetaData.ThemeWithCentralConflictChanged

interface ThemeWithCentralConflictChangedReceiver {
    suspend fun receiveThemeWithCentralConflictChanged(themeWithCentralConflictChanged: ThemeWithCentralConflictChanged)
}