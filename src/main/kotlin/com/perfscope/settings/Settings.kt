package com.perfscope.settings

import com.perfscope.settings.serializer.BooleanPropertySerializer
import com.perfscope.settings.serializer.IntegerPropertySerializer
import com.perfscope.settings.serializer.StringPropertySerializer
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.scene.text.Font
import kotlinx.serialization.Serializable

@Serializable
class Settings {

    @Serializable(with = StringPropertySerializer::class)
    val theme: StringProperty = SimpleStringProperty(Theme.DARK.name)
    @Serializable(with = IntegerPropertySerializer::class)
    val systemFontSize = SimpleIntegerProperty(15)
    @Serializable(with = StringPropertySerializer::class)
    val systemFontName: StringProperty = SimpleStringProperty(Font.getDefault().name)
    @Serializable(with = IntegerPropertySerializer::class)
    val recordingTreeFontSize = SimpleIntegerProperty(15)
    @Serializable(with = IntegerPropertySerializer::class)
    val recordingTreeFontSpacing = SimpleIntegerProperty(8)
    @Serializable(with = StringPropertySerializer::class)
    val recordingTreeFontName: StringProperty = SimpleStringProperty(Font.getDefault().name)
    @Serializable(with = BooleanPropertySerializer::class)
    val recordingListShowThreads: BooleanProperty = SimpleBooleanProperty(true)
    @Serializable(with = BooleanPropertySerializer::class)
    val recordingTreeBoldElements: BooleanProperty = SimpleBooleanProperty(true)
    @Serializable(with = IntegerPropertySerializer::class)
    val recordingListSpacing = SimpleIntegerProperty(3)
//    @Serializable(with = StringPropertySerializer::class)
//    val recordedCallWeightType: StringProperty = SimpleStringProperty(RecordedCallWeightType.CALLS.name)

    fun addListener(listener: ChangeListener<Any>) {
        theme.addListener(listener)
        systemFontSize.addListener(listener)
        systemFontName.addListener(listener)
        recordingTreeFontSize.addListener(listener)
        recordingTreeFontSpacing.addListener(listener)
        recordingTreeFontName.addListener(listener)
        recordingListShowThreads.addListener(listener)
        recordingTreeBoldElements.addListener(listener)
        recordingListSpacing.addListener(listener)
    }
}