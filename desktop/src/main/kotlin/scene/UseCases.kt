package com.soyle.stories.desktop.config.scene

import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.editProse.ContentReplacedNotifier
import com.soyle.stories.scene.coverArcSectionsInScene.CoverArcSectionsInSceneController
import com.soyle.stories.scene.coverArcSectionsInScene.CoverArcSectionsInSceneControllerImpl
import com.soyle.stories.scene.coverArcSectionsInScene.CoverCharacterArcSectionsInSceneOutputPort
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.createNewScene.CreateNewSceneControllerImpl
import com.soyle.stories.scene.createNewScene.CreateNewSceneNotifier
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.deleteScene.DeleteSceneControllerImpl
import com.soyle.stories.scene.deleteScene.DeleteSceneNotifier
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneControllerImpl
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneOutput
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneControllerImpl
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneNotifier
import com.soyle.stories.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionController
import com.soyle.stories.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionControllerImpl
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneController
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneControllerImpl
import com.soyle.stories.scene.removeCharacterFromScene.RemoveCharacterFromSceneController
import com.soyle.stories.scene.removeCharacterFromScene.RemoveCharacterFromSceneNotifier
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneNotifier
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.scene.reorderScene.ReorderSceneControllerImpl
import com.soyle.stories.scene.reorderScene.ReorderSceneNotifier
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneControllerImpl
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier
import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.*
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.deleteScene.DeleteSceneUseCase
import com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingSceneUseCase
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingSceneUseCase
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetailsUseCase
import com.soyle.stories.scene.usecases.includeCharacterInScene.GetAvailableCharactersToAddToScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInSceneUseCase
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProseUseCase
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInScene
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInSceneUseCase
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import com.soyle.stories.scene.usecases.renameScene.RenameScene
import com.soyle.stories.scene.usecases.renameScene.RenameSceneUseCase
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene
import com.soyle.stories.scene.usecases.reorderScene.ReorderSceneUseCase
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import com.soyle.stories.scene.usecases.trackSymbolInScene.*
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventNotifier

object UseCases {

    init {
        scoped<ProjectScope> {
            createNewScene()
            listAllScenes()
            renameScene()
            deleteScene()
            includeCharacterInScene()
            setMotivationForCharacterInScene()
            getSceneDetails()
            linkLocationToScene()
            removeCharacterFromScene()
            reorderScene()
            coverCharacterArcSectionsInScene()
            listOptionsToReplaceMention()
            synchronizeTrackedSymbolsWithProse()
            listSymbolsInScene()
            listAvailableSymbolsToTrackInScene()
            pinSymbolToScene()
            unpinSymbolFromScene()
            detectUnusedSymbols()
        }
    }

    private fun InProjectScope.createNewScene() {
        provide<CreateNewScene> {
            CreateNewSceneUseCase(projectId, get(), get(), get(), get())
        }

        provide<CreateNewSceneController> {
            CreateNewSceneControllerImpl(
                projectId.toString(),
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(CreateNewScene.OutputPort::class) {
            CreateNewSceneNotifier(applicationScope.get(), get<CreateStoryEventNotifier>())
        }
    }

    private fun InProjectScope.listAllScenes() {
        provide<ListAllScenes> {
            ListAllScenesUseCase(projectId, get())
        }
    }

    private fun InProjectScope.renameScene() {
        provide<RenameScene> {
            RenameSceneUseCase(get())
        }

        provide<RenameSceneController> {
            RenameSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(RenameScene.OutputPort::class) {
            RenameSceneNotifier(applicationScope.get())
        }
    }

    private fun InScope<ProjectScope>.deleteScene() {
        provide<GetPotentialChangesFromDeletingScene> {
            GetPotentialChangesFromDeletingSceneUseCase(get())
        }
        provide<DeleteScene> {
            DeleteSceneUseCase(get())
        }

        provide<DeleteSceneController> {
            DeleteSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(DeleteScene.OutputPort::class) {
            DeleteSceneNotifier(applicationScope.get())
        }
    }

    private fun InProjectScope.includeCharacterInScene() {
        provide(
            IncludeCharacterInScene::class,
            GetAvailableCharactersToAddToScene::class
        ) {
            IncludeCharacterInSceneUseCase(get(), get(), get())
        }

        provide(IncludeCharacterInSceneController::class) {
            IncludeCharacterInSceneControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(IncludeCharacterInScene.OutputPort::class) {
            IncludeCharacterInSceneOutput(get(), get())
        }
    }

    private fun InProjectScope.setMotivationForCharacterInScene() {
        provide<SetMotivationForCharacterInScene> {
            SetMotivationForCharacterInSceneUseCase(get(), get())
        }

        provide<SetMotivationForCharacterInSceneController> {
            SetMotivationForCharacterInSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(SetMotivationForCharacterInScene.OutputPort::class) {
            SetMotivationForCharacterInSceneNotifier(applicationScope.get())
        }
    }

    private fun InProjectScope.getSceneDetails() {
        provide<GetSceneDetails> {
            GetSceneDetailsUseCase(get(), get())
        }
    }

    private fun InProjectScope.linkLocationToScene() {
        provide<LinkLocationToScene> {
            LinkLocationToSceneUseCase(get(), get())
        }

        provide<LinkLocationToSceneController> {
            LinkLocationToSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(LinkLocationToScene.OutputPort::class) {
            LinkLocationToSceneNotifier(applicationScope.get())
        }
    }

    private fun InProjectScope.removeCharacterFromScene() {
        provide<RemoveCharacterFromScene> {
            RemoveCharacterFromSceneUseCase(get())
        }

        provide {
            RemoveCharacterFromSceneController(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(RemoveCharacterFromScene.OutputPort::class) {
            RemoveCharacterFromSceneNotifier(applicationScope.get())
        }
    }

    private fun InProjectScope.reorderScene() {
        provide<GetPotentialChangesFromReorderingScene> {
            GetPotentialChangesFromReorderingSceneUseCase(get())
        }
        provide<ReorderScene> {
            ReorderSceneUseCase(get())
        }

        provide<ReorderSceneController> {
            ReorderSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(ReorderScene.OutputPort::class) {
            ReorderSceneNotifier(applicationScope.get())
        }
    }

    private fun InProjectScope.coverCharacterArcSectionsInScene() {
        provide(
            CoverCharacterArcSectionsInScene::class,
            GetAvailableCharacterArcsForCharacterInScene::class
        ) {
            CoverCharacterArcSectionsInSceneUseCase(get(), get())
        }
        provide<ChangeCharacterArcSectionValueAndCoverInScene> {
            ChangeCharacterArcSectionValueAndCoverInSceneUseCase(get(), get(), get())
        }
        provide(
            CreateCharacterArcSectionAndCoverInScene::class,
            GetAvailableCharacterArcSectionTypesForCharacterArc::class
        ) {
            CreateCharacterArcSectionAndCoverInSceneUseCase(get(), get())
        }

        provide<CoverArcSectionsInSceneController> {
            CoverArcSectionsInSceneControllerImpl(
                applicationScope.get(),
                get(),
                get(),
                get(),
                get()
            )
        }

        provide(
            CoverCharacterArcSectionsInScene.OutputPort::class,
            ChangeCharacterArcSectionValueAndCoverInScene.OutputPort::class,
            CreateCharacterArcSectionAndCoverInScene.OutputPort::class
        ) {
            CoverCharacterArcSectionsInSceneOutputPort(get(), get(), get())
        }
    }

    private fun InProjectScope.listOptionsToReplaceMention() {
        provide<ListOptionsToReplaceMentionController> {
            ListOptionsToReplaceMentionControllerImpl(
                applicationScope.get(),
                get()
            )
        }

        provide<ListOptionsToReplaceMentionInSceneProse> {
            ListOptionsToReplaceMentionInSceneProseUseCase(get(), get(), get(), get())
        }
    }

    private fun InProjectScope.synchronizeTrackedSymbolsWithProse() {
        provide {
            SynchronizeTrackedSymbolsWithProseController(get(), get()).also {
                it listensTo get<ContentReplacedNotifier>()
            }
        }
        provide<SynchronizeTrackedSymbolsWithProse> {
            SynchronizeTrackedSymbolsWithProseUseCase(get(), get(), get())
        }
        provide<SynchronizeTrackedSymbolsWithProse.OutputPort> {
            SynchronizeTrackedSymbolsWithProseOutput(get(), get())
        }
    }

    private fun InProjectScope.listSymbolsInScene()
    {
        provide<ListSymbolsInScene> {
            ListSymbolsInSceneUseCase(get(), get())
        }
        provide<ListSymbolsInSceneController> {
            ListSymbolsInSceneControllerImpl(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.listAvailableSymbolsToTrackInScene()
    {
        provide<ListAvailableSymbolsToTrackInSceneController> {
            ListAvailableSymbolsToTrackInSceneControllerImpl(applicationScope.get(), get())
        }

        provide<ListAvailableSymbolsToTrackInScene> {
            ListAvailableSymbolsToTrackInSceneUseCase(get(), get())
        }
    }

    private fun InProjectScope.pinSymbolToScene()
    {
        provide<PinSymbolToScene> {
            PinSymbolToSceneUseCase(get(), get())
        }
        provide(PinSymbolToScene.OutputPort::class) {
            PinSymbolToSceneOutput(get(), get())
        }
        provide<PinSymbolToSceneController> {
            PinSymbolToSceneControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.unpinSymbolFromScene()
    {
        provide<UnpinSymbolFromScene> {
            UnpinSymbolFromSceneUseCase(get(), get())
        }
        provide(UnpinSymbolFromScene.OutputPort::class) {
            UnpinSymbolFromSceneOutput(get(), get())
        }
        provide<UnpinSymbolFromSceneController> {
            UnpinSymbolFromSceneControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.detectUnusedSymbols()
    {
        provide<DetectUnusedSymbolsInScene> {
            DetectUnusedSymbolsInSceneUseCase(get(), get())
        }
        provide(DetectUnusedSymbolsInScene.OutputPort::class) {
            DetectUnusedSymbolsOutput()
        }
        provide<DetectUnusedSymbolsInSceneController> {
            DetectUnusedSymbolsInSceneControllerImpl(applicationScope.get(), get(), get())
        }
    }

}