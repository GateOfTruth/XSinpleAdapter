public abstract class BaseRecyclerViewAdapter<K> extends RecyclerView.Adapter<BaseViewHolder> {
    protected Context mContext;

    protected List<K> dataList;

    private BaseAnimation animation;

    protected ItemClickListener itemClickListener;

    protected LongItemClickListener longItemClickListener;

    private long duration;

    private Interpolator interpolator;

    public final int header = 0, normal = 1, footer = 2;

    protected final LayoutInflater inflater;


    protected boolean isShowNothingView;

    protected boolean isShowNoMoreView;

    protected boolean isShowPagingView;


    public BaseRecyclerViewAdapter(Context mContext) {
        dataList = new ArrayList<>();
        this.mContext = mContext;
        duration = 500;
        interpolator = new LinearInterpolator();
        inflater = LayoutInflater.from(mContext);
    }


    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setLongItemClickListener(LongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }

    public void setAnimation(BaseAnimation animation) {
        this.animation = animation;
    }


    public List<K> getDataList() {
        return dataList;
    }


    public void showNoThingView() {
        isShowNothingView = true;
        notifyItemChanged(getItemCount() - 1);
    }

    public void showProgress() {
        isShowPagingView = true;
        isShowNoMoreView = false;
        isShowNothingView = false;
        notifyItemChanged(getItemCount() - 1);
    }

    public void hideProgress() {
        isShowPagingView = false;
        isShowNoMoreView = false;
        isShowNothingView = false;
        notifyItemChanged(getItemCount() - 1);
    }

    public void hideNoMoreView() {
        isShowPagingView = false;
        isShowNoMoreView = false;
        isShowNothingView = false;
        notifyItemChanged(getItemCount() - 1);
    }

    public void showNoMoreView() {
        isShowPagingView = false;
        isShowNoMoreView = true;
        isShowNothingView = false;
        notifyItemChanged(getItemCount() - 1);
    }

    public void showPagingView() {
        isShowPagingView = true;
        isShowNoMoreView = false;
        isShowNothingView = false;
        notifyItemChanged(getItemCount() - 1);
    }

    protected void dealWithFooter(BaseViewHolder holder, int position) {
        BaseCircleProgressBar progressView = holder.getView(R.id.progress_footer);
        TextView noMoreView = holder.getView(R.id.tv_noMore);
        ImageView nothingView = holder.getView(R.id.iv_nothing);
        TextView nothingTextView=holder.getView(R.id.tv_nothing_string);
        if (progressView == null || noMoreView == null || nothingView == null) {
            return;
        }
        if (isShowNothingView) {
            noMoreView.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.INVISIBLE);
            nothingView.setVisibility(View.VISIBLE);
            nothingTextView.setVisibility(View.VISIBLE);
        } else {
            if (isShowNoMoreView)
                noMoreView.setVisibility(View.VISIBLE);
            else
                noMoreView.setVisibility(View.INVISIBLE);
            if (isShowPagingView)
                progressView.setVisibility(View.VISIBLE);
            else
                progressView.setVisibility(View.INVISIBLE);
            nothingView.setVisibility(View.GONE);
            nothingTextView.setVisibility(View.GONE);
        }
    }


    public void addDataList(List<K> list, int position, boolean isClear) {
        addDataList(list, position, position, isClear);
    }

    public void addDataList(List<K> list, int itemposition, int listPosition, boolean isClear) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (isClear) {
            notifyItemRangeRemoved(itemposition, dataList.size());
            dataList.clear();
            dataList.addAll(list);
        } else {
            dataList.addAll(listPosition, list);
        }
        notifyItemRangeChanged(itemposition, dataList.size());
    }

    public void addDataList(List<K> list) {
        addDataList(list, 0, true);
    }

    public void addDataList(List<K> list, int position) {
        addDataList(list, position, false);
    }

    public void addDataList(List<K> list, boolean isclear) {
        addDataList(list, 0, isclear);
    }

    public void addData(K data, int position) {
        dataList.add(position, data);
        notifyItemRangeChanged(position, dataList.size() - position);
    }

    public void addData(K data) {
        dataList.add(data);
        notifyItemRangeChanged(dataList.size() - 1, 1);
    }

    public void updateData(K data, int dataPosition, int itemPosition) {
        dataList.set(dataPosition, data);
        notifyItemChanged(itemPosition, data);
    }

    public void removeData(int dataPosition, int itemPosition) {
        dataList.remove(dataPosition);
        notifyItemRemoved(itemPosition);
    }

    public void clearData() {
        notifyItemRangeRemoved(0, dataList.size());
        dataList.clear();
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, int position) {
        setHolderBackground(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(v, v.getTag(), holder.getAdapterPosition());
                }
            }
        });


    }


    /*
    跟布局是cardview的时候重写为空，不然cardview不显示
     */
    protected void setHolderBackground(BaseViewHolder holder, int position) {
        holder.itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_ripple));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (animation == null)
            return;
        for (Animator a : animation.getAnimators(holder.itemView)) {
            a.setDuration(duration);
            a.setInterpolator(interpolator);
            a.start();
        }
    }


    public interface ItemClickListener {
        void onClick(View view, Object data, int position);
    }

    public interface LongItemClickListener {
        void onClick(View view, Object data, int position);
    }

    public interface BaseAnimation {
        Animator[] getAnimators(View view);
    }
}