# Quick-Task

[![Builder](https://travis-ci.org/liuyueyi/quick-task.svg?branch=master)](https://travis-ci.org/liuyueyi/quick-task) 
[![](https://jitpack.io/v/liuyueyi/quick-task.svg)](https://jitpack.io/#liuyueyi/quick-task)

一个简单的动态脚本调度框架，支持运行时，实时增加,删除和修改动态脚本，可用于后端的进行接口验证、数据订正，执行定时任务或校验脚本

本项目主要涉及到的技术栈:

- groovyEngine （groovy脚本加载执行）
- commons-io （文件变动监听）
- commons-cli (命令行参数解析)


## I. 使用姿势

### 1. pom配置

两种使用姿势，一是我自己搭的私服，好处是更新及时；另一个是jitpack，优点是其对应的包就是github中的release版本

**case1:**

添加仓库地址

```xml
<repositories>
    <repository>
        <id>YiHui-Repo</id>
        <url>https://raw.githubusercontent.com/liuyueyi/maven-repository/master/repository</url>
    </repository>
</repositories>
```

添加项目依赖

```xml
<dependency>
    <groupId>com.git.hui</groupId>
    <artifactId>task-core</artifactId>
    <version>0.0.1</version>
</dependency>
```

**case2:**

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

添加依赖

```xml
<dependency>
    <groupId>com.github.liuyueyi</groupId>
    <artifactId>quick-task</artifactId>
    <version>v0.0.1</version>
</dependency>
```


### 2. 使用demo

#### a. 源码方式

源码下载之后的使用case，可以参考 `com.git.hui.task.AppLaunch`，运行main方法，监听`./task-core/src/test/java/com/git/hui/task`目录下脚本的变动即可

#### b. jar包引用

首先准备一个Groovy脚本，放在指定的目录下，如 `/tmp/script/DemoScript.groovy`

```groovy
package com.git.hui.task

import com.git.hui.task.api.ITask

class DemoScript implements ITask {
    @Override
    void run() {
        println name() + " | now > : >>" + System.currentTimeMillis()
    }

    @Override
    void interrupt() {
        println "over"
    }
}
```

对应的启动类可以如下

```java
public class AppRunner {

    // main 方式
    public static void main(String[] args) throws Exception {
        new ScriptExecuteEngine().run("/tmp/script/");
        Thread.sleep(24 *60 * 60 * 1000);
    }
    
    // junit 方式启动
    @Test
    public void testTaskRun() {
        new ScriptExecuteEngine().run("/tmp/script/");
        Thread.sleep(24 *60 * 60 * 1000);
    }
}
```

#### c. 测试

应用启动完毕之后

- 可以修改 `/tmp/script/DemoScript.groovy` 脚本的内容，保存后查看是否关闭旧的脚本并执行更新后的脚本
- 测试在 `/tmp/script` 目录下新增脚本
- 测试删除 `/tmp/script` 目录下的脚本
- 测试异常的case (如非法的groovy文件，内部运行异常等...)

**注意** 不要在groovy脚本中执行 `System.exit(1)`, 会导致整个项目都停止运行


## II. 设计原理

基本结构如下图

![脚本框架.png](https://raw.githubusercontent.com/liuyueyi/Source/master/img/blog/daywork/180628/tech.png)

从图中基本上也可以看出，这个项目的结构属于非常轻量级的，核心角色，有下面几个

- Task ： 具体的任务脚本
- TaskContainer： 持有执行任务的容器
- TaskChangeWatcher： 任务观察器，用于查看是否有新增、删除or修改任务，从而卸载旧的任务，并加载新的任务


另外一块属于扩展方面的插件体系，目前并没有给与实现，若将本框架继承在Spring生态体系中运行时，这些插件的支持就特别简单了

- RedisTemplate
- RestTemplate
- AmqpTemplate
- xxxTemplate

## II. 其他

### 0. 相关博文

**涉及到技术点博文**

- [180208-Java可以如何实现文件变动的监听](https://liuyueyi.github.io/hexblog/2018/02/08/Java%E5%8F%AF%E4%BB%A5%E5%A6%82%E4%BD%95%E5%AE%9E%E7%8E%B0%E6%96%87%E4%BB%B6%E5%8F%98%E5%8A%A8%E7%9A%84%E7%9B%91%E5%90%AC/)
- [180717-借助Maven打包可项目执行的Jar小记](https://liuyueyi.github.io/hexblog/2018/07/17/180717-%E5%80%9F%E5%8A%A9Maven%E6%89%93%E5%8C%85%E5%8F%AF%E9%A1%B9%E7%9B%AE%E6%89%A7%E8%A1%8C%E7%9A%84Jar%E5%B0%8F%E8%AE%B0/)
- [180718-jar包执行传参使用小结](https://liuyueyi.github.io/hexblog/2018/07/18/180718-jar%E5%8C%85%E6%89%A7%E8%A1%8C%E4%BC%A0%E5%8F%82%E4%BD%BF%E7%94%A8%E5%B0%8F%E7%BB%93/)

**框架相关博文**

- [180628-动态任务执行框架想法篇](https://liuyueyi.github.io/hexblog/2018/06/28/180628-%E5%8A%A8%E6%80%81%E4%BB%BB%E5%8A%A1%E6%89%A7%E8%A1%8C%E6%A1%86%E6%9E%B6%E6%83%B3%E6%B3%95%E7%AF%87/)
- [180702-QuickTask动态脚本支持框架整体介绍篇](https://liuyueyi.github.io/hexblog/2018/07/02/180702-QuickTask%E5%8A%A8%E6%80%81%E8%84%9A%E6%9C%AC%E6%94%AF%E6%8C%81%E6%A1%86%E6%9E%B6%E6%95%B4%E4%BD%93%E4%BB%8B%E7%BB%8D%E7%AF%87/)
- [180719-Quick-Task 动态脚本支持框架之使用介绍篇](https://liuyueyi.github.io/hexblog/2018/07/19/180719-Quick-Task-%E5%8A%A8%E6%80%81%E8%84%9A%E6%9C%AC%E6%94%AF%E6%8C%81%E6%A1%86%E6%9E%B6%E4%B9%8B%E4%BD%BF%E7%94%A8%E4%BB%8B%E7%BB%8D%E7%AF%87/)
- [180723-Quick-Task 动态脚本支持框架之结构设计篇](https://liuyueyi.github.io/hexblog/2018/07/23/180723-Quick-Task-%E5%8A%A8%E6%80%81%E8%84%9A%E6%9C%AC%E6%94%AF%E6%8C%81%E6%A1%86%E6%9E%B6%E4%B9%8B%E7%BB%93%E6%9E%84%E8%AE%BE%E8%AE%A1%E7%AF%87/)
- [180729-Quick-Task 动态脚本支持框架之任务动态加载](https://liuyueyi.github.io/hexblog/2018/07/29/180729-Quick-Task-%E5%8A%A8%E6%80%81%E8%84%9A%E6%9C%AC%E6%94%AF%E6%8C%81%E6%A1%86%E6%9E%B6%E4%B9%8B%E4%BB%BB%E5%8A%A1%E5%8A%A8%E6%80%81%E5%8A%A0%E8%BD%BD/)
- [180807-Quick-Task 动态脚本支持框架之Groovy脚本加载执行](https://liuyueyi.github.io/hexblog/2018/08/07/180807-Quick-Task-%E5%8A%A8%E6%80%81%E8%84%9A%E6%9C%AC%E6%94%AF%E6%8C%81%E6%A1%86%E6%9E%B6%E4%B9%8BGroovy%E8%84%9A%E6%9C%AC%E5%8A%A0%E8%BD%BD%E6%89%A7%E8%A1%8C/)


### 1. [一灰灰Blog](https://liuyueyi.github.io/hexblog)： https://liuyueyi.github.io/hexblog

一灰灰的个人博客，记录所有学习和工作中的博文，欢迎大家前去逛逛


### 2. 声明

尽信书则不如，已上内容，纯属一家之言，因个人能力有限，难免有疏漏和错误之处，如发现bug或者有更好的建议，欢迎批评指正，不吝感激

- 微博地址: [小灰灰Blog](https://weibo.com/p/1005052169825577/home)
- QQ： 一灰灰/3302797840
- 微信: 一灰/liuyueyi25

### 3. 扫描关注

公众号&博客

![QrCode](https://gitee.com/liuyueyi/Source/raw/master/img/info/blogInfoV2.png)


打赏码

![pay](https://gitee.com/liuyueyi/Source/raw/master/img/pay/pay.png)

