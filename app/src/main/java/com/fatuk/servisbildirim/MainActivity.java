package com.fatuk.servisbildirim;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.TimeZoneFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // Değişkenlerin belirlenmesi
    static EditText mMakineNoGir; //Makine numarası arama
    static TextView mMakNoText; // Makine numarası
    static TextView mBolumText; // Unvan
    static TextView mMarkaText; // Marka
    static TextView mModelText; // Model
    static TextView mSeriNoText; // Seri numarası
    static EditText mArizaVerenText; // Arızayı Veren
    static EditText mEmailText; //Kişinin emaili
    static EditText mArizaText; //Arıza veya Talep
    static TextView mTarihText; // Bildirim Tarihi
    static TextView mSaatText; // Bildirim saati
    static Spinner mTalepSpinner; //Talep tipi
    static TextView mconnText; //Bağlantı durumu
    static Button mBulButton; // Bul Butonu
    static Button mKayitButton;
    static Button mCikButton;
    static Connection connection = null; // SQL bağlantı durumu kontrol değişkeni
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Ana ekrandaki alanların ilişkilendirilmesi
        mMakineNoGir =findViewById(R.id.makineNoGir);
        mMakNoText = findViewById(R.id.makNoText);
        mBolumText = findViewById(R.id.bolumText);
        mMarkaText = findViewById(R.id.markaText);
        mModelText = findViewById(R.id.modelText);
        mSeriNoText = findViewById(R.id.seriNoText);
        mArizaVerenText = findViewById(R.id.arizaVerenText);
        mEmailText = findViewById(R.id.eMailText);
        mArizaText = findViewById(R.id.arizaText);
        mTarihText = findViewById(R.id.tarihText);
        mSaatText = findViewById(R.id.saatText);
        mTalepSpinner = findViewById(R.id.talepSppiner);
        mconnText= findViewById(R.id.connText);
        mBulButton = findViewById(R.id.bulButton);
        mKayitButton = findViewById(R.id.kayitButton);
        mCikButton = findViewById(R.id.cikButton);
        mKayitButton.setVisibility(View.INVISIBLE);

        // *********************************************
        // Talep spinner tanımlaması *******************
        ArrayAdapter adapterTalep = ArrayAdapter.createFromResource(
                this,
                R.array.is_tipi,
                R.layout.color_spinner_layout
        );
        adapterTalep.setDropDownViewResource(R.layout.color_spinner_layout);
        mTalepSpinner.setAdapter(adapterTalep);
        // *********************************************
        // Sql baplantısını yapmak için stringlere koyar
        String ip = "192.168.1.151";
        String port = "1433";
        String Classes = "net.sourceforge.jtds.jdbc.Driver";
        String database = "TS_YENİ";
        String username = "sa";
        String password = "Fa020564";
        String url = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+database;
        // ************************************************************
        // Database bağlantısın yapılması
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url,username,password);
            mconnText.setText("BAĞLANDI");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            mconnText.setText("BULUNAMADI, İNTERNETİ KONTROL EDİN");
        } catch (SQLException e){
            mconnText.setText("VERİ TABANI BULUNAMADI");
        }
        //Bul butonuna basıldı
        mBulButton.setOnClickListener(new View.OnClickListener() {
            String araText;
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
            araText = mMakineNoGir.getText().toString();
            if(connection != null){
            Statement statement = null;
            try {
                // Seçilen makinenin ekranda gösterilmesi
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery( "Select * from [Faruk DENEME] WHERE [DOSYA NO] = '" + araText + "';" );
                if(resultSet.next() == false){
                    Toast.makeText(MainActivity.this,"KAYIT BULUNAMADI",Toast.LENGTH_LONG).show();
                    mMakNoText.setText("");
                    mBolumText.setText("");
                    mArizaText.setText("");
                    mMarkaText.setText("");
                    mModelText.setText("");
                    mSeriNoText.setText("");
                    mArizaVerenText.setText("");
                    mEmailText.setText("");
                    mKayitButton.setVisibility(View.INVISIBLE);
                    return;
                } else {
                    mMakNoText.setText(resultSet.getString(4)); //Dosya No
                    mBolumText.setText(resultSet.getString(5)); // Ünvan
                    mMarkaText.setText(resultSet.getString(8)); //Marka
                    mModelText.setText(resultSet.getString(9)); //Model
                    mSeriNoText.setText(resultSet.getString(12)); // Seri No
                    // Saat ve tarih format değiştirilip gösteriliyor.
                    Date bugun = new Date();
                    String lSaatGoster = new SimpleDateFormat( "HH:mm" ).format( bugun );
                    String lGoster = new SimpleDateFormat( "dd-MM-yyyy" ).format( bugun );
                    mTarihText.setText(lGoster);
                    mSaatText.setText(lSaatGoster);

                    mKayitButton.setVisibility(View.VISIBLE);
                }


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            }
            }

        }); // Bul Butonun sonu
        // Programdan çıkış işlemleri
        mCikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"ÇIKMAK İÇİN UZUN BASINIZ",Toast.LENGTH_LONG).show();
            }
        });
        // Çıkış için *******************************************************************
        mCikButton.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder cikis = new AlertDialog.Builder( MainActivity.this );
                cikis.setPositiveButton( "TAMAM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                } )
                        .setNegativeButton( "İPTAL",null )
                        .setCancelable( true )
                        .setMessage( "!! PROGRAMDAN ÇIKILACAKTIR!!" )
                        .show();
                return false;
            }
        } );
        // ***************************************************************************

    }

}