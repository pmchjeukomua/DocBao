package com.example.nguyentrung.docbao.control;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.example.nguyentrung.docbao.Dialog.DialogLoading;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.model.News;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class AsyncTaskParseXML extends AsyncTask<String, Void, ArrayList<News>> {
    private static final String TAG = "AsyncTaskParseXML";
    private ArrayList<News> arrNews = new ArrayList<>();
    private Context context;
    private Handler handler;
    private DialogLoading dialogLoading;
    private int typeNewspaper;

    public AsyncTaskParseXML(Context context, Handler handler, int typeNewspaper) {
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
        dialogLoading = new DialogLoading(context, context.getResources().getString(R.string.loading), true, false);
    }

    @Override
    protected ArrayList<News> doInBackground(String... params) {
//        if (isRunning) {
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
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrNews;
    }


    @Override
    protected void onPostExecute(ArrayList<News> newses) {
        super.onPostExecute(newses);
//        isRunning = false;
        Message message = new Message();
        message.what = Constant.WHAT_ARR;
        message.obj = arrNews;
        handler.sendMessage(message);
//            dialogLoading.dismiss();
    }
}
