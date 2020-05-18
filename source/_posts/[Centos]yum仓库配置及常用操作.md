---
title: '[Centos]yum仓库配置及常用操作'
date: 2019/1/2 20:46:25
updated: 2020/2/20 15:41:36
comments: true
categories: 
    - [搞点骚操作]
tags: 
    - linux
---

yum时不时抽风？仓库获取不到最新资源？先理解yum是怎么工作的吧！
<!--more-->
## 常用的储存库

 

## 要注意的坑

- 储存库要启用了才有效
- `yum repo` 只能显示已启用的储存库 显示全部需要`yum repolist all`
- 如果使用`yum-config-manager`配置仓库一定要记得保存
- 配置完后记得要`yum makecache`建立缓存，如果不放心可以先`yum clean all `清除所有缓存

&gt; 上面的坑我全部踩了

## 命令 yum repolist all

```text
Loaded plugins: fastestmirror
Loading mirror speeds from cached hostfile
 * base: mirror.scalabledns.com
 * elrepo: repos.lax-noc.com
 * elrepo-extras: repos.lax-noc.com
 * elrepo-kernel: repos.lax-noc.com
 * extras: repos-lax.psychz.net
 * updates: repos-lax.psychz.net
repo id                       repo name                        status
// 这些仓库应该是系统升级时留下来的
C7.0.1406-base/x86_64         CentOS-7.0.1406 - Base           disabled
C7.0.1406-centosplus/x86_64   CentOS-7.0.1406 - CentOSPlus     disabled
C7.0.1406-extras/x86_64       CentOS-7.0.1406 - Extras         disabled
C7.0.1406-fasttrack/x86_64    CentOS-7.0.1406 - Fasttrack      disabled
C7.0.1406-updates/x86_64      CentOS-7.0.1406 - Updates        disabled
C7.1.1503-base/x86_64         CentOS-7.1.1503 - Base           disabled
C7.1.1503-centosplus/x86_64   CentOS-7.1.1503 - CentOSPlus     disabled
C7.1.1503-extras/x86_64       CentOS-7.1.1503 - Extras         disabled
C7.1.1503-fasttrack/x86_64    CentOS-7.1.1503 - Fasttrack      disabled
C7.1.1503-updates/x86_64      CentOS-7.1.1503 - Updates        disabled
C7.2.1511-base/x86_64         CentOS-7.2.1511 - Base           disabled
C7.2.1511-centosplus/x86_64   CentOS-7.2.1511 - CentOSPlus     disabled
C7.2.1511-extras/x86_64       CentOS-7.2.1511 - Extras         disabled
C7.2.1511-fasttrack/x86_64    CentOS-7.2.1511 - Fasttrack      disabled
C7.2.1511-updates/x86_64      CentOS-7.2.1511 - Updates        disabled
C7.3.1611-base/x86_64         CentOS-7.3.1611 - Base           disabled
C7.3.1611-centosplus/x86_64   CentOS-7.3.1611 - CentOSPlus     disabled
C7.3.1611-extras/x86_64       CentOS-7.3.1611 - Extras         disabled
C7.3.1611-fasttrack/x86_64    CentOS-7.3.1611 - Fasttrack      disabled
C7.3.1611-updates/x86_64      CentOS-7.3.1611 - Updates        disabled
C7.4.1708-base/x86_64         CentOS-7.4.1708 - Base           disabled
C7.4.1708-centosplus/x86_64   CentOS-7.4.1708 - CentOSPlus     disabled
C7.4.1708-extras/x86_64       CentOS-7.4.1708 - Extras         disabled
C7.4.1708-fasttrack/x86_64    CentOS-7.4.1708 - Fasttrack      disabled
C7.4.1708-updates/x86_64      CentOS-7.4.1708 - Updates        disabled
C7.5.1804-base/x86_64         CentOS-7.5.1804 - Base           disabled
C7.5.1804-centosplus/x86_64   CentOS-7.5.1804 - CentOSPlus     disabled
C7.5.1804-extras/x86_64       CentOS-7.5.1804 - Extras         disabled
C7.5.1804-fasttrack/x86_64    CentOS-7.5.1804 - Fasttrack      disabled
C7.5.1804-updates/x86_64      CentOS-7.5.1804 - Updates        disabled
C7.6.1810-base/x86_64         CentOS-7.6.1810 - Base           disabled
C7.6.1810-centosplus/x86_64   CentOS-7.6.1810 - CentOSPlus     disabled
C7.6.1810-extras/x86_64       CentOS-7.6.1810 - Extras         disabled
C7.6.1810-fasttrack/x86_64    CentOS-7.6.1810 - Fasttrack      disabled
C7.6.1810-updates/x86_64      CentOS-7.6.1810 - Updates        disabled
// centos7核心库 源码库和开发库的源就不开了
base/7/x86_64                 CentOS-7 - Base                  enabled: 10,096+1
base-debuginfo/x86_64         CentOS-7 - Debuginfo             disabled
base-source/7                 CentOS-7 - Base Sources          disabled
// 看上去是多媒体相关的库 没桌面环境应该是用不上了
c7-media                      CentOS-7 - Media                 disabled
// 这个源主要是用来替换base源的，其中有不少包是base源的增强版
// 但是如果开启这个源部分包可能会于base冲突 
// 需要在base源中exclude 或者使用yum-plugin-priorities插件来保证兼容性
// 引用WIKI: https://wiki.centos.org/zh/AdditionalResources/Repositories/CentOSPlus
centosplus/7/x86_64           CentOS-7 - Plus                  disabled
centosplus-source/7           CentOS-7 - Plus Sources          disabled
// cr = Continuous Release 下次发布内容 相当于beta版了
cr/7/x86_64                   CentOS-7 - cr                    disabled
// elrepo = The Community Enterprise Linux Repository
// http://elrepo.org 从描述中可以看出是驱动包 如果有UI可能要开
elrepo                        ELRepo.org Community Enterprise  enabled:      147
elrepo-extras                 ELRepo.org Community Enterprise  enabled:       19
elrepo-kernel                 ELRepo.org Community Enterprise  enabled:       37
elrepo-testing                ELRepo.org Community Enterprise  disabled
// 这个源要开啊 EPEL仓库 相当多常用的软件都在里面
// https://fedoraproject.org/
epel/x86_64                   Extra Packages for Enterprise Li disabled
epel-debuginfo/x86_64         Extra Packages for Enterprise Li disabled
epel-source/x86_64            Extra Packages for Enterprise Li disabled
epel-testing/x86_64           Extra Packages for Enterprise Li disabled
epel-testing-debuginfo/x86_64 Extra Packages for Enterprise Li disabled
epel-testing-source/x86_64    Extra Packages for Enterprise Li disabled
extras/7/x86_64               CentOS-7 - Extras                enabled:      305
extras-source/7               CentOS-7 - Extras Sources        disabled
fasttrack/7/x86_64            CentOS-7 - fasttrack             disabled
updates/7/x86_64              CentOS-7 - Updates               enabled:    733+5
updates-source/7              CentOS-7 - Updates Sources       disabled
repolist: 11,337
```

## 命令 yum-config-manager [yum配置管理器]
```text
Loaded plugins: fastestmirror
Usage: yum-config-manager [options] [section ...]

Options:
  Plugin Options:

  Yum Base Options:
    -h, --help          show this help message and exit
    -t, --tolerant      be tolerant of errors
    // 完全从系统缓存运行，不更新缓存
    -C, --cacheonly     run entirely from system cache, don't update cache
    -c [config file], --config=[config file]
                        config file location
    -R [minutes], --randomwait=[minutes]
                        maximum command wait time
    -d [debug level], --debuglevel=[debug level]
                        debugging output level
    --showduplicates    show duplicates, in repos, in list/search commands
    -e [error level], --errorlevel=[error level]
                        error output level
    --rpmverbosity=[debug level name]
                        debugging output level for rpm
    -q, --quiet         quiet operation
    -v, --verbose       verbose operation
    -y, --assumeyes     answer yes for all questions
    --assumeno          answer no for all questions
    --version           show Yum version and exit
    --installroot=[path]
                        set install root
    --enablerepo=[repo]
                        // 启用储存库
                        enable one or more repositories (wildcards allowed)
    --disablerepo=[repo]
                        disable one or more repositories (wildcards allowed)
    -x [package], --exclude=[package]
                        exclude package(s) by name or glob
    --disableexcludes=[repo]
                        disable exclude from main, for a repo or for
                        everything
    --disableincludes=[repo]
                        disable includepkgs for a repo or for everything
    --obsoletes         enable obsoletes processing during updates
    --noplugins         disable Yum plugins
    --nogpgcheck        disable gpg signature checking
    --disableplugin=[plugin]
                        disable plugins by name
    --enableplugin=[plugin]
                        enable plugins by name
    --skip-broken       skip packages with depsolving problems
    --color=COLOR       control whether color is used
    --releasever=RELEASEVER
                        set value of $releasever in yum config and repo files
    --downloadonly      don't update, just download
    --downloaddir=DLDIR
                        specifies an alternate directory to store packages
    --setopt=SETOPTS    set arbitrary config and repo options
    --bugfix            Include bugfix relevant packages, in updates
    --security          Include security relevant packages, in updates
    --advisory=ADVS, --advisories=ADVS
                        Include packages needed to fix the given advisory, in
                        updates
    --bzs=BZS           Include packages needed to fix the given BZ, in
                        updates
    --cves=CVES         Include packages needed to fix the given CVE, in
                        updates
    --sec-severity=SEVS, --secseverity=SEVS
                        Include security relevant packages matching the
                        severity, in updates

  yum-config-manager options:
    // 保存当前设置
    --save              save the current options (useful with --setopt)
    // 启用仓库 (自动保存)
    --enable            enable the specified repos (automatically saves)
    // 禁用仓库 (自动保存)
    --disable           disable the specified repos (automatically saves)
    // 从文件或URL添加仓库 (并启用)
    --add-repo=ADDREPO  add (and enable) the repo from the specified file or
                        url

```

## 命令 yum

```text
Loaded plugins: fastestmirror
Usage: yum [options] COMMAND

List of Commands:

check          Check for problems in the rpmdb
check-update   Check for available package updates
clean          Remove cached data
deplist        List a package's dependencies
distribution-synchronization Synchronize installed packages to the latest available versions
downgrade      downgrade a package
erase          Remove a package or packages from your system
fs             Acts on the filesystem data of the host, mainly for removing docs/lanuages for minimal hosts.
fssnapshot     Creates filesystem snapshots, or lists/deletes current snapshots.
groups         Display, or use, the groups information
help           Display a helpful usage message
history        Display, or use, the transaction history
info           Display details about a package or group of packages
install        Install a package or packages on your system
list           List a package or groups of packages
load-transaction load a saved transaction from filename
makecache      Generate the metadata cache
provides       Find what package provides the given value
reinstall      reinstall a package
repo-pkgs      Treat a repo. as a group of packages, so we can install/remove all of them
repolist       Display the configured software repositories
search         Search package details for the given string
shell          Run an interactive yum shell
swap           Simple way to swap packages, instead of using shell
update         Update a package or packages on your system
update-minimal Works like upgrade, but goes to the 'newest' package match which fixes a problem that affects your system
updateinfo     Acts on repository update information
upgrade        Update packages taking obsoletes into account
version        Display a version for the machine and/or available repos.


Options:
  -h, --help            show this help message and exit
  -t, --tolerant        be tolerant of errors
  -C, --cacheonly       run entirely from system cache, don't update cache
  -c [config file], --config=[config file]
                        config file location
  -R [minutes], --randomwait=[minutes]
                        maximum command wait time
  -d [debug level], --debuglevel=[debug level]
                        debugging output level
  --showduplicates      show duplicates, in repos, in list/search commands
  -e [error level], --errorlevel=[error level]
                        error output level
  --rpmverbosity=[debug level name]
                        debugging output level for rpm
  -q, --quiet           quiet operation
  -v, --verbose         verbose operation
  -y, --assumeyes       answer yes for all questions
  --assumeno            answer no for all questions
  --version             show Yum version and exit
  --installroot=[path]  set install root
  --enablerepo=[repo]   enable one or more repositories (wildcards allowed)
  --disablerepo=[repo]  disable one or more repositories (wildcards allowed)
  -x [package], --exclude=[package]
                        exclude package(s) by name or glob
  --disableexcludes=[repo]
                        disable exclude from main, for a repo or for
                        everything
  --disableincludes=[repo]
                        disable includepkgs for a repo or for everything
  --obsoletes           enable obsoletes processing during updates
  --noplugins           disable Yum plugins
  --nogpgcheck          disable gpg signature checking
  --disableplugin=[plugin]
                        disable plugins by name
  --enableplugin=[plugin]
                        enable plugins by name
  --skip-broken         skip packages with depsolving problems
  --color=COLOR         control whether color is used
  --releasever=RELEASEVER
                        set value of $releasever in yum config and repo files
  --downloadonly        don't update, just download
  --downloaddir=DLDIR   specifies an alternate directory to store packages
  --setopt=SETOPTS      set arbitrary config and repo options
  --bugfix              Include bugfix relevant packages, in updates
  --security            Include security relevant packages, in updates
  --advisory=ADVS, --advisories=ADVS
                        Include packages needed to fix the given advisory, in
                        updates
  --bzs=BZS             Include packages needed to fix the given BZ, in
                        updates
  --cves=CVES           Include packages needed to fix the given CVE, in
                        updates
  --sec-severity=SEVS, --secseverity=SEVS
                        Include security relevant packages matching the
                        severity, in updates

  Plugin Options:

```
