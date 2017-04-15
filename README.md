# MultimediaNote 工作日志

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
* 实现日历功能
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
