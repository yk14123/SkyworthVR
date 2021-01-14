农家Pad 链接 创维VR

hvrskyworthvr : 主项目名称
lib_network : 主项目依赖的网络模块
lib_bluetooth : 主项目依赖的蓝牙通讯模块

huaweipad ：专门测试蓝牙双端通讯的模块，支持pad和vr
lib_skyworthvr ：专门用于给unity生成[蓝牙通讯]和[wifi通讯]的aar,这个aar生成后，记得把lib里面的unity-classes删除掉，避免重复引用

skyworthvrwifi ：[测试工程]测试创维wifi接口
Farmhouse360VRvX : unity输出工程后，由AndroidStudio引入开机自启动后，生成给创维VR的apk