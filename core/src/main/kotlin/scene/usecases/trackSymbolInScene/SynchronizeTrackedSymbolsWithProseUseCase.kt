package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.*
import com.soyle.stories.prose.repositories.ProseRepository
import com.soyle.stories.prose.repositories.getProseOrError
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.theme.repositories.ThemeRepository

class SynchronizeTrackedSymbolsWithProseUseCase(
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository,
    private val themeRepository: ThemeRepository
) : SynchronizeTrackedSymbolsWithProse {

    override suspend fun invoke(proseId: Prose.Id, output: SynchronizeTrackedSymbolsWithProse.OutputPort) {
        val scene = sceneRepository.getSceneThatOwnsProse(proseId) ?: return
        val prose = proseRepository.getProseOrError(proseId)
        val symbolMentions = prose.mentions.mapNotNull { it.entityId as? MentionedSymbolId }
        val symbolIds = symbolMentions.map { it.id }.toSet()
        val themes = themeRepository.getThemesById(symbolMentions.map { it.themeId }.toSet())
        val symbols = themes.flatMap { theme ->
            theme.symbols.asSequence().filter { it.id in symbolIds }
                .map { theme to it }
        }
        val events = mutableListOf<SceneEvent>()
        val sceneWithSymbols = symbols.fold(scene) { nextScene, (theme, symbol) ->
            val update = nextScene.withSymbolTracked(theme, symbol)
            when (update) {
                is NoUpdate -> {
                }
                is Updated -> events.add(update.event)
            }
            update.scene
        }
        val sceneWithoutSymbols = sceneWithSymbols.trackedSymbols.fold(sceneWithSymbols) { nextScene, trackedSymbol ->
            if (! trackedSymbol.isPinned && trackedSymbol.symbolId !in symbolIds) {
                val update = nextScene.withoutSymbolTracked(trackedSymbol.symbolId)
                when (update) {
                    is NoUpdate -> {
                    }
                    is Updated -> events.add(update.event)
                }
                update.scene
            } else nextScene
        }

        if (events.isNotEmpty()) {
            sceneRepository.updateScene(sceneWithoutSymbols)
            output.symbolTrackedInScene(
                SynchronizeTrackedSymbolsWithProse.ResponseModel(
                    events.filterIsInstance<SymbolTrackedInScene>(),
                    events.filterIsInstance<TrackedSymbolRemoved>(),
                )
            )
        }
    }
}