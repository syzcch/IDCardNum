package com.project.syz.randomidnum.Utiles;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by syz on 2016/1/29.
 */
public class LocateInfoUtil {
    //get province list,
    // file-->database
    public static Map<Integer,List> getProvince(File file){

        String sql = "select provinceid ,province from province ";
        SQLiteDatabase db = null;
        Cursor c = null;
        Map<Integer,List> provinceData = new HashMap<Integer,List>();
        //List provinceList = null;
        try{
            db = SQLiteDatabase.openOrCreateDatabase(file, null);
            c = db.rawQuery(sql, null);
            List provinceList1 = new ArrayList();
            List provinceList2 = new ArrayList();
            while(c.moveToNext()){
                Map provinceMap = new HashMap();
                provinceMap.put(c.getString(1), c.getInt(0));
                provinceList1.add(provinceMap);
                provinceList2.add(c.getString(1));
            }
            provinceData.put(0, provinceList1);
            provinceData.put(1, provinceList2);
        }catch(Exception e){
            Log.d("WineStock", "getProvince:" + e.getMessage());
        }finally{
            if(c!=null){
                c.close();
            }
            if(db!=null){
                db.close();
            }
        }
        return provinceData;
    }
    //get city list linked to the province,
    // file-->database,id-->province ID
    public static Map<Integer,List> getCityByPid(int id,File file){

        String sql = "select cityid,city  from city where provinceid= "+id;
        SQLiteDatabase db = null;
        Cursor c = null;
        Map<Integer,List> cityData = new HashMap<Integer,List>();
        //List cityList = null;
        try{
            db = SQLiteDatabase.openOrCreateDatabase(file, null);
            c = db.rawQuery(sql, null);
            List cityList1 = new ArrayList();
            List cityList2 = new ArrayList();
            while(c.moveToNext()){
                Map cityMap = new HashMap();
                cityMap.put(c.getString(1), c.getInt(0));
                cityList1.add(cityMap);
                cityList2.add(c.getString(1));
            }
            cityData.put(0, cityList1);
            cityData.put(1, cityList2);

        }catch(Exception e){
            Log.d("WineStock", "getCityByPid:"+e.getMessage());
        }finally{
            if(c!=null){
                c.close();
            }
            if(db!=null){
                db.close();
            }
        }
        return cityData;
    }
    //get area list,
    // file-->database,id-->city ID
    public static Map<Integer,List> getAreaByPid(int id,File file){

        String sql = "select area,areaid  from area where cityid= "+id;
        SQLiteDatabase db = null;
        Cursor c = null;
        List<String> areaList = null;
        List areaIdList = null;
        Map<Integer,List> areaData = new HashMap<Integer,List>();
        try{
            db = SQLiteDatabase.openOrCreateDatabase(file, null);
            c = db.rawQuery(sql, null);
            areaList = new ArrayList<String>();
            areaIdList = new ArrayList<String>();

            while(c.moveToNext()){
                Map areaMap = new HashMap();
                areaMap.put(c.getString(0), c.getInt(1));
                areaList.add(c.getString(0));
                areaIdList.add(areaMap);
            }
            areaData.put(0, areaList);
            areaData.put(1, areaIdList);
        }catch(Exception e){
            Log.d("WineStock", "getAreaByPid:"+e.getMessage());
        }finally{
            if(c!=null){
                c.close();
            }
            if(db!=null){
                db.close();
            }
        }
        return areaData;
    }

    public static void exportToCSV( String fileName,String sql,File file) {
        Cursor c = null;
        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        SQLiteDatabase db = null;
        BufferedWriter bfw;
        File saveFile = new File("/mnt/shared/shareGeny", fileName);

        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null);
            c = db.rawQuery(sql, null);
            rowCount = c.getCount();
            colCount = c.getColumnCount();
//            Log.d("export    @@@@@@","before rowCount"+rowCount+colCount+"   "+ saveFile.toString());
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            assert(rowCount > 0);
            if (rowCount > 0) {
                c.moveToFirst();
                // write title
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(c.getColumnName(i) + ',');
                    else
                        bfw.write(c.getColumnName(i));
                }
                // newline
                bfw.newLine();
                // write data
                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);
 //                   Log.d("export    @@@@@@", "正在导出第" + (i + 1) + "条");
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            bfw.write(c.getString(j) + ',');
                        else
                            bfw.write(c.getString(j));
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }

            bfw.flush();
            bfw.close();
//            Log.d("export    @@@@@@", "导出完毕！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            Log.d("export    @@@@@@","in finally");
            if(c!=null){
                c.close();
            }
        }
    }

}
