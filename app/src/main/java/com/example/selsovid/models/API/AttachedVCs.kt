package com.example.selsovid.models.API

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttachedVCs(val data: List<String>) : Parcelable {
}