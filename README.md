# Accuracy Meter

## A beautiful meter to show the accuracy of user's actions such as speech recognition or performance in a game etc. 
 
## Features!

 •	 Set a threshold indicator, indicator's color, change the threshold at run time with an animation, threshold can be disabled
 
 •	 Set the number of lines in the meter, lines' width
 
 •	 Enable/disable the text showing progress in percentage, set progress text color, size, font, style, position (Bottom left or bottom right)
 
 •	 Animate the progress to a desired value, set the progress animation duration
 
 •   Blend the background bars into your design by manipulating the backgroundAlpha (by default 0.5f).
 

## Screen recording
 
 <img src="./screen_recording.gif" height="600">
 
# Install
 
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
## Gradle

```groovy
dependencies {
	 implementation 'com.github.SMehranB:AccuracyMeter:1.0.0'
}
```
## Maven
```xml
<dependency>
	<groupId>com.github.SMehranB</groupId>
	<artifactId>AccuracyMeter</artifactId>
	<version>1.0.0</version>
</dependency>
 ```
# Use
 
## XML
```xml
<com.smb.accuracymeter.AccuracyMeter
    android:id="@+id/accuracyMeter"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:am_animationDuration="2000"
    app:am_backgroundAlpha="0.5"
    app:am_ThresholdIndicatorEnabled="true"
    app:am_ThresholdIndicatorColor="#41FFF9"
    app:am_Threshold="75"
    app:am_linesCount="25"
    app:am_linesWidth="10dp"
    app:am_ProgressTextEnabled="false"
    app:am_ProgressTextColor="@color/white"
    app:am_ProgressTextPosition="BOTTOM_RIGHT"
    app:am_ProgressTextSize="24dp"
    app:am_ProgressTextStyle="bold" />
 ```

## Java

```java
AccuracyMeter accuracyMeter = new AccuracyMeter(this);
ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

accuracyMeter.setLayoutParams(params);
accuracyMeter.setProgressTextEnabled(true); //by default true
accuracyMeter.setThresholdRangeEnabled(true); //by default true
accuracyMeter.setThresholdIndicatorColor(Color.YELLOW); //default value is Color.GRAY
accuracyMeter.setTextColor(Color.YELLOW); //default value is Color.DKGRAY
accuracyMeter.setTextPosition(AccuracyMeter.TextPosition.BOTTOM_LEFT);
accuracyMeter.setTextSizeDp(24);
accuracyMeter.setLineCount(100);
accuracyMeter.setLineWidthDp(2);
accuracyMeter.setAnimationDuration(500); // default value is 1000
accuracyMeter.setThreshold(95); // default value is 70 (70%)
accuracyMeter.setTextStyle(Typeface.ITALIC);
accuracyMeter.setBackgroundAlpha(0.5f); //default value is 0.5

viewHolder.addView(accuracyMeter);
```

### Methods
```java
accuracyMeter.animateThresholdIndicatorTo(75) // animates indicator to 75%
accuracyMeter.animateProgressTo(83) // animates progress to 83%
```

## 📄 License
```text
MIT License

Copyright (c) 2021 Seyed Mehran Behbahani

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
