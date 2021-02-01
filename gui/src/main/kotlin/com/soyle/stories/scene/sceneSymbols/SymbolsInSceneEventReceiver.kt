package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeReceiver

interface SymbolsInSceneEventReceiver : TrackedSymbolsRenamedReceiver, TrackedSymbolsRemovedReceiver,
    SymbolsTrackedInSceneReceiver, RenamedThemeReceiver, SymbolPinnedToSceneReceiver, SymbolUnpinnedFromSceneReceiver