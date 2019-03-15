/*
 * Copyright (c) 2018 by Appndigital, Inc.
 * All Rights Reserved
 */

package com.appndigital.pushnotification.helper

import com.appndigital.pushnotification.BuildConfig
import com.appndigital.pushnotification.Constants

enum class EnvironmentType {
    DEV,
    PROD
}

class VariantHelper {

    companion object {

        fun getBackendEndPoint(): String {
            return when (getEnvironmentType()) {
                EnvironmentType.DEV -> Constants.DEV_URL
                EnvironmentType.PROD -> Constants.PROD_URL
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