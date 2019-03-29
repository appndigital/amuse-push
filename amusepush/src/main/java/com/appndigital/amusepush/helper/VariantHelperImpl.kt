/*
 * Copyright (c) 2018 by Appndigital, Inc.
 * All Rights Reserved
 */

package com.appndigital.amusepush.helper

import android.content.Context
import com.appndigital.amusepush.BuildConfig
import com.appndigital.amusepush.R

enum class EnvironmentType {
    DEV,
    PROD
}

class VariantHelper {

    companion object {

        fun getBackendEndPoint(context: Context): String {
            return when (getEnvironmentType()) {
                EnvironmentType.DEV -> context.resources.getString(R.string.dev_url)
                EnvironmentType.PROD -> context.resources.getString(R.string.prod_url)
            }
        }

        private fun getEnvironmentType(): EnvironmentType {

            if (getBuildType() == "debug") {
                return EnvironmentType.DEV
            }
            if (getBuildType() == "release") {
                return EnvironmentType.PROD
            }

            throw Exception("no EnvironmentType find for ${getBuildType()}")
        }

        private fun getBuildType(): String {
            return BuildConfig.BUILD_TYPE
        }
    }


}