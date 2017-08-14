package com.example.nguyentrung.docbao.view.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.view.activity.MainActivity;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.control.adapter.RecycleAdapterItemNews;
import com.example.nguyentrung.docbao.control.Support;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class FragmentNews extends Fragment implements RecycleAdapterItemNews.CallbackSelectedNews {
    private static final String TAG = "FragmentNews";
    private ArrayList<News> allArrNews;
    private ArrayList<News> arrNews;
    private RecyclerView recyclerView;
    private RecycleAdapterItemNews recycleAdapterItemNews;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String link;
    private int typeNewspaper;
    private boolean isRunning = false;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private String catalog;
    private boolean isTrue = true;

    public FragmentNews(ArrayList<News> allArrNews) {
        this.allArrNews = allArrNews;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        Bundle bundle = getArguments();
        arrNews = new ArrayList<>();
        arrNews.clear();
        typeNewspaper = bundle.getInt(Constant.KEY_TYPE);
        link = bundle.getString(Constant.KEY_LINK);
        catalog = bundle.getString(Constant.KEY_NAME_CATALOG);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.WHAT_ARR) {
                arrNews.addAll((Collection<? extends News>) msg.obj);
                if (typeNewspaper == Constant.TYPE_24H || typeNewspaper == Constant.TYPE_VNEXPRESS) {
                    recycleAdapterItemNews.notifyDataSetChanged();
//                    if (isRunning) {
////                        loadAds();
//                    }
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewNews);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN, Color.LTGRAY);
    }



    @Override
    public void onStart() {
        super.onStart();
        recycleAdapterItemNews = new RecycleAdapterItemNews(arrNews, getContext(), this);
        recyclerView.setAdapter(recycleAdapterItemNews);

        if (Support.isTablet(getContext())) {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "setOnRefreshListener: true");
                AsyncTaskParseXML2 asyncTaskParseXML = new AsyncTaskParseXML2(getContext(), handler, typeNewspaper);
                asyncTaskParseXML.execute(link);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000);
            }
        });
        if (typeNewspaper == Constant.TYPE_24H || typeNewspaper == Constant.TYPE_VNEXPRESS) {
            AsyncTaskParseXML2 asyncTaskParseXML = new AsyncTaskParseXML2(getContext(), handler, typeNewspaper);
            asyncTaskParseXML.execute(link);
        }
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int firstPos = 0;
                if (Support.isTablet(getContext())) {
                    firstPos = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                } else {
                    linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                }
                if (firstPos > 0) {
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    swipeRefreshLayout.setEnabled(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
    }

    @Override
    public void selectedItemNews(int pos) {
        FragmentReadingNews fragmentReadingNews = new FragmentReadingNews();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_ITEM_NEWS, arrNews.get(pos));
        bundle.putInt(Constant.KEY_TYPE_NEWSPAPER, typeNewspaper);
        bundle.putInt(Constant.KEY_FROM, Constant.NEW_NEWS);
        fragmentReadingNews.setArguments(bundle);
        FragmentController.addWithAddToBackStackAnimation(getContext(), fragmentReadingNews, this);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarAfterAddFragment(catalog);
    }

    public class AsyncTaskParseXML2 extends AsyncTask<String, Void, ArrayList<News>> {
        ArrayList<News> arrNews = new ArrayList<>();
        Context context;
        Handler handler;
        int typeNewspaper;

        public AsyncTaskParseXML2(Context context, Handler handler, int typeNewspaper) {
            this.context = context;
            this.handler = handler;
            this.typeNewspaper = typeNewspaper;
        }

        public String getCharacterDataFromElement(Element e) {

            NodeList list = e.getChildNodes();
            String data;

            for (int index = 0; index < list.getLength(); index++) {
                if (list.item(index) instanceof CharacterData) {
                    CharacterData child = (CharacterData) list.item(index);
                    data = child.getData();
                    if (data != null && data.trim().length() > 0)
                        return child.getData();
                }
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isRunning) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected ArrayList<News> doInBackground(String... params) {
            if (isRunning) {
                try {
                    URL url = new URL(params[0]);
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.parse(new InputSource(url.openStream()));
                    document.getDocumentElement().normalize();

                    NodeList nodeList = document.getElementsByTagName("item");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        Element fstElmnt = (Element) node;
                        String title = null;
                        String link = null;
                        String pubDate = null;
                        NodeList descriptionList = fstElmnt.getElementsByTagName("description");
                        Element descriptionElement = (Element) descriptionList.item(0);
                        String description = getCharacterDataFromElement(descriptionElement);
                        String URLImage = null;
                        String alt = null;
                        if (typeNewspaper == Constant.TYPE_24H) {
                            NodeList titleList = fstElmnt.getElementsByTagName("title");
                            Element titleElement = (Element) titleList.item(0);
                            titleList = titleElement.getChildNodes();
                            title = ((Node) titleList.item(0)).getNodeValue();
                            if (title.contains("&#34;")) {
                                title = title.replace("&#34;", "");
                            }
                            int startURLImage = description.indexOf("' src='");
                            int endURLImage = description.indexOf("' alt='");
                            URLImage = description.substring(startURLImage + 7, endURLImage).trim();

                            int startAlt = description.lastIndexOf("' /></a><br />");
                            alt = description.substring(startAlt + 14);
                        } else if (typeNewspaper == Constant.TYPE_VNEXPRESS) {
                            NodeList titleList = fstElmnt.getElementsByTagName("title");
                            Element titleElement = (Element) titleList.item(0);
                            titleList = titleElement.getChildNodes();
                            if (titleList.getLength() > 0) {
                                title = ((Node) titleList.item(0)).getNodeValue();
                            }
                            int startURLImage = description.indexOf("src=");
                            int endURLImage = description.lastIndexOf(" ></a></br");
                            try {
                                URLImage = description.substring(startURLImage + 5, endURLImage - 1);
                            } catch (StringIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }


                            int startAlt = description.lastIndexOf("</a></br>");
                            if (description.length() > 9) {
                                alt = description.substring(startAlt + 9);
                            }
                        }

                        NodeList linkList = fstElmnt.getElementsByTagName("link");
                        if (linkList.getLength() > 0) {
                            Element linkElement = (Element) linkList.item(0);
                            linkList = linkElement.getChildNodes();
                            link = ((Node) linkList.item(0)).getNodeValue();
                        }

                        NodeList pubDateList = fstElmnt.getElementsByTagName("pubDate");
                        if (pubDateList.getLength() > 0) {
                            Element pubDateElement = (Element) pubDateList.item(0);
                            pubDateList = pubDateElement.getChildNodes();
                            pubDate = ((Node) pubDateList.item(0)).getNodeValue();
                        }

                        if (!TextUtils.isEmpty(link) && !link.contains("http://e.vnexpress")) {
                            News news = new News(title, URLImage, alt, link, pubDate, typeNewspaper);
                            arrNews.add(news);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return arrNews;
        }

        @Override
        protected void onPostExecute(ArrayList<News> newses) {
            super.onPostExecute(newses);
            isRunning = false;
            Message message = new Message();
            message.what = Constant.WHAT_ARR;
            message.obj = arrNews;
            handler.sendMessage(message);
            swipeRefreshLayout.setRefreshing(false);
            if(isTrue){
                allArrNews.addAll(arrNews);
                Log.e("arr and Allarr",arrNews.size() + "  "+allArrNews.size());
                isTrue = false;
            }
        }
    }
}
