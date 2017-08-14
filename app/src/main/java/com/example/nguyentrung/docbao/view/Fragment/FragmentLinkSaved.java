package com.example.nguyentrung.docbao.view.Fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.control.SqliteM;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.control.adapter.RecycleAdapterHistory;
import com.example.nguyentrung.docbao.control.adapter.RecycleAdapterItemNews;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.model.NewsSave;
import com.example.nguyentrung.docbao.view.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class FragmentLinkSaved extends Fragment implements View.OnClickListener, RecycleAdapterHistory.CallbackSelectedNews {

//    private SQLiteManager sqLiteManager;
    private ArrayList<NewsSave> arrNewses = new ArrayList<>();
    private RecyclerView recyclerViewNews;
    private RecycleAdapterHistory adapterHistory;
//    private RecycleAdapterItemNews adapterItemNews;
    private LinearLayoutManager linearLayoutManager;
    public static final String TAG = "FragmentLinkSaved";
    private Paint p = new Paint();
    private Snackbar snackbar;
    private Dialog dialogConfirmDeleteAll;
    private Button btnConfirm;
    private Button btnCancel;
    private SqliteM sqLiteM;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sqLiteManager = new SQLiteManager(getContext());
//        arrNewses.addAll(sqLiteManager.getArrNews());
        sqLiteM = new SqliteM(getContext());
        arrNewses = new ArrayList<>();
        arrNewses.addAll(sqLiteM.getAllNews());
        setHasOptionsMenu(true);
        initDialogConfirmDeleteAll();
    }

    private void initDialogConfirmDeleteAll() {
        dialogConfirmDeleteAll = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_delete_all, null);
        dialogConfirmDeleteAll.setContentView(view);
        dialogConfirmDeleteAll.setCancelable(false);
        btnConfirm = (Button) view.findViewById(R.id.btnConfirmDeleteAll);
        btnCancel = (Button) view.findViewById(R.id.btnCancelDeleteAll);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_link, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        recyclerViewNews = (RecyclerView) view.findViewById(R.id.recyclerViewHistory);
//        adapterItemNews = new RecycleAdapterItemNews(arrNewsesM, getContext(), this);
//        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerViewNews.setLayoutManager(linearLayoutManager);
//        recyclerViewNews.setAdapter(adapterItemNews);
        recyclerViewNews = (RecyclerView)view.findViewById(R.id.recyclerViewHistory);
        adapterHistory = new RecycleAdapterHistory(arrNewses,getContext(),this);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewNews.setLayoutManager(linearLayoutManager);
        recyclerViewNews.setAdapter(adapterHistory);
        initSwipe();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.e(TAG, "onStop");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        arrNewses.clear();
//        sqLiteManager = new SQLiteManager(getContext());
//        arrNewses.addAll(sqLiteManager.getArrNews());
//        adapterItemNews.notifyDataSetChanged();
        arrNewses.clear();
        sqLiteM = new SqliteM(getContext());
        arrNewses.addAll(sqLiteM.getAllNews());
        adapterHistory.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_saved_link, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menuDeleteAll).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuDeleteAll) {
            dialogConfirmDeleteAll.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectedItemNews(int pos) {
        FragmentReadingNewsSave fragmentReadingNewsSave = new FragmentReadingNewsSave();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_ITEM_NEWS, arrNewses.get(pos));
        bundle.putInt(Constant.KEY_FROM, Constant.OLD_NEWS);
        fragmentReadingNewsSave.setArguments(bundle);

        FragmentController.addWithAddToBackStackAnimation(getContext(), fragmentReadingNewsSave, this);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarAfterAddFragment(getActivity().getResources().getString(R.string.savedlink));
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    final NewsSave news = arrNewses.get(position);
                    adapterHistory.removeItem(position);
                    sqLiteM.deleteNews(news.getTitle());
                    snackbar = Snackbar.make(recyclerViewNews, R.string.deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adapterHistory.addItem(news, position);
                                    recyclerViewNews.scrollToPosition(position);
                                    sqLiteM.addNews(news);
                                }
                            });
                    snackbar.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor((getResources().getColor(R.color.background_swipe)));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_delete = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_delete, p);
                    } else {
                        p.setColor(getResources().getColor(R.color.background_swipe));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewNews);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirmDeleteAll:
                sqLiteM.deleteAllRecords();
                arrNewses.clear();
                adapterHistory.notifyDataSetChanged();
                dialogConfirmDeleteAll.dismiss();
                break;
            case R.id.btnCancelDeleteAll:
                dialogConfirmDeleteAll.dismiss();
                break;
        }
    }
}
