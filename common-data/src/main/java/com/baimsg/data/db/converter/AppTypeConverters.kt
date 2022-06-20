package com.baimsg.data.db.converter

import androidx.room.TypeConverter
import com.baimsg.data.model.DEFAULT_JSON_FORMAT
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import kotlinx.serialization.Contextual
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.StructureKind

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
object AppTypeConverters {
    private val json = DEFAULT_JSON_FORMAT

    @TypeConverter
    fun listToString(value: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun stringToList(value: String): List<String> =
        json.decodeFromString(ListSerializer(String.serializer()), value)


    @TypeConverter
    fun genderEnumToInt(value: GenderEnum): Int =
        when (value) {
            GenderEnum.UNKNOWN -> -1
            GenderEnum.MALE -> 1
            GenderEnum.FEMALE -> 0
        }

    @TypeConverter
    fun intToGenderEnumTo(value: Int): GenderEnum =
        when (value) {
            1 -> GenderEnum.MALE
            0 -> GenderEnum.FEMALE
            else -> GenderEnum.UNKNOWN
        }

    @TypeConverter
    fun extensionMapToString(value: Map<String, String>): String =
        json.encodeToString(MapSerializer(String.serializer(), String.serializer()), value)

    @TypeConverter
    fun stringToExtensionMap(value: String): Map<String, String> =
        json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), value)


}