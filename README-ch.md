<div align="center"><img src="/screens/cover.png"/></div>

# TreeLayout
[![Platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

TreeLayout 树形布局，提供多种自定义属性方便定制出各种各样的树形结构，当然实现时间轴更是不在话下。

# 目前效果
<img src="/screens/example_1.png"/>
<img src="/screens/example_2.png"/>

# 使用

版本请参考 mvn repository 上的最新版本（目前最新版本是1.0.0），最新的 aar 都会发布到 jcenter 和 MavenCentral 上，确保配置了这两个仓库源，然后引入 aar 依赖：

```
// gradle
compile 'com.cheng.diyview:TreeLayout:1.0.0'
```

或者maven

```
// pom.xml in maven
<dependency>
  <groupId>com.cheng.diyview</groupId>
  <artifactId>TreeLayout</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
#### XML
```xml
<com.cheng.treelayout.TreeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Header - 0"
        android:textAllCaps="false"
        app:itemType="header"/>
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="1-TEST_NORMAL_ITEM"
        app:indentLevel="1"
        app:itemType="normal"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="2-=====LABEL====="
        app:indentLevel="2"
        app:itemType="label"/>

    <!--可以放任意类型的子控件，用法类似 LinearLayout -->

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Footer - 1"
        android:textAllCaps="false"
        app:indentLevel="0"
        app:itemType="footer"/>

</com.cheng.treelayout.TreeLayout>
```

# 目前完成的功能

1. 左侧为树形，右侧可以添加任意子控件，用法类似LinearLayout
2. 提供各类自定义属性，方便自定义主干、横枝、枝节点以及叶子
3. 可以灵活配置横枝指向目标条目的位置
4. 相对其他的树形结构实现方案高效，没有过多的嵌套层次，直接在ViewGroup中实现

# 待完善的功能
1. 点击折叠子项
2. 动画展开/折叠
3. 状态保存
4. 为子控件提供 layout_gravity 属性
5. 树形结构位置可配置（目前只能在左侧）


# 设计思路
TreeLayout 继承自 ViewGroup，可以直接在下面写控件，也可以通过 addView() 方法动态添加控件。这样可简化 TreeLayout 的使用，也使得子控件更为灵活，当然也减轻了相当多的绘制工作。
那么 TreeLayout 有两个职责：
1. 将所有添加进来的子控件垂直排布在树形结构的右侧
2. 在左侧根据子控件的位置及属性/特点绘制树形结构

为什么不直接继承 LinearLayout？
直接继承 LinearLayout 无疑要更简单一点，但是为了更灵活，以及后面更多功能的添加，直接继承自 ViewGroup，这样的可操作性更强。 

# TreeLayout 支持的自定义属性

```xml
<declare-styleable name="TreeLayout">
    <!-- 背景色 -->
    <attr name="android:background"/>
    <attr name="android:layout_gravity"/>
    <!-- 主干宽度 -->
    <attr name="trunkWidth" format="dimension"/>
    <!-- 主干颜色 -->
    <attr name="trunkColor" format="color"/>
    <!-- 是允许自定义横枝 -->
    <attr name="isCustomBranchEnable" format="boolean"/>
    <!-- 是显示横枝 -->
    <attr name="isShowBranch" format="boolean"/>
    <!-- 横枝宽度 -->
    <attr name="branchWidth" format="dimension"/>
    <!-- 横枝高度/厚度 -->
    <attr name="branchHeight" format="dimension"/>
    <!-- 横枝颜色 -->
    <attr name="branchColor" format="color"/>
    <!-- 是否显示树叶 -->
    <attr name="isShowLeaf" format="boolean"/>
    <!-- 树叶图片 -->
    <attr name="leafSrc" format="reference|color"/>
    <!-- 树叶图片宽度 -->
    <attr name="leafWidth" format="dimension"/>
    <!-- 树叶图片长度 -->
    <attr name="leafHeight" format="dimension"/>
    <!-- 是否显示枝节点 -->
    <attr name="isShowNode" format="boolean"/>
    <!-- 枝节处图片 -->
    <attr name="nodeSrc" format="reference|color"/>
    <!-- 枝节处图片宽度 -->
    <attr name="nodeWidth" format="dimension"/>
    <!-- 枝节处图片长度 -->
    <attr name="nodeHeight" format="dimension"/>
    <!-- TreeLayout缩进 -->
    <attr name="android:padding"/>
    <!-- TreeLayout左侧缩进 -->
    <attr name="android:paddingLeft"/>
    <!-- TreeLayout上侧缩进 -->
    <attr name="android:paddingTop"/>
    <!-- TreeLayout右侧缩进 -->
    <attr name="android:paddingRight"/>
    <!-- TreeLayout下侧缩进 -->
    <attr name="android:paddingBottom"/>
    <!-- TreeLayout内部的树形结构缩进 -->
    <attr name="treePadding" format="dimension"/>
    <!-- TreeLayout内部的树形结构左侧缩进 -->
    <attr name="treePaddingLeft" format="dimension"/>
    <!-- TreeLayout内部的树形结构上侧缩进 -->
    <attr name="treePaddingTop" format="dimension"/>
    <!-- TreeLayout内部的树形结构右侧缩进 -->
    <attr name="treePaddingRight" format="dimension"/>
    <!-- TreeLayout内部的树形结构下侧缩进 -->
    <attr name="treePaddingBottom" format="dimension"/>
    <!-- 横枝与右侧条目间距 -->
    <attr name="marginRightItem" format="dimension"/>
    <!-- Item 缩进值 -->
    <attr name="indentValue" format="dimension"/>
    <!-- 是否连接到缩进的条目 -->
    <attr name="isLinkIndented" format="boolean"/>
    <!-- 是否使用默认动画展开收起 -->
    <attr name="useDefaultAnimation" format="boolean"/>
    <!-- 树的展开状态 -->
    <attr name="isTreeExpanded" format="boolean"/>
    <!-- 树是否允许展开/折叠操作 -->
    <attr name="isToggleEnable" format="boolean"/>
    <!-- 主干类型 -->
    <attr name="trunkType">
        <!-- 默认为普通类型：目录树 -->
        <enum name="normal" value="0"/>
        <!-- 通栏类型：时间轴 -->
        <enum name="full" value="1"/>
    </attr>
</declare-styleable>
```

# TreeLayout LayoutParams 支持的自定义属性

```xml
<declare-styleable name="TreeLayout_Layout">
    <!-- 单独指定横枝宽度 -->
    <attr name="customBranchWidth" format="dimension"/>
    <!-- 单独指定横枝高度 -->
    <attr name="customBranchHeight" format="dimension"/>
    <!-- 单独指定横枝颜色 -->
    <attr name="customBranchColor" format="color"/>
    <!-- 单独指定叶子 Src -->
    <attr name="customLeafSrc" format="reference|color"/>
    <!-- 单独指定叶子宽度 -->
    <attr name="customLeafWidth" format="dimension"/>
    <!-- 单独指定叶子高度 -->
    <attr name="customLeafHeight" format="dimension"/>
    <!-- 单独指定枝节点 Src -->
    <attr name="customNodeSrc" format="reference|color"/>
    <!-- 单独指定枝节点宽度 -->
    <attr name="customNodeWidth" format="dimension"/>
    <!-- 单独指定枝节点高度 -->
    <attr name="customNodeHeight" format="dimension"/>
    <!-- Item 缩进级别 -->
    <attr name="indentLevel" format="integer"/>
    <!-- 横枝指向的目标的位置 -->
    <attr name="branchTargetIndex" format="integer"/>
    <!-- 横枝距离指向目标条目顶部的偏移值 -->
    <attr name="offsetTargetTop" format="dimension"/>
    <!-- Item 条目类型 -->
    <attr name="itemType">
        <!-- 默认为普通类型：左侧有横枝 -->
        <enum name="normal" value="0"/>
        <!-- 标签类型：左侧没有横枝 -->
        <enum name="label" value="1"/>
        <!-- Header 类型：位于整个 TreeLayout 的头部，没有缩进 -->
        <enum name="header" value="2"/>
        <!-- Footer 类型：位于整个 TreeLayout 的底部，没有缩进 -->
        <enum name="footer" value="3"/>
    </attr>
</declare-styleable>
```

# 开源许可证
  
TreeLayout 遵循 MIT 开源许可证协议。