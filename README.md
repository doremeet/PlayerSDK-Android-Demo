# PlayerSDK-Android-Demo
Demo player with IBandSDK player for Andorid.

## How to Use


**Token** Go to strings.xml and put the demo token into "iband_token" key.

**StreamId** Go to PlayerActivity class and put the demo StreamId into STREAM_ID (located in the top of class).


## Getting Started

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
		maven { url 'https://dl.bintray.com/pureops/cedexis-oss-maven' }
	}
}
```
**Step 2.** Add the dependency

```gradle
dependencies {
    compile 'com.cedexis:android-radar:0.2.5'
    compile 'com.google.android.exoplayer:exoplayer:r2.4.4'
    compile 'io.iband:PlayerSDK-Android:v0.1.4@aar'
}
```

## Documation

[JavaDoc](https://jitpack.io/com/github/doremeet/PlayerSDK-Android/v0.1.4/javadoc/index.html)


