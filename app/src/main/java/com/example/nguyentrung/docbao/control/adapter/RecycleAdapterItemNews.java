package com.example.nguyentrung.docbao.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;

import java.util.ArrayList;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class RecycleAdapterItemNews extends RecyclerView.Adapter<RecycleAdapterItemNews.ViewHolderItemNews>{

    private ArrayList<News> arrNewses;
    private LayoutInflater inflater;
    private Context context;
    private CallbackSelectedNews callbackSelectedNews;

    public RecycleAdapterItemNews(ArrayList<News> arrNewses, Context context, CallbackSelectedNews callbackSelectedNews) {
        this.arrNewses = arrNewses;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.callbackSelectedNews = callbackSelectedNews;
    }

    @Override
    public ViewHolderItemNews onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_news, parent, false);
        return new ViewHolderItemNews(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderItemNews holder, int position) {
        News news = arrNewses.get(position);
        Glide.with(context)
                .load(news.getUrlImage())
                .fitCenter()
                .into(holder.img);
        holder.tvTitle.setText(news.getTitle());
        holder.tvPubdate.setText(news.getDate());
        holder.tvDescription.setText(news.getDescription());
    }

    @Override
    public int getItemCount() {
        return arrNewses.size();
    }



    class ViewHolderItemNews extends RecyclerView.ViewHolder{
        private ImageView img;
        private TextView tvTitle;
        private TextView tvPubdate;
        private TextView tvDescription;


        public ViewHolderItemNews(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imgItemNews);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitleItemNews);
            tvPubdate = (TextView) itemView.findViewById(R.id.tvPubdateItemNews);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescriptionItemNews);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbackSelectedNews.selectedItemNews(getAdapterPosition());
                }
            });
        }
    }

    public interface CallbackSelectedNews{
        void selectedItemNews(int pos);
    }

    public void addItem(News news, int position) {
        arrNewses.add(position, news);
        notifyItemInserted(arrNewses.size());
        notifyItemRangeChanged(position, arrNewses.size());
    }

    public void removeItem(int position) {
        arrNewses.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrNewses.size());
    }

}