# Sample Android Portum SDK

Goal of this project demonstrate how to write own SDK for Portum Ad Server API. This project contains two modules:
 - sdk
 - sample

## SDK module

SDK module implemented as android library which expose single activity `com.portum.android.sdk.PortumAdActivity` and provide single point to work with SDK as developer `com.portum.android.sdk.PortumFacade`. For more information about `com.portum.android.sdk.PortumFacade` look in comments for methods.

Another classes puted in `com.portum.android.sdk.internal` package and all classes in this package have `final` modifier to exclude inproper usages

Packages description:
 - `com.portum.android.sdk.internal.adapter` - json conversion adapters
 - `com.portum.android.sdk.internal.helper` - helper classes
 - `com.portum.android.sdk.internal.model` - data models
 - `com.portum.android.sdk.internal.network` - ad server API client implementation

Important note that sdk have configuration of server location in `build.gradle`, so during debug it's point to stage server, and during release it's point to release server

## Sample module

Sample module implemented as simple single activity android application. On this activity you can set `Ad Unit Id` and show an ad related to this `Ad Unit Id`, `com.portum.android.sdk.PortumAdActivity` will be shown asynchronously

## Third-party dependencies

All third-party dependencies listed in `sdk/build.gradle`

 - [OkHttp](https://github.com/square/okhttp)
 - [Moshi](https://github.com/square/moshi)
 - [Universal Image Loader](https://github.com/nostra13/Android-Universal-Image-Loader)