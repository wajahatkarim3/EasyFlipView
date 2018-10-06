EasyFlipView
============
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b6f4c512c5cf4705a41a04fe101a6c6e)](https://www.codacy.com/app/wajahatkarim3/EasyFlipView?utm_source=github.com&utm_medium=referral&utm_content=wajahatkarim3/EasyFlipView&utm_campaign=badger)
[![Build Status](https://travis-ci.org/wajahatkarim3/EasyFlipView.svg?branch=master)](https://travis-ci.org/wajahatkarim3/EasyFlipView) [![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fwajahatkarim3%2FEasyFlipView.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fwajahatkarim3%2FEasyFlipView?ref=badge_shield)
[ ![Download](https://api.bintray.com/packages/wajahatkarim3/EasyFlipView/com.wajahatkarim3.EasyFlipView/images/download.svg) ](https://bintray.com/wajahatkarim3/EasyFlipView/com.wajahatkarim3.EasyFlipView/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-EasyFlipView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5051) [![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=15) [![](https://img.shields.io/badge/MaterialUp-EasyFlipView-yellowgreen.png)](https://material.uplabs.com/posts/easyflipview) [![Say Thanks!](https://img.shields.io/badge/Say%20Thanks-!-1EAEDB.svg)](https://saythanks.io/to/wajahatkarim3)

A quick and easy flip view through which you can create views with two sides like credit cards, poker cards etc. 

![](https://github.com/wajahatkarim3/EasyFlipView/blob/master/Art/demo.gif)

Changelog
=========
Changes exist in the [releases](https://github.com/wajahatkarim3/EasyFlipView/releases) tab.

Installation
============
Add this in your app's build.gradle file:
```groovy
dependencies {
  implementation 'com.wajahatkarim3.EasyFlipView:EasyFlipView:2.1.0'
}
```

Or add EasyFlipView as a new dependency inside your pom.xml

```xml
<dependency> 
  <groupId>com.wajahatkarim3.EasyFlipView</groupId>
  <artifactId>EasyFlipView</artifactId> 
  <version>2.1.0</version>
  <type>pom</type> 
</dependency>
```
Usage
=====

XML
---
EasyFlipView In XML layouts("Vertical")
```xml
<com.wajahatkarim3.easyflipview.EasyFlipView
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	app:flipOnTouch="true"
	app:flipEnabled="true"
	app:flipDuration="400"
	app:flipType="vertical"
	app:flipFrom="front"
	>

	<!-- Back Layout Goes Here -->
	<include layout="@layout/flash_card_layout_back"/>
        
	<!-- Front Layout Goes Here -->
	<include layout="@layout/flash_card_layout_front"/>

</com.wajahatkarim3.easyflipview.EasyFlipView>
```

EasyFlipView In XML layouts("Horizontal")
```xml
<com.wajahatkarim3.easyflipview.EasyFlipView
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	app:flipOnTouch="true"
	app:flipEnabled="true"
	app:flipDuration="400"
	app:flipFrom="right"
	app:flipType="horizontal"
	>

	<!-- Back Layout Goes Here -->
	<include layout="@layout/flash_card_layout_back"/>

	<!-- Front Layout Goes Here -->
	<include layout="@layout/flash_card_layout_front"/>

</com.wajahatkarim3.easyflipview.EasyFlipView>
```

All customizable attributes for EasyFlipView
<table>
    <th>Attribute Name</th>
    <th>Default Value</th>
    <th>Description</th>
    <tr>
        <td>app:flipOnTouch="true"</td>
        <td>true</td>
        <td>Whether card should be flipped on touch or not.</td>
    </tr>
    <tr>
        <td>app:flipDuration="400"</td>
        <td>400</td>
        <td>The duration of flip animation in milliseconds.</td>
    </tr>
    <tr>
        <td>app:flipEnabled="true"</td>
        <td>true</td>
        <td>If this is set to false, then it won't flip ever in Single View and it has to be always false for RecyclerView</td>
    </tr>
     <tr>
            <td>app:flipType="horizontal"</td>
            <td>vertical</td>
            <td>Whether card should flip in vertical or horizontal</td>
        </tr>
        <tr>
                    <td>app:flipType="horizontal"</td>
                    <td>vertical</td>
                    <td>Whether card should flip in vertical or horizontal</td>
                </tr>
        	 <tr>
                    <td>app:flipFrom="right"
        		app:flipFrom="back"</td>
                    <td>left
        		front</td>
                    <td>Whether card should flip from left to right Or right to left(Horizontal type) or car should flip to front or back(Vertical type)</td>
                </tr>
    </table>

In Code (Java)
----
```java
// Flips the view with or without animation
mYourFlipView.flipTheView();
mYourFlipView.flipTheView(false);

// Sets and Gets the Flip Animation Duration in milliseconds (Default is 400 ms)
mYourFlipView.setFlipDuration(1000);
int dur = mYourFlipView.getFlipDuration();

// Sets and gets the flip enable status (Default is true)
mYourFlipView.setFlipEnabled(false);
boolean flipStatus = mYourFlipView.isFlipEnabled();

// Sets and gets the flip on touch status (Default is true)
mYourFlipView.setFlipOntouch(false);
boolean flipTouchStatus = mYourFlipView.isFlipOnTouch();

// Get current flip state in enum (FlipState.FRONT_SIDE or FlipState.BACK_SIDE)
EasyFlipView.FlipState flipSide = mYourFlipView.getCurrentFlipState();

// Get whether front/back side of flip is visible or not.
boolean frontVal = mYourFlipView.isFrontSide();
boolean backVal = mYourFlipView.isBackSide();

// Get/Set the FlipType to FlipType.Horizontal
boolean isHorizontal = mYourFlipView.isHorizontalType();
mYourFlipView.setToHorizontalType();

// Get/Set the FlipType to FlipType.Vertical
boolean isVertical = mYourFlipView.isVerticalType();
mYourFlipView.setToVerticalType();

```

Flip Animation Listener
---
```java
EasyFlipView easyFlipView = (EasyFlipView) findViewById(R.id.easyFlipView);
easyFlipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView flipView, EasyFlipView.FlipState newCurrentSide) 
            {
                
                // ...
                // Your code goes here
                // ...
                
            }
        });
```

Donations
=============

This project needs you! If you would like to support this project's further development, the creator of this project or the continuous maintenance of this project, feel free to donate. Your donation is highly appreciated (and I love food, coffee and beer). Thank you!

**PayPal**

* **[Donate $5](https://www.paypal.me/WajahatKarim/5)**: Thank's for creating this project, here's a tea (or some juice) for you!
* **[Donate $10](https://www.paypal.me/WajahatKarim/10)**: Wow, I am stunned. Let me take you to the movies!
* **[Donate $15](https://www.paypal.me/WajahatKarim/15)**: I really appreciate your work, let's grab some lunch!
* **[Donate $25](https://www.paypal.me/WajahatKarim/25)**: That's some awesome stuff you did right there, dinner is on me!
* **[Donate $50](https://www.paypal.me/WajahatKarim/50)**: I really really want to support this project, great job!
* **[Donate $100](https://www.paypal.me/WajahatKarim/100)**: You are the man! This project saved me hours (if not days) of struggle and hard work, simply awesome!
* **[Donate $2799](https://www.paypal.me/WajahatKarim/2799)**: Go buddy, buy Macbook Pro for yourself!

Of course, you can also choose what you want to donate, all donations are awesome!

Developed By
============
```
Wajahat Karim
```
- Website (http://wajahatkarim.com)
- Twitter (http://twitter.com/wajahatkarim)
- Medium (http://www.medium.com/@wajahatkarim3)
- LinkedIn (http://www.linkedin.com/in/wajahatkarim)

Special Thanks
=========
- [**iGio90**](https://github.com/iGio90) for adding dynamic views support [Pull Request # 10](https://github.com/wajahatkarim3/EasyFlipView/pull/10)
- [**Sachin Varma**](https://www.linkedin.com/in/sachin-varma-58b243118/) for adding vertical animations support [Pull Request # 12](https://github.com/wajahatkarim3/EasyFlipView/pull/12)
- [**Sachin Varma**](https://www.linkedin.com/in/sachin-varma-58b243118/) for adding multi-dimension animations support [Pull Request # 23](https://github.com/wajahatkarim3/EasyFlipView/pull/23)


# How to Contribute
1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request

# License

    Copyright 2017 Wajahat Karim

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fwajahatkarim3%2FEasyFlipView.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fwajahatkarim3%2FEasyFlipView?ref=badge_large)