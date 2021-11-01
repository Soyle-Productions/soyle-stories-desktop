package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.TimelineViewPortRulerLabelComponentDouble
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.PickResult
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class `Ruler Time Label Unit Test` {

    private val component = TimelineViewPortRulerLabelComponentDouble()

    @Nested
    inner class `Can select time by clicking on label` {

        private val selection = TimelineSelectionModel()

        @Test
        fun `click without keys pressed should start selection`() {
            val label = component.TimeSpanLabel(selection)
            label.range = TimeRange(42L .. 43)
            val event = MouseEvent(
                this,
                label,
                MouseEvent.MOUSE_PRESSED,
                8.0, 4.0,
                8.0, 4.0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                PickResult(label, 8.0, 4.0)
            )

            label.fireEvent(event)

            val singleSelection = selection.timeRanges.single()
            assertThat(singleSelection.start).isEqualTo(UnitOfTime(42))
            assertThat(singleSelection.endInclusive).isEqualTo(UnitOfTime(43))
        }

        @Test
        fun `dragging over a time label should extend the current selection`() {
            selection.restart(UnitOfTime(16))
            val label = component.TimeSpanLabel(selection)
            label.range = TimeRange(42L .. 43)
            val event = MouseEvent(
                this,
                label,
                MouseEvent.MOUSE_DRAGGED,
                8.0, 4.0,
                8.0, 4.0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                true, // <- simulates the primary mouse dragging into this label
                false,
                false,
                false,
                false,
                false,
                PickResult(label, 8.0, 4.0)
            )

            label.fireEvent(event)

            val singleSelection = selection.timeRanges.single()
            assertThat(singleSelection.start).isEqualTo(UnitOfTime(16))
            assertThat(singleSelection.endInclusive).isEqualTo(UnitOfTime(43))
        }

        @Test
        fun `if shift is pressed, should extend selection`() {
            selection.restart(UnitOfTime(16))
            val label = component.TimeSpanLabel(selection)
            label.range = TimeRange(42L .. 43)
            val event = MouseEvent(
                this,
                label,
                MouseEvent.MOUSE_PRESSED,
                8.0, 4.0,
                8.0, 4.0,
                MouseButton.PRIMARY,
                1,
                true, // <- simulates shift key pressed
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                PickResult(label, 8.0, 4.0)
            )

            label.fireEvent(event)

            val singleSelection = selection.timeRanges.single()
            assertThat(singleSelection.start).isEqualTo(UnitOfTime(16))
            assertThat(singleSelection.endInclusive).isEqualTo(UnitOfTime(43))
        }

        @Test
        fun `if control or command is pressed, should add new selection`() {
            selection.restart(UnitOfTime(16))
            val label = component.TimeSpanLabel(selection)
            label.range = TimeRange(42L .. 43)
            val event = MouseEvent(
                this,
                label,
                MouseEvent.MOUSE_PRESSED,
                8.0, 4.0,
                8.0, 4.0,
                MouseButton.PRIMARY,
                1,
                false,
                true, // <- simulates shift key pressed
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                PickResult(label, 8.0, 4.0)
            )

            label.fireEvent(event)

            val previousSelection = selection.timeRanges.single { it.start == UnitOfTime(16) }
            val createdSelection = selection.timeRanges.single { it.start == UnitOfTime(42) }
            assertThat(previousSelection.endInclusive).isEqualTo(UnitOfTime(17))
            assertThat(createdSelection.endInclusive).isEqualTo(UnitOfTime(43))
        }

        @Test
        fun `if control and command is not pressed, should restart selection`() {
            selection.restart(UnitOfTime(16))
            val label = component.TimeSpanLabel(selection)
            label.range = TimeRange(42L .. 43)
            val event = MouseEvent(
                this,
                label,
                MouseEvent.MOUSE_PRESSED,
                8.0, 4.0,
                8.0, 4.0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                PickResult(label, 8.0, 4.0)
            )

            label.fireEvent(event)

            val singleSelection = selection.timeRanges.single()
            assertThat(singleSelection.start).isEqualTo(UnitOfTime(42))
            assertThat(singleSelection.endInclusive).isEqualTo(UnitOfTime(43))
        }

        @Test
        fun `when representing a larger time range, should select the entire time range`() {
            val label = component.TimeSpanLabel(selection)
            label.range = TimeRange(42L .. 64)
            val event = MouseEvent(
                this,
                label,
                MouseEvent.MOUSE_PRESSED,
                8.0, 4.0,
                8.0, 4.0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                PickResult(label, 8.0, 4.0)
            )

            label.fireEvent(event)

            val singleSelection = selection.timeRanges.single()
            assertThat(singleSelection.start).isEqualTo(UnitOfTime(42))
            assertThat(singleSelection.endInclusive).isEqualTo(UnitOfTime(64))
        }

    }

    companion object {
        init {
            FxToolkit.registerPrimaryStage()
        }
    }

}