1、ListView的基本用法
	首先回顾下ListView的基本用法，新建一个ListViewDemo项目，修改activity_main.xml中的代码，如下所示：
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ListView
        android:id="@+id/list_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</LinearLayout>
	然后在layout目录下为列表项新建一个自定义的布局文件listview_item.xml，在这个布局中，定义了一个TextView用于显示文本，一个ImageView用于显示图片，代码如下所示：
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    android:orientation="horizontal">

    <TextView
        android:id="@+id/text_view"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="@string/app_name"
        android:textSize="20sp" />

    <ImageView
        android:id="@+id/image_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@mipmap/ic_launcher" />

</LinearLayout>
	  提取列表项的属性：一个字符串和一张图片的ID，新建实体类ItemBean，作为ListView适配器的适配类型，代码如下所示：
public class ItemBean {

    private String text;
    private int imageId;

    public ItemBean(String text, int inmageId) {
        this.text = text;
        this.imageId = inmageId;
    }

    public String getText() {
        return text;
    }

    public int getImageId() {
        return imageId;
    }
}
	接下来需要创建一个自定义的适配器，Android提供了很多适配器的实现类，常用的实现类及其特点如下：
	ArrayAdapter：简单易用的Adapter，通常用于将数组或List集合的数据封装成多个列表项。
	SimpleAdapter：稍复杂但功能强大的Adapter，可用于将List集合的数据封装成多个列表项。
	SimpleCursorAdapter：与SimpleAdapter基本相似，只是用于封装Cursor提供的数据。
	BaseAdapter：以上Adapter的基类，通常用于扩展。扩展BaseAdapter可以对各列表项进行最大限度的定制。
	因为之后要对适配器进行封装，所以选择BaseAdapter作为实现类比较合适。新建类ListViewAdapter继承BaseAdapter，代码如下所示：
public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private List<ItemBean> datas;

    public ListViewAdapter(Context context, List<ItemBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ItemBean getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemBean item = getItem(i);
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(context, R.layout.listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(item.getText());
        viewHolder.imageView.setImageResource(item.getImageId());
        return view;
    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
	其中的getView()方法在每个列表项被滚动到屏幕内的时候都会被调用，如果每次都将布局重新加载一遍，当ListView快速滚动的时候，性能就会受到影响，这时可以使用getView()方法中的参数view，将之前加载好的布局进行缓存，以便之后可以进行重用。
	此外，每次在getView()中还会重复调用findViewById()方法来获取控件的实例，这时可以借助ViewHolder内部类，将控件的实例进行缓存。当view不为null的时候，调用View的getTag()方法获取到ViewHolder对象，就可以使用控件的实例了。
2、ViewHolder的优化分析
	通过上文对ListView基本用法的回顾，ViewHolder集成了列表项布局内各种控件的实例，如果要封装一个通用的ViewHolder，可以采用容器来存储控件，然后定义一个方法通过ID获取控件。当key的值为int类型，value的值为Object类型时，推荐使用SparseArray<T>，从源码的注释可以看出它和Map类似，并且效率更高，如下图所示：
 
封装后的ViewHolder代码如下所示：
public class ViewHolder {
    
    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mViews = new SparseArray<View>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getViewHolder(
            Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            return holder;
        }
    }

    // 通过ID获取控件
    public <T extends View>T getViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getmConvertView() {
        return mConvertView;
    }
}
	在ListViewAdapter的getView()方法中使用封装好的ViewHolder，代码如下所示：
@Override
public View getView(int i, View view, ViewGroup viewGroup) {
    ViewHolder viewHolder = ViewHolder.getViewHolder(
            context, view, viewGroup, R.layout.listview_item);
    TextView tv = viewHolder.getViewById(R.id.text_view);
    tv.setText(datas.get(i).getText());
    return viewHolder.getmConvertView();
}
	对比封装前的getView()方法，可以明显感觉到在代码上更加的简洁，可复用性也更强。其实ViewHolder还能进一步优化，因为ViewHolder的主要功能是通过ID获取控件，使用频率较高，所以可以考虑专门封装获取某一控件并进行赋值的方法，完整的ViewHolder的代码如下所示：
public class ViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mViews = new SparseArray<View>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getViewHolder(
            Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            return holder;
        }
    }

    // 通过ID获取控件
    public <T extends View>T getViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getmConvertView() {
        return mConvertView;
    }

    // 通过字符串设置TextView的值
    public ViewHolder setText(int viewId, String text) {
        TextView tv = getViewById(viewId);
        tv.setText(text);
        return this;
    }

    // 通过资源文件设置ImageView的值
    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView iv = getViewById(viewId);
        iv.setImageResource(resId);
        return this;
    }

    // 通过位图设置ImageView的值
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getViewById(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setImageURI(int viewId, String url) {
        ImageView iv = getViewById(viewId);
        // 通过网络图片设置ImageView的值
        return this;
    }
}
3、Adapter的优化分析
	接下来继续对Adapter进行优化分析，通过观察自定义的Adapter类可以发现，不同的Adapter除了getView()方法中的部分代码有所差异，其他方法基本相同，因此可以将这些方法抽取成一个抽象类CommonAdapter<T>，不同的Bean可以使用泛型来支持，代码如下所示：
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mDatas;
    private int mLayoutId;

    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        mInflater.from(context);
        this.mDatas = datas;
        this.mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.getViewHolder(mContext, convertView, parent, mLayoutId);
        convertView(holder, getItem(position));
        return holder.getmConvertView();
    }

    public abstract void convertView(ViewHolder holder, T t);
}
	在getView()方法中调用了一个公开的抽象方法convertView()来获取并操作控件，继承或通过匿名内部类来实现此方法。不过内部类这种方式并不被推荐，因为ListView中可能有许多的逻辑处理，会使Activity的代码看起来很臃肿，所以建议新建CommonAdapter的子类，在子类中对列表项内的控件进行操作，代码如下所示：
public class ListViewAdapter extends CommonAdapter<ItemBean> {

    public ListViewAdapter(Context context, List<ItemBean> datas) {
        super(context, datas, R.layout.listview_item);
    }

    @Override
    public void convertView(ViewHolder holder, ItemBean bean) {
        holder.setText(R.id.text_view, bean.getText())
                .setImageResource(R.id.image_view, bean.getImageId());
    }
}
	至此，ListView可复用适配器的封装及性能优化就全部结束了，GridView也同样适用。
