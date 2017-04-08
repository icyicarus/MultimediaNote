# MultimediaNote 工作日志

### 04/07/2017

##### DONE
重绘了NoteList的Card，可以使用Fresco异步加载图片<br>
通过简单逻辑实现了Note内无图片时不显示图片框<br>
在RecyclerView的适配器内添加接口，实现了Note和Media的删除逻辑<br>
##### TODO
实现Share功能<br>
尝试使用RecyclerView的添加删除动画<br>
尝试将旧的定位功能移植进来

### 04/05/2017
##### DONE
实现了Note里添加Media的逻辑<br>
使用Fresco加载图片<br>
##### TODO
重绘NoteList的Card，因Fresco不支持百分比布局<br>
实现删除Media的逻辑

### 04/04/2017
##### DONE
重写了MediaList的适配器，添加元素后可以正确显示<br>
##### TODO
完善NoteFragment的逻辑，包括保存删除等

### 04/03/2017
##### DONE
移除了旧版的数据库view，使用自定义的CardView浏览数据库，并实现Card内的按钮点击Sample<br>
##### TODO
由于旧的Adapter不好用，重新实现日记页面的MediaList，使用RecyclerView取代ListView，重写Adapter
