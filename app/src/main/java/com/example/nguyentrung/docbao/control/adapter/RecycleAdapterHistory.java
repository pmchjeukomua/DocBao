package com.example.nguyentrung.docbao.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyentrung.docbao.control.SqliteM;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.model.NewsSave;
import com.example.nguyentrung.docbao.view.Fragment.FragmentLinkSaved;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class RecycleAdapterHistory extends RecyclerView.Adapter<RecycleAdapterHistory.ViewHolderNews> {
    private ArrayList<NewsSave> arrayNews = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private CallbackSelectedNews callbackSelectedNews;
    private SqliteM sqliteM;

    public RecycleAdapterHistory(ArrayList<NewsSave> arrayNews, Context context,CallbackSelectedNews callbackSelectedNews) {
        this.arrayNews = arrayNews;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.callbackSelectedNews = callbackSelectedNews;
    }

    @Override
    public ViewHolderNews onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_news, parent, false);
        return new ViewHolderNews(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderNews holder, int position) {
        NewsSave news = arrayNews.get(position);
//        Glide.with(context)
//                .load(news.getUrlImage())
//                .fitCenter()
//                .into(holder.img);
//        try {
//            byte[] outImage = news.getu();
//            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
//            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//            holder.img.setImageBitmap(theImage);
//        }catch (Exception e){
//            Log.e("sdasd","loi");
//        }

        //conver image
        sqliteM = new SqliteM(context);
        byte[] outImage = sqliteM.getImage(news.getImg());
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        holder.img.setImageBitmap(theImage);
        holder.tvTitle.setText(news.getTitle());
        holder.tvPubdate.setText(news.getPubdate());
        holder.tvDescription.setText(news.getDescription());
    }

    @Override
    public int getItemCount() {
        return arrayNews.size();
    }

    class ViewHolderNews extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView tvTitle;
        private TextView tvPubdate;
        private TextView tvDescription;

        public ViewHolderNews(View itemView) {
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
    public void addItem(NewsSave news, int position) {
        arrayNews.add(position, news);
        notifyItemInserted(arrayNews.size());
        notifyItemRangeChanged(position, arrayNews.size());
    }

    public void removeItem(int position) {
        arrayNews.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayNews.size());
    }
}
