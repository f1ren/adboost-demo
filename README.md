adboost-demo
============

Build instructions:

1. Copy the following into the 'libs' folder:
<pre>
  libs/
    armeabi-v7a/
      libocs.so
    adboost-v1.3.jar
</pre>
2. Copy into the libs folder the jar(s) of the ad network(s) that you want to integrate with. Note that some of them will require you to reference an entire project, such as 'google-play-services_lib' or 'mopub-sdk'.
3. Edit AndroidManifest.xml: Fill in the relevant stuff to your ad networks, if any, and delete what you don't need (specifically package name for AirPush).
4. Edit <code>DynamicActivity.java</code> and assign to <code>MY_AD_NETWORKS</code> a new array with names of the ad networks from step 2.
