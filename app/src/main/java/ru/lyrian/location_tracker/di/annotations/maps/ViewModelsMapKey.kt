package ru.lyrian.location_tracker.di.annotations.maps

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@MustBeDocumented
@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER])
@Retention(value = AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelsMapKey(val value: KClass<out ViewModel>)
