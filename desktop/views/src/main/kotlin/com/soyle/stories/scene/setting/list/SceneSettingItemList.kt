package com.soyle.stories.scene.setting.list

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.invitation
import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.inviteButton
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import com.soyle.stories.domain.scene.events.LocationUsedInScene
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneReceiver
import com.soyle.stories.scene.locationsInScene.listLocationsInScene.ListLocationsInSceneController
import com.soyle.stories.scene.locationsInScene.listLocationsToUse.ListLocationsToUseInSceneController
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.scene.setting.list.SceneSettingInviteImage.Companion.sceneSettingInviteImage
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import com.soyle.stories.scene.setting.list.SceneSettingItemList.Styles.Companion.sceneSettingItemList
import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.useLocationButton.AvailableSceneSettingModel
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Stylesheet.Companion.header

class SceneSettingItemList(
    private val sceneId: Scene.Id,

    private val locale: SceneSettingItemListLocale,

    private val listLocationsInSceneController: ListLocationsInSceneController,

    private val sceneSettingRemoved: Notifier<LocationRemovedFromSceneReceiver>,
    private val sceneSettingAdded: Notifier<LocationUsedInSceneReceiver>,

    private val makeSceneSettingItem: SceneSettingItemView.Factory,
    private val makeUseLocationButton: UseLocationButton.Factory
) : VBox() {

    /**
     * [invoke]
     */
    fun interface Factory {

        operator fun invoke(sceneId: Scene.Id): SceneSettingItemList
    }

    private val model: ObjectProperty<SceneSettingItemListModel> =
        object : ObjectPropertyBase<SceneSettingItemListModel>(SceneSettingItemListModel.Loading) {
            override fun getBean(): Any = this@SceneSettingItemList
            override fun getName(): String = "model"
            override fun set(newValue: SceneSettingItemListModel?) {
                if (!Platform.isFxApplicationThread()) {
                    runLater { super.set(newValue) }
                } else {
                    super.set(newValue)
                }
            }
        }

    private fun loadUsedLocations() {
        model.set(SceneSettingItemListModel.Loading)
        listLocationsInSceneController
            .listLocationsInScene(sceneId) {
                model.set(
                    SceneSettingItemListModel.Loaded(
                        it.map {
                            SceneSettingItemModel(
                                sceneId,
                                it.id,
                                it.locationName,
                                booleanProperty(false)
                            )
                        }.toObservable(),
                        listProperty(FXCollections.emptyObservableList()),
                    )
                )
            }
            .invokeOnCompletion { failure ->
                if (failure != null) model.set(SceneSettingItemListModel.Error)
            }
    }

    private val domainEventListener = object :
        LocationRemovedFromSceneReceiver,
        LocationUsedInSceneReceiver {
        override suspend fun receiveLocationRemovedFromScenes(events: List<LocationRemovedFromScene>) {
            val removedLocationIds = events.asSequence()
                .filter { it.sceneId == sceneId }
                .map { it.sceneSetting.id }
                .toSet()
            if (removedLocationIds.isEmpty()) return
            guiUpdate {
                val currentModel = model.value
                if (currentModel is SceneSettingItemListModel.Loaded) {
                    currentModel.sceneSettings.removeIf { it.locationId in removedLocationIds }
                }
            }
        }

        override suspend fun receiveLocationUsedInScene(locationUsedInScene: LocationUsedInScene) {
            if (locationUsedInScene.sceneId != sceneId) return
            guiUpdate {
                val currentModel = model.value
                if (currentModel is SceneSettingItemListModel.Loaded) {
                    currentModel.sceneSettings.add(
                        SceneSettingItemModel(
                            sceneId,
                            locationUsedInScene.sceneSetting.id,
                            locationUsedInScene.sceneSetting.locationName,
                            booleanProperty(false)
                        )
                    )
                }
            }
        }
    }

    init {
        sceneSettingRemoved.addListener(domainEventListener)
        sceneSettingAdded.addListener(domainEventListener)
    }

    private val isLoading = booleanBinding(model) { model.value == SceneSettingItemListModel.Loading }

    init {
        addClass(sceneSettingItemList)
        toggleClass(loading, isLoading)

        dynamicContent(model) { determineContent() }
        loadUsedLocations()
    }

    private fun determineContent() {
        when (val state = model.value) {
            is SceneSettingItemListModel.Loading -> progressindicator()
            is SceneSettingItemListModel.Error -> error()
            is SceneSettingItemListModel.Loaded -> loaded(state)
        }
    }

    @ViewBuilder
    private fun error() {
        fieldLabel {
            addClass(Stylesheet.error)
            textProperty().bind(locale.failedToLoadUsedLocations)
        }
        primaryButton {
            addClass(ComponentsStyles.invitation)
            addClass(Styles.retry)
            textProperty().bind(locale.retry)
            action(::loadUsedLocations)
        }
    }

    @ViewBuilder
    private fun loaded(
        model: SceneSettingItemListModel.Loaded,
        useLocationButton: UseLocationButton = useLocationButton(model)
    ) {
        if (model.sceneSettings.isEmpty()) invitation(useLocationButton)
        else populated(model, useLocationButton)

        val wasEmpty = model.sceneSettings.isEmpty()
        val emptyListener = object : ListChangeListener<SceneSettingItemModel> {
            override fun onChanged(c: ListChangeListener.Change<out SceneSettingItemModel>?) {
                if (c == null) return
                if (wasEmpty != c.list.isEmpty()) {
                    model.sceneSettings.removeListener(this)
                    val currentModel = this@SceneSettingItemList.model.value
                    if (currentModel is SceneSettingItemListModel.Loaded) {
                        children.clear()
                        loaded(currentModel, useLocationButton)
                    }
                }
            }
        }
        model.sceneSettings.addListener(emptyListener)
    }

    @ViewBuilder
    private fun invitation(useLocationButton: UseLocationButton) {
        toggleClass(invitation, true)
        sceneSettingInviteImage()
        toolTitle { textProperty().bind(locale.useLocationsAsSceneSetting) }
        textflow {
            addClass(TextStyles.fieldLabel)
            dynamicContent(locale.noLocationUsedInSceneMessage) {
                it?.invoke(this)
            }
        }
        add(useLocationButton)
    }

    @ViewBuilder
    private fun populated(model: SceneSettingItemListModel.Loaded, useLocationButton: UseLocationButton) {
        toggleClass(invitation, false)
        hbox {
            addClass(header)
            sectionTitle(locale.sceneSettings)
            spacer()
            add(useLocationButton)
        }
        vbox {
            addClass(Styles.itemList)
            bindChildren(model.sceneSettings) {
                makeSceneSettingItem(it)
            }
        }
    }

    private fun useLocationButton(model: SceneSettingItemListModel.Loaded) =
        makeUseLocationButton(sceneId).apply {
            toggleClass(inviteButton, model.sceneSettings.emptyProperty())
        }

    override fun getUserAgentStylesheet(): String = Styles().externalForm

    class Styles : Stylesheet() {

        companion object {

            val sceneSettingItemList by cssclass()
            val itemList by cssclass()
            val retry by cssclass()

            init {
                importStylesheet<Styles>()
                SceneSettingItemView.Styles
                UseLocationButton.Styles
            }
        }

        init {
            sceneSettingItemList {
                spacing = 16.px
                padding = box(16.px)
                fillWidth = true
                and(loading) {
                    alignment = Pos.CENTER
                }

                error {
                    textFill = Color.RED
                }

                header {
                    alignment = Pos.CENTER_LEFT
                }

                itemList {
                    spacing = 8.px
                    fillWidth = false
                    alignment = Pos.TOP_LEFT
                }
            }
        }

    }

}