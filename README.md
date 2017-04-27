# MultimediaNote 工作日志

### 04/26/2017
##### DONE 
* 经尝试，闹钟功能遇到一些技术难题，网上教程不很明确，缓行
##### TODO
* 快速操作入口

### 04/24/2017
##### DONE
* 在保存Note时引入了操作队列概念，同时实现了保存按钮的功能
* 测试了FragmentTransaction中add和replace的区别，并对生命周期进行了详细测试
##### TODO
* 移植闹钟功能
* 快速操作入口

### 04/22/2017
##### DONE
* 引入了Volley，在地图中查询坐标和地址时使用异步网络通信
* 显示有Note的日期
##### TODO
* 将闹钟移植进来
* 实现保存按钮功能
* 思考FragmentTransaction中add和replace的区别，测试生命周期

### 04/17/2017
##### DONE
* 实现了日历功能和相关的切换逻辑
##### TODO
* 实现闹钟功能
* 实现保存按钮功能
* 尝试将地图内的网络通信改为异步通信
* 显示有Note的日期

### 04/15/2017
##### DONE
* 把录音机作为库引入，修改后可以从Fragment里启动
* 视频和音频回调时自动删除临时文件
* 没有位置信息时进入地图会滚动到最后已知位置
* 解决了地图和录音机的运行时权限问题

##### TODO
* 实现日历功能

### 04/14/2017
##### DONE
* 实现了Share功能
* 开启了两个RecyclerView的默认添加删除动画
* 引入了第三方录音机（现需要手动开启权限）
* 使用旧的定位功能为Note添加位置
##### TODO
* 解决第三方录音机的运行时权限问题
* 第一次打开地图时滚动到自己位置
* 若不给位置权限，地图应被禁用，直到手动开启
* 解决视频和音频录制的临时文件问题
* 当前录音机返回时调用了UserInterface里的onActivityResult，需要根据Github上的issue对录音机进行修改


### 04/07/2017
##### DONE
* 重绘了NoteList的Card，可以使用Fresco异步加载图片
* 通过简单逻辑实现了Note内无图片时不显示图片框
* 在RecyclerView的适配器内添加接口，实现了Note和Media的删除逻辑
##### TODO
* 实现Share功能
* 尝试使用RecyclerView的添加删除动画
* 尝试将旧的定位功能移植进来

### 04/05/2017
##### DONE
* 实现了Note里添加Media的逻辑
* 使用Fresco加载图片
##### TODO
* 重绘NoteList的Card，因Fresco不支持百分比布局
* 实现删除Media的逻辑

### 04/04/2017
##### DONE
* 重写了MediaList的适配器，添加元素后可以正确显示
##### TODO
* 完善NoteFragment的逻辑，包括保存删除等

### 04/03/2017
##### DONE
* 移除了旧版的数据库view，使用自定义的CardView浏览数据库，并实现Card内的按钮点击Sample
##### TODO
* 由于旧的Adapter不好用，重新实现日记页面的MediaList，使用RecyclerView取代ListView，重写Adapter
