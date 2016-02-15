package com.project.syz.randomidnum;

import android.content.ClipboardManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.project.syz.randomidnum.Utiles.LocateInfoUtil.getAreaByPid;
import static com.project.syz.randomidnum.Utiles.LocateInfoUtil.getCityByPid;
import static com.project.syz.randomidnum.Utiles.LocateInfoUtil.getProvince;


public class MainActivity extends ActionBarActivity {

    private long exitTime = 0;

    private Spinner spprovince;
    private Spinner spcity;
    private Spinner sparea;
    private File dir,file;

    private Spinner spyear;
    private Spinner spmonth;
    private Spinner spday;
    private Spinner snum;
    private ArrayList<String> dataYear = new ArrayList<String>();
    private ArrayList<String> dataMonth = new ArrayList<String>();
    private ArrayList<String> dataDay = new ArrayList<String>();
    private ArrayList<String> dataNum = new ArrayList<String>();
    private ArrayAdapter<String> adapterSpYear;
    private ArrayAdapter<String> adapterSpMonth;
    private ArrayAdapter<String> adapterSpDay;
    private ArrayAdapter<String> adapterSpNum;

    private RadioGroup radioSex;
    private String sex;

    private final int BUFFER_SIZE = 400000;
    private SQLiteDatabase sqlDB;

    private List provinceList,provinceData;
    private List cityList,cityData;
    private List areaList,areaData;
    private String selectedProvince = "";
    private String selectedCity = "";
    private String selectedArea = "";

    private Context mContext;
    private CalIdInfo singleCalInfo;
    private int locNum = 0;
    private TextView resText;

    private Button btRun;
//    private Button btLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioSex = (RadioGroup)findViewById(R.id.radioSex);
        spyear = (Spinner) findViewById(R.id.spyear);
        spmonth = (Spinner) findViewById(R.id.spmonth);
        spday = (Spinner) findViewById(R.id.spday);
        spprovince = (Spinner) findViewById(R.id.spprovince);
        spcity = (Spinner) findViewById(R.id.spcity);
        sparea = (Spinner) findViewById(R.id.sparea);
        snum = (Spinner)  findViewById(R.id.number);
        resText = (TextView) findViewById(R.id.showResult);
        //set textview with scroll function
        resText.setMovementMethod(ScrollingMovementMethod.getInstance());
        btRun = (Button) findViewById(R.id.runCreate);
        btRun.setVisibility(View.INVISIBLE);
//        btLoad = (Button)findViewById(R.id.loadInfo);

        mContext = this;
        new LoadingDB().execute("");
    }

    private class LoadingDB extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                loadInfo();
                return "Sucessful!";
            } catch (Exception e) {
                Log.e("Database Loading", "Error in loading database");
                e.printStackTrace();
                return "Error in loading database";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            btRun.setVisibility(View.VISIBLE);
//            btLoad.setText("重新加载");
        }
    }

    private void CopyAndLoadDB() {
        // loading database to data/data/package name/database/<db_name> when user install it.
        dir = new File("data/data/" + getPackageName() + "/databases");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }
        file = new File(dir, "china_province_city_zone_new.db3");
        try {
            if (!file.exists()) {
                InputStream is = getResources().openRawResource(
                        R.raw.china_province_city_zone_new);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;

                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            sqlDB = SQLiteDatabase.openOrCreateDatabase(file,null);
        }
        catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
    }

    private void InitNumber(){
        for (int i = 1; i <= 20; i++) {
            dataNum.add("" + i);
        }
        adapterSpNum = new ArrayAdapter<String>(this, R.layout.spinner_item, dataNum);
        adapterSpNum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snum.setAdapter(adapterSpNum);
        snum.setSelection(0);
    }

    private void setDateInfo(){
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i <= 80; i++) {
            dataYear.add("" + (cal.get(Calendar.YEAR) - 80 + i));
        }
        adapterSpYear = new ArrayAdapter<String>(this, R.layout.spinner_item, dataYear);
        adapterSpYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spyear.setAdapter(adapterSpYear);
        spyear.setSelection(80);// 2016 default

        // 12 months
        for (int i = 1; i <= 12; i++) {
            dataMonth.add("" + (i < 10 ? "0" + i : i));
        }
        adapterSpMonth = new ArrayAdapter<String>(this, R.layout.spinner_item, dataMonth);
        adapterSpMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spmonth.setAdapter(adapterSpMonth);

        adapterSpDay = new ArrayAdapter<String>(this, R.layout.spinner_item, dataDay);
        adapterSpDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spday.setAdapter(adapterSpDay);

        spmonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                dataDay.clear();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.valueOf(spyear.getSelectedItem().toString()));
                cal.set(Calendar.MONTH, arg2);
                int dayofm = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= dayofm; i++) {
                    dataDay.add("" + (i < 10 ? "0" + i : i));
                }
                adapterSpDay.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }
    /*
    public void loadInfo(View view) {
        setDateInfo();
        CopyAndLoadDB();

        InitialProvince();
        singleCalInfo = CalIdInfo.getInstance();
        btRun.setVisibility(View.VISIBLE);
        btLoad.setText("重新加载");
    }
    */

    public void loadInfo() {
        setDateInfo();
        CopyAndLoadDB();

        InitialProvince();
        InitNumber();
        singleCalInfo = CalIdInfo.getInstance();
    }

    public void runIt(View view) {
        String res;
        String resAll="";
        int num = 0;

        //get num here
        num = Integer.parseInt(snum.getSelectedItem().toString());
//        Log.d("@@@@@@@@", String.valueOf(num));
//        Toast.makeText(this,"num"+ String.valueOf(num),Toast.LENGTH_LONG).show();

        // get gender here
        for(int i=0; i<radioSex.getChildCount(); i++){
            RadioButton r = (RadioButton)radioSex.getChildAt(i);
            if(r.isChecked()){
                sex = r.getText().toString();
                break;
            }
        }

        if(spyear.getSelectedItem().toString() != "" && spmonth.getSelectedItem().toString() != "" && spday.getSelectedItem().toString() != ""
            && sex != "" && locNum != 0
            && selectedProvince != "" && selectedCity != "" && selectedArea != "")
        {
            for(int i = 0; i < num; i++){
                res = singleCalInfo.calIdInfoAll(locNum, spyear.getSelectedItem().toString(), spmonth.getSelectedItem().toString(), spday.getSelectedItem().toString(), sex);
                resAll = resAll + res + "\n";
            }
 //           res = singleCalInfo.calIdInfoAll(locNum, spyear.getSelectedItem().toString(), spmonth.getSelectedItem().toString(), spday.getSelectedItem().toString(), sex);
            resAll = resAll.substring(0,resAll.length()-1);
            resText.setText(resAll);

            //copy id number to Clipboard for later use
            ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cmb.setText(resAll);
            Toast.makeText(this,"The ID Number has been copied to Clipboard",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Some information is empty! ",Toast.LENGTH_LONG).show();
        }
    }


    private void InitialProvince() {
        try {
            Map<Integer, List> data = getProvince(file);
            provinceList = data.get(1);
            provinceData = data.get(0);
            ArrayAdapter provinceAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, provinceList);
            provinceAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // pattern
            spprovince.setAdapter(provinceAdapter);
            spprovince.setOnItemSelectedListener(itemSelectedListener);
        } catch (Exception e) {
            Log.d("WineStock", "InitialProvince:" + e.getMessage());
        }
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
            int position, long id) {
            try {
                Spinner spProvince = (Spinner) parent;
                selectedProvince = (String) spProvince
                        .getItemAtPosition(position);
                ArrayAdapter cityAdapter = null;
                Map<String, Integer> data = (Map) provinceData.get(position);// get province id
                // get city
                int pid = data.get(selectedProvince);// get province id
                Map<Integer, List> citymap = getCityByPid(pid, file);
                cityList = citymap.get(1);
                cityData = citymap.get(0);
                cityAdapter = new ArrayAdapter(mContext,android.R.layout.simple_spinner_item, cityList);
                cityAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spcity.setAdapter(cityAdapter);
                spcity.setOnItemSelectedListener(citySelectedListener);
            } catch (Exception e) {
                Log.d("WineStock", "Select Province error:" + e.getMessage());
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    };

    // city event
    private AdapterView.OnItemSelectedListener citySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
             int position, long id) {
            try {
                Spinner spCity = (Spinner) parent;
                selectedCity = (String) spCity.getItemAtPosition(position);
                ArrayAdapter areaAdapter = null;
                Map<String, Integer> data = (Map) cityData.get(position);// get province id
                // get city
                int pid = data.get(selectedCity);// get city's id


//                List<String> listArea = getAreaByPid(pid, file);
                Map<Integer, List> areaMap = getAreaByPid(pid, file);
                areaList = areaMap.get(0);
                areaData = areaMap.get(1);

                areaAdapter = new ArrayAdapter(mContext,
                        android.R.layout.simple_spinner_item, areaList);
                areaAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sparea.setAdapter(areaAdapter);
                sparea.setOnItemSelectedListener(areaSelectedListener);
            } catch (Exception e) {
                Log.d("WineStock", "Select City error:" + e.getMessage());
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    };

    // Get area info
    private AdapterView.OnItemSelectedListener areaSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
            int position, long id) {
            try {
                Spinner spArea = (Spinner) parent;
                selectedArea = (String) spArea.getItemAtPosition(position);

                Map<String, Integer> data = (Map) areaData.get(position);
                locNum = data.get(selectedArea);

                if (selectedProvince != "" && selectedCity != ""
                        && selectedArea != "") {


                    String strAdd = selectedProvince + selectedCity
                            + selectedArea + locNum;
                    Log.d("finish location info @@@ ", strAdd);
                }
            } catch (Exception e) {
                Log.d("WineStock", "Select Area error:" + e.getMessage());
                String strAdd = selectedProvince + selectedCity
                        + selectedArea + locNum;
                Log.d("finish location info @@@ ", strAdd);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    // press back button twice to exit this app
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_gone) {
            finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                exitTime = System.currentTimeMillis();
            }
            else{
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
