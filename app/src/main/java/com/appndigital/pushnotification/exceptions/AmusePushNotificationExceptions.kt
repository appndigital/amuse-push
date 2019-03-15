package com.appndigital.pushnotification.exceptions

class DeviceUnsupportedException : Exception()

class GooglePlayServicesOutDatedException : Exception()

class GooglePlayServicesNotInstalledException : Exception()

class UnknownErrorException(message: String) : Exception(message)

class GetTagFromApiException() : Exception()

class SendTokenApiException(message: String) : Exception(message)