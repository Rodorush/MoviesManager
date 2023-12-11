package br.com.rodorush.moviesmanager.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Movie (
    @PrimaryKey
    val name: String = "",
    val releaseYear: Int = 0,
    val studio: String = "",
    val duration: Int = 0,
    var watched: Boolean = false,
    val rating: Double,
    val genre: Genre
) : Parcelable