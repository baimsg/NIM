package com.baimsg.fog.ext.annotation

/**
 * Create by Baimsg on 2022/7/27
 * This annotation could keep no fog for string
 **/
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class StringFogIgnore
