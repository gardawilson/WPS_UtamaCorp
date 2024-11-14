package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import java.text.DecimalFormat;
import android.view.Gravity;

import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
public class S4S extends AppCompatActivity {

    private EditText NoS4S;
    private EditText Date;
    private EditText Time;
    private EditText NoSTA;
    private Spinner SpinKayu;
    private Spinner SpinTelly;
    private Spinner SpinSPK;
    private Spinner SpinSPKAsal;
    private Spinner SpinProfile;
    private Spinner SpinFisik;
    private Spinner SpinGrade;
    private Spinner SpinMesin;
    private Spinner SpinSusun;
    private Calendar calendar;
    private RadioGroup radioGroup;
    private RadioButton radioButtonMesin;
    private RadioButton radioButtonBSusun;
    private Button BtnDataBaru;
    private Button BtnSimpan;
    private Button BtnBatal;
    private Button BtnHapusDetail;
    private boolean isDataBaruClicked = false;
    private CheckBox CBAfkir;
    private CheckBox CBLembur;
    private Button BtnInputDetail;
    private EditText DetailLebarS4S;
    private EditText DetailTebalS4S;
    private EditText DetailPanjangS4S;
    private EditText DetailPcsS4S;
    private static int currentNumber = 1;
    private Button BtnPrint;
    private TextView M3;
    private TextView JumlahPcs;
    private boolean isCBAfkir, isCBLembur;
    private Button BtnSearch;
    private int rowCount = 0;
    private TableLayout Tabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_s4_s);

        NoSTA = findViewById(R.id.NoSTA);
        NoS4S = findViewById(R.id.NoS4S);
        Date = findViewById(R.id.Date);
        Time = findViewById(R.id.Time);
        SpinKayu = findViewById(R.id.SpinKayu);
        SpinTelly = findViewById(R.id.SpinTelly);
        SpinSPK = findViewById(R.id.SpinSPK);
        SpinSPKAsal = findViewById(R.id.SpinSPKAsal);
        SpinProfile = findViewById(R.id.SpinProfile);
        SpinFisik = findViewById(R.id.SpinFisik);
        SpinGrade = findViewById(R.id.SpinGrade);
        calendar = Calendar.getInstance();
        SpinMesin = findViewById(R.id.SpinMesin);
        SpinSusun = findViewById(R.id.SpinSusun);
        radioButtonMesin = findViewById(R.id.radioButtonMesin);
        radioButtonBSusun = findViewById(R.id.radioButtonBSusun);
        BtnDataBaru = findViewById(R.id.BtnDataBaru);
        BtnSimpan = findViewById(R.id.BtnSimpan);
        BtnBatal = findViewById(R.id.BtnBatal);
        BtnHapusDetail = findViewById(R.id.BtnHapusDetail);
        CBLembur = findViewById(R.id.CBLembur);
        CBAfkir = findViewById(R.id.CBAfkir);
        BtnInputDetail = findViewById(R.id.BtnInputDetail);
        DetailPcsS4S = findViewById(R.id.DetailPcsS4S);
        DetailTebalS4S = findViewById(R.id.DetailTebalS4S);
        DetailPanjangS4S = findViewById(R.id.DetailPanjangS4S);
        DetailLebarS4S = findViewById(R.id.DetailLebarS4S);
        BtnPrint = findViewById(R.id.BtnPrint);
        M3 = findViewById(R.id.M3);
        JumlahPcs = findViewById(R.id.JumlahPcs);
        BtnSearch = findViewById(R.id.BtnSearch);
        Tabel = findViewById(R.id.Tabel);
        BtnPrint.setEnabled(false);

        radioButtonMesin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SpinMesin.setEnabled(true);
                    SpinSusun.setEnabled(false);
                }
            }
        });

        radioButtonBSusun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SpinSusun.setEnabled(true);
                    SpinMesin.setEnabled(false);
                }
            }
        });

        setCurrentDateTime();

        BtnDataBaru.setOnClickListener(v -> {
            if (!isDataBaruClicked) {
                resetSpinners();
                new LoadJenisKayuTask().execute();
                new LoadTellyTask().execute();
                new LoadSPKTask().execute();
                new LoadSPKAsalTask().execute();
                new LoadProfileTask().execute();
                new LoadFisikTask().execute();
                new LoadGradeTask().execute();
                new LoadMesinTask().execute();
                new LoadSusunTask().execute();

                isDataBaruClicked = true;
                setCurrentDateTime();
            } else {
                Toast.makeText(S4S.this, "Tombol Data Baru sudah diklik. Klik Simpan terlebih dahulu.", Toast.LENGTH_SHORT).show();
            }
            BtnSimpan.setEnabled(true);
            new SetAndSaveNoS4STask().execute();
            BtnPrint.setEnabled(false);
            BtnBatal.setEnabled(true);
            radioButtonMesin.setEnabled(true);
            radioButtonBSusun.setEnabled(true);
            setCurrentDateTime();
            clearTableData2();
            BtnDataBaru.setEnabled(false);
        });


        BtnSimpan.setOnClickListener(v -> {
            String noS4S = NoS4S.getText().toString();
            String dateCreate = Date.getText().toString();
            String time = Time.getText().toString();
            String noSTAsal = NoSTA.getText().toString();

            Telly selectedTelly = (Telly) SpinTelly.getSelectedItem();
            SPK selectedSPK = (SPK) SpinSPK.getSelectedItem();
            SPKAsal selectedSPKasal = (SPKAsal) SpinSPKAsal.getSelectedItem();
            Profile selectedProfile = (Profile) SpinProfile.getSelectedItem();
            Fisik selectedFisik = (Fisik) SpinFisik.getSelectedItem();
            Grade selectedGrade = (Grade) SpinGrade.getSelectedItem();
            JenisKayu selectedJenisKayu = (JenisKayu) SpinKayu.getSelectedItem();
            Mesin selectedMesin = (Mesin) SpinMesin.getSelectedItem();
            Susun selectedSusun = (Susun) SpinSusun.getSelectedItem();

            String idGrade = selectedGrade != null ? selectedGrade.getIdGrade() : null;
            String idTelly = selectedTelly != null ? selectedTelly.getIdTelly() : null;
            String noSPK = selectedSPK != null ? selectedSPK.getNoSPK() : null;
            String noSPKasal = selectedSPKasal != null ? selectedSPKasal.getNoSPKAsal() : null;
            String idProfile = selectedProfile != null ? selectedProfile.getIdFJProfile() : null;
            String idJenisKayu = selectedJenisKayu != null ? selectedJenisKayu.getIdJenisKayu() : null;
            String noProduksi = selectedMesin != null ? selectedMesin.getNoProduksi() : null;
            String noBongkarSusun = selectedSusun != null ? selectedSusun.getNoBongkarSusun() : null;
            int isReject = CBAfkir.isChecked() ? 1 : 0;
            int isLembur = CBLembur.isChecked() ? 1 : 0;

            if (noS4S.isEmpty() || dateCreate.isEmpty() || time.isEmpty() ||
                    NoSTA.getText().toString().isEmpty()||
                    selectedTelly == null ||
                    selectedSPK == null ||
                    selectedProfile == null ||
                    selectedFisik == null ||
                    selectedGrade == null ||
                    selectedJenisKayu == null ||
                    (!radioButtonMesin.isChecked() && !radioButtonBSusun.isChecked()) ||
                    (radioButtonMesin.isChecked() && selectedMesin == null) ||
                    (radioButtonBSusun.isChecked() && selectedSusun == null)) {

                Toast.makeText(S4S.this, "Pastikan semua field terisi dengan benar.", Toast.LENGTH_SHORT).show();
                return;
            }
            BtnDataBaru.setEnabled(true);
            BtnPrint.setEnabled(true);

            new UpdateDatabaseTask(
                    noS4S,
                    dateCreate,
                    time,
                    idTelly,
                    noSPK,
                    noSPKasal,
                    idGrade,
                    idJenisKayu,
                    idProfile,
                    isReject,
                    isLembur

            ).execute();
            new UpdateNoSTAsalTask(
                    noS4S,
                    noSTAsal
            ).execute();

            if (radioButtonMesin.isChecked() && SpinMesin.isEnabled() && noProduksi != null) {
                new SaveToDatabaseTask(noProduksi, noS4S).execute();
                for (int i = 0; i < temporaryDataListDetail.size(); i++) {
                    S4S.DataRow dataRow = temporaryDataListDetail.get(i);
                    saveDataDetailToDatabase(noS4S, i + 1, Double.parseDouble(dataRow.tebal), Double.parseDouble(dataRow.lebar),
                            Double.parseDouble(dataRow.panjang), Integer.parseInt(dataRow.pcs));
                }
            } else if (radioButtonBSusun.isChecked() && SpinSusun.isEnabled() && noBongkarSusun != null) {
                new SaveBongkarSusunTask(noBongkarSusun, noS4S).execute();
                for (int i = 0; i < temporaryDataListDetail.size(); i++) {
                    S4S.DataRow dataRow = temporaryDataListDetail.get(i);
                    saveDataDetailToDatabase(noS4S, i + 1, Double.parseDouble(dataRow.tebal), Double.parseDouble(dataRow.lebar),
                            Double.parseDouble(dataRow.panjang), Integer.parseInt(dataRow.pcs));
                }
            } else {
                Toast.makeText(S4S.this, "Pilih opsi yang valid untuk disimpan.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(S4S.this, "Data berhasil disimpan dan tampilan telah dikosongkan.", Toast.LENGTH_SHORT).show();

        });

        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noFJoin = NoS4S.getText().toString().trim();

                if (!noFJoin.isEmpty()) {

                }

                clearTableData2();
                Toast.makeText(S4S.this, "Tampilan telah dikosongkan.", Toast.LENGTH_SHORT).show();
            }
        });

        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noS4S = NoS4S.getText().toString();


                DetailLebarS4S.setText("");
                DetailPanjangS4S.setText("");
                DetailTebalS4S.setText("");
                DetailPcsS4S.setText("");
                NoS4S.setText("");
                NoSTA.setText("");

                if (!noS4S.isEmpty()) {
                    new LoadMesinTask2(noS4S).execute();
                    new LoadSusunTask2(noS4S).execute();
                    new LoadFisikTask2(noS4S).execute();
                    new LoadGradeTask2(noS4S).execute();
                    new LoadJenisKayuTask2(noS4S).execute();
                    new LoadTellyTask2(noS4S).execute();
                    new LoadSPKTask2(noS4S).execute();
                    new LoadSPKAsalTask2(noS4S).execute();
                    new LoadProfileTask2(noS4S).execute();
                    new SearchAllDataTask(noS4S).execute();

                    radioButtonMesin.setEnabled(true);
                    SpinSPK.setEnabled(true);
                    SpinSPKAsal.setEnabled(true);
                    radioButtonBSusun.setEnabled(true);
                } else {
                    Log.e("Input Error", "NoS4S is empty");
                    radioButtonMesin.setEnabled(false);
                    radioButtonBSusun.setEnabled(false);
                }

                BtnSimpan.setEnabled(false);
                BtnBatal.setEnabled(false);
                BtnPrint.setEnabled(true);
            }
        });

        SpinKayu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JenisKayu selectedJenisKayu = (JenisKayu) parent.getItemAtPosition(position);
                String idJenisKayu = selectedJenisKayu.getIdJenisKayu();
                new LoadGradeTask().execute(idJenisKayu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Date.setOnClickListener(v -> showDatePickerDialog());

        Time.setOnClickListener(v -> showTimePickerDialog());

        BtnInputDetail.setOnClickListener(v -> {
            String noS4S = NoS4S.getText().toString();

            if (!noS4S.isEmpty()) {
                addDataDetail(noS4S);
            } else {
                Toast.makeText(S4S.this, "NoS4S tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }

            jumlahpcs();
        });

        BtnHapusDetail.setOnClickListener(v -> {
            resetDetailData();
        });

//        BtnPrint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    String noS4S = NoS4S.getText() != null ? NoS4S.getText().toString() : "";
//                    String Kayu = SpinKayu.getSelectedItem() != null ? SpinKayu.getSelectedItem().toString() : "";
//                    String Grade = SpinGrade.getSelectedItem() != null ? SpinGrade.getSelectedItem().toString() : "";
//                    String Fisik = SpinFisik.getSelectedItem() != null ? SpinFisik.getSelectedItem().toString() : "";
//                    String Tanggal = Date.getText() != null ? Date.getText().toString() : "";
//                    String Waktu = Time.getText() != null ? Time.getText().toString() : "";
//                    String Telly = SpinTelly.getSelectedItem() != null ? SpinTelly.getSelectedItem().toString() : "";
//                    String Mesin = SpinMesin.getSelectedItem() != null ? SpinMesin.getSelectedItem().toString() : "";
//                    String noSPK = SpinSPK.getSelectedItem() != null ? SpinSPK.getSelectedItem().toString() : "";
//                    String noSPKasal = SpinSPKAsal.getSelectedItem() != null ? SpinSPKAsal.getSelectedItem().toString() : "";
//                    String jlh = JumlahPcs.getText() != null ? JumlahPcs.getText().toString() : "";
//                    String m3 = M3.getText() != null ? M3.getText().toString() : "";
//                    String Susun = SpinSusun.getSelectedItem() != null ? SpinSusun.getSelectedItem().toString() : "";
//
//                    Uri pdfUri = createPdf(noS4S, Kayu, Grade, Fisik, Tanggal, Waktu, Telly, Mesin, Susun, noSPK, noSPKasal, jlh, m3);
//
//                    if (pdfUri != null) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(pdfUri, "application/pdf");
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//                        intent.setPackage("com.mi.globalbrowser");
//
//                        try {
//                            startActivity(intent);
//                        } catch (ActivityNotFoundException e) {
//                            Toast.makeText(S4S.this, "Mi Browser not found. Please install Mi Browser or use another app to open the PDF.", Toast.LENGTH_LONG).show();
//
//                            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW);
//                            fallbackIntent.setDataAndType(pdfUri, "application/pdf");
//                            fallbackIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            startActivity(Intent.createChooser(fallbackIntent, "Open PDF with"));
//                        }
//                    } else {
//                        Toast.makeText(S4S.this, "Error creating PDF", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(S4S.this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(S4S.this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                //clearTableData();
//            }
//        });

    }

    @SuppressLint("NewApi")
    private Connection ConnectionClass() {
        Connection con = null;
        String ip = "192.168.10.100";
        String port = "1433";
        String username = "sa";
        String password = "Utama1234";
        String databasename = "WPS_Test";

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + databasename + ";User=" + username + ";password=" + password + ";";
            con = DriverManager.getConnection(connectionUrl);
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }

        return con;
    }


    private class SearchAllDataTask extends AsyncTask<String, Void, Boolean> {
        private String noS4S;

        public SearchAllDataTask(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d("SearchAllDataTask", "Searching for NoS4S: " + noS4S);
            Connection con = ConnectionClass();
            boolean isDataFound = false;

            if (con != null) {
                try {
                    String query = "SELECT h.DateCreate, h.Jam, " +
                            "d.Lebar, d.Panjang, d.Tebal, d.JmlhBatang, d.NoUrut, " +
                            "h.IsReject, h.IsLembur " +
                            "FROM dbo.S4S_h AS h " +
                            "INNER JOIN dbo.S4S_d AS d ON h.NoS4S = d.NoS4S " +
                            "WHERE h.NoS4S = ?";

                    Log.d("SearchAllDataTask", "Preparing statement: " + query);
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        final String dateCreate = rs.getString("DateCreate");
                        final int no = rs.getInt("NoUrut");
                        final String jam = rs.getString("Jam");
                        final int lebar = rs.getInt("Lebar");
                        final int panjang = rs.getInt("Panjang");
                        final int tebal = rs.getInt("Tebal");
                        final int jmlhBatang = rs.getInt("JmlhBatang");
                        final boolean isReject = rs.getInt("IsReject") == 1;
                        final boolean isLembur = rs.getInt("IsLembur") == 1;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NoS4S.setText(noS4S);
                                Date.setText(dateCreate != null ? dateCreate : "");
                                Time.setText(jam != null ? jam : "");
                                CBAfkir.setChecked(isReject);
                                CBLembur.setChecked(isLembur);

                                m3();
                                jumlahpcs();
                            }
                        });

                        isDataFound = true;
                    } else {
                        Log.e("SearchAllDataTask", "No data found for NoS4S: " + noS4S);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (SQLException e) {
                    Log.e("Database Error", "SQL Exception: " + e.getMessage());
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e1) {
                            Log.e("Connection Close Error", e1.getMessage());
                        }
                    }
                } catch (Exception e) {
                    Log.e("General Error", e.getMessage());
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e1) {
                            Log.e("Connection Close Error", e1.getMessage());
                        }
                    }
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }

            return isDataFound;
        }
    }


    //Fungsi untuk add Data Detail

    private static class DataRow {
        String tebal;
        String lebar;
        String panjang;
        String pcs;
        int rowId;
        private static int nextId = 1;

        DataRow(String tebal, String lebar, String panjang, String pcs) {
            this.tebal = tebal;
            this.lebar = lebar;
            this.panjang = panjang;
            this.pcs = pcs;
            this.rowId = nextId++;
        }
    }

    private List<DataRow> temporaryDataListDetail = new ArrayList<>();

    private void addDataDetail(String noS4S) {
        String tebal = DetailTebalS4S.getText().toString();
        String panjang = DetailPanjangS4S.getText().toString();
        String lebar = DetailLebarS4S.getText().toString();
        String pcs = DetailPcsS4S.getText().toString();

        if (tebal.isEmpty() || panjang.isEmpty() || lebar.isEmpty() || pcs.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Buat objek DataRow baru
            DataRow newDataRow = new DataRow(tebal, lebar, panjang, pcs);
            temporaryDataListDetail.add(newDataRow);

            // Buat baris tabel baru
            TableRow newRow = new TableRow(this);
            newRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            DecimalFormat df = new DecimalFormat("#,###.##");

            // Tambahkan kolom-kolom data dengan weight
            addTextViewToRowWithWeight(newRow, String.valueOf(++rowCount), 1f);
            addTextViewToRowWithWeight(newRow, df.format(Float.parseFloat(tebal)), 1f);
            addTextViewToRowWithWeight(newRow, df.format(Float.parseFloat(lebar)), 1f);
            addTextViewToRowWithWeight(newRow, df.format(Float.parseFloat(panjang)), 1f);
            addTextViewToRowWithWeight(newRow, df.format(Integer.parseInt(pcs)), 1f);

            // Buat dan tambahkan tombol hapus
            Button deleteButton = new Button(this);
            deleteButton.setText("Hapus");
            deleteButton.setTextSize(12);

            // Atur style tombol
            TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            buttonParams.setMargins(5, 5, 5, 5);
            deleteButton.setLayoutParams(buttonParams);
            deleteButton.setPadding(10, 5, 10, 5);
            deleteButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            deleteButton.setTextColor(Color.BLACK);

            // Set listener tombol hapus
            deleteButton.setOnClickListener(v -> {
                Tabel.removeView(newRow);
                temporaryDataListDetail.remove(newDataRow);
                updateRowNumbers();
                jumlahpcs();
            });

            newRow.addView(deleteButton);
            Tabel.addView(newRow);

            // Bersihkan field input
            DetailTebalS4S.setText("");
            DetailPanjangS4S.setText("");
            DetailLebarS4S.setText("");
            DetailPcsS4S.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    // Metode helper yang baru untuk menambahkan TextView dengan weight
    private void addTextViewToRowWithWeight(TableRow row, String text, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, weight);
        params.setMargins(5, 5, 5, 5);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(10, 10, 10, 10);
        row.addView(textView);
    }

    // Metode untuk memperbarui nomor baris setelah penghapusan
    private void updateRowNumbers() {
        for (int i = 1; i < Tabel.getChildCount(); i++) {
            TableRow row = (TableRow) Tabel.getChildAt(i);
            TextView numberView = (TextView) row.getChildAt(0);
            numberView.setText(String.valueOf(i));
        }
        rowCount = Tabel.getChildCount() - 1;
    }
    private void resetDetailData() {
        // Reset temporary list detail
        temporaryDataListDetail.clear();

        // Reset row counter
        rowCount = 0;

        // Reset tabel detail (hapus semua baris kecuali header)
        if (Tabel.getChildCount() > 1) {
            Tabel.removeViews(1, Tabel.getChildCount() - 1);
        }

        // Reset input fields
        if (DetailTebalS4S != null) {
            DetailTebalS4S.setText("");
        }
        if (DetailLebarS4S != null) {
            DetailLebarS4S.setText("");
        }
        if (DetailPanjangS4S != null) {
            DetailPanjangS4S.setText("");
        }
        if (DetailPcsS4S != null) {
            DetailPcsS4S.setText("");
        }
    }

    private void saveDataDetailToDatabase(String noS4S, int noUrut, double tebal, double lebar, double panjang, int pcs) {
        new S4S.SaveDataTaskDetail().execute(noS4S, String.valueOf(noUrut), String.valueOf(tebal), String.valueOf(lebar),
                String.valueOf(panjang), String.valueOf(pcs));
    }

    private class SaveDataTaskDetail extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String noS4S = params[0];
            String noUrut = params[1];
            String tebal = params[2];
            String lebar = params[3];
            String panjang = params[4];
            String pcs = params[5];

            try {
                Connection connection = ConnectionClass();
                if (connection != null) {
                    String query = "INSERT INTO dbo.S4S_d (NoS4S, NoUrut, Tebal, Lebar, Panjang, JmlhBatang) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, noS4S);
                    preparedStatement.setInt(2, Integer.parseInt(noUrut));
                    preparedStatement.setDouble(3, Double.parseDouble(tebal));
                    preparedStatement.setDouble(4, Double.parseDouble(lebar));
                    preparedStatement.setDouble(5, Double.parseDouble(panjang));
                    preparedStatement.setInt(6, Integer.parseInt(pcs));

                    int rowsAffected = preparedStatement.executeUpdate();
                    return rowsAffected > 0;
                } else {
                    Log.e("DB_CONNECTION", "Koneksi ke database gagal");
                }
            } catch (SQLException e) {
                Log.e("DB_ERROR", "SQL Exception: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d("DB_INSERT", "Data Detail berhasil disimpan");
            } else {
                Log.e("DB_INSERT", "Data gagal disimpan");
            }
        }
    }


    private void clearTableData2() {
        NoS4S.setText("");
        M3.setText("");
        JumlahPcs.setText("");
        CBAfkir.setChecked(false);
        CBLembur.setChecked(false);

        currentNumber = 1;
    }

    private void resetSpinners() {
        if (SpinKayu.getAdapter() != null) {
            SpinKayu.setSelection(0);
        }
        if (SpinMesin.getAdapter() != null) {
            SpinMesin.setSelection(0);
        }
        if (SpinSusun.getAdapter() != null) {
            SpinSusun.setSelection(0);
        }
        if (SpinTelly.getAdapter() != null) {
            SpinTelly.setSelection(0);
        }
        if (SpinGrade.getAdapter() != null) {
            SpinGrade.setSelection(0);
        }
        if (SpinProfile.getAdapter() != null) {
            SpinProfile.setSelection(0);
        }
        if (SpinFisik.getAdapter() != null) {
            SpinFisik.setSelection(0);
        }
        if (SpinSPK.getAdapter() != null) {
            SpinSPK.setSelection(0);
        }
        if (SpinSPKAsal.getAdapter() != null) {
            SpinSPKAsal.setSelection(0);
        }

        BtnDataBaru.setEnabled(true);
        isDataBaruClicked = true;
    }

    private void m3() {

    }


    private void jumlahpcs() {
        TableLayout table = findViewById(R.id.Tabel);
        int childCount = table.getChildCount();

        int totalPcs = 0;

        for (int i = 1; i < childCount; i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            TextView pcsTextView = (TextView) row.getChildAt(4); // Indeks pcs

            String pcsString = pcsTextView.getText().toString().replace(",", "");
            int pcs = Integer.parseInt(pcsString);
            totalPcs += pcs;
        }

        JumlahPcs.setText(String.valueOf(totalPcs));
    }


    private void setCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        Date.setText(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        Time.setText(currentTime);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(S4S.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                Date.setText(selectedDate);
                new LoadMesinTask().execute(selectedDate);
                new LoadSusunTask().execute(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(S4S.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                SimpleDateFormat timeFormat = new SimpleDateFormat(" HH:mm:ss", Locale.getDefault());
                String updatedTime = timeFormat.format(calendar.getTime());
                Time.setText(updatedTime);
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private Uri createPdf(String noS4S, String Kayu, String Grade, String Fisik, String Tanggal, String Waktu, String Telly, String Mesin, String Susun, String noSPK, String noSPKasal, String tebal, String lebar, String panjang, String pcs, String jlh, String m3) throws IOException {
        Uri pdfUri = null;
        ContentResolver resolver = getContentResolver();
        String fileName = "myPDF.pdf";
        String relativePath = Environment.DIRECTORY_DOWNLOADS;

        deleteExistingPdf(fileName, relativePath);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            OutputStream outputStream = resolver.openOutputStream(uri);
            if (outputStream != null) {
                try {
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdfDocument = new PdfDocument(writer);
                    Document document = new Document(pdfDocument);

                    pdfDocument.setDefaultPageSize(PageSize.A6);
                    document.setMargins(0, 5, 0, 5);

                    String mesinAtauSusun;
                    if (Mesin.isEmpty()) {
                        mesinAtauSusun = Susun.isEmpty() ? "Mesin & Susun tidak tersedia" : "B.Susun : " + Susun;
                    } else {
                        mesinAtauSusun = "Mesin : " + Mesin;
                    }

                    Paragraph judul = new Paragraph("LABEL S4S\n").setFontSize(8).setTextAlignment(TextAlignment.CENTER);
                    Paragraph isi = new Paragraph("").setFontSize(7)
                            .add("No S4S  : " + noS4S + "                                              ")
                            .add("Tanggal : " + Tanggal + " " + Waktu + "\n")
                            .add("Kayu    : " + Kayu + "                                                    ")
                            .add("Telly   : " + Telly + "\n")
                            .add("Grade   : " + Grade + "                                          ")
                            .add(mesinAtauSusun + "\n")
                            .add("Fisik   : " + Fisik + "                                                           ")
                            .add("No SPK  : " + noSPK + "\n");

                    float[] width = {50f, 50f, 50f, 50f};
                    Table table = new Table(width);
                    table.setHorizontalAlignment(HorizontalAlignment.CENTER).setFontSize(7);
                    table.addCell(new Cell().add(new Paragraph("Tebal (mm)").setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph("Lebar (mm)").setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph("Panjang (mm)").setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph("Pcs").setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(tebal).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(lebar).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(panjang).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(pcs).setTextAlignment(TextAlignment.CENTER)));

                    BarcodeQRCode qrCode = new BarcodeQRCode(noS4S);
                    PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
                    Image qrCodeImage = new Image(qrCodeObject).setWidth(60).setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(0).setMarginTop(0);

                    Paragraph pcsm3 = new Paragraph("").setFontSize(7).setTextAlignment(TextAlignment.RIGHT).setMarginRight(67)
                            .add("Jmlh Pcs = " + jlh + "\t" + "\n")
                            .add("m3 = " + m3 + "\n");

                    Paragraph garis = new Paragraph("--------------------------------------------------------------").setTextAlignment(TextAlignment.CENTER);
                    Paragraph output = new Paragraph("Output").setTextAlignment(TextAlignment.CENTER).setFontSize(6).setMarginTop(0).setMarginBottom(0);
                    Paragraph input = new Paragraph("Input").setTextAlignment(TextAlignment.CENTER).setFontSize(6).setMarginTop(0).setMarginBottom(0);
                    Paragraph s4s = new Paragraph(noS4S).setTextAlignment(TextAlignment.CENTER).setFontSize(6).setMarginTop(0).setMarginBottom(0);
                    document.add(judul);
                    document.add(isi);
                    document.add(table);
                    document.add(pcsm3);
                    document.add(output);
                    document.add(qrCodeImage);
                    document.add(s4s);
                    document.add(garis);
                    document.add(judul);
                    document.add(isi);
                    document.add(table);
                    document.add(pcsm3);
                    document.add(input);
                    document.add(qrCodeImage);
                    document.add(s4s);

                    document.close();

                    pdfUri = uri;

                    Toast.makeText(this, "PDF Created at " + uri.getPath(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("Failed to create PDF", e);
                } finally {
                    outputStream.close();
                }
            }
        }

        return pdfUri;
    }

    private void deleteExistingPdf(String fileName, String relativePath) {
        Uri uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " + MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = {fileName, relativePath};

        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            Uri existingUri = ContentUris.withAppendedId(uri, id);
            getContentResolver().delete(existingUri, null, null);
            Log.d("Delete PDF", "Old PDF deleted: " + existingUri.toString());
        } else {
            Log.d("Delete PDF", "No existing PDF found");
        }

        if (cursor != null) {
            cursor.close();
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            if (file.delete()) {
                Log.d("Delete PDF", "File deleted from file system: " + file.getPath());
            } else {
                Log.d("Delete PDF", "Failed to delete file from file system: " + file.getPath());
            }
        } else {
            Log.d("Delete PDF", "File not found in file system: " + file.getPath());
        }
    }



    private String getIdJenisKayu(String namaJenisKayu) {
        if (namaJenisKayu != null) {
            return "IdJenisKayu";
        }
        return null;
    }

    private class SaveBongkarSusunTask extends AsyncTask<Void, Void, Boolean> {
        private String noBongkarSusun;
        private String noS4S;

        public SaveBongkarSusunTask(String noBongkarSusun, String noS4S) {
            this.noBongkarSusun = noBongkarSusun;
            this.noS4S = noS4S;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection con = ConnectionClass();

            if (con != null) {
                try {
                    String query = "INSERT INTO dbo.BongkarSusunOutputS4S (NoS4S, NoBongkarSusun) VALUES (?, ?)";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);
                    ps.setString(2, noBongkarSusun);
                    ps.executeUpdate();
                    ps.close();
                    con.close();
                    return true;
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                    return false;
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(S4S.this, "Data berhasil disimpan.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SaveToDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private String noProduksi, noS4S;

        public SaveToDatabaseTask(String noProduksi, String noS4S) {
            this.noProduksi = noProduksi;
            this.noS4S = noS4S;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "INSERT INTO dbo.S4SProduksiOutput (NoProduksi, NoS4S) VALUES (?, ?)";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noProduksi);
                    ps.setString(2, noS4S);

                    int rowsInserted = ps.executeUpdate();
                    ps.close();
                    con.close();
                    return rowsInserted > 0;
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                    return false;
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(S4S.this, "Data berhasil disimpan ke database.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(S4S.this, "Gagal menyimpan data ke database.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class UpdateDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private String noS4S, dateCreate, time, idTelly, noSPK, noSPKasal, idGrade, idJenisKayu, idFJProfile;
        private int isReject, isLembur, noSTAsal;

        public UpdateDatabaseTask(String noS4S, String dateCreate, String time, String idTelly, String noSPK, String noSPKasal,
                                  String idGrade, String idJenisKayu, String idFJProfile,
                                  int isReject, int isLembur) {
            this.noS4S = noS4S;
            this.dateCreate = dateCreate;
            this.time = time;
            this.idTelly = idTelly;
            this.noSPK = noSPK;
            this.noSPKasal = noSPKasal;
            this.idGrade = idGrade;
            this.idJenisKayu = idJenisKayu;
            this.idFJProfile = idFJProfile;
            this.isReject = isReject;
            this.isLembur = isLembur;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "UPDATE dbo.S4S_h SET DateCreate = ?, Jam = ?, IdOrgTelly = ?, NoSPK = ?, NoSPKAsal = ?, IdGrade = ?, " +
                            "IdFJProfile = ?, IdFisik = 4, IdJenisKayu = ?, IdWarehouse = 4, IsReject = ?, IsLembur = ? WHERE NoS4S = ?";

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, dateCreate);
                    ps.setString(2, time);
                    ps.setString(3, idTelly);
                    ps.setString(4, noSPK);
                    ps.setString(5, noSPKasal);
                    ps.setString(6, idGrade);
                    ps.setString(7, idFJProfile);
                    ps.setString(8, idJenisKayu);
                    ps.setInt(9, isReject);
                    ps.setInt(10, isLembur);
                    ps.setString(11, noS4S);

                    int rowsUpdated = ps.executeUpdate();
                    ps.close();
                    con.close();
                    return rowsUpdated > 0;
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                    return false;
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
                return false;
            }
        }
    }

    private class UpdateNoSTAsalTask extends AsyncTask<Void, Void, Boolean> {
        private String noS4S;
        private String noSTAsal;
        private String errorMessage = null;

        public UpdateNoSTAsalTask(String noS4S, String noSTAsal) {
            this.noS4S = noS4S;
            this.noSTAsal = noSTAsal;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    Log.d("UpdateNoSTAsalTask", "Checking and updating NoSTAsal for NoS4S: " + noS4S);

                    String checkQuery = "SELECT NoSTAsal FROM dbo.S4S_h WHERE NoSTAsal = ?";
                    PreparedStatement checkPs = con.prepareStatement(checkQuery);
                    checkPs.setString(1, noSTAsal);
                    ResultSet rs = checkPs.executeQuery();

                    if (!rs.next()) {
                        errorMessage = "NoSTAsal tidak ditemukan di tabel ST_h.";
                        Log.e("UpdateNoSTAsalTask", errorMessage);
                        return false;
                    }

                    String selectQuery = "SELECT NoSTAsal FROM dbo.S4S_h WHERE NoS4S = ?";
                    PreparedStatement selectPs = con.prepareStatement(selectQuery);
                    selectPs.setString(1, noS4S);
                    ResultSet selectRs = selectPs.executeQuery();

                    if (selectRs.next()) {
                        String currentNoSTAsal = selectRs.getString("NoSTAsal");
                        if (currentNoSTAsal == null) {
                            String updateQuery = "UPDATE dbo.S4S_h SET NoSTAsal = ? WHERE NoS4S = ?";
                            PreparedStatement updatePs = con.prepareStatement(updateQuery);
                            updatePs.setString(1, noSTAsal);
                            updatePs.setString(2, noS4S);

                            int rowsUpdated = updatePs.executeUpdate();
                            updatePs.close();
                            Log.d("UpdateNoSTAsalTask", "Rows updated: " + rowsUpdated);
                            return rowsUpdated > 0;
                        } else {
                            errorMessage = "NoSTAsal sudah memiliki nilai: " + currentNoSTAsal;
                            Log.e("UpdateNoSTAsalTask", errorMessage);
                            return false;
                        }
                    } else {
                        errorMessage = "NoS4S tidak ditemukan di tabel S4S_h.";
                        Log.e("UpdateNoSTAsalTask", errorMessage);
                        return false;
                    }
                } catch (SQLException e) {
                    errorMessage = "SQL Error: " + e.getMessage();
                    Log.e("Database Error", errorMessage);
                    return false;
                } finally {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        Log.e("Connection Close Error", e.getMessage());
                    }
                }
            } else {
                errorMessage = "Koneksi ke database gagal.";
                Log.e("Connection Error", errorMessage);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(S4S.this, "NoSTAsal berhasil diperbarui.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(S4S.this, "Gagal memperbarui NoSTAsal: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SetAndSaveNoS4STask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Connection con = ConnectionClass();
            String newNoS4S = null;
            if (con != null) {
                try {
                    String query = "SELECT MAX(NoS4S) FROM dbo.S4S_h";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String lastNoS4S = rs.getString(1);

                        if (lastNoS4S != null && lastNoS4S.startsWith("R.")) {
                            String numericPart = lastNoS4S.substring(2);
                            int numericValue = Integer.parseInt(numericPart);
                            int newNumericValue = numericValue + 1;

                            newNoS4S = "R." + String.format("%06d", newNumericValue);
                        }
                    }

                    rs.close();
                    ps.close();

                    if (newNoS4S != null) {
                        String insertQuery = "INSERT INTO dbo.S4S_h (NoS4S) VALUES (?)";
                        PreparedStatement insertPs = con.prepareStatement(insertQuery);
                        insertPs.setString(1, newNoS4S);
                        insertPs.executeUpdate();
                        insertPs.close();
                    }

                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return newNoS4S;
        }

        @Override
        protected void onPostExecute(String newNoS4S) {
            if (newNoS4S != null) {
                NoS4S.setText(newNoS4S);
                Toast.makeText(S4S.this, "NoS4S berhasil diatur dan disimpan.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Error", "Failed to set or save NoS4S.");
                Toast.makeText(S4S.this, "Gagal mengatur atau menyimpan NoS4S.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class LoadJenisKayuTask extends AsyncTask<Void, Void, List<JenisKayu>> {
        @Override
        protected List<JenisKayu> doInBackground(Void... voids) {
            List<JenisKayu> jenisKayuList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "SELECT IdJenisKayu, Jenis FROM dbo.MstJenisKayu WHERE enable = 1";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String idJenisKayu = rs.getString("IdJenisKayu");
                        String namaJenisKayu = rs.getString("Jenis");

                        JenisKayu jenisKayu = new JenisKayu(idJenisKayu, namaJenisKayu);
                        jenisKayuList.add(jenisKayu);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return jenisKayuList;
        }

        @Override
        protected void onPostExecute(List<JenisKayu> jenisKayuList) {
            if (!jenisKayuList.isEmpty()) {
                ArrayAdapter<JenisKayu> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, jenisKayuList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinKayu.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load jenis kayu.");
            }
        }
    }
    public class LoadJenisKayuTask2 extends AsyncTask<String, Void, List<JenisKayu>> {
        private String noS4S;

        public LoadJenisKayuTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<JenisKayu> doInBackground(String... params) {
            List<JenisKayu> jenisKayuList = new ArrayList<>();
            Connection con = ConnectionClass();

            if (con != null) {
                try {
                    String query = "SELECT j.IdJenisKayu, j.Jenis " +
                            "FROM dbo.MstJenisKayu AS j " +
                            "INNER JOIN dbo.S4S_h AS h ON h.IdJenisKayu = j.IdJenisKayu " +
                            "WHERE h.NoS4S = ? AND j.enable = 1";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String idJenisKayu = rs.getString("IdJenisKayu");
                        String namaJenisKayu = rs.getString("Jenis");

                        JenisKayu jenisKayu = new JenisKayu(idJenisKayu, namaJenisKayu);
                        jenisKayuList.add(jenisKayu);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return jenisKayuList;
        }

        @Override
        protected void onPostExecute(List<JenisKayu> jenisKayuList) {
            if (!jenisKayuList.isEmpty()) {
                ArrayAdapter<JenisKayu> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, jenisKayuList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinKayu.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load jenis kayu.");
            }
        }
    }


    private class LoadTellyTask extends AsyncTask<Void, Void, List<Telly>> {
        @Override
        protected List<Telly> doInBackground(Void... voids) {
            List<Telly> tellyList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "SELECT IdOrgTelly, NamaOrgTelly FROM dbo.MstOrgTelly WHERE enable = 1";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String idOrgTelly = rs.getString("IdOrgTelly");
                        String namaOrgTelly = rs.getString("NamaOrgTelly");

                        Telly telly = new Telly(idOrgTelly, namaOrgTelly);
                        tellyList.add(telly);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }

            return tellyList;
        }

        @Override
        protected void onPostExecute(List<Telly> tellyList) {
            if (!tellyList.isEmpty()) {
                ArrayAdapter<Telly> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, tellyList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                SpinTelly.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load telly data.");
            }
        }
    }


    private class LoadTellyTask2 extends AsyncTask<String, Void, List<Telly>> {
        private String noS4S;

        public LoadTellyTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<Telly> doInBackground(String... params) {
            List<Telly> tellyList = new ArrayList<>();
            Connection con = ConnectionClass();

            if (con != null) {
                try {
                    String query = "SELECT t.IdOrgTelly, t.NamaOrgTelly " +
                            "FROM dbo.MstOrgTelly AS t " +
                            "INNER JOIN dbo.S4S_h AS h ON h.IdOrgTelly = t.IdOrgTelly " +
                            "WHERE h.NoS4S = ? AND t.enable = 1";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String idOrgTelly = rs.getString("IdOrgTelly");
                        String namaOrgTelly = rs.getString("NamaOrgTelly");

                        Telly telly = new Telly(idOrgTelly, namaOrgTelly);
                        tellyList.add(telly);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return tellyList;
        }

        @Override
        protected void onPostExecute(List<Telly> tellyList) {
            if (!tellyList.isEmpty()) {
                ArrayAdapter<Telly> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, tellyList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinTelly.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load telly data.");
            }
        }
    }


    private class LoadSPKTask extends AsyncTask<Void, Void, List<SPK>> {
        @Override
        protected List<SPK> doInBackground(Void... voids) {
            List<SPK> spkList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "SELECT NoSPK FROM dbo.MstSPK_h WHERE enable = 1";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String noSPK = rs.getString("NoSPK");

                        SPK spk = new SPK(noSPK);
                        spkList.add(spk);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return spkList;
        }

        @Override
        protected void onPostExecute(List<SPK> spkList) {
            if (!spkList.isEmpty()) {
                ArrayAdapter<SPK> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, spkList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinSPK.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load SPK data.");
            }
        }
    }

    private class LoadSPKAsalTask extends AsyncTask<Void, Void, List<SPKAsal>> {
        @Override
        protected List<SPKAsal> doInBackground(Void... voids) {
            List<SPKAsal> spkAsalList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "SELECT NoSPK FROM dbo.MstSPK_h WHERE enable = 1";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String noSPKasal = rs.getString("NoSPK");
                        SPKAsal spkAsal = new SPKAsal(noSPKasal);
                        spkAsalList.add(spkAsal);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return spkAsalList;
        }

        @Override
        protected void onPostExecute(List<SPKAsal> spkAsalList) {
            if (!spkAsalList.isEmpty()) {
                ArrayAdapter<SPKAsal> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, spkAsalList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinSPKAsal.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load SPK data.");
            }
        }
    }

    private class LoadSPKTask2 extends AsyncTask<Void, Void, List<SPK>> {
        private String noS4S;

        // Constructor to accept NoS4S
        public LoadSPKTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<SPK> doInBackground(Void... params) {
            List<SPK> spkList = new ArrayList<>();
            Connection con = ConnectionClass(); // Ensure this method establishes a database connection

            if (con != null) {
                try {
                    String query = "SELECT NoSPK FROM dbo.S4S_h WHERE NoS4S = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S); // Set the noS4S parameter

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String noSPK = rs.getString("NoSPK");

                        SPK spk = new SPK(noSPK); // Assuming SPK class has a constructor that accepts noSPK
                        spkList.add(spk);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return spkList;
        }

        @Override
        protected void onPostExecute(List<SPK> spkList) {
            if (!spkList.isEmpty()) {
                ArrayAdapter<SPK> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, spkList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinSPK.setAdapter(adapter);

                SpinSPK.setEnabled(true);

            } else {
                Log.e("Error", "No SPK data found for the provided NoS4S.");
                SpinSPK.setAdapter(null);
                SpinSPK.setEnabled(false);

                Toast.makeText(S4S.this, "Tidak ada data SPK yang ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadSPKAsalTask2 extends AsyncTask<Void, Void, List<SPKAsal>> {
        private String noS4S;

        // Constructor to accept NoS4S
        public LoadSPKAsalTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<SPKAsal> doInBackground(Void... params) {
            List<SPKAsal> spkAsalList = new ArrayList<>();
            Connection con = ConnectionClass(); // Ensure this method establishes a database connection

            if (con != null) {
                try {
                    String query = "SELECT NoSPK FROM dbo.S4S_h WHERE NoS4S = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S); // Set the noS4S parameter

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String noSPKasal = rs.getString("NoSPK");

                        SPKAsal spkAsal = new SPKAsal (noSPKasal); // Assuming SPK class has a constructor that accepts noSPK
                        spkAsalList.add(spkAsal);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return spkAsalList;
        }

        @Override
        protected void onPostExecute(List<SPKAsal> spkAsalList) {
            if (!spkAsalList.isEmpty()) {
                ArrayAdapter<SPKAsal> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, spkAsalList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinSPKAsal.setAdapter(adapter);

                SpinSPKAsal.setEnabled(true);

            } else {
                Log.e("Error", "No SPK data found for the provided NoS4S.");
                SpinSPKAsal.setAdapter(null);
                SpinSPKAsal.setEnabled(false);

                Toast.makeText(S4S.this, "Tidak ada data SPK yang ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class LoadProfileTask extends AsyncTask<Void, Void, List<Profile>> {
        @Override
        protected List<Profile> doInBackground(Void... voids) {
            List<Profile> profileList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "SELECT Profile, IdFJProfile FROM dbo.MstFJProfile";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String namaProfile = rs.getString("Profile");
                        String idFJProfile = rs.getString("IdFJProfile");
                        Profile profileObj = new Profile(namaProfile, idFJProfile);
                        profileList.add(profileObj);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return profileList;
        }

        @Override
        protected void onPostExecute(List<Profile> profileList) {
            if (!profileList.isEmpty()) {
                ArrayAdapter<Profile> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, profileList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinProfile.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load profile data.");
            }
        }
    }

    private class LoadProfileTask2 extends AsyncTask<String, Void, List<Profile>> {
        private String noS4S;

        public LoadProfileTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<Profile> doInBackground(String... voids) {
            List<Profile> profileList = new ArrayList<>();
            Connection con = ConnectionClass(); // Assumes this method exists to establish a DB connection

            if (con != null) {
                try {
                    String query = "SELECT p.Profile, p.IdFJProfile " +
                            "FROM dbo.MstFJProfile AS p " +
                            "INNER JOIN dbo.S4S_h AS h ON h.IdFJProfile = p.IdFJProfile " +
                            "WHERE h.NoS4S = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String namaProfile = rs.getString("Profile");
                        String idFJProfile = rs.getString("IdFJProfile");
                        Profile profileObj = new Profile(namaProfile, idFJProfile);
                        profileList.add(profileObj);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return profileList;
        }

        @Override
        protected void onPostExecute(List<Profile> profileList) {
            if (!profileList.isEmpty()) {
                ArrayAdapter<Profile> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, profileList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinProfile.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load profile data.");
            }
        }
    }

    private class LoadFisikTask extends AsyncTask<Void, Void, List<Fisik>> {
        @Override
        protected List<Fisik> doInBackground(Void... voids) {
            List<Fisik> fisikList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "SELECT NamaWarehouse FROM dbo.MstWarehouse WHERE IdWarehouse = 4";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String namaWarehouse = rs.getString("NamaWarehouse");

                        Fisik fisik = new Fisik(namaWarehouse);
                        fisikList.add(fisik);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return fisikList;
        }

        @Override
        protected void onPostExecute(List<Fisik> fisikList) {
            if (!fisikList.isEmpty()) {
                ArrayAdapter<Fisik> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, fisikList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinFisik.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load fisik data.");
            }
        }
    }

    private class LoadFisikTask2 extends AsyncTask<String, Void, List<Fisik>> {
        private String noS4S;

        public LoadFisikTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<Fisik> doInBackground(String... params) {
            List<Fisik> fisikList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String query = "SELECT mw.NamaWarehouse " +
                            "FROM dbo.MstWarehouse mw " +
                            "INNER JOIN dbo.S4S_h s4s ON mw.IdWarehouse = s4s.IdWarehouse " +
                            "WHERE s4s.noS4S = ?";

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String namaWarehouse = rs.getString("NamaWarehouse");
                        Fisik fisik = new Fisik(namaWarehouse);
                        fisikList.add(fisik);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", "Error during query execution: " + e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }

            return fisikList;
        }

        @Override
        protected void onPostExecute(List<Fisik> fisikList) {
            if (!fisikList.isEmpty()) {
                ArrayAdapter<Fisik> adapter = new ArrayAdapter<>(S4S.this,
                        android.R.layout.simple_spinner_item, fisikList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinFisik.setAdapter(adapter);
            } else {
                Log.e("Error", "No warehouse found.");
                ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(S4S.this,
                        android.R.layout.simple_spinner_item, new String[]{"Tidak ada Fisik"});
                emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinFisik.setAdapter(emptyAdapter);
            }
        }
    }


    private class LoadGradeTask extends AsyncTask<String, Void, List<Grade>> {
        @Override
        protected List<Grade> doInBackground(String... params) {
            List<Grade> gradeList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String idJenisKayuStr = params[0];
                    int idJenisKayu;

                    try {
                        idJenisKayu = Integer.parseInt(idJenisKayuStr);
                    } catch (NumberFormatException e) {
                        Log.e("Conversion Error", "IdJenisKayu should be an integer: " + idJenisKayuStr);
                        return gradeList;
                    }

                    String category = "S4S";

                    String query = "SELECT DISTINCT a.IdGrade, a.NamaGrade " +
                            "FROM MstGrade a " +
                            "INNER JOIN MstGrade_d b ON a.IdGrade = b.IdGrade " +
                            "WHERE a.Enable = 1 AND b.IdJenisKayu = ? AND b.Category = ?";

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, idJenisKayu);
                    ps.setString(2, category);

                    Log.d("LoadGradeTask", "Executing query: " + query + " with IdJenisKayu: " + idJenisKayu);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String idGrade = rs.getString("IdGrade");
                        String namaGrade = rs.getString("NamaGrade");

                        if (idGrade != null && namaGrade != null) {
                            Log.d("LoadGradeTask", "Fetched Grade: IdGrade = " + idGrade + ", NamaGrade = " + namaGrade);
                            Grade gradeObj = new Grade(idGrade, namaGrade);
                            gradeList.add(gradeObj);
                        }
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return gradeList;
        }

        @Override
        protected void onPostExecute(List<Grade> gradeList) {
            if (!gradeList.isEmpty()) {
                ArrayAdapter<Grade> adapter = new ArrayAdapter<>(S4S.this,
                        android.R.layout.simple_spinner_item, gradeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinGrade.setAdapter(adapter);
            } else {
                Log.e("Error", "Tidak ada grade");
                ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(S4S.this,
                        android.R.layout.simple_spinner_item, new String[]{"Pilih Menu"});
                emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinGrade.setAdapter(emptyAdapter);
            }
        }
    }

    private class LoadGradeTask2 extends AsyncTask<String, Void, List<Grade>> {
        private String noS4S;

        public LoadGradeTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<Grade> doInBackground(String... params) {
            List<Grade> gradeList = new ArrayList<>();
            Connection con = ConnectionClass();
            if (con != null) {
                try {
                    String idJenisKayuStr = params[0];
                    int idJenisKayu;

                    try {
                        idJenisKayu = Integer.parseInt(idJenisKayuStr);
                    } catch (NumberFormatException e) {
                        Log.e("Conversion Error", "IdJenisKayu should be an integer: " + idJenisKayuStr);
                        return gradeList;
                    }

                    String category = "S4S";

                    String query = "SELECT DISTINCT a.IdGrade, a.NamaGrade " +
                            "FROM MstGrade a " +
                            "INNER JOIN MstGrade_d b ON a.IdGrade = b.IdGrade " +
                            "WHERE a.Enable = 1 AND b.IdJenisKayu = ? AND b.Category = ? AND b.NoS4S = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, idJenisKayu);
                    ps.setString(2, category);
                    ps.setString(3, noS4S);
                    Log.d("LoadGradeTask", "Executing query: " + query + " with IdJenisKayu: " + idJenisKayu);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String idGrade = rs.getString("IdGrade");
                        String namaGrade = rs.getString("NamaGrade");

                        if (idGrade != null && namaGrade != null) {
                            Log.d("LoadGradeTask", "Fetched Grade: IdGrade = " + idGrade + ", NamaGrade = " + namaGrade);
                            Grade gradeObj = new Grade(idGrade, namaGrade);
                            gradeList.add(gradeObj);
                        }
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return gradeList;
        }

        @Override
        protected void onPostExecute(List<Grade> gradeList) {
            if (!gradeList.isEmpty()) {
                ArrayAdapter<Grade> adapter = new ArrayAdapter<>(S4S.this,
                        android.R.layout.simple_spinner_item, gradeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinGrade.setAdapter(adapter);
            } else {
                Log.e("Error", "Tidak ada grade");
                ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(S4S.this,
                        android.R.layout.simple_spinner_item, new String[]{"Tidak ada grade"});
                emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinGrade.setAdapter(emptyAdapter);
            }
        }
    }


    private class LoadMesinTask extends AsyncTask<String, Void, List<Mesin>> {
        @Override
        protected List<Mesin> doInBackground(String... params) {
            List<Mesin> mesinList = new ArrayList<>();
            Connection con = ConnectionClass();

            if (con != null) {
                try {
                    String selectedDate = params[0];

                    String query = "SELECT a.IdMesin, b.NamaMesin, a.NoProduksi FROM dbo.S4SProduksi_h a " +
                            "INNER JOIN dbo.MstMesin b ON a.IdMesin = b.IdMesin WHERE Tanggal = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, selectedDate);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String idMesin = rs.getString("IdMesin");
                        String nomorProduksi = rs.getString("NoProduksi");
                        String namaMesin = rs.getString("NamaMesin");

                        Mesin mesin = new Mesin(nomorProduksi, namaMesin);
                        mesinList.add(mesin);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return mesinList;
        }

        @Override
        protected void onPostExecute(List<Mesin> mesinList) {
            if (!mesinList.isEmpty()) {
                ArrayAdapter<Mesin> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, mesinList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinMesin.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load mesin data.");
            }
        }
    }

    private class LoadMesinTask2 extends AsyncTask<Void, Void, List<Mesin>> {
        private String noS4S;

        public LoadMesinTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<Mesin> doInBackground(Void... params) {
            List<Mesin> mesinList = new ArrayList<>();
            Connection con = ConnectionClass();

            if (con != null) {
                try {
                    String query = "SELECT b.NoProduksi, d.NamaMesin FROM S4S_h a " +
                            "INNER JOIN S4SProduksiOutput b ON b.NoS4S = a.NoS4S " +
                            "INNER JOIN S4SProduksi_h c ON c.NoProduksi = b.NoProduksi " +
                            "INNER JOIN MstMesin d ON d.IdMesin = c.IdMesin " +
                            "WHERE a.NoS4S = ?";

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String nomorProduksi = rs.getString("NoProduksi");
                        String namaMesin = rs.getString("NamaMesin");

                        Mesin mesin = new Mesin(nomorProduksi, namaMesin);
                        mesinList.add(mesin);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return mesinList;
        }

        @Override
        protected void onPostExecute(List<Mesin> mesinList) {
            if (!mesinList.isEmpty()) {
                ArrayAdapter<Mesin> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, mesinList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinMesin.setAdapter(adapter);

                radioButtonMesin.setEnabled(true);
                radioButtonBSusun.setEnabled(false);
            } else {
                Log.e("Error", "Failed to load mesin data.");
                radioButtonMesin.setEnabled(false);
                radioButtonBSusun.setEnabled(false);

                Toast.makeText(S4S.this, "Tidak ada data mesin yang ditemukan.", Toast.LENGTH_SHORT).show();
                SpinMesin.setAdapter(null);
            }
        }
    }


    private class LoadSusunTask extends AsyncTask<String, Void, List<Susun>> {
        @Override
        protected List<Susun> doInBackground(String... params) {
            List<Susun> susunList = new ArrayList<>();
            Connection con = ConnectionClass();

            if (con != null) {
                try {
                    String selectedDate = params[0];

                    String query = "SELECT NoBongkarSusun FROM dbo.BongkarSusun_h WHERE Tanggal = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, selectedDate);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String nomorBongkarSusun = rs.getString("NoBongkarSusun");

                        Susun susun = new Susun(nomorBongkarSusun);
                        susunList.add(susun);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return susunList;
        }

        @Override
        protected void onPostExecute(List<Susun> susunList) {
            if (!susunList.isEmpty()) {
                ArrayAdapter<Susun> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, susunList);
                SpinSusun.setAdapter(adapter);
            } else {
                Log.e("Error", "Failed to load susun data");
            }
        }
    }
    private class LoadSusunTask2 extends AsyncTask<Void, Void, List<Susun>> {
        private String noS4S;

        public LoadSusunTask2(String noS4S) {
            this.noS4S = noS4S;
        }

        @Override
        protected List<Susun> doInBackground(Void... params) {
            List<Susun> susunList = new ArrayList<>();
            Connection con = ConnectionClass();

            if (con != null) {
                try {
                    String query = "SELECT NoBongkarSusun FROM dbo.BongkarSusunOutputS4S WHERE NoS4S = ?"; // Filter by noS4S
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, noS4S);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String nomorBongkarSusun = rs.getString("NoBongkarSusun");

                        Susun susun = new Susun(nomorBongkarSusun);
                        susunList.add(susun);
                    }

                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    Log.e("Database Error", e.getMessage());
                }
            } else {
                Log.e("Connection Error", "Failed to connect to the database.");
            }
            return susunList;
        }

        @Override
        protected void onPostExecute(List<Susun> susunList) {
            if (!susunList.isEmpty()) {
                ArrayAdapter<Susun> adapter = new ArrayAdapter<>(S4S.this, android.R.layout.simple_spinner_item, susunList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinSusun.setAdapter(adapter);

                radioButtonMesin.setEnabled(false);
                radioButtonBSusun.setEnabled(true);
            } else {
                Log.e("Error", "Failed to load susun data.");
                radioButtonMesin.setEnabled(false);
                radioButtonBSusun.setEnabled(false);

                Toast.makeText(S4S.this, "Tidak ada data susun yang ditemukan.", Toast.LENGTH_SHORT).show();
                SpinSusun.setAdapter(null);
            }
        }
    }



    public class JenisKayu {
        private String idJenisKayu;
        private String namaJenisKayu;

        public JenisKayu(String idJenisKayu, String namaJenisKayu) {
            this.idJenisKayu = idJenisKayu;
            this.namaJenisKayu = namaJenisKayu;
        }

        public String getIdJenisKayu() {
            return idJenisKayu;
        }

        public String getNamaJenisKayu() {
            return namaJenisKayu;
        }

        @Override
        public String toString() {
            return namaJenisKayu;
        }
    }


    public class Telly {
        private String idTelly;
        private String namaTelly;

        public Telly(String idTelly, String namaTelly) {
            this.idTelly = idTelly;
            this.namaTelly = namaTelly;
        }

        public String getIdTelly() {
            return idTelly;
        }

        public String getNamaTelly() {
            return namaTelly;
        }

        @Override
        public String toString() {
            return namaTelly;
        }
    }


    public class SPK {
        private String noSPK;

        public SPK(String noSPK) {
            this.noSPK = noSPK;
        }

        public String getNoSPK() {
            return noSPK;
        }

        @Override
        public String toString() {
            return noSPK;
        }
    }

    public class SPKAsal {
        private String noSPKasal;

        public SPKAsal(String noSPKasal) {
            this.noSPKasal = noSPKasal;
        }

        public String getNoSPKAsal() {
            return noSPKasal;
        }

        @Override
        public String toString() {
            return noSPKasal;
        }
    }

    public class Profile {
        private String idFJProfile;
        private String namaProfile;

        public Profile(String namaProfile, String idFJProfile) {
            this.namaProfile = namaProfile;
            this.idFJProfile = idFJProfile;
        }

        public String getIdFJProfile() {
            return idFJProfile;
        }

        public String getNamaProfile() {
            return namaProfile;
        }

        @Override
        public String toString() {
            return namaProfile;
        }
    }



    public class Fisik {
        private String idWarehouse; // Jika diperlukan
        private String namaWarehouse;

        public Fisik(String namaWarehouse) {
            this.namaWarehouse = namaWarehouse;
        }

        public String getIdWarehouse() {
            return idWarehouse;
        }

        public void setIdWarehouse(String idWarehouse) {
            this.idWarehouse = idWarehouse;
        }

        public String getNamaWarehouse() {
            return namaWarehouse;
        }

        @Override
        public String toString() {
            return namaWarehouse;
        }
    }


    public class Grade {
        private String idGrade;
        private String namaGrade;

        public Grade(String idGrade, String namaGrade) {
            this.idGrade = idGrade;
            this.namaGrade = namaGrade;
        }

        public String getIdGrade() {
            return idGrade;
        }

        public String getNamaGrade() {
            return namaGrade;
        }

        @Override
        public String toString() {
            return namaGrade;
        }
    }

    public class Mesin {
        private String noProduksi;
        private String namaMesin;

        public Mesin(String noProduksi, String namaMesin) {
            this.noProduksi = noProduksi;
            this.namaMesin = namaMesin;
        }

        public String getNoProduksi() {
            return noProduksi;
        }

        public String getNamaMesin() {
            return namaMesin;
        }

        @Override
        public String toString() {
            return namaMesin + " - " + noProduksi;
        }
    }

    public class Susun {
        private String nomorBongkarSusun;

        public Susun(String nomorBongkarSusun) {
            this.nomorBongkarSusun = nomorBongkarSusun;
        }

        public String getNoBongkarSusun() {
            return nomorBongkarSusun;
        }

        public void setNoBongkarSusun(String nomorBongkarSusun) {
            this.nomorBongkarSusun = nomorBongkarSusun;
        }

        @Override
        public String toString() {
            return nomorBongkarSusun;
        }
    }
}

