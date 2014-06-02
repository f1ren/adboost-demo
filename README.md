adboost-demo
============

Build instructions:

1. Copy the following into the 'libs' folder:
<pre>
  libs/
    armeabi-v7a/
      libocs.so
    adboost.jar
</pre>
2. Copy into the libs folder the jar(s) of the ad network(s) that you want to integrate with. Note that some of them will require you to reference an entire project, such as 'google-play-services_lib' or 'mopub-sdk'.
3. Edit AndroidManifest.xml: Remove the stuff that's irrelevant to the ad network(s) you're integarting with, and fill in the stuff which is relevant, if there is any (specifically package names for AirPush, LeadBolt and StartApp).
4. Edit res/values/adnet_config.xml and fill in the API key for AdBoost and the IDs for the ad networks you wish to use.
5. Comment out the code in the activities of the irrelevant ad networks (it won't compile because the jar is not in the libs folder).

Optional (if using only one ad network):

6. Edit AndroidManifest.xml and move the &lt;intent-filter&gt; element from MainActivity to the activity of that ad network.
7. Edit the java file of that activity and uncomment the code in the onCreate and onDestroy methods.
