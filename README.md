农家Pad 链接 创维VR

------------------[Pad端主项目]------------------

hvrskyworthvr : 主项目名称
lib_network : 主项目依赖的网络模块
lib_bluetooth : 主项目依赖的蓝牙通讯模块
huaweimdm : 主项目依赖的华为MDM模块

------------------[VR端调用接口]------------------

lib_skyworthvr ：专门用于给unity生成[蓝牙通讯]和[wifi通讯]的aar,这个aar生成后，记得把lib里面的unity-classes删除掉，避免重复引用

------------------[测试工程]------------------

huaweipad ：专门测试[蓝牙双端通讯]的模块，支持pad和vr
skyworthvrwifi ：测试[创维wifi接口]
Farmhouse360VRvX : unity输出工程后，由AndroidStudio引入[开机自启动]后，生成给创维VR的apk