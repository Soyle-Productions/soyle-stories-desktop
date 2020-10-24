package com.soyle.stories.desktop.config.scene

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterNotifier
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.character.renameCharacter.RenamedCharacterNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneNotifier
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneNotifier
import com.soyle.stories.scene.createNewScene.CreateNewSceneNotifier
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogController
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogPresenter
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogViewListener
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialogModel
import com.soyle.stories.scene.deleteScene.DeleteSceneNotifier
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogController
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogModel
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogPresenter
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogViewListener
import com.soyle.stories.scene.deleteSceneRamifications.*
import com.soyle.stories.scene.includeCharacterInScene.IncludedCharacterInSceneNotifier
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneNotifier
import com.soyle.stories.scene.removeCharacterFromScene.RemoveCharacterFromSceneNotifier
import com.soyle.stories.scene.renameScene.RenameSceneNotifier
import com.soyle.stories.scene.reorderScene.ReorderSceneNotifier
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogController
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogModel
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogPresenter
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogViewListener
import com.soyle.stories.scene.reorderSceneRamifications.*
import com.soyle.stories.scene.sceneDetails.*
import com.soyle.stories.scene.sceneDetails.includedCharacter.*
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneController
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInScenePresenter
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneState
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneViewListener
import com.soyle.stories.scene.sceneList.SceneListController
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.sceneList.SceneListPresenter
import com.soyle.stories.scene.sceneList.SceneListViewListener
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier

object Presentation {

    init {
        scoped<ProjectScope> {

            provide<CreateNewSceneDialogViewListener> {
                CreateNewSceneDialogController(
                    CreateNewSceneDialogPresenter(
                        get<CreateSceneDialogModel>(),
                        get<CreateNewSceneNotifier>()
                    ),
                    get()
                )
            }
            provide<DeleteSceneDialogViewListener> {
                val presenter = DeleteSceneDialogPresenter(
                    get<DeleteSceneDialogModel>()
                )
                DeleteSceneDialogController(
                    applicationScope.get(),
                    presenter,
                    get(),
                    get(),
                    get(),
                    presenter,
                    get()
                )
            }
            provide<ReorderSceneDialogViewListener> {
                ReorderSceneDialogController(
                    applicationScope.get(),
                    ReorderSceneDialogPresenter(
                        get<ReorderSceneDialogModel>()
                    ),
                    get(),
                    get(),
                    get(),
                    get()
                )
            }

            provide<SceneListViewListener> {
                SceneListController(
                    applicationScope.get(),
                    get(),
                    SceneListPresenter(
                        get<SceneListModel>(),
                        get<CreateNewSceneNotifier>(),
                        get<RenameSceneNotifier>(),
                        get<DeleteSceneNotifier>(),
                        get<ReorderSceneNotifier>()
                    ),
                    get(),
                    get(),
                    get()
                )
            }

        }

        scoped<DeleteSceneRamificationsScope> {

            provide<DeleteSceneRamificationsViewListener> {
                DeleteSceneRamificationsController(
                    sceneId,
                    toolId,
                    applicationScope.get(),
                    applicationScope.get(),
                    projectScope.get(),
                    DeleteSceneRamificationsPresenter(
                        get<DeleteSceneRamificationsModel>(),
                        projectScope.get<DeleteSceneNotifier>(),
                        projectScope.get<RemovedCharacterNotifier>(),
                        projectScope.get<SetMotivationForCharacterInSceneNotifier>()
                    ),
                    projectScope.get(),
                    projectScope.get()
                )
            }

        }

        scoped<ReorderSceneRamificationsScope> {
            provide<ReorderSceneRamificationsViewListener> {
                ReorderSceneRamificationsController(
                    sceneId,
                    toolId,
                    reorderIndex,
                    applicationScope.get(),
                    projectScope.get(),
                    ReorderSceneRamificationsPresenter(
                        get<ReorderSceneRamificationsModel>(),
                        projectScope.get<DeleteSceneNotifier>(),
                        projectScope.get<RemoveCharacterFromSceneNotifier>(),
                        projectScope.get<SetMotivationForCharacterInSceneNotifier>()
                    ),
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }

        scoped<SceneDetailsScope> {
            provide<SceneDetailsViewListener> {
                SceneDetailsController(
                    sceneId.toString(),
                    projectScope.applicationScope.get(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    SceneDetailsPresenter(
                        sceneId.toString(),
                        get<SceneDetailsModel>(),
                        projectScope.get(),
                        projectScope.get<LinkLocationToSceneNotifier>(),
                        projectScope.get<ReorderSceneNotifier>(),
                    ),
                    projectScope.get(),
                )
            }

            provide<IncludedCharactersInSceneViewListener> {
                val presenter = IncludedCharactersInScenePresenter(
                    sceneId.toString(),
                    get<IncludedCharactersInSceneState>()
                ).also {
                    it listensTo projectScope.get<CreatedCharacterNotifier>()
                    it listensTo projectScope.get<RemovedCharacterNotifier>()
                    it listensTo projectScope.get<IncludedCharacterInSceneNotifier>()
                    it listensTo projectScope.get<RemoveCharacterFromSceneNotifier>()
                }

                IncludedCharactersInSceneController(
                    sceneId.toString(),
                    projectScope.get(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter
                )
            }
        }

        scoped<IncludedCharacterScope> {
            provide<IncludedCharacterInSceneViewListener> {

                val presenter = IncludedCharacterInScenePresenter(
                    sceneDetailsScope.sceneId.toString(),
                    characterId,
                    get<IncludedCharacterInSceneState>()
                ).apply {
                    listensTo(projectScope.get<RenamedCharacterNotifier>())
                    listensTo(projectScope.get<SetMotivationForCharacterInSceneNotifier>())
                    listensTo(projectScope.get<CharacterArcSectionsCoveredBySceneNotifier>())
                    listensTo(projectScope.get<CharacterArcSectionUncoveredInSceneNotifier>())
                }

                IncludedCharacterInSceneController(
                    sceneDetailsScope.sceneId.toString(),
                    storyEventId,
                    characterId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    presenter
                )
            }
        }

    }

}