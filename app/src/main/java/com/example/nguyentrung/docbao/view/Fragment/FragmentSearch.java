package com.example.nguyentrung.docbao.view.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.view.activity.MainActivity;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.control.adapter.RecycleAdapterItemNews;

import java.util.ArrayList;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class FragmentSearch extends Fragment implements RecycleAdapterItemNews.CallbackSelectedNews {
    private EditText editSearch;
    private RecyclerView recyclerViewNews;
    private RecycleAdapterItemNews adapterItemNews;
    private ArrayList<News> arrNewses;
    private LinearLayoutManager linearLayoutManager;
    private String string;
    private ArrayList<News> arr;
    private TextView tvNoNews;

    public FragmentSearch(ArrayList<News> arrNewses) {
        this.arrNewses = arrNewses;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editSearch = (EditText) view.findViewById(R.id.edit_search);
        editSearch.requestFocus();
        tvNoNews = (TextView)view.findViewById(R.id.tvNoNews);
        arr = new ArrayList<>();
        recyclerViewNews = (RecyclerView) view.findViewById(R.id.recyclerViewSearch);
        adapterItemNews = new RecycleAdapterItemNews(arr, getContext(), this);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    string = editSearch.getText().toString();
                    arr.clear();
                    if (!string.equals("")) {
                        for (int i = 0; i < arrNewses.size(); i++) {
                            if (arrNewses.get(i).getTitle().toLowerCase().contains(string.toLowerCase()) ){
                                arr.add(arrNewses.get(i));
                            }
                        }
                    }
                    if(arr.size() ==0){
                        tvNoNews.setVisibility(View.VISIBLE);
                    }else {
                        tvNoNews.setVisibility(View.INVISIBLE);
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerViewNews.setLayoutManager(linearLayoutManager);
                        recyclerViewNews.setAdapter(adapterItemNews);
                    }
                }
                return false;
            }
        });
//        editSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    @Override
    public void selectedItemNews(int pos) {
        FragmentReadingNews fragmentReadingNews = new FragmentReadingNews();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_ITEM_NEWS, arr.get(pos));
        bundle.putInt(Constant.KEY_TYPE_NEWSPAPER, arr.get(pos).getType());
        bundle.putInt(Constant.KEY_FROM, Constant.NEW_NEWS);
        fragmentReadingNews.setArguments(bundle);

        FragmentController.addWithAddToBackStackAnimation(getContext(), fragmentReadingNews, this);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarAfterAddFragment("Tìm kiếm");

    }

//    boolean KMP(String source, String find) {
//        source = source.toLowerCase();
//        find = find.toLowerCase();
//        int[] next = new int[100];
//        int i = 0, len, j = -1, lensource;
//
//        len = find.length();
//        lensource = source.length();
//        next[0] = -1;
//        do {
//            if (j == -1 || find.charAt(i) == find.charAt(j)) {
//                i++;
//                j++;
//                next[i] = j;
//            } else
//                j = next[j];
//        } while (i < len - 1);
//        i = j = 0;
//        do {
//            if (j == 0 || source.charAt(i) == find.charAt(j)) {
//                i++;
//                j++;
//            } else
//                j = next[j];
//        } while (j < len && i < lensource);
//        if (j >= len)
//            return true;
//        else
//            return false;
//    }

}
