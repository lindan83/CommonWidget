# CommonWidget
一个通用的控件库
===
支持标题，左侧，右侧View设置，也支持各自独立设置自定义View
===

###默认情况下：
* 标题为文本
* 左侧可指定文本和图片，也可指定其中一种
* 右侧可指定文本和图片，也可指定其中一种

###设置自定义View：
* 可以创建独立的layout xml文件
* 通过自定义属性指定layout id

###自定义属性：
##### 标题属性
titleText 标题文本
titleTextColor 标题文本颜色，默认黑色
titleTextSize 标题文本大小，默认20sp
titleVisibility 标题是否可见，默认true
customTitleViewLayoutId 自定义标题View Layout Resource Id，如指定此属性，则其他标题属性将忽略

##### 左侧View属性
leftVisibility 左侧View是否可见，默认true
leftText 左侧文本
leftTextColor 左侧文本颜色，默认黑色
leftTextSize 左侧文本大小，默认18sp
leftDrawable 左侧图片
leftDrawableAlignment 左侧图片相对于左侧文本位置，值有left和right
leftPadding 左侧内边距，默认16dp
customLeftViewLayoutId 自定义左侧View Layout Resource Id，如指定此属性，则其他左侧View属性将忽略

##### 右侧View属性
rightVisibility 右侧View是否可见，默认false
rightText 右侧文本
rightTextColor 右侧文本颜色，默认黑色
rightTextSize 右侧文本大小，默认18sp
rightDrawable 右侧图片
rightDrawableAlignment 右侧图片相对于右侧文本位置，值有left和right
rightPadding 右侧内边距，默认16dp
customRightViewLayoutId 自定义右侧View Layout Resource Id，如指定此属性，则其他右侧View属性将忽略

##### 其他属性
internalSpacing 左侧View、标题、右侧View之间的间距


目前存在问题
======
* 宽高均不可指定wrap_content值，必须为具体dp数值或match_parent
* 如果高度指定了wrap_content，将被强制设置为48dp