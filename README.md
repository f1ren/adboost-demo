adboost-demo
============

Build instructions:

1. [Download AdBoost SDK for Android](https://dashboard.adience.com/dashboard/#/download) and extract the `libs` folder into the `AdBoostDemo` project's folder:
```
  AdBoostDemo/
    libs/
      armeabi-v7a/
        libocs.so
      adboost-v###.jar
```
2. Copy into the `libs` folder the jar(s) of the ad network(s) that you want to integrate with. Note that some of them will require you to reference an entire project, such as `google-play-services_lib` or `mopub-sdk`.

