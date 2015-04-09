# Material Floating Label

Floating Label according to Material Design spec.

Contains floating label, floating helper/error and floating character counter widgets
for use mainly with `EditText` but capable of handling any other view.

The library is now available from API 4.

## How to get the library?

To use this library add the following to your module's `build.gradle`:
```groovy
dependencies {
    compile 'net.xpece.material:floating-label:0.2.1'
}
```

The library depends on NineOldAndroids.

## Usage

Your "input unit" layout may look like this (using `appcompat-v7` library):

        <LinearLayout
            android:orientation="vertical"
            android:paddingTop="8dp"
            android:paddingBottom="8dp"
            android:paddingLeft="4dp"
            android:paddingRight="4dp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <net.xpece.material.floatinglabel.FloatingLabelView
                android:paddingLeft="@dimen/abc_control_inset_material"
                android:paddingRight="@dimen/abc_control_inset_material"
                app:flv_textDefault="Password"
                app:flv_ownerView="@+id/et_password"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

            <EditText
                android:id="@+id/et_password"
                android:inputType="textPassword"
                android:hint="Password"
                android:minEms="10"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

            <net.xpece.material.floatinglabel.FloatingHelperView
                android:id="@+id/helper_password"
                android:paddingLeft="@dimen/abc_control_inset_material"
                android:paddingRight="@dimen/abc_control_inset_material"
                app:flv_textDefault="Enter your password"
                app:flv_textError="The password is wrong. Try again."
                app:flv_ownerView="@id/et_password"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>
        </LinearLayout>

## Customization

All floating labels extend `TextView` so all its attributes are applicable as well.

All custom attributes may be accessed via getters and setters.

### All floating label widgets

These custom attributes may be used with all floating label widgets:

    <item name="flv_trigger">focus</item>

Trigger determines when the label shows.
- `focus` means it will show on enter and hide on leave with no text entered.
- `text` means it will show when there's a text in the owner view. If the owner view is not a
 `TextView` or it's descendant it will behave as `manual`.
- `manual` means you have complete control over showing and hiding the label.

    <item name="flv_colorDefault">?android:textColorSecondary</item>

Default color determines the label text color when the owner view has no focus.
Typically this is the secondary text color.

    <item name="flv_textDefault">@android:string/untitled</item>

The default text value specifies text to be used in default state (for label it's always).
Please note that this value is completely independent of any `EditText`'s hint.

    <item name="flv_ownerView">@android:id/text1</item>

The owner view attribute allows you to specify the owner view in a XML layout.
Typically the owner view is an `EditText` or a `Spinner`. It owns the label/helper.

    <item name="flv_ownerViewPosition">bottom</item>

Owner view position determines where the owner view lies in relation to the label.
`bottom` signifies the owner view lies below the label.
Valid values are `top`, `left`, `right` and `bottom`.
These are used when animating showing and hiding of the label.

### FloatingLabelView

The `FloatingLabelView` can be customized using the following attributes:

    <item name="flv_colorActivated">?android:textColorPrimary</item>

Activated color determines the label text color when the owner view has focus.
Typically this is the accent color.

### FloatingHelperView

Floating helper may or may not be displayed typically below the owner view.
It has an optional error state which is always triggered manually and cleared on text change
(if applicable).

    <item name="flv_colorError">@color/flv_error</item>

Error color determines text color when the helper is in error state.
Optionally it may color the owner view background.

    <item name="flv_ownerViewBackgroundError">@null</item>

Specifies custom background for owner view when in error state.
If no value is specified owner view's original background is used.
May be colored by error color.

    <item name="flv_ownerViewUseColorError">true</item>

If true the owner view background becomes colored with error color when in error state.

### CharacterCounterView extends FloatingHelperView

The character counter must be used in conjunction with a `TextView` descendant.

    <item name="flv_characterLimit">10</item>

Character limit signifies when the counter text color turns negative.

### Default themes

You may specify a custom default style for all of these widgets
via corresponding attribute in your theme definition:

    <item name="floatingLabelViewStyle">...</item>
    <item name="floatingHelperViewStyle">...</item>
    <item name="characterCounterViewStyle">...</item>

The style will look similar to this:

    <style name="Widget.FloatingLabelView" parent="">
        <item name="android:textAppearance">@style/TextAppearance.FloatingLabelView</item>
        <item name="android:minHeight">16dp</item>
        <item name="android:gravity">center_vertical</item>
        <item name="flv_trigger">focus</item>
        <item name="flv_colorDefault">?android:textColorSecondary</item>
        <item name="flv_colorActivated">?android:textColorPrimary</item>
        <item name="flv_colorError">@color/flv_error</item>
        <item name="flv_ownerViewBackgroundError">@null</item>
        <item name="flv_ownerViewUseColorError">true</item>
        <item name="flv_ownerViewPosition">bottom</item>
    </style>

## Changelog

**0.2.2**
- Fixed issues with preview

**0.2.1**
- *FIXED:* Correct text and drawable state after OC (orientation change)
- *FIXED:* Label shows properly after OC on non-empty TextViews
- Helper now remains in error state after OC

**0.2.0**
- First release
- Completely new modular approach
- *NEW!* Error state
- *NEW!* Character counter
- *FIXED:* Activated color

## Known Issues

### 1. Single onFocusChangeListener

An Android view supports only one `OnFocusChangeListener` attached to it. To work around this I've
created the `OnFocusListenerWrapper` class, which allows you to attach multiple focus listeners to a
view. If you plan using focus listeners yourself, you'll need these, as this library relies heavily
upon focus listeners:

    OnFocusChangeListenerWrapper.add(view, listener);
    OnFocusChangeListenerWrapper.remove(view, listener);

### 2. Text animations

`FloatingHelperView` does not animate text and text color transitions between default and error
states. It is to be determined if this is really necessary.

## Credit

This project started as a fork of MrEngineer/FloatingLabelLayout with the intention of adding helper
support. During development I decided to change approach and structure and not to pollute the
original solution by these changes. Credit for the project therefore goes to these people as well:

1. [Matt Smith](http://mattdsmith.com/float-label-pattern/) and [Google](http://www.google.com/design/spec/components/text-fields.html#text-fields-floating-labels) for the idea
2. [Chris Banes](https://gist.github.com/chrisbanes/11247418) for his implementation
3. [MrEngineer](https://github.com/MrEngineer13) for a place to start

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
