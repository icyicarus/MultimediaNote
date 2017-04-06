# MultimediaNote 工作日志

**04/05/2017**
<br>
*DONE*<br>
实现了Note里添加Media的逻辑<br>
使用Fresco加载图片<br>
*TODO*<br>
重绘NoteList的Card，因Fresco不支持百分比布局<br>
实现删除Media的逻辑

**04/04/2017**
<br>
*DONE*<br>
重写了MediaList的适配器，添加元素后可以正确显示<br>
*TODO*<br>
完善NoteFragment的逻辑，包括保存删除等

**04/03/2017**
<br>
*DONE*<br>
移除了旧版的数据库view，使用自定义的CardView浏览数据库，并实现Card内的按钮点击Sample<br>
*TODO*<br>
由于旧的Adapter不好用，重新实现日记页面的MediaList，使用RecyclerView取代ListView，重写Adapter
