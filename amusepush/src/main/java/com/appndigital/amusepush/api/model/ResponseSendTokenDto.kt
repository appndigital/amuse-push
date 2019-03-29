package com.appndigital.amusepush.api.model

import com.squareup.moshi.Json

data class ResponseSendTokenDto(

    @field:Json(name = "id")
    val id: Int,

    @field:Json(name = "message")
    val message: String? = null

)
