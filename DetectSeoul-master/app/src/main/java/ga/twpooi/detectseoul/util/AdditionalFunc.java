package ga.twpooi.detectseoul.util;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by tw on 2017-09-04.
 */

public class AdditionalFunc {

    public static String replaceNewLineString(String s){

        String str = s.replaceAll("\n", "\\\\n");
        return str;

    }

    public static long getMilliseconds(int year, int month, int day){

        long days = 0;

        try {
            String cdate = String.format("%d%02d%02d", year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(cdate);
            days = date.getTime();
            System.out.println(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;

    }

    public static long getTodayMilliseconds(){
        Calendar now = Calendar.getInstance();
        return getMilliseconds(now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DAY_OF_MONTH));
    }

    public static int getDday(long eTime){

        long cTime = System.currentTimeMillis();
        Date currentDate = new Date(cTime);
        Date finishDate = new Date(eTime);
//        System.out.println(cTime + ", " + eTime);

        DateFormat df = new SimpleDateFormat("yyyy");
        int currentYear = Integer.parseInt(df.format(currentDate));
        int finishYear = Integer.parseInt(df.format(finishDate));
        df = new SimpleDateFormat("MM");
        int currentMonth = Integer.parseInt(df.format(currentDate));
        int finishMonth = Integer.parseInt(df.format(finishDate));
        df = new SimpleDateFormat("dd");
        int currentDay = Integer.parseInt(df.format(currentDate));
        int finishDay = Integer.parseInt(df.format(finishDate));

//        System.out.println(currentYear + ", " + currentMonth + ", " + currentDay);
//        System.out.println(finishYear + ", " + finishMonth + ", " + finishDay);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(currentYear, currentMonth, currentDay);
        end.set(finishYear, finishMonth, finishDay);

        Date startDate = start.getTime();
        Date endDate = end.getTime();

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);


        return (int)diffDays;
    }

    public static String getDateString(long time){

        Date currentDate = new Date(time);
        DateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일");
        return df.format(currentDate);

    }

    public static String parseDateString(String d, String t){

        String date = "";

        try {
            date = d.substring(0, 4) + "." + d.substring(4, 6) + "." + d.substring(6, 8) + " " + t.substring(0, 2) + ":" + t.substring(2, 4) + ":" + t.substring(4, 6);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return date;

    }

    public static ArrayList<String> stringToArrayList(String str){

        ArrayList<String> list = new ArrayList<>();

        for(String s : str.split(",")){
            if(!"".equals(s)){
                list.add(s);
            }
        }

        return list;
    }

    public static String[] arrayListToStringArray(ArrayList<String> list){
        String[] st = new String[list.size()];
        for(int i=0; i<list.size(); i++){
            st[i] = list.get(i);
        }
        return st;
    }

    public static String arrayListToString(ArrayList<String> list) {

        String str = "";
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i);
            if (i + 1 < list.size()) {
                str += ",";
            }
        }

        return str;

    }

    public static String integerArrayListToString(ArrayList<Integer> list){

        String str = "";
        for(int i=0; i<list.size(); i++){
            str += list.get(i);
            if(i+1<list.size()){
                str += ",";
            }
        }
        return str;
    }

    public static String needEnglishText(){
        String language = Locale.getDefault().getDisplayLanguage();
        if( language.equals( Locale.KOREAN.getDisplayLanguage() ) ){
            return "0";
        }else{
            return "1";
        }
    }

    public static String convertAttraction(String attraction){
        if(needEnglishText().equals("1")){
            switch (attraction){
                case "heunginjimun":
                    attraction = "Heunginjimun Gate";
                    break;
                case "nseoultower":
                    attraction = "N Seoul Tower";
                    break;
                case "thenationalmuseumofkorea":
                    attraction = "The National Museum of Korea";
                    break;
                case "gyeongbokgung":
                    attraction = "Gyeongbokgung";
                    break;
                case "seonyugyo":
                    attraction = "Seonyugyo Bridge";
                    break;
            }
        }else{
            switch (attraction){
                case "heunginjimun":
                    attraction = "흥인지문(동대문)";
                    break;
                case "nseoultower":
                    attraction = "N서울타워";
                    break;
                case "thenationalmuseumofkorea":
                    attraction = "국립중앙박물관";
                    break;
                case "gyeongbokgung":
                    attraction = "경복궁";
                    break;
                case "seonyugyo":
                    attraction = "선유교";
                    break;
            }
        }
        return attraction;
    }

    public static Attraction getAttractionInfo(String data){

        ArrayList<Attraction> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                Attraction attraction = new Attraction();

                attraction.id = (String)temp.get("id");
                attraction.title = (String)temp.get("title");
                attraction.sContents = (String)temp.get("sContents");
                attraction.sContents.replaceAll("   ", "\n");
                attraction.contents = (String)temp.get("contents");
                attraction.contents.replaceAll("   ", "\n");
                attraction.address = (String)temp.get("address");
                attraction.district = (String)temp.get("district");
                attraction.telephone = (String)temp.get("telephone");
                attraction.web = (String)temp.get("web");
                attraction.url = (String)temp.get("url");
                String lat = (String)temp.get("lat");
                String lng = (String)temp.get("lng");
                if("".equals(lat)){
                    attraction.lat = 0.0;
                }else{
                    attraction.lat = Double.parseDouble(lat);
                }
                if("".equals(lng)){
                    attraction.lng = 0.0;
                }else{
                    attraction.lng = Double.parseDouble(lng);
                }

                String cTemp = (String)temp.get("categorize");
                ArrayList<String> cList = new ArrayList<>();
                for(String s : cTemp.split(",")){
                    if("".equals(s)){
                        continue;
                    }
                    cList.add(s);
                }
                attraction.categorize = cList;

                String mainImage = (String)temp.get("mainImage");
                String subImage = (String)temp.get("subImage");
                ArrayList<String> pictureList = new ArrayList<>();
                pictureList.add(mainImage);
                for(String s : subImage.split(",")){
                    if("".equals(s)){
                        continue;
                    }
                    pictureList.add(s);
                }
                attraction.picture = pictureList;

                String detailInfo = (String)temp.get("detail");
                ArrayList<String[]> detailList = new ArrayList<>();
                for(String s : detailInfo.split("&&")){
                    if("".equals(s)){
                        continue;
                    }
                    String[] strs = s.split("##");
                    detailList.add(strs);
                }
                attraction.detail = detailList;

                list.add(attraction);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(list.size() > 0){
            return list.get(0);
        }else{
            return null;
        }

    }

    public static ArrayList<Attraction> getAttractionInfoList(String data){

        ArrayList<Attraction> list = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray results = jObject.getJSONArray("result");
            String countTemp = (String)jObject.get("num_result");
            int count = Integer.parseInt(countTemp);

            for ( int i = 0; i < count; ++i ) {
                JSONObject temp = results.getJSONObject(i);

                Attraction attraction = new Attraction();

                attraction.id = (String)temp.get("id");
                attraction.title = (String)temp.get("title");
                attraction.sContents = (String)temp.get("sContents");
                attraction.sContents.replaceAll("   ", "\n");
                attraction.contents = (String)temp.get("contents");
                attraction.contents.replaceAll("   ", "\n");
                attraction.address = (String)temp.get("address");
                attraction.district = (String)temp.get("district");
                attraction.telephone = (String)temp.get("telephone");
                attraction.web = (String)temp.get("web");
                attraction.url = (String)temp.get("url");
                String lat = (String)temp.get("lat");
                String lng = (String)temp.get("lng");
                if("".equals(lat)){
                    attraction.lat = 0.0;
                }else{
                    attraction.lat = Double.parseDouble(lat);
                }
                if("".equals(lng)){
                    attraction.lng = 0.0;
                }else{
                    attraction.lng = Double.parseDouble(lng);
                }

                String cTemp = (String)temp.get("categorize");
                ArrayList<String> cList = new ArrayList<>();
                for(String s : cTemp.split(",")){
                    if("".equals(s)){
                        continue;
                    }
                    cList.add(s);
                }
                attraction.categorize = cList;

                String mainImage = (String)temp.get("mainImage");
                String subImage = (String)temp.get("subImage");
                ArrayList<String> pictureList = new ArrayList<>();
                pictureList.add(mainImage);
                for(String s : subImage.split(",")){
                    if("".equals(s)){
                        continue;
                    }
                    pictureList.add(s);
                }
                attraction.picture = pictureList;

                String detailInfo = (String)temp.get("detail");
                ArrayList<String[]> detailList = new ArrayList<>();
                for(String s : detailInfo.split("&&")){
                    if("".equals(s)){
                        continue;
                    }
                    String[] strs = s.split("##");
                    detailList.add(strs);
                }
                attraction.detail = detailList;

                list.add(attraction);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }


}
