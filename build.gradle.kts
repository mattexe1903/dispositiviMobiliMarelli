// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        // Aggiungi qui i tuoi repository, se necessario
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")  // Google Services plugin
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
}

