# irma_android_library

IRMA android library which provides the shared functionality between the IRMA android applications. In particular it enables reading of irma_configuration on android via the `AndroidWalker` library and it offers an interface for dealing with pin code entry in android applications.

This library is used by

 * [irma_android_verifier](https://github.com/credentials/irma_android_verifier/)
 * [irma_android_management](https://github.com/credentials/irma_android_management/)
 * [irma_android_cardproxy](https://github.com/credentials/irma_android_verifier/)

## Prerequisites

This library has the following dependencies.  All these dependencies will be automatically downloaded by gradle when building or installing the library.

Internal dependencies:

 * [credentials_idemix](https://github.com/credentials/credentials_idemix/), The IRMA credentials API implementation for Idemix

The build system depends on gradle version at least 2.1, which is why we've included the gradle wrapper, so you always have the right version.

## Building

Run

    ./gradlew build

## Installing

You can install the library to your local maven repository by running

    ./gradlew install

It will then be found by other gradle build scripts.
