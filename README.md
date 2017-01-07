EasyFlipView
============
 [ ![Download](https://api.bintray.com/packages/wajahatkarim3/EasyFlipView/com.wajahatkarim3.EasyFlipView/images/download.svg) ](https://bintray.com/wajahatkarim3/EasyFlipView/com.wajahatkarim3.EasyFlipView/_latestVersion)  [![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=15)

A quick and easy flip view through which you can create views with two sides like credit cards, poker cards etc. 

![](https://github.com/wajahatkarim3/EasyFlipView/blob/master/Art/demo.gif)

Demo
====
Install [Demo](https://github.com/wajahatkarim3/EasyFlipView/releases/download/1.0.0/EasyFlipView-Demo_v1.0.0.apk) app or APK from [Releases](https://github.com/wajahatkarim3/EasyFlipView/releases) on your device and click on any card to flip it!

Installation
============
Add this in your app's build.gradle file:
```groovy
dependencies {
  compile 'com.wajahatkarim3.EasyFlipView:EasyFlipView:1.0.0'
}
```

Or add DBFlowManager as a new dependency inside your pom.xml

```xml
<dependency> 
  <groupId>com.wajahatkarim3.EasyFlipView</groupId>
  <artifactId>EasyFlipView</artifactId> 
  <version>1.0.0</version> 
  <type>pom</type> 
</dependency>
```
Usage
=====

XML
---
EasyFlipView In XML layouts
```xml
<com.wajahatkarim3.easyflipview.EasyFlipView
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	app:flipOnTouch="true"
	app:flipEnabled="true"
	app:flipDuration="400">

	<!-- Back Layout Goes Here -->
	<include layout="@layout/flash_card_layout_back"/>
        
	<!-- Front Layout Goes Here -->
	<include layout="@layout/flash_card_layout_front"/>

</com.wajahatkarim3.easyflipview.EasyFlipView>
```
All customizable attributes for EasyFlipView
```xml
<declare-styleable name="easy_flip_view">
	<!-- Whether card should be flipped on touch or not (Default is true) -->
	<attr name="flipOnTouch" format="boolean"/>
	<!-- The duration of flip animation in milliseconds (Default is 400 ms) -->
	<attr name="flipDuration" format="integer"/>
	<!-- If this is set to false, then it won't flip ever (Default is true) -->
	<attr name="flipEnabled" format="boolean"/>
</declare-styleable>
```

In Code (Java)
----
```java
// Flips the view with animation
mYourFlipView.flipTheView();

// Sets and Gets the Flip Animation Duration in milliseconds (Default is 400 ms)
mYourFlipView.setFlipDuration(1000);
int dur = mYourFlipView.getFlipDuration();

// Sets and gets the flip enable status (Default is true)
mYourFlipView.setFlipEnabled(false);
boolean flipStatus = mYourFlipView.isFlipEnabled();

// Sets and gets the flip on touch status (Default is true)
mYourFlipView.setFlipOntouch(false);
boolean flipTouchStatus = mYourFlipView.isFlipOnTouch();

```

Developed By
============
```
Wajahat Karim
```
- Website (http://wajahatkarim.com)
- Twitter (http://twitter.com/wajahatkarim)
- Medium (http://www.medium.com/@wajahatkarim3)
- LinkedIn (http://www.linkedin.com/in/wajahatkarim)

# How to Contribute
1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request

# License

    Copyright 2016 Wajahat Karim

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
