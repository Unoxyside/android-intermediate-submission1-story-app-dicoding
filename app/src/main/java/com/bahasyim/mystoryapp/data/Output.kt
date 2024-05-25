package com.bahasyim.mystoryapp.data

sealed class Output<out R> private constructor(){
    data class Success<out T>(val value: T) : Output<T>()
    data class Error(val error: String) : Output<Nothing>()
    object Loading : Output<Nothing>()
}