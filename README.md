# RecyclerView曝光统计

基于Android常用官方列表控件`RecyclerView`实现接近真实用户Item浏览曝光统计。可用于各种需要进行流量转化相关分析场景。采用装饰者模式，模块体量小、集成成本低。

## 快速使用

调用静态工厂类`ItemViewReporterFactory`的`getItemReporter(RecyclerView)`获得曝光统计实例。所有相关方法都是通过该实例执行。

```java
ItemViewReporterApi itemReporter = ItemViewReporterFactory.getItemReporter(recyclerview);
```

注意：

- 由于需要依赖RecyclerView的`LayoutManager`，因此推荐在RecyclerView初始化完成后调用此方法。
- LayoutManager需要是`LinearLayoutManger`的子类，否则会抛出异常。

初始化完成后会自动开始进行曝光监听。

## 获取曝光数据

有两种曝光数据获取途径：

- 默认会在内部实现一个`SparseIntArray`集合，数据结构为`Key/Value`，kv皆为int类型，key代表RecyclerView对应position项，value存储该项曝光次数。调用`getData()`获得数据集合。

```java
SparseIntArray data = itemReporter.getData();
```

- 可手动添加回调`setOnExposeCallback()`，拿到每一次的曝光项集合和曝光项对应itemView集合。

```java
itemReporter.setOnExposeCallback(new OnExposeCallback() {
            @Override
            public void onExpose(List<Integer> exposePosition, List<View> exposeView) {
                //do something
            }
        });
```

## 其他

- onResume()

  RecyclerView容器生命周期，根据业务需要，可在RecyclerView"可见"时调用，每调用一次会增加一次曝光。例如：Activity生命周期onResume()，Fragment生命周期onVisible(true)。

- reset()

  重置内部曝光集合。根据业务需要可在item位置发生变化时调用。

- release()

  释放相关资源，避免造成OOM以及一些额外的性能损耗。注意该方法调用后不要再使用其他任意方法，否则会抛出异常。

- setTounchInterval(long interval)

  多次点击滑动时的最小间隔，低于该间隔的点击曝光事件将不处理，单位为ms。

- SetResumeInterval(long interval)

  多次可见时的最小间隔，低于该间隔的可见曝光事件将不处理，单位为ms。







