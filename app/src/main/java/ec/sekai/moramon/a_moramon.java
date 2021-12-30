package ec.sekai.moramon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellInfoWcdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellSignalStrengthWcdma;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.net.URL;
import java.net.HttpURLConnection;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class a_moramon extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //Variable
    boolean demo;   //usa celdas demo?
    boolean norelease;  //imprime logs en consola?
    int demo_server_mas_adjcs_n;
    int demo_tech;
    boolean demo_onerun;
    int demo_timetick;

    int demo_count_server3g;
    ArrayList demo_cellids3g;
    ArrayList demo_lac3g;
    ArrayList demo_psc3g;
    ArrayList demo_rscp3g;
    int demo_count_adjc3g;
    ArrayList demo_adjc_psc3g;
    ArrayList demo_adjc_rscp3g;
    int demo_count_server4g;
    ArrayList demo_eutrascis4g;
    ArrayList demo_tac4g;
    ArrayList demo_pci4g;
    ArrayList demo_rsrp4g;
    int demo_count_adjc4g;
    ArrayList demo_adjc_pci4g;
    ArrayList demo_adjc_rsrp4g;
    int demo_count_server2g;
    ArrayList demo_cellids2g;
    ArrayList demo_lac2g;
    ArrayList demo_rxlev2g;
    int demo_count_adjc2g;
    ArrayList demo_adjc_cellid2g;
    ArrayList demo_adjc_rxlev2g;
    //componentes graficos
    private LinearLayout mmLayout;
    private TextView tv_main_info;
    private Context contexto;
    //clases de RF
    private TelephonyManager tm;
    MyPhoneStateListener ServerRscp3gListener;
    String phonemodel;
    //bolean que evita colision de updates
    private boolean isUpdatingCell;
    //string con informacion de celda 3G
    private String main_info;
    int server_cellid3G;
    int server_lac3G;
    int server_psc3G;
    String server_name3G;
    int server_uarfcn3G;
    int server_rnc3G;
    int server_rscp3G;
    double server_lat3G;
    double server_lon3G;
    int servermp_cellid3G;
    int servermp_psc3G;
    boolean isMP;
    int server_eutraci4G;
    int server_cellid4G;
    int server_tac4G;
    int server_pci4G;
    String server_name4G;
    int server_enb4G;
    int server_rsrp4G;
    int server_rsrq4G;
    double server_snr4G;
    int server_rssi4G;
    double server_lat4G;
    double server_lon4G;
    int server_cellid2G;
    int server_lac2G;
    String server_name2G;
    String server_bsc2G;
    int server_bcch2G;
    String server_bsic2G;
    int server_rxlev2G;
    //als para punteros
    private ArrayList al_mm3g_bytes;
    private ArrayList al_mm3g_starts;
    private ArrayList al_mm3g_finishs;
    private ArrayList al_mm3gpsc_bytes;
    private ArrayList al_mm4g_bytes;
    private ArrayList al_mm4g_starts;
    private ArrayList al_mm4g_finishs;
    private ArrayList al_mm4gpci_bytes;
    private ArrayList al_mm2g_bytes;
    private ArrayList al_mm2g_starts;
    private ArrayList al_mm2g_finishs;
    //als para buffer
    private ArrayList al_3gservers_cellid;
    private ArrayList al_3gservers_name;
    private ArrayList al_3gservers_uarfcn;
    private ArrayList al_3gservers_lat;
    private ArrayList al_3gservers_lon;
    private int server3G_buffer_size;
    private ArrayList al_adjcs_psc3g;
    private ArrayList al_adjcs_name3g;
    private ArrayList al_adjcs_uarfcn3g;
    private ArrayList al_adjcs_cellid3g;
    private ArrayList al_4gservers_eutraci;
    private ArrayList al_4gservers_name;
    private ArrayList al_4gservers_lat;
    private ArrayList al_4gservers_lon;
    private int server4G_buffer_size;
    private ArrayList al_adjcs_pci4g;
    private ArrayList al_adjcs_name4g;
    private ArrayList al_2gservers_cellid;
    private ArrayList al_2gservers_name;
    private ArrayList al_2gservers_bsc;
    private ArrayList al_2gservers_bcch;
    private ArrayList al_2gservers_bsic;
    private int server2G_buffer_size;
    private ArrayList al_adjcs_cellid2g;
    private ArrayList al_adjcs_name2g;
    //bases de datos
    Database_Moramon db_mm_helper;
    SQLiteDatabase db_mm;
    //clases para timers
    private Timer ticks;
    private TimerTask ticksTask;
    private int ticks_count;
    //GPS
    private Location location;
    protected LocationManager locationManager;
    //Wear
    private GoogleApiClient mGoogleApiClient;
    NodeApi.GetConnectedNodesResult nodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        demo = true;
        norelease = true;
        demo_server_mas_adjcs_n = 3;
        demo_tech = 2;  //0 gsm, 1 wcdma, 2 lte
        demo_onerun = false;
        demo_timetick = 2000;

        demo_count_server3g = 0;
        demo_cellids3g = new ArrayList();
        demo_cellids3g.add(62415);//227517//30909
        demo_cellids3g.add(30909);//227517//30909
        demo_cellids3g.add(37909);//168981//37909
        demo_cellids3g.add(65535);//292511//30367
        demo_cellids3g.add(30367);//292511//30367
        demo_cellids3g.add(30367);//292511//30367
        demo_cellids3g.add(30909);//227517//30909
        demo_cellids3g.add(31745);//556033//31745
        demo_lac3g = new ArrayList();
        demo_lac3g.add(12345);
        demo_lac3g.add(45673);
        demo_lac3g.add(45673);
        demo_lac3g.add(12345);
        demo_lac3g.add(54353);
        demo_lac3g.add(76533);
        demo_lac3g.add(12345);
        demo_lac3g.add(24689);
        demo_psc3g = new ArrayList();
        demo_psc3g.add(111);//192
        demo_psc3g.add(192);//192
        demo_psc3g.add(192);//192
        demo_psc3g.add(111);//111
        demo_psc3g.add(111);//111
        demo_psc3g.add(111);//111
        demo_psc3g.add(192);//192
        demo_psc3g.add(500);//500
        demo_rscp3g = new ArrayList();
        demo_rscp3g.add(-50);
        demo_rscp3g.add(-80);
        demo_rscp3g.add(-82);
        demo_rscp3g.add(-53);
        demo_rscp3g.add(-67);
        demo_rscp3g.add(-62);
        demo_rscp3g.add(-92);
        demo_rscp3g.add(-74);
        demo_count_adjc3g = 0;
        demo_adjc_psc3g = new ArrayList();
        demo_adjc_psc3g.add(500);
        demo_adjc_psc3g.add(132);//50
        demo_adjc_psc3g.add(111);
        demo_adjc_psc3g.add(351);
        demo_adjc_psc3g.add(111);//438
        demo_adjc_psc3g.add(50);
        demo_adjc_rscp3g = new ArrayList();
        demo_adjc_rscp3g.add(-70);
        demo_adjc_rscp3g.add(-80);
        demo_adjc_rscp3g.add(-90);
        demo_adjc_rscp3g.add(-75);
        demo_adjc_rscp3g.add(-85);
        demo_adjc_rscp3g.add(-95);
        demo_count_server4g = 0;
        demo_eutrascis4g = new ArrayList();
        demo_eutrascis4g.add(8343298);//7181760
        demo_eutrascis4g.add(7181760);//
        demo_eutrascis4g.add(7262144);
        demo_eutrascis4g.add(7262142);
        demo_eutrascis4g.add(7262144);
        demo_eutrascis4g.add(7262144);
        demo_tac4g = new ArrayList();
        demo_tac4g.add(9000);
        demo_tac4g.add(9000);
        demo_tac4g.add(9000);
        demo_tac4g.add(9000);
        demo_tac4g.add(9000);
        demo_tac4g.add(9000);
        demo_pci4g = new ArrayList();
        demo_pci4g.add(428);
        demo_pci4g.add(428);
        demo_pci4g.add(439);
        demo_pci4g.add(440);
        demo_pci4g.add(439);
        demo_pci4g.add(439);
        demo_rsrp4g = new ArrayList();
        demo_rsrp4g.add(-99);
        demo_rsrp4g.add(-92);
        demo_rsrp4g.add(-94);
        demo_rsrp4g.add(-90);
        demo_rsrp4g.add(-96);
        demo_rsrp4g.add(-98);
        demo_count_adjc4g = 0;
        demo_adjc_pci4g = new ArrayList();
        demo_adjc_pci4g.add(405);
        demo_adjc_pci4g.add(378);
        demo_adjc_pci4g.add(469);
        demo_adjc_rsrp4g = new ArrayList();
        demo_adjc_rsrp4g.add(-101);
        demo_adjc_rsrp4g.add(-98);
        demo_adjc_rsrp4g.add(-94);
        demo_count_server2g = 0;
        demo_cellids2g = new ArrayList();
        demo_cellids2g.add(65215);
        demo_cellids2g.add(12367);
        demo_cellids2g.add(11053);
        demo_cellids2g.add(12367);
        demo_cellids2g.add(14053);
        demo_cellids2g.add(14053);
        demo_cellids2g.add(12056);
        demo_cellids2g.add(14053);
        demo_lac2g = new ArrayList();
        demo_lac2g.add(12345);
        demo_lac2g.add(45673);
        demo_lac2g.add(45673);
        demo_lac2g.add(12345);
        demo_lac2g.add(54353);
        demo_lac2g.add(76533);
        demo_lac2g.add(12345);
        demo_lac2g.add(24689);
        demo_rxlev2g = new ArrayList();
        demo_rxlev2g.add(-50);
        demo_rxlev2g.add(-80);
        demo_rxlev2g.add(-82);
        demo_rxlev2g.add(-53);
        demo_rxlev2g.add(-67);
        demo_rxlev2g.add(-62);
        demo_rxlev2g.add(-92);
        demo_rxlev2g.add(-74);
        demo_count_adjc2g = 0;
        demo_adjc_cellid2g = new ArrayList();
        demo_adjc_cellid2g.add(64844);
        demo_adjc_cellid2g.add(-1);//10260
        demo_adjc_cellid2g.add(14050);
        demo_adjc_rxlev2g = new ArrayList();
        demo_adjc_rxlev2g.add(-101);
        demo_adjc_rxlev2g.add(-98);
        demo_adjc_rxlev2g.add(-94);

        mmLayout = new LinearLayout(this);
        mmLayout.setOrientation(LinearLayout.VERTICAL);
        tv_main_info = new TextView(this);
        if(!norelease)tv_main_info.setTypeface(Typeface.SERIF);
        if(!norelease)tv_main_info.setTextSize(15);
        tv_main_info.setText("Loading...");
        mmLayout.addView(tv_main_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(mmLayout);
        contexto = this;

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        ServerRscp3gListener = new MyPhoneStateListener();
        tm.listen(ServerRscp3gListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        phonemodel = Build.MODEL;

        isUpdatingCell = false;

        main_info = "";
        server_cellid3G = 0;
        server_lac3G = 0;
        server_psc3G = 0;
        server_name3G = "";
        server_uarfcn3G = 0;
        server_rnc3G = 0;
        server_rscp3G = 0;
        server_lat3G = 0;
        server_lon3G = 0;
        servermp_cellid3G = -1;
        servermp_psc3G = -1;
        isMP = false;
        server_eutraci4G = 0;
        server_cellid4G = 0;
        server_tac4G = 0;
        server_pci4G = 0;
        server_name4G = "";
        server_enb4G = 0;
        server_rsrp4G = 0;
        server_rsrq4G = 0;
        server_snr4G = 0;
        server_rssi4G = 0;
        server_lat4G = 0;
        server_lon4G = 0;
        server_cellid2G = 0;
        server_lac2G = 0;
        server_name2G = "";
        server_bsc2G = "";
        server_bcch2G = 0;
        server_bsic2G = "";
        server_rxlev2G = 0;

        al_mm3g_bytes = new ArrayList();
        al_mm3g_starts = new ArrayList();
        al_mm3g_finishs = new ArrayList();
        al_mm3gpsc_bytes = new ArrayList();
        al_mm4g_bytes = new ArrayList();
        al_mm4g_starts = new ArrayList();
        al_mm4g_finishs = new ArrayList();
        al_mm4gpci_bytes = new ArrayList();
        al_mm2g_bytes = new ArrayList();
        al_mm2g_starts = new ArrayList();
        al_mm2g_finishs = new ArrayList();

        al_3gservers_cellid = new ArrayList();
        al_3gservers_name = new ArrayList();
        al_3gservers_uarfcn = new ArrayList();
        al_3gservers_lat = new ArrayList();
        al_3gservers_lon = new ArrayList();
        server3G_buffer_size = 5;
        al_adjcs_psc3g = new ArrayList();
        al_adjcs_name3g = new ArrayList();
        al_adjcs_uarfcn3g = new ArrayList();
        al_adjcs_cellid3g = new ArrayList();
        al_4gservers_eutraci = new ArrayList();
        al_4gservers_name = new ArrayList();
        al_4gservers_lat = new ArrayList();
        al_4gservers_lon = new ArrayList();
        server4G_buffer_size = 5;
        al_adjcs_pci4g = new ArrayList();
        al_adjcs_name4g = new ArrayList();
        al_2gservers_cellid = new ArrayList();
        al_2gservers_name = new ArrayList();
        al_2gservers_bsc = new ArrayList();
        al_2gservers_bcch = new ArrayList();
        al_2gservers_bsic = new ArrayList();
        server2G_buffer_size = 5;
        al_adjcs_cellid2g = new ArrayList();
        al_adjcs_name2g = new ArrayList();

        db_mm_helper = null;
        db_mm = null;

        //carga db_mm3gindex en als
        try {
            db_mm_helper = new Database_Moramon(contexto);
            db_mm = db_mm_helper.getReadableDatabase();
            Cursor cursor_mm3gindex = db_mm.rawQuery("SELECT  * FROM tai", null);
            if (cursor_mm3gindex.moveToFirst()) {
                do {
                    al_mm3g_bytes.add(cursor_mm3gindex.getInt(1));
                    al_mm3g_starts.add(cursor_mm3gindex.getInt(2));
                    al_mm3g_finishs.add(cursor_mm3gindex.getInt(3));
                } while (cursor_mm3gindex.moveToNext());
            }
            //debug
            if(norelease)System.out.println("Exito cargando db_mm3gindex en als: " + al_mm3g_bytes.size());
        }
        catch (Exception e){
            if(norelease)System.out.println("Error cargando db_mm3gindex en als: " + e.getMessage());
            if(norelease)e.printStackTrace();
        } finally {
            db_mm.close();
        }

        //carga db_mm3gpscindex en als
        try {
            db_mm_helper = new Database_Moramon(contexto);
            db_mm = db_mm_helper.getReadableDatabase();
            Cursor cursor_mm3gpscindex = db_mm.rawQuery("SELECT  * FROM tapi", null);
            if (cursor_mm3gpscindex.moveToFirst()) {
                do {
                    al_mm3gpsc_bytes.add(cursor_mm3gpscindex.getInt(1));
                } while (cursor_mm3gpscindex.moveToNext());
            }
            //debug
            if(norelease)System.out.println("Exito cargando db_mm3gpscindex en als: " + al_mm3gpsc_bytes.size());
        }
        catch (Exception e){
            if(norelease)System.out.println("Error cargando db_mm3gpscindex en als: " + e.getMessage());
            if(norelease)e.printStackTrace();
        } finally {
            db_mm.close();
        }

        //carga db_mm4gindex en als
        try {
            db_mm_helper = new Database_Moramon(contexto);
            db_mm = db_mm_helper.getReadableDatabase();
            Cursor cursor_mm4gindex = db_mm.rawQuery("SELECT  * FROM rai", null);
            if (cursor_mm4gindex.moveToFirst()) {
                do {
                    al_mm4g_bytes.add(cursor_mm4gindex.getInt(1));
                    al_mm4g_starts.add(cursor_mm4gindex.getInt(2));
                    al_mm4g_finishs.add(cursor_mm4gindex.getInt(3));
                } while (cursor_mm4gindex.moveToNext());
            }
            //debug
            if(norelease)System.out.println("Exito cargando db_mm4gindex en als: " + al_mm4g_bytes.size());
        }
        catch (Exception e){
            if(norelease)System.out.println("Error cargando db_mm4gindex en als: " + e.getMessage());
            if(norelease)e.printStackTrace();
        } finally {
            db_mm.close();
        }

        //carga db_mm4gpciindex en als
        try {
            db_mm_helper = new Database_Moramon(contexto);
            db_mm = db_mm_helper.getReadableDatabase();
            Cursor cursor_mm4gpciindex = db_mm.rawQuery("SELECT  * FROM rapi", null);
            if (cursor_mm4gpciindex.moveToFirst()) {
                do {
                    al_mm4gpci_bytes.add(cursor_mm4gpciindex.getInt(1));
                } while (cursor_mm4gpciindex.moveToNext());
            }
            //debug
            if(norelease)System.out.println("Exito cargando db_mm4gpciindex en als: " + al_mm4gpci_bytes.size());
        }
        catch (Exception e){
            if(norelease)System.out.println("Error cargando db_mm4gpciindex en als: " + e.getMessage());
            if(norelease)e.printStackTrace();
        } finally {
            db_mm.close();
        }

        //carga db_mm2gindex en als
        try {
            db_mm_helper = new Database_Moramon(contexto);
            db_mm = db_mm_helper.getReadableDatabase();
            Cursor cursor_mm2gindex = db_mm.rawQuery("SELECT  * FROM sai", null);
            if (cursor_mm2gindex.moveToFirst()) {
                do {
                    al_mm2g_bytes.add(cursor_mm2gindex.getInt(1));
                    al_mm2g_starts.add(cursor_mm2gindex.getInt(2));
                    al_mm2g_finishs.add(cursor_mm2gindex.getInt(3));
                } while (cursor_mm2gindex.moveToNext());
            }
            //debug
            if(norelease)System.out.println("Exito cargando db_mm2gindex en als: " + al_mm2g_bytes.size());
        }
        catch (Exception e){
            if(norelease)System.out.println("Error cargando db_mm2gindex en als: " + e.getMessage());
            if(norelease)e.printStackTrace();
        } finally {
            db_mm.close();
        }

        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                server_lat3G = location.getLatitude();
                server_lon3G = location.getLongitude();
            }
        }*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        ticks_count = -1;
        ticks = new Timer();
        ticksTask = new TimerTask() {
            public void run() {
                ticks_count++;
                if(!isUpdatingCell){
                    if(al_mm3g_bytes.isEmpty()) return;
                    if(al_mm4g_bytes.isEmpty()) return;
                    if(al_mm2g_bytes.isEmpty()) return;
                    isUpdatingCell = true;
                    if( phonemodel.equals("Android SDK built for x86") ||
                            phonemodel.equals("G8341") ||
                            phonemodel.equals("D5803")) { // Sony Experia Z3 Compact
                        UpdateCell();
                    } else {
                        UpdateCell_lite();
                    }
                    if(demo_count_server3g < demo_cellids3g.size() - 1) demo_count_server3g++;
                    if(demo_count_server4g < demo_eutrascis4g.size() - 1) demo_count_server4g++;
                    if(demo_count_server2g < demo_cellids2g.size() - 1) demo_count_server2g++;
                }
            }
        };
        if(demo && demo_onerun) ticks.schedule(ticksTask, 2000);
        else if(demo) ticks.schedule(ticksTask, 2000, demo_timetick);
        else ticks.schedule(ticksTask, 2000, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_a_moramon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {
            new ActualizaTask(this).execute();
            return true;
        }
        if (id == R.id.action_stop) {
            ticks.cancel();
            return true;
        }
        if (id == R.id.action_modowear) {
            new WearTask().execute();
            return true;
        }
        if (id == R.id.action_privacypolicy) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sekai.ec/moramon/privacy_policy.html")));
            return true;
        }
        if (id == R.id.action_about) {
            // get your about_toast.xml ayout
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.about_toast,(ViewGroup) findViewById(R.id.custom_toast_layout_id));
            // set a dummy image
            ImageView image = (ImageView) layout.findViewById(R.id.image);
            image.setImageResource(R.mipmap.ic_launcher);
            // set a message
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Programming: Pablo Mora,PhD c\nRF Consulting: Bupatindra Malla,PhD c\nRF Consulting: Ing Cristian Mora\nDistributed by: Sekai 2015");
            // Toast...
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void UpdateCell() {
        List<CellInfo> cellInfo = tm.getAllCellInfo();
        boolean isServer = true;
        int tech = 0;
        main_info = "";

        if(cellInfo == null) {
            main_info = "Celdas detectadas: no";
        } else if(cellInfo.size() == 0 && !demo) {
            main_info = "Celdas detectadas: 0";
        } else {
            ArrayList cand_adjc_psc3g = new ArrayList();
            ArrayList cand_adjc_name3g = new ArrayList();
            ArrayList cand_adjc_uarfcn3g = new ArrayList();
            ArrayList cand_adjc_cellid3g = new ArrayList();
            ArrayList oficial_adjc_psc3g = new ArrayList();
            ArrayList oficial_adjc_rscp3g = new ArrayList();
            if(demo){
                demo_count_adjc3g = 0;
                demo_count_adjc4g = 0;
                demo_count_adjc2g = 0;
            }
//            for(CellInfo cellinfo:cellInfo) {
            int cellInfo_n = demo?demo_server_mas_adjcs_n:cellInfo.size();
            for(int cellInfo_index = 0; cellInfo_index < cellInfo_n; cellInfo_index++) {
                CellInfo cellinfo = null;
                if(!demo) cellinfo = cellInfo.get(cellInfo_index);
                if(isServer){
                    if (cellinfo instanceof CellInfoWcdma || (demo_tech == 1 && demo)) {
                        tech = 2;
                        main_info += "Tecnologia: 3G\n\nServidora\n";
                        CellInfoWcdma cellinfowcdma = null;
                        CellIdentityWcdma cellidentitywcdma = null;
                        CellSignalStrengthWcdma cellsignalstrengthwcdma = null;
                        String cellid3Gcomp_hex = "";
                        String cellid3G_hex = "";
                        String rnc3G_hex = "";
                        int parsed_cellid3G = 0;
                        int parsed_rnc3G = 0;
                        int parsed_lac3G = 0;
                        int parsed_psc3G = 0;
                        int parsed_rscp3G = 0;
                        if(!demo){
                            cellinfowcdma = (CellInfoWcdma)cellinfo;
                            cellidentitywcdma = cellinfowcdma.getCellIdentity();
                            cellsignalstrengthwcdma = cellinfowcdma.getCellSignalStrength();
                            //debug
                            //if(norelease)System.out.println(cellsignalstrengthwcdma);
                            cellid3Gcomp_hex = Integer.toHexString(cellidentitywcdma.getCid());
                            parsed_lac3G = cellidentitywcdma.getLac();
                            parsed_psc3G = cellidentitywcdma.getPsc();
                            parsed_rscp3G = -113 + (cellsignalstrengthwcdma.getAsuLevel() * 2);
                        } else {
                            cellid3Gcomp_hex = Integer.toHexString((int)demo_cellids3g.get(demo_count_server3g));
                            parsed_lac3G = (int)demo_lac3g.get(demo_count_server3g);
                            parsed_psc3G = (int)demo_psc3g.get(demo_count_server3g);
                            parsed_rscp3G = (int)demo_rscp3g.get(demo_count_server3g);
                        }
                        cellid3G_hex = cellid3Gcomp_hex.substring(cellid3Gcomp_hex.length() - 4);
                        parsed_cellid3G = Integer.parseInt(cellid3G_hex, 16);
                        rnc3G_hex = cellid3Gcomp_hex.substring(0, cellid3Gcomp_hex.length() - 4);
                        if(rnc3G_hex.compareTo("") != 0){
                            parsed_rnc3G = Integer.parseInt(rnc3G_hex, 16);
                        } else {
                            parsed_rnc3G = 999;
                        }
                        if(parsed_cellid3G == server_cellid3G || (parsed_cellid3G == servermp_cellid3G && parsed_psc3G != servermp_psc3G) || parsed_cellid3G == 65535) {
                            if (server_psc3G != parsed_psc3G || (parsed_cellid3G == servermp_cellid3G && parsed_psc3G != servermp_psc3G) || parsed_cellid3G == 65535) {
                                if (server_psc3G == parsed_psc3G){
                                    //debug
                                    if(norelease)System.out.println("\nCellid3gMP no varia: " + parsed_cellid3G);
                                } else{
                                    al_adjcs_psc3g = new ArrayList();
                                    al_adjcs_name3g = new ArrayList();
                                    al_adjcs_uarfcn3g = new ArrayList();
                                    al_adjcs_cellid3g = new ArrayList();
                                    if(!isMP){
                                        servermp_cellid3G = server_cellid3G;
                                        servermp_psc3G = server_psc3G;
                                        isMP = true;
                                    }
                                    int psc3g_serv_byte = 0;
                                    int psc3g_serv_cursor = 0;
                                    //debug
                                    if(norelease)System.out.println("\nCellid3g psc no coinciden: " + parsed_cellid3G + " " + parsed_psc3G + " server: " + server_psc3G);
                                    for(int psc3gserv_count = 0; psc3gserv_count < 256; psc3gserv_count++){
                                        //debug
                                        //System.out.println("Comparando rango server psc: " + (psc3gserv_count * 2) + " - " + ((psc3gserv_count * 2) + 1));
                                        if(parsed_psc3G == psc3gserv_count * 2 || parsed_psc3G == (psc3gserv_count * 2) + 1) {
                                            psc3g_serv_byte = (int) al_mm3gpsc_bytes.get(psc3gserv_count);
                                            psc3g_serv_cursor = psc3gserv_count;
                                            //debug
                                            if(norelease)System.out.println("Rango server psc encontrado: " + (psc3gserv_count * 2) + " - " + ((psc3gserv_count * 2) + 1) + " bytes: " + psc3g_serv_byte);
                                            break;
                                        }
                                    }
                                    InputStream in_mm3gpsc_serv = null;
                                    String FILENAME3G = "help2";
                                    try {
                                        in_mm3gpsc_serv = openFileInput(FILENAME3G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error abriendo file server mm3gpsc: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                    int ch_mm3gpsc, i_mm3gpsc=0;
                                    String mm3gpsc_serv_psc = "";
                                    String mm3gpsc_serv_name = "";
                                    String mm3gpsc_serv_lat = "";
                                    String mm3gpsc_serv_lon = "";
                                    String mm3gpsc_serv_uarfcn = "";
                                    String mm3gpsc_serv_cellid = "";
                                    double serv3g_lat = 0;
                                    double serv3g_lon = 0;
                                    StringBuffer mm3gpsc_serv_data = new StringBuffer();
                                    ArrayList psc3g_serv_name = new ArrayList();
                                    ArrayList psc3g_serv_dist = new ArrayList();
                                    ArrayList psc3g_serv_uarfcn = new ArrayList();
                                    ArrayList psc3g_serv_cellid = new ArrayList();
                                    ArrayList psc3g_serv_lat = new ArrayList();
                                    ArrayList psc3g_serv_lon = new ArrayList();
                                    try{
                                        in_mm3gpsc_serv.skip(psc3g_serv_byte);
                                        while (in_mm3gpsc_serv.available()>0){
                                            ch_mm3gpsc = in_mm3gpsc_serv.read();
                                            if (ch_mm3gpsc == ',') {
                                                switch (i_mm3gpsc){
                                                    case 0:
                                                        mm3gpsc_serv_psc = mm3gpsc_serv_data.toString();
                                                        break;
                                                    case 1:
                                                        mm3gpsc_serv_name = mm3gpsc_serv_data.toString();
                                                        break;
                                                    case 2:
                                                        mm3gpsc_serv_lat = mm3gpsc_serv_data.toString();
                                                        break;
                                                    case 3:
                                                        mm3gpsc_serv_lon = mm3gpsc_serv_data.toString();
                                                        break;
                                                    case 4:
                                                        mm3gpsc_serv_uarfcn = mm3gpsc_serv_data.toString();
                                                        break;
                                                }
                                                i_mm3gpsc++;
                                                mm3gpsc_serv_data = new StringBuffer();
                                            } else if (ch_mm3gpsc == '\n'){
                                                mm3gpsc_serv_cellid = mm3gpsc_serv_data.toString();
                                                int mm3gpsc_serv_dist = 0;
                                                //debug
                                                //System.out.println("Buscando psc: " + mm3gpsc_serv_psc + " " + mm3gpsc_serv_name + " " + mm3gpsc_serv_lat + " " + mm3gpsc_serv_lon);
                                                if (Integer.parseInt(mm3gpsc_serv_psc) > (psc3g_serv_cursor * 2) + 1) break;
                                                if ( mm3gpsc_serv_psc.compareTo(String.valueOf(parsed_psc3G))==0) {
                                                    psc3g_serv_name.add(mm3gpsc_serv_name);
                                                    psc3g_serv_uarfcn.add(Integer.parseInt(mm3gpsc_serv_uarfcn));
                                                    psc3g_serv_cellid.add(Integer.parseInt(mm3gpsc_serv_cellid));
                                                    serv3g_lat = Double.parseDouble(mm3gpsc_serv_lat);
                                                    psc3g_serv_lat.add(serv3g_lat);
                                                    serv3g_lon = Double.parseDouble(mm3gpsc_serv_lon);
                                                    psc3g_serv_lon.add(serv3g_lon);
                                                    mm3gpsc_serv_dist = (int)(Math.acos(Math.sin(server_lat3G * Math.PI / 180) * Math.sin(serv3g_lat * Math.PI / 180) + Math.cos(server_lat3G * Math.PI / 180) *
                                                            Math.cos(serv3g_lat * Math.PI / 180) * Math.cos(serv3g_lon * Math.PI / 180 - server_lon3G * Math.PI / 180)) * 6371000);
                                                    psc3g_serv_dist.add(mm3gpsc_serv_dist);
                                                    //debug
                                                    //if(norelease)System.out.println("Distancia psc: " + mm3gpsc_serv_name + " " + mm3gpsc_serv_dist);
                                                }
                                                i_mm3gpsc = 0;
                                                mm3gpsc_serv_data = new StringBuffer();
                                            }
                                            else mm3gpsc_serv_data.append((char)ch_mm3gpsc);
                                        }
                                        in_mm3gpsc_serv.close();
                                        int dist_serv_min = 5000000;
                                        for(int dist_serv_temp :(ArrayList<Integer>) psc3g_serv_dist){
                                            if(dist_serv_min > dist_serv_temp) dist_serv_min = dist_serv_temp;
                                        }
                                        int serv_psc_found_index = 0;
                                        for(int dist_serv_temp2 :(ArrayList<Integer>) psc3g_serv_dist){
                                            if(dist_serv_min == dist_serv_temp2) {
                                                server_name3G = (String)psc3g_serv_name.get(serv_psc_found_index);
                                                server_uarfcn3G = (int)psc3g_serv_uarfcn.get(serv_psc_found_index);
                                                server_cellid3G = (int)psc3g_serv_cellid.get(serv_psc_found_index);
                                                server_psc3G = parsed_psc3G;
                                                server_lac3G = 0;
                                                server_rnc3G = 0;
                                                server_lat3G = (double)psc3g_serv_lat.get(serv_psc_found_index);
                                                server_lon3G = (double)psc3g_serv_lon.get(serv_psc_found_index);
                                                //debug
                                                if(norelease)System.out.println("Cellid con psc encontrado: " + server_cellid3G + " " + server_name3G);
                                                break;
                                            }
                                            serv_psc_found_index++;
                                        }
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error encontrando server psc: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                }
                            } else {
                                //debug
                                if(norelease)System.out.println("\nCellid3g no varia: " + parsed_cellid3G);
                            }
                        } else {
                            al_adjcs_psc3g = new ArrayList();
                            al_adjcs_name3g = new ArrayList();
                            al_adjcs_uarfcn3g = new ArrayList();
                            al_adjcs_cellid3g = new ArrayList();
                            servermp_cellid3G = -1;
                            servermp_psc3G = -1;
                            isMP = false;
                            if(al_3gservers_cellid.indexOf(parsed_cellid3G) != -1) {
                                int server3g_buffer_index = al_3gservers_cellid.indexOf(parsed_cellid3G);
                                //debug
                                if(norelease)System.out.println("\nCellid3g en buffer: " + server3g_buffer_index + " - " + parsed_cellid3G);
                                server_name3G = (String)al_3gservers_name.get(server3g_buffer_index);
                                server_uarfcn3G = (int)al_3gservers_uarfcn.get(server3g_buffer_index);
                                server_cellid3G = parsed_cellid3G;
                                server_psc3G = parsed_psc3G;
                                server_lac3G = parsed_lac3G;
                                server_rnc3G = parsed_rnc3G;
                                server_lat3G = (double)al_3gservers_lat.get(server3g_buffer_index);
                                server_lon3G = (double)al_3gservers_lon.get(server3g_buffer_index);
                            } else {
                                int cellid3G_finish = 0;
                                int cellid3G_byte = -1;
                                //debug
                                if(norelease)System.out.println("\nNuevo cellid3g: " + parsed_cellid3G);
                                for(int cellid3G_start :(ArrayList<Integer>) al_mm3g_starts){
                                    cellid3G_finish = (int) al_mm3g_finishs.get(al_mm3g_starts.indexOf(cellid3G_start));
                                    //debug
                                    //System.out.println("Comparando rango: " + cellid3G_start + " - " + cellid3G_finish);
                                    if(parsed_cellid3G >= cellid3G_start && parsed_cellid3G <= cellid3G_finish) {
                                        cellid3G_byte = (int) al_mm3g_bytes.get(al_mm3g_starts.indexOf(cellid3G_start));
                                        //debug
                                        if(norelease)System.out.println("Rango encontrado: " + cellid3G_start + " - " + cellid3G_finish + " bytes: " + cellid3G_byte);
                                        break;
                                    }
                                }
                                if(cellid3G_byte == -1){
                                    server_name3G = "Celda no en DB: " + parsed_cellid3G;
                                    server_uarfcn3G = -2;
                                    server_cellid3G = -2;
                                    server_psc3G = -2;
                                    server_lac3G = -2;
                                    server_rnc3G = -2;
                                    server_lat3G = -2;
                                    server_lon3G = -2;
                                } else {
                                    InputStream in_mm3g = null;
                                    String FILENAME3G = "help1";
                                    try {
                                        in_mm3g = openFileInput(FILENAME3G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error abriendo file mm3g: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                    int ch_mm3g, i_mm3g=0;
                                    String mm3g_cellid = "";
                                    String mm3g_name = "";
                                    String mm3g_uarfcn = "";
                                    String mm3g_psc = "";
                                    String mm3g_lat = "";
                                    String mm3g_lon = "";
                                    server_name3G = "";
                                    server_uarfcn3G = 0;
                                    StringBuffer mm3g_data = new StringBuffer();
                                    try{
                                        in_mm3g.skip(cellid3G_byte);
                                        while (in_mm3g.available()>0){
                                            ch_mm3g = in_mm3g.read();
                                            if (ch_mm3g == ',') {
                                                switch (i_mm3g){
                                                    case 0:
                                                        mm3g_cellid = mm3g_data.toString();
                                                        break;
                                                    case 1:
                                                        mm3g_name = mm3g_data.toString();
                                                        break;
                                                    case 2:
                                                        mm3g_uarfcn = mm3g_data.toString();
                                                        break;
                                                    case 3:
                                                        mm3g_psc = mm3g_data.toString();
                                                        break;
                                                    case 4:
                                                        mm3g_lat = mm3g_data.toString();
                                                        break;
                                                }
                                                i_mm3g++;
                                                mm3g_data = new StringBuffer();
                                            } else if (ch_mm3g == '\n'){
                                                mm3g_lon = mm3g_data.toString();
                                                if (Integer.parseInt(mm3g_cellid) > cellid3G_finish) break;
                                                //debug
                                                //if(norelease)System.out.println("Buscando mm3g_cellid: " + mm3g_cellid + " " + mm3g_name + " " + mm3g_uarfcn);
                                                if ( mm3g_cellid.compareTo(String.valueOf(parsed_cellid3G))==0) {
                                                    server_name3G = mm3g_name;
                                                    server_uarfcn3G = Integer.parseInt(mm3g_uarfcn);
                                                    server_cellid3G = parsed_cellid3G;
                                                    server_psc3G = parsed_psc3G;
                                                    server_lac3G = parsed_lac3G;
                                                    server_rnc3G = parsed_rnc3G;
                                                    server_lat3G = Double.parseDouble(mm3g_lat);
                                                    server_lon3G = Double.parseDouble(mm3g_lon);
                                                    if(al_3gservers_cellid.size() >= server3G_buffer_size) {
                                                        //debug
                                                        if(norelease)System.out.println("Cellid a remover del buffer: " + al_3gservers_cellid.get(0) + " tamano: " + al_3gservers_cellid.size());
                                                        al_3gservers_cellid.remove(0);
                                                        al_3gservers_name.remove(0);
                                                        al_3gservers_uarfcn.remove(0);
                                                        al_3gservers_lat.remove(0);
                                                        al_3gservers_lon.remove(0);
                                                    }
                                                    al_3gservers_cellid.add(server_cellid3G);
                                                    al_3gservers_name.add(server_name3G);
                                                    al_3gservers_uarfcn.add(server_uarfcn3G);
                                                    al_3gservers_lat.add(server_lat3G);
                                                    al_3gservers_lon.add(server_lon3G);
                                                    //debug
                                                    if(norelease)System.out.println("Nuevo cellid en buffer: " + parsed_cellid3G + " tamano: " + al_3gservers_cellid.size());
                                                    break;
                                                }
                                                i_mm3g = 0;
                                                mm3g_data = new StringBuffer();
                                            } else
                                                mm3g_data.append((char)ch_mm3g);
                                        }
                                        if(server_name3G.compareTo("") == 0){
                                            server_name3G = "Celda no en DB: " + parsed_cellid3G;
                                            server_uarfcn3G = -1;
                                            server_cellid3G = -1;
                                            server_psc3G = -1;
                                            server_lac3G = -1;
                                            server_rnc3G = -1;
                                            server_lat3G = -1;
                                            server_lon3G = -1;
                                        }
                                        in_mm3g.close();
                                        //debug
                                        if(norelease)System.out.println("Exito encontrando mm3g_cellid: " + server_name3G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error encontrando mm3g_cellid: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if(parsed_cellid3G == 65535) server_cellid3G = parsed_cellid3G;
                        main_info += server_name3G + "\ncellid: " + server_cellid3G + "\nuarfcn: " + server_uarfcn3G + "\nsc: " + server_psc3G + "\nlac: " + server_lac3G + "\nrnc: " + server_rnc3G + "\nrscp: " + server_rscp3G;
                    }
                    if (cellinfo instanceof CellInfoLte || (demo_tech == 2 && demo)) {
                        tech = 3;
                        main_info += "Tecnologia: 4G\n\nServidora\n";
                        CellInfoLte cellinfolte = null;
                        CellIdentityLte cellidentitylte = null;
                        CellSignalStrengthLte cellsignalstrengthlte = null;
                        int parsed_eutraci4G = 0;
                        String eutraci4Gcomp_hex = "";
                        String cellid4G_hex = "";
                        String enb4G_hex = "";
                        int parsed_cellid4G = 0;
                        int parsed_enb4G = 0;
                        int parsed_tac4G = 0;
                        int parsed_pci4G = 0;
                        int parsed_rsrp4G = 0;
                        if(!demo){
                            cellinfolte = (CellInfoLte)cellinfo;
                            cellidentitylte = cellinfolte.getCellIdentity();
                            cellsignalstrengthlte = cellinfolte.getCellSignalStrength();
                            //debug
                            //if(norelease)System.out.println(cellsignalstrengthlte);
                            parsed_eutraci4G = cellidentitylte.getCi();
                            parsed_tac4G = cellidentitylte.getTac();
                            parsed_pci4G = cellidentitylte.getPci();
                            parsed_rsrp4G = -113 + (cellsignalstrengthlte.getAsuLevel() * 2);
                        } else {
                            parsed_eutraci4G = (int)demo_eutrascis4g.get(demo_count_server4g);
                            parsed_tac4G = (int)demo_tac4g.get(demo_count_server4g);
                            parsed_pci4G = (int)demo_pci4g.get(demo_count_server4g);
                            parsed_rsrp4G = (int)demo_rsrp4g.get(demo_count_server4g);
                        }
                        eutraci4Gcomp_hex = Integer.toHexString(parsed_eutraci4G);
                        cellid4G_hex = eutraci4Gcomp_hex.substring(eutraci4Gcomp_hex.length() - 2);
                        parsed_cellid4G = Integer.parseInt(cellid4G_hex, 16);
                        enb4G_hex = eutraci4Gcomp_hex.substring(0, eutraci4Gcomp_hex.length() - 2);
                        if(enb4G_hex.compareTo("") != 0){
                            parsed_enb4G = Integer.parseInt(enb4G_hex, 16);
                        } else {
                            parsed_enb4G = 99999;
                        }
                        if(parsed_eutraci4G == server_eutraci4G) {
                            //debug
                            if(norelease)System.out.println("\nCellid4g no varia: " + parsed_eutraci4G);
                        } else {
                            al_adjcs_pci4g = new ArrayList();
                            al_adjcs_name4g = new ArrayList();
                            if(al_4gservers_eutraci.indexOf(parsed_eutraci4G) != -1) {
                                int server4g_buffer_index = al_4gservers_eutraci.indexOf(parsed_eutraci4G);
                                //debug
                                if(norelease)System.out.println("\nCellid4g en buffer: " + server4g_buffer_index + " - " + parsed_eutraci4G);
                                server_eutraci4G = parsed_eutraci4G;
                                server_name4G = (String)al_4gservers_name.get(server4g_buffer_index);
                                server_enb4G = parsed_enb4G;
                                server_cellid4G = parsed_cellid4G;
                                server_pci4G = parsed_pci4G;
                                server_tac4G = parsed_tac4G;
                                server_lat4G = (double)al_4gservers_lat.get(server4g_buffer_index);
                                server_lon4G = (double)al_4gservers_lon.get(server4g_buffer_index);
                            } else {
                                int eutraci4G_finish = 0;
                                int eutraci4G_byte = -1;
                                //debug
                                if(norelease)System.out.println("\nNuevo cellid4g: " + parsed_eutraci4G);
                                for(int eutraci4G_start :(ArrayList<Integer>) al_mm4g_starts){
                                    eutraci4G_finish = (int) al_mm4g_finishs.get(al_mm4g_starts.indexOf(eutraci4G_start));
                                    //debug
                                    //if(norelease)System.out.println("Comparando rango: " + eutraci4G_start + " - " + eutraci4G_finish);
                                    if(parsed_eutraci4G >= eutraci4G_start && parsed_eutraci4G <= eutraci4G_finish) {
                                        eutraci4G_byte = (int) al_mm4g_bytes.get(al_mm4g_starts.indexOf(eutraci4G_start));
                                        //debug
                                        if(norelease)System.out.println("Rango encontrado: " + eutraci4G_start + " - " + eutraci4G_finish + " bytes: " + eutraci4G_byte);
                                        break;
                                    }
                                }
                                if(eutraci4G_byte == -1){
                                    server_name4G = "Celda no en DB: " + parsed_enb4G + " " + parsed_cellid4G;
                                    server_eutraci4G = -2;
                                    server_cellid4G = -2;
                                    server_pci4G = -2;
                                    server_tac4G = -2;
                                    server_enb4G = -2;
                                    server_lat4G = -2;
                                    server_lon4G = -2;
                                } else {
                                    InputStream in_mm4g = null;
                                    String FILENAME4G = "help3";
                                    try {
                                        in_mm4g = openFileInput(FILENAME4G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error abriendo file mm4g: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                    int ch_mm4g, i_mm4g=0;
                                    String mm4g_eutraci = "";
                                    String mm4g_name = "";
                                    String mm4g_pci = "";
                                    String mm4g_lat = "";
                                    String mm4g_lon = "";
                                    server_name4G = "";
                                    StringBuffer mm4g_data = new StringBuffer();
                                    try{
                                        in_mm4g.skip(eutraci4G_byte);
                                        while (in_mm4g.available()>0){
                                            ch_mm4g = in_mm4g.read();
                                            if (ch_mm4g == ',') {
                                                switch (i_mm4g){
                                                    case 0:
                                                        mm4g_eutraci = mm4g_data.toString();
                                                        break;
                                                    case 1:
                                                        mm4g_name = mm4g_data.toString();
                                                        break;
                                                    case 2:
                                                        mm4g_pci = mm4g_data.toString();
                                                        break;
                                                    case 3:
                                                        mm4g_lat = mm4g_data.toString();
                                                        break;
                                                }
                                                i_mm4g++;
                                                mm4g_data = new StringBuffer();
                                            } else if (ch_mm4g == '\n'){
                                                mm4g_lon = mm4g_data.toString();
                                                if (Integer.parseInt(mm4g_eutraci) > eutraci4G_finish) break;
                                                //debug
                                                //if(norelease)System.out.println("Buscando mm4g_eutra: " + mm4g_eutraci + " " + mm4g_name);
                                                if ( mm4g_eutraci.compareTo(String.valueOf(parsed_eutraci4G))==0) {
                                                    server_name4G = mm4g_name;
                                                    server_eutraci4G = parsed_eutraci4G;
                                                    server_enb4G = parsed_enb4G;
                                                    server_cellid4G = parsed_cellid4G;
                                                    server_pci4G = parsed_pci4G;
                                                    server_tac4G = parsed_tac4G;
                                                    server_lat4G = Double.parseDouble(mm4g_lat);
                                                    server_lon4G = Double.parseDouble(mm4g_lon);
                                                    if(al_4gservers_eutraci.size() >= server4G_buffer_size) {
                                                        //debug
                                                        if(norelease)System.out.println("Cellid a remover del buffer: " + al_4gservers_eutraci.get(0) + " tamano: " + al_4gservers_eutraci.size());
                                                        al_4gservers_eutraci.remove(0);
                                                        al_4gservers_name.remove(0);
                                                        al_4gservers_lat.remove(0);
                                                        al_4gservers_lon.remove(0);
                                                    }
                                                    al_4gservers_eutraci.add(server_eutraci4G);
                                                    al_4gservers_name.add(server_name4G);
                                                    al_4gservers_lat.add(server_lat4G);
                                                    al_4gservers_lon.add(server_lon4G);
                                                    //debug
                                                    if(norelease)System.out.println("Nuevo cellid en buffer: " + parsed_eutraci4G + " tamano: " + al_4gservers_eutraci.size());
                                                    break;
                                                }
                                                i_mm4g = 0;
                                                mm4g_data = new StringBuffer();
                                            } else
                                                mm4g_data.append((char)ch_mm4g);
                                        }
                                        if(server_name4G.compareTo("") == 0){
                                            server_name4G = "Celda no en DB: " + parsed_enb4G + " " + parsed_cellid4G;
                                            server_enb4G = -1;
                                            server_cellid4G = -1;
                                            server_pci4G = -1;
                                            server_tac4G = -1;
                                            server_eutraci4G = -1;
                                            server_lat4G = -1;
                                            server_lon4G = -1;
                                        }
                                        in_mm4g.close();
                                        //debug
                                        if(norelease)System.out.println("Exito encontrando mm4g_cellid: " + server_name4G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error encontrando mm4g_cellid: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                }
                            }
                        }
                        //if(parsed_cellid4G == 65535) server_cellid4G = parsed_cellid4G;
                        main_info += server_name4G + "\ncellid: " + server_cellid4G + "\nenb: " + server_enb4G + "\npci: " + server_pci4G + "\ntac: " + server_tac4G + "\nrsrp: " + server_rsrp4G + "\nrsrq: " + server_rsrq4G + "\nsnr: " + server_snr4G + "\nrssi: " + server_rssi4G;
                    }

                    if (cellinfo instanceof CellInfoGsm || (demo_tech == 0 && demo)) {
                        tech = 4;
                        main_info += "Tecnologia: 2G\n\nServidora\n";
                        CellInfoGsm cellinfogsm = null;
                        CellIdentityGsm cellidentitygsm = null;
                        CellSignalStrengthGsm cellsignalstrengthgsm = null;
                        int parsed_cellid2G = 0;
                        int parsed_lac2G = 0;
                        int parsed_rxlev2G = 0;
                        if(!demo){
                            cellinfogsm = (CellInfoGsm)cellinfo;
                            cellidentitygsm = cellinfogsm.getCellIdentity();
                            cellsignalstrengthgsm = cellinfogsm.getCellSignalStrength();
                            //debug
                            //if(norelease)System.out.println(cellsignalstrengthgsm);
                            parsed_cellid2G = cellidentitygsm.getCid();
                            parsed_lac2G = cellidentitygsm.getLac();
                            parsed_rxlev2G = -113 + (cellsignalstrengthgsm.getAsuLevel() * 2);
                        } else {
                            parsed_cellid2G = (int)demo_cellids2g.get(demo_count_server2g);
                            parsed_lac2G = (int)demo_lac2g.get(demo_count_server2g);
                            parsed_rxlev2G = (int)demo_rxlev2g.get(demo_count_server2g);
                        }
                        if(parsed_cellid2G == server_cellid2G) {
                            //debug
                            if(norelease)System.out.println("\nCellid2g no varia: " + parsed_cellid2G);
                        } else {
                            al_adjcs_cellid2g = new ArrayList();
                            al_adjcs_name2g = new ArrayList();
                            if(al_2gservers_cellid.indexOf(parsed_cellid2G) != -1) {
                                int server2g_buffer_index = al_2gservers_cellid.indexOf(parsed_cellid2G);
                                //debug
                                if(norelease)System.out.println("\nCellid2g en buffer: " + server2g_buffer_index + " - " + parsed_cellid2G);
                                server_cellid2G = parsed_cellid2G;
                                server_name2G = (String)al_2gservers_name.get(server2g_buffer_index);
                                server_lac2G = parsed_lac2G;
                                server_bsc2G = (String)al_2gservers_bsc.get(server2g_buffer_index);
                                server_bcch2G = (int)al_2gservers_bcch.get(server2g_buffer_index);
                                server_bsic2G = (String)al_2gservers_bsic.get(server2g_buffer_index);
                            } else {
                                int cellid2G_finish = 0;
                                int cellid2G_byte = -1;
                                //debug
                                if(norelease)System.out.println("\nNuevo cellid2g: " + parsed_cellid2G);
                                for(int cellid2G_start :(ArrayList<Integer>) al_mm2g_starts){
                                    cellid2G_finish = (int) al_mm2g_finishs.get(al_mm2g_starts.indexOf(cellid2G_start));
                                    //debug
                                    //if(norelease)System.out.println("Comparando rango: " + cellid2G_start + " - " + cellid2G_finish);
                                    if(parsed_cellid2G >= cellid2G_start && parsed_cellid2G <= cellid2G_finish) {
                                        cellid2G_byte = (int) al_mm2g_bytes.get(al_mm2g_starts.indexOf(cellid2G_start));
                                        //debug
                                        if(norelease)System.out.println("Rango encontrado: " + cellid2G_start + " - " + cellid2G_finish + " bytes: " + cellid2G_byte);
                                        break;
                                    }
                                }
                                if(cellid2G_byte == -1){
                                    server_name2G = "Celda no en DB: " + parsed_cellid2G;
                                    server_cellid2G = -2;
                                    server_lac2G = -2;
                                    server_bsc2G = "-2";
                                    server_bcch2G = -2;
                                    server_bsic2G = "-2";
                                } else {
                                    InputStream in_mm2g = null;
                                    String FILENAME2G = "help5";
                                    try {
                                        in_mm2g = openFileInput(FILENAME2G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error abriendo file mm2g: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                    int ch_mm2g, i_mm2g=0;
                                    String mm2g_cellid = "";
                                    String mm2g_name = "";
                                    String mm2g_bsc = "";
                                    String mm2g_freq = "";
                                    server_name2G = "";
                                    StringBuffer mm2g_data = new StringBuffer();
                                    try{
                                        in_mm2g.skip(cellid2G_byte);
                                        while (in_mm2g.available()>0){
                                            ch_mm2g = in_mm2g.read();
                                            if (ch_mm2g == ',') {
                                                switch (i_mm2g){
                                                    case 0:
                                                        mm2g_cellid = mm2g_data.toString();
                                                        break;
                                                    case 1:
                                                        mm2g_name = mm2g_data.toString();
                                                        break;
                                                    case 2:
                                                        mm2g_bsc = mm2g_data.toString();
                                                        break;
                                                }
                                                i_mm2g++;
                                                mm2g_data = new StringBuffer();
                                            } else if (ch_mm2g == '\n'){
                                                mm2g_freq = mm2g_data.toString();
                                                if (Integer.parseInt(mm2g_cellid) > cellid2G_finish) break;
                                                //debug
                                                //if(norelease)System.out.println("Buscando mm2g_cellid: " + mm2g_cellid + " " + mm2g_name);
                                                if (mm2g_cellid.compareTo(String.valueOf(parsed_cellid2G)) == 0) {
                                                    server_name2G = mm2g_name;
                                                    server_cellid2G = parsed_cellid2G;
                                                    server_bsc2G = mm2g_bsc;
                                                    server_lac2G = parsed_lac2G;
                                                    server_bcch2G = Integer.parseInt(mm2g_freq.substring(0,mm2g_freq.indexOf("-")));
                                                    server_bsic2G = mm2g_freq.substring(mm2g_freq.indexOf("-") + 1);
                                                    if(al_2gservers_cellid.size() >= server2G_buffer_size) {
                                                        //debug
                                                        if(norelease)System.out.println("Cellid a remover del buffer: " + al_2gservers_cellid.get(0) + " tamano: " + al_2gservers_cellid.size());
                                                        al_2gservers_cellid.remove(0);
                                                        al_2gservers_name.remove(0);
                                                        al_2gservers_bsc.remove(0);
                                                        al_2gservers_bcch.remove(0);
                                                        al_2gservers_bsic.remove(0);
                                                    }
                                                    al_2gservers_cellid.add(server_cellid2G);
                                                    al_2gservers_name.add(server_name2G);
                                                    al_2gservers_bsc.add(server_bsc2G);
                                                    al_2gservers_bcch.add(server_bcch2G);
                                                    al_2gservers_bsic.add(server_bsic2G);
                                                    //debug
                                                    if(norelease)System.out.println("Nuevo cellid en buffer: " + parsed_cellid2G + " tamano: " + al_2gservers_cellid.size());
                                                    break;
                                                }
                                                i_mm2g = 0;
                                                mm2g_data = new StringBuffer();
                                            } else
                                                mm2g_data.append((char)ch_mm2g);
                                        }
                                        if(server_name2G.compareTo("") == 0){
                                            server_name2G = "Celda no en DB: " + parsed_cellid2G;
                                            server_cellid2G = -1;
                                            server_lac2G = -1;
                                            server_bsc2G = "-1";
                                            server_bcch2G = -1;
                                            server_bsic2G = "-1";
                                        }
                                        in_mm2g.close();
                                        //debug
                                        if(norelease)System.out.println("Exito encontrando mm2g_cellid: " + server_name2G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error encontrando mm2g_cellid: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                }
                            }
                        }
                        main_info += server_name2G + "\ncellid: " + server_cellid2G + "\nlac: " + server_lac2G + "\nbsc: " + server_bsc2G + "\nbcch: " + server_bcch2G+ "\nbsic: " + server_bsic2G  + "\nrxlev: " + server_rxlev2G;
                    }
                    if(norelease)main_info += "\n" + ticks_count + " " + cellInfo_index + " " + al_2gservers_cellid + " " + al_adjcs_cellid2g + " "  + (!demo?cellinfo.isRegistered():"");
                    main_info += "\n\nVecinas";
                    isServer = false;
                } else {
                    if (cellinfo instanceof CellInfoWcdma || (demo_tech == 1 && demo)) {
                        CellInfoWcdma cellinfowcdma = null;
                        CellIdentityWcdma cellidentitywcdma = null;
                        CellSignalStrengthWcdma cellsignalstrengthwcdma = null;
                        int parsed_psc3g_adjc = 0;
                        int psc3g_adjc_cursor = 0;
                        int parsed_rscp3g_adjc = 0;
                        if(!demo){
                            cellinfowcdma = (CellInfoWcdma)cellinfo;
                            cellidentitywcdma = cellinfowcdma.getCellIdentity();
                            parsed_psc3g_adjc = cellidentitywcdma.getPsc();
                            cellsignalstrengthwcdma = cellinfowcdma.getCellSignalStrength();
                            //debug
                            //if(norelease)System.out.println(cellsignalstrengthwcdma);
                            parsed_rscp3g_adjc = cellsignalstrengthwcdma.getDbm();
                        } else {
                            parsed_psc3g_adjc = (int)demo_adjc_psc3g.get(demo_count_adjc3g);
                            parsed_rscp3g_adjc = (int)demo_adjc_rscp3g.get(demo_count_adjc3g);
                        }
                        oficial_adjc_psc3g.add(parsed_psc3g_adjc);
                        oficial_adjc_rscp3g.add(parsed_rscp3g_adjc);
                        if(al_adjcs_psc3g.indexOf(parsed_psc3g_adjc) != -1) {
                            int adjcs_psc3g_index = 0;
                            //debug
                            if(norelease)System.out.println("Psc3g ya en buffer: " + parsed_psc3g_adjc);
                            for(int adjcs_psc3g_temp:(ArrayList<Integer>)al_adjcs_psc3g){
                                if(adjcs_psc3g_temp == parsed_psc3g_adjc){
                                    cand_adjc_name3g.add(al_adjcs_name3g.get(adjcs_psc3g_index));
                                    cand_adjc_uarfcn3g.add(al_adjcs_uarfcn3g.get(adjcs_psc3g_index));
                                    cand_adjc_cellid3g.add(al_adjcs_cellid3g.get(adjcs_psc3g_index));
                                    cand_adjc_psc3g.add(al_adjcs_psc3g.get(adjcs_psc3g_index));
                                }
                                adjcs_psc3g_index++;
                            }
                        } else {
                            if(cand_adjc_psc3g.indexOf(parsed_psc3g_adjc) != -1) {
                                //debug
                                if(norelease)System.out.println("Psc3g ya en lista candidatos: " + parsed_psc3g_adjc);
                            } else {
                                int psc3g_adjc_byte = 0;
                                //debug
                                if(norelease)System.out.println("Psc3g nuevo: " + parsed_psc3g_adjc);
                                for(int psc3gadjc_count = 0; psc3gadjc_count < 256; psc3gadjc_count++){
                                    //debug
                                    //System.out.println("Comparando rango psc: " + (psc3gadjc_count * 2) + " - " + ((psc3gadjc_count * 2) + 1));
                                    if(parsed_psc3g_adjc == psc3gadjc_count * 2 || parsed_psc3g_adjc == (psc3gadjc_count * 2) + 1) {
                                        psc3g_adjc_byte = (int) al_mm3gpsc_bytes.get(psc3gadjc_count);
                                        psc3g_adjc_cursor = psc3gadjc_count;
                                        //debug
                                        if(norelease)System.out.println("Rango psc encontrado: " + (psc3gadjc_count * 2) + " - " + ((psc3gadjc_count * 2) + 1) + " bytes: " + psc3g_adjc_byte);
                                        break;
                                    }
                                }
                                InputStream in_mm3gpsc = null;
                                String FILENAME3G = "help2";
                                try {
                                    in_mm3gpsc = openFileInput(FILENAME3G);
                                } catch(Exception e){
                                    if(norelease)System.out.println("Error abriendo file mm3gpsc: " + e.getMessage());
                                    if(norelease)e.printStackTrace();
                                }
                                int ch_mm3gpsc, i_mm3gpsc=0;
                                String mm3gpsc_psc = "";
                                String mm3gpsc_name = "";
                                String mm3gpsc_lat = "";
                                String mm3gpsc_lon = "";
                                String mm3gpsc_uarfcn = "";
                                String mm3gpsc_cellid = "";
                                double adjc_lat = 0;
                                double adjc_lon = 0;
                                StringBuffer mm3gpsc_data = new StringBuffer();
                                ArrayList precand_adjc3g_name = new ArrayList();
                                ArrayList precand_adjc3g_dist = new ArrayList();
                                ArrayList precand_adjc3g_uarfcn = new ArrayList();
                                ArrayList precand_adjc3g_cellid = new ArrayList();
                                try{
                                    in_mm3gpsc.skip(psc3g_adjc_byte);
                                    while (in_mm3gpsc.available()>0){
                                        ch_mm3gpsc = in_mm3gpsc.read();
                                        if (ch_mm3gpsc == ',') {
                                            switch (i_mm3gpsc){
                                                case 0:
                                                    mm3gpsc_psc = mm3gpsc_data.toString();
                                                    break;
                                                case 1:
                                                    mm3gpsc_name = mm3gpsc_data.toString();
                                                    break;
                                                case 2:
                                                    mm3gpsc_lat = mm3gpsc_data.toString();
                                                    break;
                                                case 3:
                                                    mm3gpsc_lon = mm3gpsc_data.toString();
                                                    break;
                                                case 4:
                                                    mm3gpsc_uarfcn = mm3gpsc_data.toString();
                                                    break;
                                            }
                                            i_mm3gpsc++;
                                            mm3gpsc_data = new StringBuffer();
                                        } else if (ch_mm3gpsc == '\n'){
                                            mm3gpsc_cellid = mm3gpsc_data.toString();
                                            if (Integer.parseInt(mm3gpsc_psc) > (psc3g_adjc_cursor * 2) + 1) break;
                                            int mm3gpsc_dist = 0;
                                            //debug
                                            //if(norelease)System.out.println("Buscando psc: " + mm3gpsc_psc + " " + mm3gpsc_name + " " + mm3gpsc_lat + " " + mm3gpsc_lon);
                                            if ( mm3gpsc_psc.compareTo(String.valueOf(parsed_psc3g_adjc))==0) {
                                                precand_adjc3g_name.add(mm3gpsc_name);
                                                precand_adjc3g_uarfcn.add(Integer.parseInt(mm3gpsc_uarfcn));
                                                precand_adjc3g_cellid.add(Integer.parseInt(mm3gpsc_cellid));
                                                adjc_lat = Double.parseDouble(mm3gpsc_lat);
                                                adjc_lon = Double.parseDouble(mm3gpsc_lon);
                                                mm3gpsc_dist = (int)(Math.acos(Math.sin(server_lat3G * Math.PI / 180) * Math.sin(adjc_lat * Math.PI / 180) + Math.cos(server_lat3G * Math.PI / 180) *
                                                        Math.cos(adjc_lat * Math.PI / 180) * Math.cos(adjc_lon * Math.PI / 180 - server_lon3G * Math.PI / 180)) * 6371000);
                                                precand_adjc3g_dist.add(mm3gpsc_dist);
                                                //debug
                                                //if(norelease)System.out.println("Distancia psc: " + mm3gpsc_name + " " + mm3gpsc_dist);
                                            }
                                            i_mm3gpsc = 0;
                                            mm3gpsc_data = new StringBuffer();
                                        }
                                        else mm3gpsc_data.append((char)ch_mm3gpsc);
                                    }
                                    in_mm3gpsc.close();
                                    int dist_min = 5000000;
                                    for(int dist_temp:(ArrayList<Integer>)precand_adjc3g_dist){
                                        if(dist_min > dist_temp) dist_min = dist_temp;
                                    }
                                    int precand_count = 0;
                                    for(int dist_temp2:(ArrayList<Integer>)precand_adjc3g_dist){
                                        if(dist_min == dist_temp2) {
                                            cand_adjc_name3g.add((String)precand_adjc3g_name.get(precand_count));
                                            al_adjcs_name3g.add((String)precand_adjc3g_name.get(precand_count));
                                            cand_adjc_uarfcn3g.add((int)precand_adjc3g_uarfcn.get(precand_count));
                                            al_adjcs_uarfcn3g.add((int)precand_adjc3g_uarfcn.get(precand_count));
                                            cand_adjc_cellid3g.add((int)precand_adjc3g_cellid.get(precand_count));
                                            al_adjcs_cellid3g.add((int)precand_adjc3g_cellid.get(precand_count));
                                            cand_adjc_psc3g.add(parsed_psc3g_adjc);
                                            al_adjcs_psc3g.add(parsed_psc3g_adjc);
                                            //debug
                                            if(norelease)System.out.println("Exito encontrando cand psc: " + (String)precand_adjc3g_name.get(precand_count) + " distmin: " + dist_min);
                                        }
                                        precand_count++;
                                    }
                                } catch(Exception e){
                                    if(norelease)System.out.println("Error encontrando cand psc: " + e.getMessage());
                                    if(norelease)e.printStackTrace();
                                }
                            }
                        }
                    }
                    demo_count_adjc3g++;

                    if (cellinfo instanceof CellInfoLte || (demo_tech == 2 && demo)) {
                        CellInfoLte cellinfolte = null;
                        CellIdentityLte cellidentitylte = null;
                        CellSignalStrengthLte cellsignalstrengthlte = null;
                        int parsed_pci4g_adjc = 0;
                        int pci4g_adjc_cursor = 0;
                        int parsed_rsrp4g_adjc = 0;
                        int parsed_rsrq4g_adjc = 0;
                        if(!demo){
                            cellinfolte = (CellInfoLte)cellinfo;
                            cellidentitylte = cellinfolte.getCellIdentity();
                            parsed_pci4g_adjc = cellidentitylte.getPci();
                            cellsignalstrengthlte = cellinfolte.getCellSignalStrength();
                            //debug
                            //if(norelease)System.out.println(cellsignalstrengthlte);
                            String cellsignalstrengthltetxt = cellsignalstrengthlte.toString();
                            String cellsignalstrengthltetemp = cellsignalstrengthltetxt.substring(cellsignalstrengthltetxt.indexOf(" ") + 1);
                            cellsignalstrengthltetemp = cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf(" ") + 1);
                            parsed_rsrp4g_adjc = Integer.parseInt(cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf("=") + 1,cellsignalstrengthltetemp.indexOf(" ")));
                            cellsignalstrengthltetemp = cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf(" ") + 1);
                            parsed_rsrq4g_adjc = Integer.parseInt(cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf("=") + 1,cellsignalstrengthltetemp.indexOf(" ")));
                        } else {
                            parsed_pci4g_adjc = (int)demo_adjc_pci4g.get(demo_count_adjc4g);
                            parsed_rsrp4g_adjc = (int)demo_adjc_rsrp4g.get(demo_count_adjc4g);
                        }
                        if(al_adjcs_pci4g.indexOf(parsed_pci4g_adjc) != -1) {
                            int adjcs_pci4g_index = 0;
                            //debug
                            if(norelease)System.out.println("Pci4g ya en buffer: " + parsed_pci4g_adjc);
                            for(int adjcs_pci4g_temp:(ArrayList<Integer>)al_adjcs_pci4g){
                                if(adjcs_pci4g_temp == parsed_pci4g_adjc){
                                    main_info += "\n" + parsed_rsrp4g_adjc + " " + parsed_rsrq4g_adjc + " " + al_adjcs_name4g.get(adjcs_pci4g_index);
                                    //debug
                                    if(norelease)System.out.println(al_adjcs_name4g.get(adjcs_pci4g_index));
                                }
                                adjcs_pci4g_index++;
                            }
                        } else {
                            int pci4g_adjc_byte = 0;
                            //debug
                            if(norelease)System.out.println("Pci4 nuevo: " + parsed_pci4g_adjc);
                            for(int pci4gadjc_count = 0; pci4gadjc_count < 61; pci4gadjc_count++){
                                //debug
                                //if(norelease)System.out.println("Comparando rango pci: " + (pci4gadjc_count * 8) + " - " + ((pci4gadjc_count * 8) + 8));
                                if(parsed_pci4g_adjc >= pci4gadjc_count * 8 && parsed_pci4g_adjc < (pci4gadjc_count * 8) + 8) {
                                    pci4g_adjc_byte = (int) al_mm4gpci_bytes.get(pci4gadjc_count);
                                    pci4g_adjc_cursor = pci4gadjc_count;
                                    //debug
                                    if(norelease)System.out.println("Rango pci encontrado: " + (pci4gadjc_count * 8) + " - " + ((pci4gadjc_count * 8) + 8) + " bytes: " + pci4g_adjc_byte);
                                    break;
                                }
                            }
                            InputStream in_mm4gpci = null;
                            String FILENAME4G = "help4";
                            try {
                                in_mm4gpci = openFileInput(FILENAME4G);
                            } catch(Exception e){
                                if(norelease)System.out.println("Error abriendo file mm4gpci: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                            int ch_mm4gpci, i_mm4gpci=0;
                            String mm4gpci_pci = "";
                            String mm4gpci_name = "";
                            String mm4gpci_lat = "";
                            String mm4gpci_lon = "";
                            String mm4gpci_eutraci = "";
                            double adjc_lat = 0;
                            double adjc_lon = 0;
                            StringBuffer mm4gpci_data = new StringBuffer();
                            ArrayList precand_adjc4g_name = new ArrayList();
                            ArrayList precand_adjc4g_dist = new ArrayList();
                            try{
                                in_mm4gpci.skip(pci4g_adjc_byte);
                                while (in_mm4gpci.available()>0){
                                    ch_mm4gpci = in_mm4gpci.read();
                                    if (ch_mm4gpci == ',') {
                                        switch (i_mm4gpci){
                                            case 0:
                                                mm4gpci_pci = mm4gpci_data.toString();
                                                break;
                                            case 1:
                                                mm4gpci_name = mm4gpci_data.toString();
                                                break;
                                            case 2:
                                                mm4gpci_lat = mm4gpci_data.toString();
                                                break;
                                            case 3:
                                                mm4gpci_lon = mm4gpci_data.toString();
                                                break;
                                        }
                                        i_mm4gpci++;
                                        mm4gpci_data = new StringBuffer();
                                    } else if (ch_mm4gpci == '\n'){
                                        mm4gpci_eutraci = mm4gpci_data.toString();
                                        if (Integer.parseInt(mm4gpci_pci) >= (pci4g_adjc_cursor * 8) + 8) break;
                                        int mm4gpci_dist = 0;
                                        //debug
                                        //if(norelease)System.out.println("Buscando pci: " + mm4gpci_pci + " " + mm4gpci_name + " " + mm4gpci_lat + " " + mm4gpci_lon);
                                        if ( mm4gpci_pci.compareTo(String.valueOf(parsed_pci4g_adjc))==0) {
                                            precand_adjc4g_name.add(mm4gpci_name);
                                            adjc_lat = Double.parseDouble(mm4gpci_lat);
                                            adjc_lon = Double.parseDouble(mm4gpci_lon);
                                            mm4gpci_dist = (int)(Math.acos(Math.sin(server_lat4G * Math.PI / 180) * Math.sin(adjc_lat * Math.PI / 180) + Math.cos(server_lat4G * Math.PI / 180) *
                                                    Math.cos(adjc_lat * Math.PI / 180) * Math.cos(adjc_lon * Math.PI / 180 - server_lon4G * Math.PI / 180)) * 6371000);
                                            precand_adjc4g_dist.add(mm4gpci_dist);
                                            //debug
                                            //if(norelease)System.out.println("Distancia pci: " + mm4gpci_name + " " + mm4gpci_dist);
                                        }
                                        i_mm4gpci = 0;
                                        mm4gpci_data = new StringBuffer();
                                    }
                                    else mm4gpci_data.append((char)ch_mm4gpci);
                                }
                                in_mm4gpci.close();
                                int dist_min = 5000000;
                                for(int dist_temp:(ArrayList<Integer>)precand_adjc4g_dist){
                                    if(dist_min > dist_temp) dist_min = dist_temp;
                                }
                                main_info += "\n" + parsed_rsrp4g_adjc + " " + parsed_rsrq4g_adjc + " " + precand_adjc4g_name.get(precand_adjc4g_dist.indexOf(dist_min));
                                al_adjcs_pci4g.add(parsed_pci4g_adjc);
                                al_adjcs_name4g.add(precand_adjc4g_name.get(precand_adjc4g_dist.indexOf(dist_min)));
                                //debug
                                if(norelease)System.out.println(precand_adjc4g_name.get(precand_adjc4g_dist.indexOf(dist_min)));
                            } catch(Exception e){
                                if(norelease)System.out.println("Error encontrando adjc pci: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                        }
                    }
                    demo_count_adjc4g++;

                    if (cellinfo instanceof CellInfoGsm || (demo_tech == 0 && demo)) {
                        CellInfoGsm cellinfogsm = null;
                        CellIdentityGsm cellidentitygsm = null;
                        CellSignalStrengthGsm cellsignalstrengthgsm = null;
                        int parsed_cellid2g_adjc = 0;
                        int parsed_rxlev2g_adjc = 0;
                        if(!demo){
                            cellinfogsm = (CellInfoGsm)cellinfo;
                            cellidentitygsm = cellinfogsm.getCellIdentity();
                            parsed_cellid2g_adjc = cellidentitygsm.getCid();
                            cellsignalstrengthgsm = cellinfogsm.getCellSignalStrength();
                            //debug
                            //if(norelease)System.out.println(cellsignalstrengthgsm);
                            parsed_rxlev2g_adjc = cellsignalstrengthgsm.getDbm();
                        } else {
                            parsed_cellid2g_adjc = (int)demo_adjc_cellid2g.get(demo_count_adjc2g);
                            parsed_rxlev2g_adjc = (int)demo_adjc_rxlev2g.get(demo_count_adjc2g);
                        }
                        if(parsed_cellid2g_adjc == -1){
                            demo_count_adjc2g++;
                            //debug
                            if(norelease)System.out.println("Adjc cellid: " + parsed_cellid2g_adjc);
                            continue;
                        }
                        if(al_adjcs_cellid2g.indexOf(parsed_cellid2g_adjc) != -1) {
                            int adjcs_cellid2g_index = 0;
                            //debug
                            if(norelease)System.out.println("Adjc2g ya en buffer: " + parsed_cellid2g_adjc);
                            for(int adjcs_cellid2g_temp:(ArrayList<Integer>)al_adjcs_cellid2g){
                                if(adjcs_cellid2g_temp == parsed_cellid2g_adjc){
                                    main_info += "\n" + parsed_rxlev2g_adjc + " " + al_adjcs_name2g.get(adjcs_cellid2g_index);
                                    //debug
                                    if(norelease)System.out.println(al_adjcs_name2g.get(adjcs_cellid2g_index));
                                }
                                adjcs_cellid2g_index++;
                            }
                        } else {
                            int cellid2G_finish = 0;
                            int cellid2G_byte = -1;
                            //debug
                            if(norelease)System.out.println("Adjc2g nuevo: " + parsed_cellid2g_adjc);
                            for(int cellid2G_start :(ArrayList<Integer>) al_mm2g_starts){
                                cellid2G_finish = (int) al_mm2g_finishs.get(al_mm2g_starts.indexOf(cellid2G_start));
                                //debug
                                //System.out.println("Comparando rango: " + cellid2G_start + " - " + cellid2G_finish);
                                if(parsed_cellid2g_adjc >= cellid2G_start && parsed_cellid2g_adjc <= cellid2G_finish) {
                                    cellid2G_byte = (int) al_mm2g_bytes.get(al_mm2g_starts.indexOf(cellid2G_start));
                                    //debug
                                    if(norelease)System.out.println("Rango encontrado: " + cellid2G_start + " - " + cellid2G_finish + " bytes: " + cellid2G_byte);
                                    break;
                                }
                            }
                            if(cellid2G_byte == -1){
                                main_info += "\n-2 " + parsed_cellid2g_adjc;
                                System.out.println(parsed_cellid2g_adjc);
                            } else {
                                InputStream in_mm2g = null;
                                String FILENAME2G = "help5";
                                try {
                                    in_mm2g = openFileInput(FILENAME2G);
                                } catch(Exception e){
                                    if(norelease)System.out.println("Error abriendo file mm2g: " + e.getMessage());
                                    if(norelease)e.printStackTrace();
                                }
                                int ch_mm2g, i_mm2g=0;
                                String mm2g_cellid = "";
                                String mm2g_name = "";
                                String mm2g_bsc = "";
                                String mm2g_freq = "";
                                String adjc2g_name = "";
                                StringBuffer mm2g_data = new StringBuffer();
                                try{
                                    in_mm2g.skip(cellid2G_byte);
                                    while (in_mm2g.available()>0){
                                        ch_mm2g = in_mm2g.read();
                                        if (ch_mm2g == ',') {
                                            switch (i_mm2g){
                                                case 0:
                                                    mm2g_cellid = mm2g_data.toString();
                                                    break;
                                                case 1:
                                                    mm2g_name = mm2g_data.toString();
                                                    break;
                                                case 2:
                                                    mm2g_bsc = mm2g_data.toString();
                                                    break;
                                            }
                                            i_mm2g++;
                                            mm2g_data = new StringBuffer();
                                        } else if (ch_mm2g == '\n'){
                                            mm2g_freq = mm2g_data.toString();
                                            if (Integer.parseInt(mm2g_cellid) > cellid2G_finish) break;
                                            //debug
                                            //if(norelease)System.out.println("Buscando mm2g_cellid: " + mm2g_cellid + " " + mm2g_name);
                                            if (mm2g_cellid.compareTo(String.valueOf(parsed_cellid2g_adjc)) == 0) {
                                                main_info += "\n" + parsed_rxlev2g_adjc + " " + mm2g_name;
                                                adjc2g_name = mm2g_name;
                                                al_adjcs_cellid2g.add(parsed_cellid2g_adjc);
                                                al_adjcs_name2g.add(mm2g_name);
                                                //debug
                                                if(norelease)System.out.println(mm2g_name);
                                                break;
                                            }
                                            i_mm2g = 0;
                                            mm2g_data = new StringBuffer();
                                        } else
                                            mm2g_data.append((char)ch_mm2g);
                                    }
                                    if(adjc2g_name.compareTo("") == 0){
                                        main_info += "\n-1 " + parsed_cellid2g_adjc;
                                        //debug
                                        if(norelease)System.out.println(parsed_cellid2g_adjc);
                                    }
                                    in_mm2g.close();
                                    //debug
                                    if(norelease)System.out.println("Exito encontrando adjc mm2g_cellid: " + adjc2g_name);
                                } catch(Exception e){
                                    if(norelease)System.out.println("Error encontrando adjc mm2g_cellid: " + e.getMessage());
                                    if(norelease)e.printStackTrace();
                                }
                            }
                        }
                    }
                    demo_count_adjc2g++;
                }
            }

            if(cellInfo.size() > 1 && tech == 2) {
                //debug
                if(norelease)System.out.println(oficial_adjc_psc3g + " " + oficial_adjc_rscp3g + " " + cand_adjc_name3g);

                //Calcula que portadoras tienen las candidatas
                ArrayList cand_adjc3g_portadoras = new ArrayList();
                for(int cand_adjc_uarfcn3g_temp:(ArrayList<Integer>)cand_adjc_uarfcn3g) {
                    if(cand_adjc3g_portadoras.indexOf(cand_adjc_uarfcn3g_temp) == -1) cand_adjc3g_portadoras.add(cand_adjc_uarfcn3g_temp);
                }
                if(norelease)System.out.println("adjc freqs: " + cand_adjc3g_portadoras);


                //Calcula cuantos shifts de senal hay
                int n_portad_detect = 1;
                int n_portad_detect_signal = 0;
                ArrayList adjc3g_expert = new ArrayList();
                ArrayList adjc3g_expert_psc = new ArrayList();
                ArrayList adjc3g_expert_rscp = new ArrayList();
                int cand_port_detect_index = 0;
                for(int cand_port_detect_temp :(ArrayList<Integer>)oficial_adjc_rscp3g) {
                    if(cand_port_detect_temp > n_portad_detect_signal || adjc3g_expert_psc.indexOf(oficial_adjc_psc3g.get(cand_port_detect_index)) != -1 || (n_portad_detect == 1 && (int)oficial_adjc_psc3g.get(cand_port_detect_index) == server_psc3G)){
                        if(!adjc3g_expert_psc.isEmpty()){
                            adjc3g_expert.add(new ADJC3G_Expert(adjc3g_expert_psc,adjc3g_expert_rscp,server_psc3G,cand_adjc3g_portadoras,cand_adjc_psc3g,cand_adjc_uarfcn3g,n_portad_detect-1));
                            adjc3g_expert_psc = new ArrayList();
                            adjc3g_expert_rscp = new ArrayList();
                            n_portad_detect++;
                        }
                    }
                    adjc3g_expert_psc.add(oficial_adjc_psc3g.get(cand_port_detect_index));
                    adjc3g_expert_rscp.add(oficial_adjc_rscp3g.get(cand_port_detect_index));
                    n_portad_detect_signal = cand_port_detect_temp;
                    cand_port_detect_index++;
                }
                if(!adjc3g_expert_psc.isEmpty())
                    adjc3g_expert.add(new ADJC3G_Expert(adjc3g_expert_psc,adjc3g_expert_rscp,server_psc3G,cand_adjc3g_portadoras,cand_adjc_psc3g,cand_adjc_uarfcn3g,n_portad_detect-1));
                //debug
                if(norelease)System.out.println("n psc blocks: " + n_portad_detect);
                //debug
                for(ADJC3G_Expert ADJC3G_Expert_temp :(ArrayList<ADJC3G_Expert>)adjc3g_expert) {
                    if(norelease)System.out.println(ADJC3G_Expert_temp.toString());
                }


                //Calcula cuantos psc repetidos maximos hay
                int n_psc_repet = 0;
                int n_psc_repet_max = 0;
                ArrayList psc_veces_psc = new ArrayList();
                ArrayList psc_veces = new ArrayList();
                psc_veces_psc.add(server_psc3G);
                psc_veces.add(1);
                for(int psc_repet_temp:(ArrayList<Integer>)oficial_adjc_psc3g) {
                    if(psc_veces_psc.indexOf(psc_repet_temp) == -1){
                        psc_veces_psc.add(psc_repet_temp);
                        psc_veces.add(1);
                    }
                    else{
                        n_psc_repet = (int)psc_veces.get(psc_veces_psc.indexOf(psc_repet_temp));
                        n_psc_repet++;
                        if(n_psc_repet > n_psc_repet_max) n_psc_repet_max = n_psc_repet;
                        psc_veces.set(psc_veces_psc.indexOf(psc_repet_temp),n_psc_repet);
                    }
                }
                //debug
                if(norelease)System.out.println("n psc veces: " + n_psc_repet_max + " " + psc_veces_psc + " " + psc_veces);


                ArrayList adjc_list3g_rscp = new ArrayList();
                ArrayList adjc_list3g_psc = new ArrayList();
                ArrayList adjc_list3g_cellid = new ArrayList();
                ArrayList adjc_list3g_name = new ArrayList();
                ArrayList adjc_list3g_uarfcn = new ArrayList();
                boolean all_freqs_detected = false;
                if(norelease)main_info += "\n" + oficial_adjc_psc3g + "\n" + oficial_adjc_rscp3g + " " + n_portad_detect + " " + n_psc_repet_max;


                //Intrafreq 850
                if(!all_freqs_detected && (server_uarfcn3G >= 4357 && server_uarfcn3G <= 4458)) {
                    ADJC3G_Expert intrafreq3G = (ADJC3G_Expert)adjc3g_expert.get(0);
                    if(!intrafreq3G.tiene_server_psc && (int)intrafreq3G.pscs_not_parsed_xfreq.get(cand_adjc3g_portadoras.indexOf(server_uarfcn3G)) == 0){
                        intrafreq3G.freq_definido = server_uarfcn3G;
                    }
                }
                //debug
                all_freqs_detected = true;
                for(ADJC3G_Expert ADJC3G_Expert_temp :(ArrayList<ADJC3G_Expert>)adjc3g_expert) {
                    if(norelease)System.out.println(ADJC3G_Expert_temp.toString());
                    if(ADJC3G_Expert_temp.freq_definido == 0)all_freqs_detected = false;
                }


                //Server psc 2 o 3 veces
                if(!all_freqs_detected && (int)psc_veces.get(0) > 1){
                    ArrayList serverpsc3_rscp = new ArrayList();
                    ArrayList serverpsc3_rscp_sort = new ArrayList();
                    ArrayList serverpsc3_expertindex = new ArrayList();
                    for(ADJC3G_Expert ADJC3G_Expert_temp :(ArrayList<ADJC3G_Expert>)adjc3g_expert) {
                        if(ADJC3G_Expert_temp.ofic_psc3G.indexOf(server_psc3G) != -1){
                            for(int ADJC3G_Expert_temp_psc :(ArrayList<Integer>)ADJC3G_Expert_temp.ofic_psc3G) {
                                if(ADJC3G_Expert_temp_psc == server_psc3G){
                                    int pscindex_inexpert = ADJC3G_Expert_temp.ofic_psc3G.indexOf(server_psc3G);
                                    serverpsc3_rscp.add(ADJC3G_Expert_temp.ofic_rscp3G.get(pscindex_inexpert));
                                    serverpsc3_rscp_sort.add(ADJC3G_Expert_temp.ofic_rscp3G.get(pscindex_inexpert));
                                    serverpsc3_expertindex.add(ADJC3G_Expert_temp.this_index);
                                }
                            }
                        }
                    }
                    Collections.sort(serverpsc3_rscp_sort,Collections.reverseOrder());
                    ArrayList is_used_port = new ArrayList();
                    int n850 = 0;
                    int n1900 = 0;
                    for(int adjc3g_portadoras_temp:(ArrayList<Integer>)cand_adjc3g_portadoras){
                        if(adjc3g_portadoras_temp >= 4357 && adjc3g_portadoras_temp <= 4458) n850++;
                        else n1900++;
                        if(adjc3g_portadoras_temp == server_uarfcn3G) is_used_port.add(1);
                        else is_used_port.add(0);
                    }
                    for(int rscp_sort_temp:(ArrayList<Integer>)serverpsc3_rscp_sort){
                        boolean encontro_freq = false;
                        int is_used_port_index = 0;
                        for(int n850_temp = 0; n850_temp < n850; n850_temp++){
                            is_used_port_index = 0;
                            for(int is_used_port_temp:(ArrayList<Integer>)is_used_port){
                                int is_used_port_uarfcn = (int)cand_adjc3g_portadoras.get(is_used_port_index);
                                if((is_used_port_uarfcn >= 4357 && is_used_port_uarfcn <= 4458) && is_used_port_temp == 0){
                                    encontro_freq = true;
                                    ((ADJC3G_Expert)adjc3g_expert.get((int)serverpsc3_expertindex.get(serverpsc3_rscp.indexOf(rscp_sort_temp)))).freq_definido = is_used_port_uarfcn;
                                    is_used_port.set(is_used_port_index,1);
                                    break;
                                }
                                is_used_port_index++;
                            }
                            if(encontro_freq) break;
                        }
                        if(encontro_freq) continue;
                        for(int n1900_temp = 0; n1900_temp < n1900; n1900_temp++){
                            is_used_port_index = 0;
                            for(int is_used_port_temp:(ArrayList<Integer>)is_used_port){
                                int is_used_port_uarfcn = (int)cand_adjc3g_portadoras.get(is_used_port_index);
                                if(is_used_port_temp == 0){
                                    encontro_freq = true;
                                    ((ADJC3G_Expert)adjc3g_expert.get((int)serverpsc3_expertindex.get(serverpsc3_rscp.indexOf(rscp_sort_temp)))).freq_definido = is_used_port_uarfcn;
                                    is_used_port.set(is_used_port_index,1);
                                    break;
                                }
                                is_used_port_index++;
                            }
                            if(encontro_freq) break;
                        }
                    }
                }
                //debug
                all_freqs_detected = true;
                for(ADJC3G_Expert ADJC3G_Expert_temp :(ArrayList<ADJC3G_Expert>)adjc3g_expert) {
                    if(norelease)System.out.println(ADJC3G_Expert_temp.toString());
                    if(ADJC3G_Expert_temp.freq_definido == 0)all_freqs_detected = false;
                }


                //Intrafreq 1900
                if(!all_freqs_detected && (server_uarfcn3G < 4357 || server_uarfcn3G > 4458) && ((ADJC3G_Expert)adjc3g_expert.get(0)).freq_definido == 0) {
                    ADJC3G_Expert intrafreq3G = (ADJC3G_Expert)adjc3g_expert.get(0);
                    if(!intrafreq3G.tiene_server_psc && (int)intrafreq3G.pscs_not_parsed_xfreq.get(cand_adjc3g_portadoras.indexOf(server_uarfcn3G)) == 0){
                        intrafreq3G.freq_definido = server_uarfcn3G;
                    }
                }
                //debug
                all_freqs_detected = true;
                for(ADJC3G_Expert ADJC3G_Expert_temp :(ArrayList<ADJC3G_Expert>)adjc3g_expert) {
                    if(norelease)System.out.println(ADJC3G_Expert_temp.toString());
                    if(ADJC3G_Expert_temp.freq_definido == 0)all_freqs_detected = false;
                }


                //Crea lista final
                for(ADJC3G_Expert ADJC3G_Expert_temp :(ArrayList<ADJC3G_Expert>)adjc3g_expert) {
                    if(ADJC3G_Expert_temp.freq_definido != 0){
                        int ofic_psc3G_index = 0;
                        int cand_adjc_psc3g_index = 0;
                        for (int ofic_psc3G_temp:(ArrayList<Integer>)ADJC3G_Expert_temp.ofic_psc3G){
                            cand_adjc_psc3g_index = 0;
                            for (int cand_adjc_psc3g_temp:(ArrayList<Integer>)cand_adjc_psc3g){
                                if(ofic_psc3G_temp == cand_adjc_psc3g_temp && ADJC3G_Expert_temp.freq_definido == ((int)cand_adjc_uarfcn3g.get(cand_adjc_psc3g_index))){
                                    adjc_list3g_rscp.add(ADJC3G_Expert_temp.ofic_rscp3G.get(ofic_psc3G_index));
                                    adjc_list3g_psc.add(ofic_psc3G_temp);
                                    adjc_list3g_cellid.add(cand_adjc_cellid3g.get(cand_adjc_psc3g_index));
                                    adjc_list3g_name.add(cand_adjc_name3g.get(cand_adjc_psc3g_index));
                                    adjc_list3g_uarfcn.add(cand_adjc_uarfcn3g.get(cand_adjc_psc3g_index));
                                    break;
                                }
                                cand_adjc_psc3g_index++;
                            }
                            ofic_psc3G_index++;
                        }
                    }
                }


                //Imprime lista final
                int adjc_list3g_rscp_index = 0;
                for(int adjc_list3g_rscp_temp:(ArrayList<Integer>)adjc_list3g_rscp){
                    //main_info += "\n" + adjc_list3g_rscp_temp + " " + adjc_list3g_uarfcn.get(adjc_list3g_rscp_index) + " " + adjc_list3g_psc.get(adjc_list3g_rscp_index) + " " + adjc_list3g_cellid.get(adjc_list3g_rscp_index) + " " + adjc_list3g_name.get(adjc_list3g_rscp_index);
                    main_info += "\n" + adjc_list3g_rscp_temp + " " + adjc_list3g_uarfcn.get(adjc_list3g_rscp_index) + " " + adjc_list3g_name.get(adjc_list3g_rscp_index);
                    //debug
                    if(norelease)System.out.println(adjc_list3g_name.get(adjc_list3g_rscp_index));
                    adjc_list3g_rscp_index++;
                }
            }
        }

        runOnUiThread(new Runnable() {
            public void run() {
                tv_main_info.setText(main_info);
                if(nodes != null && nodes.getNodes().size() != 0){
                    for(Node node : nodes.getNodes()){
                        if(norelease)System.out.println("Enviando a " + node.getDisplayName());
                        Wearable.MessageApi.sendMessage(
                                mGoogleApiClient,node.getId(),"/path/message"
                                , main_info.getBytes()).setResultCallback(
                                new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                        if(!sendMessageResult.getStatus().isSuccess()){
                                            if(norelease)System.out.println("Fallo envio");
                                        } else {
                                            if(norelease)System.out.println("Funciona envio");
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        });

        isUpdatingCell = false;
    }

    public void UpdateCell_lite() {
        GsmCellLocation cellLocation = (GsmCellLocation) tm.getCellLocation();
        String cellLocationtxt = cellLocation.toString();
        List<CellInfo> cellInfo = tm.getAllCellInfo();
        int tech = 0;
        main_info = "";

        if(cellLocation == null) {
            main_info = "Celdas detectadas: no";
        } else {
            int networktype = tm.getNetworkType();
            if ((networktype == tm.NETWORK_TYPE_UMTS || networktype == tm.NETWORK_TYPE_HSDPA || networktype == tm.NETWORK_TYPE_HSPA || networktype == tm.NETWORK_TYPE_HSPAP || networktype == tm.NETWORK_TYPE_HSUPA) || demo) {
                tech = 2;
                main_info += "Tecnologia: 3G\n\nServidora\n";
                String cellid3Gcomp_hex = "";
                String cellid3G_hex = "";
                String rnc3G_hex = "";
                int parsed_cellid3G = 0;
                int parsed_rnc3G = 0;
                int parsed_lac3G = 0;
                int parsed_psc3G = 0;
                if(!demo){
                    //debug
                    //if(norelease)System.out.println(cellsignalstrengthwcdma);
                    cellid3Gcomp_hex = Integer.toHexString(cellLocation.getCid());
                    parsed_lac3G = cellLocation.getLac();
                    if(phonemodel.equals("SM-G920I")){
                        boolean isServer = true;
                        for(CellInfo cellinfo:cellInfo) {
                            if (isServer) {
                                CellInfoWcdma cellinfowcdma = null;
                                CellIdentityWcdma cellidentitywcdma = null;
                                cellinfowcdma = (CellInfoWcdma)cellinfo;
                                cellidentitywcdma = cellinfowcdma.getCellIdentity();
                                parsed_psc3G = cellidentitywcdma.getPsc();
                                isServer = false;
                            }
                        }
                    } else {
                        parsed_psc3G = cellLocation.getPsc();
                        if(parsed_psc3G < 0) parsed_psc3G = 0;
                    }
                    //main_info += parsed_psc3G;
                } else {
                    cellid3Gcomp_hex = Integer.toHexString((int)demo_cellids3g.get(demo_count_server3g));
                    parsed_lac3G = (int)demo_lac3g.get(demo_count_server3g);
                    parsed_psc3G = (int)demo_psc3g.get(demo_count_server3g);
                }
                cellid3G_hex = cellid3Gcomp_hex.substring(cellid3Gcomp_hex.length() - 4);
                parsed_cellid3G = Integer.parseInt(cellid3G_hex, 16);
                rnc3G_hex = cellid3Gcomp_hex.substring(0, cellid3Gcomp_hex.length() - 4);
                if(rnc3G_hex.compareTo("") != 0){
                    parsed_rnc3G = Integer.parseInt(rnc3G_hex, 16);
                } else {
                    parsed_rnc3G = 999;
                }
                if (parsed_cellid3G == server_cellid3G){
                    //debug
                    if(norelease)System.out.println("\nCellid3g no varia: " + parsed_cellid3G);
                } else {
                    al_adjcs_psc3g = new ArrayList();
                    al_adjcs_name3g = new ArrayList();
                    al_adjcs_uarfcn3g = new ArrayList();
                    al_adjcs_cellid3g = new ArrayList();
                    if(al_3gservers_cellid.indexOf(parsed_cellid3G) != -1) {
                        int server3g_buffer_index = al_3gservers_cellid.indexOf(parsed_cellid3G);
                        //debug
                        if(norelease)System.out.println("\nCellid3g en buffer: " + server3g_buffer_index + " - " + parsed_cellid3G);
                        server_name3G = (String)al_3gservers_name.get(server3g_buffer_index);
                        server_uarfcn3G = (int)al_3gservers_uarfcn.get(server3g_buffer_index);
                        server_cellid3G = parsed_cellid3G;
                        server_psc3G = parsed_psc3G;
                        server_lac3G = parsed_lac3G;
                        server_rnc3G = parsed_rnc3G;
                        server_lat3G = (double)al_3gservers_lat.get(server3g_buffer_index);
                        server_lon3G = (double)al_3gservers_lon.get(server3g_buffer_index);
                    } else {
                        int cellid3G_finish = 0;
                        int cellid3G_byte = -1;
                        //debug
                        if(norelease)System.out.println("\nNuevo cellid3g: " + parsed_cellid3G);
                        for(int cellid3G_start :(ArrayList<Integer>) al_mm3g_starts){
                            cellid3G_finish = (int) al_mm3g_finishs.get(al_mm3g_starts.indexOf(cellid3G_start));
                            //debug
                            //System.out.println("Comparando rango: " + cellid3G_start + " - " + cellid3G_finish);
                            if(parsed_cellid3G >= cellid3G_start && parsed_cellid3G <= cellid3G_finish) {
                                cellid3G_byte = (int) al_mm3g_bytes.get(al_mm3g_starts.indexOf(cellid3G_start));
                                //debug
                                if(norelease)System.out.println("Rango encontrado: " + cellid3G_start + " - " + cellid3G_finish + " bytes: " + cellid3G_byte);
                                break;
                            }
                        }
                        if(cellid3G_byte == -1){
                            server_name3G = "Celda no en DB: " + parsed_cellid3G;
                            server_uarfcn3G = -2;
                            server_cellid3G = -2;
                            server_psc3G = -2;
                            server_lac3G = -2;
                            server_rnc3G = -2;
                            server_lat3G = -2;
                            server_lon3G = -2;
                        } else {
                            InputStream in_mm3g = null;
                            String FILENAME3G = "help1";
                            try {
                                in_mm3g = openFileInput(FILENAME3G);
                            } catch(Exception e){
                                if(norelease)System.out.println("Error abriendo file mm3g: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                            int ch_mm3g, i_mm3g=0;
                            String mm3g_cellid = "";
                            String mm3g_name = "";
                            String mm3g_uarfcn = "";
                            String mm3g_psc = "";
                            String mm3g_lat = "";
                            String mm3g_lon = "";
                            server_name3G = "";
                            server_uarfcn3G = 0;
                            StringBuffer mm3g_data = new StringBuffer();
                            try{
                                in_mm3g.skip(cellid3G_byte);
                                while (in_mm3g.available()>0){
                                    ch_mm3g = in_mm3g.read();
                                    if (ch_mm3g == ',') {
                                        switch (i_mm3g){
                                            case 0:
                                                mm3g_cellid = mm3g_data.toString();
                                                break;
                                            case 1:
                                                mm3g_name = mm3g_data.toString();
                                                break;
                                            case 2:
                                                mm3g_uarfcn = mm3g_data.toString();
                                                break;
                                            case 3:
                                                mm3g_psc = mm3g_data.toString();
                                                break;
                                            case 4:
                                                mm3g_lat = mm3g_data.toString();
                                                break;
                                        }
                                        i_mm3g++;
                                        mm3g_data = new StringBuffer();
                                    } else if (ch_mm3g == '\n'){
                                        mm3g_lon = mm3g_data.toString();
                                        if (Integer.parseInt(mm3g_cellid) > cellid3G_finish) break;
                                        //debug
                                        //if(norelease)System.out.println("Buscando mm3g_cellid: " + mm3g_cellid + " " + mm3g_name + " " + mm3g_uarfcn);
                                        if ( mm3g_cellid.compareTo(String.valueOf(parsed_cellid3G))==0) {
                                            server_name3G = mm3g_name;
                                            server_uarfcn3G = Integer.parseInt(mm3g_uarfcn);
                                            server_cellid3G = parsed_cellid3G;
                                            server_psc3G = parsed_psc3G;
                                            server_lac3G = parsed_lac3G;
                                            server_rnc3G = parsed_rnc3G;
                                            server_lat3G = Double.parseDouble(mm3g_lat);
                                            server_lon3G = Double.parseDouble(mm3g_lon);
                                            if(al_3gservers_cellid.size() >= server3G_buffer_size) {
                                                //debug
                                                if(norelease)System.out.println("Cellid a remover del buffer: " + al_3gservers_cellid.get(0) + " tamano: " + al_3gservers_cellid.size());
                                                al_3gservers_cellid.remove(0);
                                                al_3gservers_name.remove(0);
                                                al_3gservers_uarfcn.remove(0);
                                                al_3gservers_lat.remove(0);
                                                al_3gservers_lon.remove(0);
                                            }
                                            al_3gservers_cellid.add(server_cellid3G);
                                            al_3gservers_name.add(server_name3G);
                                            al_3gservers_uarfcn.add(server_uarfcn3G);
                                            al_3gservers_lat.add(server_lat3G);
                                            al_3gservers_lon.add(server_lon3G);
                                            //debug
                                            if(norelease)System.out.println("Nuevo cellid en buffer: " + parsed_cellid3G + " tamano: " + al_3gservers_cellid.size());
                                            break;
                                        }
                                        i_mm3g = 0;
                                        mm3g_data = new StringBuffer();
                                    } else
                                        mm3g_data.append((char)ch_mm3g);
                                }
                                if(server_name3G.compareTo("") == 0){
                                    server_name3G = "Celda no en DB: " + parsed_cellid3G;
                                    server_uarfcn3G = -1;
                                    server_cellid3G = -1;
                                    server_psc3G = -1;
                                    server_lac3G = -1;
                                    server_rnc3G = -1;
                                    server_lat3G = -1;
                                    server_lon3G = -1;
                                }
                                in_mm3g.close();
                                //debug
                                if(norelease)System.out.println("Exito encontrando mm3g_cellid: " + server_name3G);
                            } catch(Exception e){
                                if(norelease)System.out.println("Error encontrando mm3g_cellid: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                        }
                    }
                }
                if(parsed_cellid3G == 65535) server_cellid3G = parsed_cellid3G;
                main_info += server_name3G + "\ncellid: " + server_cellid3G + "\nuarfcn: " + server_uarfcn3G + "\nsc: " + server_psc3G + "\nlac: " + server_lac3G + "\nrnc: " + server_rnc3G + "\nrscp: " + server_rscp3G;
            }
            if ((networktype == tm.NETWORK_TYPE_LTE)) {
                tech = 3;
                main_info += "Tecnologia: 4G\n\nServidora\n";
                int parsed_eutraci4G = 0;
                String eutraci4Gcomp_hex = "";
                String cellid4G_hex = "";
                String enb4G_hex = "";
                int parsed_cellid4G = 0;
                int parsed_enb4G = 0;
                int parsed_tac4G = 0;
                int parsed_pci4G = 0;
                if(!demo){
                    //debug
                    //if(norelease)System.out.println(cellsignalstrengthlte);
                    parsed_eutraci4G = cellLocation.getCid();
                    parsed_tac4G = cellLocation.getLac();
                    parsed_pci4G = cellLocation.getPsc();
                    if(parsed_pci4G < 0) parsed_pci4G = 0;
                } else {
                    parsed_eutraci4G = (int)demo_eutrascis4g.get(demo_count_server4g);
                    parsed_tac4G = (int)demo_tac4g.get(demo_count_server4g);
                    parsed_pci4G = (int)demo_pci4g.get(demo_count_server4g);
                }
                eutraci4Gcomp_hex = Integer.toHexString(parsed_eutraci4G);
                cellid4G_hex = eutraci4Gcomp_hex.substring(eutraci4Gcomp_hex.length() - 2);
                parsed_cellid4G = Integer.parseInt(cellid4G_hex, 16);
                enb4G_hex = eutraci4Gcomp_hex.substring(0, eutraci4Gcomp_hex.length() - 2);
                if(enb4G_hex.compareTo("") != 0){
                    parsed_enb4G = Integer.parseInt(enb4G_hex, 16);
                } else {
                    parsed_enb4G = 99999;
                }
                if(parsed_eutraci4G == server_eutraci4G) {
                    //debug
                    if(norelease)System.out.println("\nCellid4g no varia: " + parsed_eutraci4G);
                } else {
                    al_adjcs_pci4g = new ArrayList();
                    al_adjcs_name4g = new ArrayList();
                    if(al_4gservers_eutraci.indexOf(parsed_eutraci4G) != -1) {
                        int server4g_buffer_index = al_4gservers_eutraci.indexOf(parsed_eutraci4G);
                        //debug
                        if(norelease)System.out.println("\nCellid4g en buffer: " + server4g_buffer_index + " - " + parsed_eutraci4G);
                        server_eutraci4G = parsed_eutraci4G;
                        server_name4G = (String)al_4gservers_name.get(server4g_buffer_index);
                        server_enb4G = parsed_enb4G;
                        server_cellid4G = parsed_cellid4G;
                        server_pci4G = parsed_pci4G;
                        server_tac4G = parsed_tac4G;
                        server_lat4G = (double)al_4gservers_lat.get(server4g_buffer_index);
                        server_lon4G = (double)al_4gservers_lon.get(server4g_buffer_index);
                    } else {
                        int eutraci4G_finish = 0;
                        int eutraci4G_byte = -1;
                        //debug
                        if(norelease)System.out.println("\nNuevo cellid4g: " + parsed_eutraci4G);
                        for(int eutraci4G_start :(ArrayList<Integer>) al_mm4g_starts){
                            eutraci4G_finish = (int) al_mm4g_finishs.get(al_mm4g_starts.indexOf(eutraci4G_start));
                            //debug
                            //if(norelease)System.out.println("Comparando rango: " + eutraci4G_start + " - " + eutraci4G_finish);
                            if(parsed_eutraci4G >= eutraci4G_start && parsed_eutraci4G <= eutraci4G_finish) {
                                eutraci4G_byte = (int) al_mm4g_bytes.get(al_mm4g_starts.indexOf(eutraci4G_start));
                                //debug
                                if(norelease)System.out.println("Rango encontrado: " + eutraci4G_start + " - " + eutraci4G_finish + " bytes: " + eutraci4G_byte);
                                break;
                            }
                        }
                        if(eutraci4G_byte == -1){
                            server_name4G = "Celda no en DB: " + parsed_enb4G + " " + parsed_cellid4G;
                            server_eutraci4G = -2;
                            server_cellid4G = -2;
                            server_pci4G = -2;
                            server_tac4G = -2;
                            server_enb4G = -2;
                            server_lat4G = -2;
                            server_lon4G = -2;
                        } else {
                            InputStream in_mm4g = null;
                            String FILENAME4G = "help3";
                            try {
                                in_mm4g = openFileInput(FILENAME4G);
                            } catch(Exception e){
                                if(norelease)System.out.println("Error abriendo file mm4g: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                            int ch_mm4g, i_mm4g=0;
                            String mm4g_eutraci = "";
                            String mm4g_name = "";
                            String mm4g_pci = "";
                            String mm4g_lat = "";
                            String mm4g_lon = "";
                            server_name4G = "";
                            StringBuffer mm4g_data = new StringBuffer();
                            try{
                                in_mm4g.skip(eutraci4G_byte);
                                while (in_mm4g.available()>0){
                                    ch_mm4g = in_mm4g.read();
                                    if (ch_mm4g == ',') {
                                        switch (i_mm4g){
                                            case 0:
                                                mm4g_eutraci = mm4g_data.toString();
                                                break;
                                            case 1:
                                                mm4g_name = mm4g_data.toString();
                                                break;
                                            case 2:
                                                mm4g_pci = mm4g_data.toString();
                                                break;
                                            case 3:
                                                mm4g_lat = mm4g_data.toString();
                                                break;
                                        }
                                        i_mm4g++;
                                        mm4g_data = new StringBuffer();
                                    } else if (ch_mm4g == '\n'){
                                        mm4g_lon = mm4g_data.toString();
                                        if (Integer.parseInt(mm4g_eutraci) > eutraci4G_finish) break;
                                        //debug
                                        //if(norelease)System.out.println("Buscando mm4g_eutra: " + mm4g_eutraci + " " + mm4g_name);
                                        if ( mm4g_eutraci.compareTo(String.valueOf(parsed_eutraci4G))==0) {
                                            server_name4G = mm4g_name;
                                            server_eutraci4G = parsed_eutraci4G;
                                            server_enb4G = parsed_enb4G;
                                            server_cellid4G = parsed_cellid4G;
                                            server_pci4G = parsed_pci4G;
                                            server_tac4G = parsed_tac4G;
                                            server_lat4G = Double.parseDouble(mm4g_lat);
                                            server_lon4G = Double.parseDouble(mm4g_lon);
                                            if(al_4gservers_eutraci.size() >= server4G_buffer_size) {
                                                //debug
                                                if(norelease)System.out.println("Cellid a remover del buffer: " + al_4gservers_eutraci.get(0) + " tamano: " + al_4gservers_eutraci.size());
                                                al_4gservers_eutraci.remove(0);
                                                al_4gservers_name.remove(0);
                                                al_4gservers_lat.remove(0);
                                                al_4gservers_lon.remove(0);
                                            }
                                            al_4gservers_eutraci.add(server_eutraci4G);
                                            al_4gservers_name.add(server_name4G);
                                            al_4gservers_lat.add(server_lat4G);
                                            al_4gservers_lon.add(server_lon4G);
                                            //debug
                                            if(norelease)System.out.println("Nuevo cellid en buffer: " + parsed_eutraci4G + " tamano: " + al_4gservers_eutraci.size());
                                            break;
                                        }
                                        i_mm4g = 0;
                                        mm4g_data = new StringBuffer();
                                    } else
                                        mm4g_data.append((char)ch_mm4g);
                                }
                                if(server_name4G.compareTo("") == 0){
                                    server_name4G = "Celda no en DB: " + parsed_enb4G + " " + parsed_cellid4G;
                                    server_enb4G = -1;
                                    server_cellid4G = -1;
                                    server_pci4G = -1;
                                    server_tac4G = -1;
                                    server_eutraci4G = -1;
                                    server_lat4G = -1;
                                    server_lon4G = -1;
                                }
                                in_mm4g.close();
                                //debug
                                if(norelease)System.out.println("Exito encontrando mm4g_cellid: " + server_name4G);
                            } catch(Exception e){
                                if(norelease)System.out.println("Error encontrando mm4g_cellid: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                        }
                    }
                }
                //if(parsed_cellid4G == 65535) server_cellid4G = parsed_cellid4G;
                main_info += server_name4G + "\ncellid: " + server_cellid4G + "\nenb: " + server_enb4G + "\npci: " + server_pci4G + "\ntac: " + server_tac4G + "\nrsrp: " + server_rsrp4G + "\nrsrq: " + server_rsrq4G + "\nsnr: " + server_snr4G + "\nrssi: " + server_rssi4G;
            }

            if ((networktype == tm.NETWORK_TYPE_GPRS || networktype == tm.NETWORK_TYPE_EDGE)) {
                tech = 4;
                main_info += "Tecnologia: 2G\n\nServidora\n";
                int parsed_cellid2G = 0;
                int parsed_lac2G = 0;
                if(!demo){
                    //debug
                    //if(norelease)System.out.println(cellsignalstrengthgsm);
                    parsed_cellid2G = cellLocation.getCid();
                    parsed_lac2G = cellLocation.getLac();
                } else {
                    parsed_cellid2G = (int)demo_cellids2g.get(demo_count_server2g);
                    parsed_lac2G = (int)demo_lac2g.get(demo_count_server2g);
                }
                if(parsed_cellid2G == server_cellid2G) {
                    //debug
                    if(norelease)System.out.println("\nCellid2g no varia: " + parsed_cellid2G);
                } else {
                    al_adjcs_cellid2g = new ArrayList();
                    al_adjcs_name2g = new ArrayList();
                    if(al_2gservers_cellid.indexOf(parsed_cellid2G) != -1) {
                        int server2g_buffer_index = al_2gservers_cellid.indexOf(parsed_cellid2G);
                        //debug
                        if(norelease)System.out.println("\nCellid2g en buffer: " + server2g_buffer_index + " - " + parsed_cellid2G);
                        server_cellid2G = parsed_cellid2G;
                        server_name2G = (String)al_2gservers_name.get(server2g_buffer_index);
                        server_lac2G = parsed_lac2G;
                        server_bsc2G = (String)al_2gservers_bsc.get(server2g_buffer_index);
                        server_bcch2G = (int)al_2gservers_bcch.get(server2g_buffer_index);
                        server_bsic2G = (String)al_2gservers_bsic.get(server2g_buffer_index);
                    } else {
                        int cellid2G_finish = 0;
                        int cellid2G_byte = -1;
                        //debug
                        if(norelease)System.out.println("\nNuevo cellid2g: " + parsed_cellid2G);
                        for(int cellid2G_start :(ArrayList<Integer>) al_mm2g_starts){
                            cellid2G_finish = (int) al_mm2g_finishs.get(al_mm2g_starts.indexOf(cellid2G_start));
                            //debug
                            //if(norelease)System.out.println("Comparando rango: " + cellid2G_start + " - " + cellid2G_finish);
                            if(parsed_cellid2G >= cellid2G_start && parsed_cellid2G <= cellid2G_finish) {
                                cellid2G_byte = (int) al_mm2g_bytes.get(al_mm2g_starts.indexOf(cellid2G_start));
                                //debug
                                if(norelease)System.out.println("Rango encontrado: " + cellid2G_start + " - " + cellid2G_finish + " bytes: " + cellid2G_byte);
                                break;
                            }
                        }
                        if(cellid2G_byte == -1){
                            server_name2G = "Celda no en DB: " + parsed_cellid2G;
                            server_cellid2G = -2;
                            server_lac2G = -2;
                            server_bsc2G = "-2";
                            server_bcch2G = -2;
                            server_bsic2G = "-2";
                        } else {
                            InputStream in_mm2g = null;
                            String FILENAME2G = "help5";
                            try {
                                in_mm2g = openFileInput(FILENAME2G);
                            } catch(Exception e){
                                if(norelease)System.out.println("Error abriendo file mm2g: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                            int ch_mm2g, i_mm2g=0;
                            String mm2g_cellid = "";
                            String mm2g_name = "";
                            String mm2g_bsc = "";
                            String mm2g_freq = "";
                            server_name2G = "";
                            StringBuffer mm2g_data = new StringBuffer();
                            try{
                                in_mm2g.skip(cellid2G_byte);
                                while (in_mm2g.available()>0){
                                    ch_mm2g = in_mm2g.read();
                                    if (ch_mm2g == ',') {
                                        switch (i_mm2g){
                                            case 0:
                                                mm2g_cellid = mm2g_data.toString();
                                                break;
                                            case 1:
                                                mm2g_name = mm2g_data.toString();
                                                break;
                                            case 2:
                                                mm2g_bsc = mm2g_data.toString();
                                                break;
                                        }
                                        i_mm2g++;
                                        mm2g_data = new StringBuffer();
                                    } else if (ch_mm2g == '\n'){
                                        mm2g_freq = mm2g_data.toString();
                                        if (Integer.parseInt(mm2g_cellid) > cellid2G_finish) break;
                                        //debug
                                        //if(norelease)System.out.println("Buscando mm2g_cellid: " + mm2g_cellid + " " + mm2g_name);
                                        if (mm2g_cellid.compareTo(String.valueOf(parsed_cellid2G)) == 0) {
                                            server_name2G = mm2g_name;
                                            server_cellid2G = parsed_cellid2G;
                                            server_bsc2G = mm2g_bsc;
                                            server_lac2G = parsed_lac2G;
                                            server_bcch2G = Integer.parseInt(mm2g_freq.substring(0,mm2g_freq.indexOf("-")));
                                            server_bsic2G = mm2g_freq.substring(mm2g_freq.indexOf("-") + 1);
                                            if(al_2gservers_cellid.size() >= server2G_buffer_size) {
                                                //debug
                                                if(norelease)System.out.println("Cellid a remover del buffer: " + al_2gservers_cellid.get(0) + " tamano: " + al_2gservers_cellid.size());
                                                al_2gservers_cellid.remove(0);
                                                al_2gservers_name.remove(0);
                                                al_2gservers_bsc.remove(0);
                                                al_2gservers_bcch.remove(0);
                                                al_2gservers_bsic.remove(0);
                                            }
                                            al_2gservers_cellid.add(server_cellid2G);
                                            al_2gservers_name.add(server_name2G);
                                            al_2gservers_bsc.add(server_bsc2G);
                                            al_2gservers_bcch.add(server_bcch2G);
                                            al_2gservers_bsic.add(server_bsic2G);
                                            //debug
                                            if(norelease)System.out.println("Nuevo cellid en buffer: " + parsed_cellid2G + " tamano: " + al_2gservers_cellid.size());
                                            break;
                                        }
                                        i_mm2g = 0;
                                        mm2g_data = new StringBuffer();
                                    } else
                                        mm2g_data.append((char)ch_mm2g);
                                }
                                if(server_name2G.compareTo("") == 0){
                                    server_name2G = "Celda no en DB: " + parsed_cellid2G;
                                    server_cellid2G = -1;
                                    server_lac2G = -1;
                                    server_bsc2G = "-1";
                                    server_bcch2G = -1;
                                    server_bsic2G = "-1";
                                }
                                in_mm2g.close();
                                //debug
                                if(norelease)System.out.println("Exito encontrando mm2g_cellid: " + server_name2G);
                            } catch(Exception e){
                                if(norelease)System.out.println("Error encontrando mm2g_cellid: " + e.getMessage());
                                if(norelease)e.printStackTrace();
                            }
                        }
                    }
                }
                main_info += server_name2G + "\ncellid: " + server_cellid2G + "\nlac: " + server_lac2G + "\nbsc: " + server_bsc2G + "\nbcch: " + server_bcch2G+ "\nbsic: " + server_bsic2G  + "\nrxlev: " + server_rxlev2G;
            }
            if(norelease)main_info += "\n" + ticks_count + " " + al_2gservers_cellid + " " + al_adjcs_cellid2g + " ";
            main_info += "\n\nVecinas";

            if(phonemodel.equals("SM-G920I")){
                boolean isserver = true;
                for(CellInfo cellinfo:cellInfo) {
                    if (isserver) {
                        isserver = false;
                    } else {
                        if(phonemodel.equals("SM-G920I")){
                            if (cellinfo instanceof CellInfoLte || demo) {
                                CellInfoLte cellinfolte = null;
                                CellIdentityLte cellidentitylte = null;
                                CellSignalStrengthLte cellsignalstrengthlte = null;
                                int parsed_pci4g_adjc = 0;
                                int pci4g_adjc_cursor = 0;
                                int parsed_rsrp4g_adjc = 0;
                                int parsed_rsrq4g_adjc = 0;
                                if(!demo){
                                    cellinfolte = (CellInfoLte)cellinfo;
                                    cellidentitylte = cellinfolte.getCellIdentity();
                                    parsed_pci4g_adjc = cellidentitylte.getPci();
                                    cellsignalstrengthlte = cellinfolte.getCellSignalStrength();
                                    //debug
                                    //if(norelease)System.out.println(cellsignalstrengthlte);
                                    String cellsignalstrengthltetxt = cellsignalstrengthlte.toString();
                                    String cellsignalstrengthltetemp = cellsignalstrengthltetxt.substring(cellsignalstrengthltetxt.indexOf(" ") + 1);
                                    cellsignalstrengthltetemp = cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf(" ") + 1);
                                    parsed_rsrp4g_adjc = Integer.parseInt(cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf("=") + 1,cellsignalstrengthltetemp.indexOf(" ")));
                                    cellsignalstrengthltetemp = cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf(" ") + 1);
                                    parsed_rsrq4g_adjc = Integer.parseInt(cellsignalstrengthltetemp.substring(cellsignalstrengthltetemp.indexOf("=") + 1,cellsignalstrengthltetemp.indexOf(" ")));
                                } else {
                                    parsed_pci4g_adjc = (int)demo_adjc_pci4g.get(demo_count_adjc4g);
                                    parsed_rsrp4g_adjc = (int)demo_adjc_rsrp4g.get(demo_count_adjc4g);
                                }
                                if(al_adjcs_pci4g.indexOf(parsed_pci4g_adjc) != -1) {
                                    int adjcs_pci4g_index = 0;
                                    //debug
                                    if(norelease)System.out.println("Pci4g ya en buffer: " + parsed_pci4g_adjc);
                                    for(int adjcs_pci4g_temp:(ArrayList<Integer>)al_adjcs_pci4g){
                                        if(adjcs_pci4g_temp == parsed_pci4g_adjc){
                                            main_info += "\n-" + parsed_rsrp4g_adjc + " " + parsed_rsrq4g_adjc + " " + al_adjcs_name4g.get(adjcs_pci4g_index);
                                            //debug
                                            if(norelease)System.out.println(al_adjcs_name4g.get(adjcs_pci4g_index));
                                        }
                                        adjcs_pci4g_index++;
                                    }
                                } else {
                                    int pci4g_adjc_byte = 0;
                                    //debug
                                    if(norelease)System.out.println("Pci4 nuevo: " + parsed_pci4g_adjc);
                                    for(int pci4gadjc_count = 0; pci4gadjc_count < 61; pci4gadjc_count++){
                                        //debug
                                        //if(norelease)System.out.println("Comparando rango pci: " + (pci4gadjc_count * 8) + " - " + ((pci4gadjc_count * 8) + 8));
                                        if(parsed_pci4g_adjc >= pci4gadjc_count * 8 && parsed_pci4g_adjc < (pci4gadjc_count * 8) + 8) {
                                            pci4g_adjc_byte = (int) al_mm4gpci_bytes.get(pci4gadjc_count);
                                            pci4g_adjc_cursor = pci4gadjc_count;
                                            //debug
                                            if(norelease)System.out.println("Rango pci encontrado: " + (pci4gadjc_count * 8) + " - " + ((pci4gadjc_count * 8) + 8) + " bytes: " + pci4g_adjc_byte);
                                            break;
                                        }
                                    }
                                    InputStream in_mm4gpci = null;
                                    String FILENAME4G = "help4";
                                    try {
                                        in_mm4gpci = openFileInput(FILENAME4G);
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error abriendo file mm4gpci: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                    int ch_mm4gpci, i_mm4gpci=0;
                                    String mm4gpci_pci = "";
                                    String mm4gpci_name = "";
                                    String mm4gpci_lat = "";
                                    String mm4gpci_lon = "";
                                    String mm4gpci_eutraci = "";
                                    double adjc_lat = 0;
                                    double adjc_lon = 0;
                                    StringBuffer mm4gpci_data = new StringBuffer();
                                    ArrayList precand_adjc4g_name = new ArrayList();
                                    ArrayList precand_adjc4g_dist = new ArrayList();
                                    try{
                                        in_mm4gpci.skip(pci4g_adjc_byte);
                                        while (in_mm4gpci.available()>0){
                                            ch_mm4gpci = in_mm4gpci.read();
                                            if (ch_mm4gpci == ',') {
                                                switch (i_mm4gpci){
                                                    case 0:
                                                        mm4gpci_pci = mm4gpci_data.toString();
                                                        break;
                                                    case 1:
                                                        mm4gpci_name = mm4gpci_data.toString();
                                                        break;
                                                    case 2:
                                                        mm4gpci_lat = mm4gpci_data.toString();
                                                        break;
                                                    case 3:
                                                        mm4gpci_lon = mm4gpci_data.toString();
                                                        break;
                                                }
                                                i_mm4gpci++;
                                                mm4gpci_data = new StringBuffer();
                                            } else if (ch_mm4gpci == '\n'){
                                                mm4gpci_eutraci = mm4gpci_data.toString();
                                                if (Integer.parseInt(mm4gpci_pci) >= (pci4g_adjc_cursor * 8) + 8) break;
                                                int mm4gpci_dist = 0;
                                                //debug
                                                //if(norelease)System.out.println("Buscando pci: " + mm4gpci_pci + " " + mm4gpci_name + " " + mm4gpci_lat + " " + mm4gpci_lon);
                                                if ( mm4gpci_pci.compareTo(String.valueOf(parsed_pci4g_adjc))==0) {
                                                    precand_adjc4g_name.add(mm4gpci_name);
                                                    adjc_lat = Double.parseDouble(mm4gpci_lat);
                                                    adjc_lon = Double.parseDouble(mm4gpci_lon);
                                                    mm4gpci_dist = (int)(Math.acos(Math.sin(server_lat4G * Math.PI / 180) * Math.sin(adjc_lat * Math.PI / 180) + Math.cos(server_lat4G * Math.PI / 180) *
                                                            Math.cos(adjc_lat * Math.PI / 180) * Math.cos(adjc_lon * Math.PI / 180 - server_lon4G * Math.PI / 180)) * 6371000);
                                                    precand_adjc4g_dist.add(mm4gpci_dist);
                                                    //debug
                                                    //if(norelease)System.out.println("Distancia pci: " + mm4gpci_name + " " + mm4gpci_dist);
                                                }
                                                i_mm4gpci = 0;
                                                mm4gpci_data = new StringBuffer();
                                            }
                                            else mm4gpci_data.append((char)ch_mm4gpci);
                                        }
                                        in_mm4gpci.close();
                                        int dist_min = 5000000;
                                        for(int dist_temp:(ArrayList<Integer>)precand_adjc4g_dist){
                                            if(dist_min > dist_temp) dist_min = dist_temp;
                                        }
                                        main_info += "\n-" + parsed_rsrp4g_adjc + " " + parsed_rsrq4g_adjc + " " + precand_adjc4g_name.get(precand_adjc4g_dist.indexOf(dist_min));
                                        al_adjcs_pci4g.add(parsed_pci4g_adjc);
                                        al_adjcs_name4g.add(precand_adjc4g_name.get(precand_adjc4g_dist.indexOf(dist_min)));
                                        //debug
                                        if(norelease)System.out.println(precand_adjc4g_name.get(precand_adjc4g_dist.indexOf(dist_min)));
                                    } catch(Exception e){
                                        if(norelease)System.out.println("Error encontrando adjc pci: " + e.getMessage());
                                        if(norelease)e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        runOnUiThread(new Runnable() {
            public void run() {
                tv_main_info.setText(main_info);
            }
        });

        isUpdatingCell = false;
    }

    private class ActualizaTask extends AsyncTask<Void, Void, Void> {
        private String actualiza_resultado;
        private a_moramon actividad;

        public ActualizaTask(a_moramon f_actividad) {
            actividad = f_actividad;
        }

        protected Void doInBackground(Void... fvoid) {
            StringBuffer log_buffer;
            InputStream in;
            URL url;
            HttpURLConnection urlConnection = null;
            int char_ioTemp;
            ArrayList al_readline = new ArrayList();

            Database_Moramon db_mm_helper;
            SQLiteDatabase db_mm = null;
            String sql = "";
            String n_imei = "";
            actualiza_resultado = "";
            SQLiteStatement statement;

            byte [] fconn_bytes;
            String FILENAME;
            FileOutputStream fos;

            int READPHONESTATEpermissionCheck = ContextCompat.checkSelfPermission(actividad, Manifest.permission.READ_PHONE_STATE);
            int REQUEST_READ_PHONE_STATE = 1;
            int ACCESSCOARSELOCATIONpermissionCheck = ContextCompat.checkSelfPermission(actividad, Manifest.permission.ACCESS_COARSE_LOCATION);
            int REQUEST_ACCESS_COARSE_LOCATION = 2;

            if (READPHONESTATEpermissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(actividad, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                actualiza_resultado = "Se requiere el permiso";
                return null;
            } else if(ACCESSCOARSELOCATIONpermissionCheck != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(actividad, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PHONE_STATE);
                actualiza_resultado = "Se requiere el permiso";
                return null;
            } else {
                //Autenticando
                n_imei = tm.getDeviceId();
                if(demo) n_imei = "000000000000000";
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con aut");
                    url = new URL("http://sekai.ec/moramon/maut.php?imei=" + n_imei);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de aut " + url);
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        if(char_ioTemp == '\n') {
                            al_readline.add(log_buffer.toString());
                            log_buffer = new StringBuffer();
                            continue;
                        }
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando aut: " + al_readline.size() + " lines read " + al_readline);
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando aut: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 0";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }
                if(((String)al_readline.get(0)).compareTo("0") == 0){
                    actualiza_resultado = "No autorizado";
                    return null;
                }

                //Descarga el archivo mm3gindex en un al
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm3gindex");
                    url = new URL("http://sekai.ec/moramon/tai");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();
                    al_readline = new ArrayList();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm3gindex");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        if(char_ioTemp == '\n') {
                            al_readline.add(log_buffer.toString());
                            log_buffer = new StringBuffer();
                            continue;
                        }
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm3gindex: " + al_readline.size() + " lines read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm3gindex: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 1";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }

                //Carga al_mm3gindex en la db_mm
                String mm3gindex_temp = "";
                try {
                    db_mm_helper = new Database_Moramon(contexto);
                    db_mm = db_mm_helper.getWritableDatabase();
                    db_mm.execSQL("DROP TABLE IF EXISTS tai");
                    db_mm.execSQL("CREATE TABLE tai (_id INTEGER PRIMARY KEY AUTOINCREMENT, cellidbyte INTEGER, cellidstart INTEGER, cellidfinish INTEGER)");
                    db_mm.beginTransaction();
                    sql ="INSERT INTO tai (cellidbyte, cellidstart, cellidfinish) VALUES (?, ?, ?)";
                    statement = db_mm.compileStatement(sql);
                    for(String mm3gindex:(ArrayList<String>)al_readline){
                        statement.clearBindings();
                        statement.bindLong(1, Integer.parseInt(mm3gindex.substring(0, mm3gindex.indexOf(','))));
                        mm3gindex_temp = mm3gindex.substring(mm3gindex.indexOf(',')+1,mm3gindex.length());
                        statement.bindLong(2, Integer.parseInt(mm3gindex_temp.substring(0, mm3gindex_temp.indexOf(','))));
                        mm3gindex_temp = mm3gindex_temp.substring(mm3gindex_temp.indexOf(',') + 1, mm3gindex_temp.length());
                        statement.bindLong(3, Integer.parseInt(mm3gindex_temp));
                        statement.executeInsert();
                    }
                    db_mm.setTransactionSuccessful();// This commits the transaction if there were no exceptions
                    //debug
                    if(norelease)System.out.println("Exito cargando mm3gindex en db_mm");
                } catch (Exception e){
                    if(norelease)System.out.println("Error cargando mm3gindex en db_mm: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 2";
                    return null;
                } finally {
                    db_mm.endTransaction();
                    db_mm.close();
                }


                //Descarga el archivo mm3g en un buffer
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm3g");
                    url = new URL("http://sekai.ec/moramon/ta");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm3g");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm3g: " + log_buffer.length() + " bytes read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm3g: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 3";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //carga el buffer mm3g en archivo help1
                fconn_bytes = log_buffer.toString().getBytes();
                FILENAME = "help1";
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(fconn_bytes);
                    fos.close();
                    //debug
                    if(norelease)System.out.println("Exito grabando archivo mm3g: " + fconn_bytes.length + " bytes saved");
                } catch(Exception e){
                    if(norelease)System.out.println("Error grabando archivo mm3g: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 4";
                    return null;
                }


                //Descarga el archivo mm3gpscindex en un al
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm3gpscindex");
                    url = new URL("http://sekai.ec/moramon/tapi");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();
                    al_readline = new ArrayList();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm3gpscindex");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        if(char_ioTemp == '\n') {
                            al_readline.add(log_buffer.toString());
                            //System.out.println(log_buffer.toString());
                            log_buffer = new StringBuffer();
                            continue;
                        }
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm3gpscindex: " + al_readline.size() + " lines read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm3gpscindex: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 5";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //Carga al_mm3gpscindex en la db_mm
                try {
                    db_mm_helper = new Database_Moramon(contexto);
                    db_mm = db_mm_helper.getWritableDatabase();
                    db_mm.execSQL("DROP TABLE IF EXISTS tapi");
                    db_mm.execSQL("CREATE TABLE tapi (_id INTEGER PRIMARY KEY AUTOINCREMENT, pscbyte INTEGER)");
                    db_mm.beginTransaction();
                    sql ="INSERT INTO tapi (pscbyte) VALUES (?)";
                    statement = db_mm.compileStatement(sql);
                    for(String mm3gpscindex :(ArrayList<String>)al_readline){
                        statement.clearBindings();
                        statement.bindLong(1, Integer.parseInt(mm3gpscindex));
                        statement.executeInsert();
                    }
                    db_mm.setTransactionSuccessful();// This commits the transaction if there were no exceptions
                    //debug
                    if(norelease)System.out.println("Exito cargando mm3gpscindex en db_mm");
                } catch (Exception e){
                    if(norelease)System.out.println("Error cargando mm3gpscindex en db_mm: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 6";
                    return null;
                } finally {
                    db_mm.endTransaction();
                    db_mm.close();
                }


                //Descarga el archivo mm3gpsc en un buffer
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm3gpsc");
                    url = new URL("http://sekai.ec/moramon/tap");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm3gpsc");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm3gpsc: " + log_buffer.length() + " bytes read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm3gpsc: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 7";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //carga el buffer mm3gpsc en archivo help2
                fconn_bytes = log_buffer.toString().getBytes();
                FILENAME = "help2";
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(fconn_bytes);
                    fos.close();
                    //debug
                    if(norelease)System.out.println("Exito grabando archivo mm3gpsc: " + fconn_bytes.length + " bytes saved");
                } catch(Exception e){
                    if(norelease)System.out.println("Error grabando archivo mm3gpsc: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 8";
                    return null;
                }


                //Descarga el archivo mm4gindex en un al
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm4gindex");
                    url = new URL("http://sekai.ec/moramon/rai");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();
                    al_readline = new ArrayList();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm4gindex");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        if(char_ioTemp == '\n') {
                            al_readline.add(log_buffer.toString());
                            log_buffer = new StringBuffer();
                            continue;
                        }
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm4gindex: " + al_readline.size() + " lines read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm4gindex: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 9";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //Carga al_mm4gindex en la db_mm
                String mm4gindex_temp = "";
                try {
                    db_mm_helper = new Database_Moramon(contexto);
                    db_mm = db_mm_helper.getWritableDatabase();
                    db_mm.execSQL("DROP TABLE IF EXISTS rai");
                    db_mm.execSQL("CREATE TABLE rai (_id INTEGER PRIMARY KEY AUTOINCREMENT, eutracibyte INTEGER, eutracistart INTEGER, eutracifinish INTEGER)");
                    db_mm.beginTransaction();
                    sql ="INSERT INTO rai (eutracibyte, eutracistart, eutracifinish) VALUES (?, ?, ?)";
                    statement = db_mm.compileStatement(sql);
                    for(String mm4gindex:(ArrayList<String>)al_readline){
                        statement.clearBindings();
                        statement.bindLong(1, Integer.parseInt(mm4gindex.substring(0, mm4gindex.indexOf(','))));
                        mm4gindex_temp = mm4gindex.substring(mm4gindex.indexOf(',')+1,mm4gindex.length());
                        statement.bindLong(2, Integer.parseInt(mm4gindex_temp.substring(0, mm4gindex_temp.indexOf(','))));
                        mm4gindex_temp = mm4gindex_temp.substring(mm4gindex_temp.indexOf(',') + 1, mm4gindex_temp.length());
                        statement.bindLong(3, Integer.parseInt(mm4gindex_temp));
                        statement.executeInsert();
                    }
                    db_mm.setTransactionSuccessful();// This commits the transaction if there were no exceptions
                    //debug
                    if(norelease)System.out.println("Exito cargando mm4gindex en db_mm");
                } catch (Exception e){
                    if(norelease)System.out.println("Error cargando mm4gindex en db_mm: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 10";
                    return null;
                } finally {
                    db_mm.endTransaction();
                    db_mm.close();
                }


                //Descarga el archivo mm4g en un buffer
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm4g");
                    url = new URL("http://sekai.ec/moramon/ra");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm4g");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm4g: " + log_buffer.length() + " bytes read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm4g: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 11";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //carga el buffer mm4g en archivo help3
                fconn_bytes = log_buffer.toString().getBytes();
                FILENAME = "help3";
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(fconn_bytes);
                    fos.close();
                    //debug
                    if(norelease)System.out.println("Exito grabando archivo mm4g: " + fconn_bytes.length + " bytes saved");
                } catch(Exception e){
                    if(norelease)System.out.println("Error grabando archivo mm4g: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 12";
                    return null;
                }


                //Descarga el archivo mm4gpciindex en un al
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm4gpciindex");
                    url = new URL("http://sekai.ec/moramon/rapi");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();
                    al_readline = new ArrayList();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm4gpciindex");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        if(char_ioTemp == '\n') {
                            al_readline.add(log_buffer.toString());
                            //System.out.println(log_buffer.toString());
                            log_buffer = new StringBuffer();
                            continue;
                        }
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm4gpciindex: " + al_readline.size() + " lines read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm4gpciindex: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 13";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //Carga al_mm4gpciindex en la db_mm
                try {
                    db_mm_helper = new Database_Moramon(contexto);
                    db_mm = db_mm_helper.getWritableDatabase();
                    db_mm.execSQL("DROP TABLE IF EXISTS rapi");
                    db_mm.execSQL("CREATE TABLE rapi (_id INTEGER PRIMARY KEY AUTOINCREMENT, pcibyte INTEGER)");
                    db_mm.beginTransaction();
                    sql ="INSERT INTO rapi (pcibyte) VALUES (?)";
                    statement = db_mm.compileStatement(sql);
                    for(String mm4gpciindex :(ArrayList<String>)al_readline){
                        statement.clearBindings();
                        statement.bindLong(1, Integer.parseInt(mm4gpciindex));
                        statement.executeInsert();
                    }
                    db_mm.setTransactionSuccessful();// This commits the transaction if there were no exceptions
                    //debug
                    if(norelease)System.out.println("Exito cargando mm4gpciindex en db_mm");
                } catch (Exception e){
                    if(norelease)System.out.println("Error cargando mm4gpciindex en db_mm: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 14";
                    return null;
                } finally {
                    db_mm.endTransaction();
                    db_mm.close();
                }


                //Descarga el archivo mm4gpci en un buffer
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm4gpci");
                    url = new URL("http://sekai.ec/moramon/rap");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm4gpci");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm4gpci: " + log_buffer.length() + " bytes read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm4gpci: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 15";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //carga el buffer mm3gpsc en archivo help4
                fconn_bytes = log_buffer.toString().getBytes();
                FILENAME = "help4";
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(fconn_bytes);
                    fos.close();
                    //debug
                    if(norelease)System.out.println("Exito grabando archivo mm4gpci: " + fconn_bytes.length + " bytes saved");
                } catch(Exception e){
                    if(norelease)System.out.println("Error grabando archivo mm4gpci: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 16";
                    return null;
                }


                //Descarga el archivo mm2gindex en un al
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm2gindex");
                    url = new URL("http://sekai.ec/moramon/sai");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();
                    al_readline = new ArrayList();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm2gindex");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        if(char_ioTemp == '\n') {
                            al_readline.add(log_buffer.toString());
                            log_buffer = new StringBuffer();
                            continue;
                        }
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm2gindex: " + al_readline.size() + " lines read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm2gindex: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 17";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //Carga al_mm2gindex en la db_mm
                String mm2gindex_temp = "";
                try {
                    db_mm_helper = new Database_Moramon(contexto);
                    db_mm = db_mm_helper.getWritableDatabase();
                    db_mm.execSQL("DROP TABLE IF EXISTS sai");
                    db_mm.execSQL("CREATE TABLE sai (_id INTEGER PRIMARY KEY AUTOINCREMENT, cellidbyte INTEGER, cellidstart INTEGER, cellidfinish INTEGER)");
                    db_mm.beginTransaction();
                    sql ="INSERT INTO sai (cellidbyte, cellidstart, cellidfinish) VALUES (?, ?, ?)";
                    statement = db_mm.compileStatement(sql);
                    for(String mm2gindex:(ArrayList<String>)al_readline){
                        statement.clearBindings();
                        statement.bindLong(1, Integer.parseInt(mm2gindex.substring(0, mm2gindex.indexOf(','))));
                        mm2gindex_temp = mm2gindex.substring(mm2gindex.indexOf(',')+1,mm2gindex.length());
                        statement.bindLong(2, Integer.parseInt(mm2gindex_temp.substring(0, mm2gindex_temp.indexOf(','))));
                        mm2gindex_temp = mm2gindex_temp.substring(mm2gindex_temp.indexOf(',') + 1, mm2gindex_temp.length());
                        statement.bindLong(3, Integer.parseInt(mm2gindex_temp));
                        statement.executeInsert();
                    }
                    db_mm.setTransactionSuccessful();// This commits the transaction if there were no exceptions
                    //debug
                    if(norelease)System.out.println("Exito cargando mm2gindex en db_mm");
                } catch (Exception e){
                    if(norelease)System.out.println("Error cargando mm2gindex en db_mm: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 18";
                    return null;
                } finally {
                    db_mm.endTransaction();
                    db_mm.close();
                }


                //Descarga el archivo mm2g en un buffer
                try {
                    //debug
                    if(norelease)System.out.println("Conectando con mm2g");
                    url = new URL("http://sekai.ec/moramon/sa");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    log_buffer = new StringBuffer();

                    //debug
                    if(norelease)System.out.println("Obteniendo stream de mm2g");
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    while ((char_ioTemp = in.read()) != '&'){
                        log_buffer.append((char)char_ioTemp);
                    }
                    //debug
                    if(norelease)System.out.println("Exito descargando mm2g: " + log_buffer.length() + " bytes read");
                    in.close();
                } catch (Exception e) {
                    if(norelease)System.out.println("Error descargando mm2g: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 19";
                    return null;
                } finally {
                    urlConnection.disconnect();
                }


                //carga el buffer mm2g en archivo help5
                fconn_bytes = log_buffer.toString().getBytes();
                FILENAME = "help5";
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(fconn_bytes);
                    fos.close();
                    //debug
                    if(norelease)System.out.println("Exito grabando archivo mm2g: " + fconn_bytes.length + " bytes saved");
                } catch(Exception e){
                    if(norelease)System.out.println("Error grabando archivo mm2g: " + e.getMessage());
                    if(norelease)e.printStackTrace();
                    actualiza_resultado = "Error actualizando 20";
                    return null;
                }

                actualiza_resultado = "Actualizado, reinicie la aplicacion";
                return null;
            }
        }


        protected void onPostExecute(Void result){
            tv_main_info.setText(actualiza_resultado);
        }
    }

    class MyPhoneStateListener extends PhoneStateListener{
        public void onSignalStrengthsChanged(SignalStrength signalStrength){
            super.onSignalStrengthsChanged(signalStrength);
            String signalStrengthtxt = signalStrength.toString();
            if(phonemodel.equals("D5803") || phonemodel.equals("G8341")) {
                String signalStrengthtemp = signalStrengthtxt.substring(signalStrengthtxt.indexOf(" ") + 1);
                server_rscp3G = -114 + (Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" "))) * 2);
                server_rxlev2G = -113 + (Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" "))) * 2);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_rssi4G = -113 + (Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")))) * 2;
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_rsrp4G = Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")));
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_rsrq4G = Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")));
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_snr4G = Double.parseDouble(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")))/10;
                //debug
                //if(norelease)System.out.println(signalStrengthtxt);
            } else {
                String signalStrengthtemp = signalStrengthtxt.substring(signalStrengthtxt.indexOf(" ") + 1);
                server_rscp3G = -114 + (Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" "))) * 2);
                server_rxlev2G = -113 + (Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" "))) * 2);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_rssi4G = -113 + (Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")))) * 2;
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_rsrp4G = Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")));
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_rsrq4G = Integer.parseInt(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")));
                signalStrengthtemp = signalStrengthtemp.substring(signalStrengthtemp.indexOf(" ") + 1);
                server_snr4G = Double.parseDouble(signalStrengthtemp.substring(0, signalStrengthtemp.indexOf(" ")))/10;
            }

        }
    }

    class ADJC3G_Expert {
        ArrayList ofic_psc3G;
        ArrayList ofic_rscp3G;
        int rscp3G_min;
        int rscp3G_max;
        boolean tiene_server_psc;
        ArrayList pscs_not_parsed_xfreq;
        int has_same_psc_inleft;
        int has_same_psc_inrigth;
        int freq_definido;
        int this_index;

        public ADJC3G_Expert(ArrayList f_psc3G, ArrayList f_rscp3G, int f_server_psc, ArrayList f_n_freqs, ArrayList f_cand_pscs, ArrayList f_cand_freqs, int f_this_index){
            freq_definido = 0;
            this_index = f_this_index;
            ofic_rscp3G = f_rscp3G;
            ofic_psc3G = f_psc3G;
            rscp3G_max = (int) ofic_rscp3G.get(ofic_rscp3G.size()-1);
            rscp3G_min = (int) ofic_rscp3G.get(0);
            tiene_server_psc = false;
            if(ofic_psc3G.indexOf(f_server_psc) != -1)
                tiene_server_psc = true;
            int pscs_not_parsed_count;
            int cand_psc_index;
            boolean psc_freq_found;
            pscs_not_parsed_xfreq = new ArrayList();
            for(int f_n_freq:(ArrayList<Integer>)f_n_freqs){
                pscs_not_parsed_count = 0;
                for(int f_ofic_psc:(ArrayList<Integer>)ofic_psc3G){
                    psc_freq_found = false;
                    cand_psc_index = 0;
                    for(int f_cand_psc:(ArrayList<Integer>)f_cand_pscs){
                        if(f_ofic_psc == f_cand_psc && f_n_freq == ((int)f_cand_freqs.get(cand_psc_index)))
                            psc_freq_found = true;
                        cand_psc_index++;
                    }
                    if(!psc_freq_found)
                        pscs_not_parsed_count++;
                }
                pscs_not_parsed_xfreq.add(pscs_not_parsed_count);
            }
        }

        public String toString(){
            return "this_index: " + this_index + " freq_definido:" + freq_definido + " tiene_server_psc:" + tiene_server_psc + " pscs_not_parsed_xfreq:" + pscs_not_parsed_xfreq +
                    " rscp3G_max:" + rscp3G_max + " rscp3G_min:" + rscp3G_min + " " + ofic_psc3G + " " + ofic_rscp3G;
        }
    }

    @Override
    public void onConnected(Bundle bundle){}

    @Override
    public void onConnectionSuspended(int i){}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){}

    private class WearTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... fvoid) {
            nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            return null;
        }
        protected void onPostExecute(Void result){
            if(norelease)System.out.println("Wears encontrados: " + nodes.getNodes().size());
            if(nodes.getNodes().size() == 0){
                tv_main_info.setText("No conectado a Wear");
            } else {
                tv_main_info.setText("Conectado a Wear");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    tv_main_info.setText("Permiso dado, vuelva a actualizar");
                }
                break;

            default:
                break;
        }
    }
}
