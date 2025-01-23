package com.example.myapplication;
import com.example.myapplication.model.TableConfig;
import com.example.myapplication.model.TooltipData;
import com.example.myapplication.utils.CustomProgressDialog;
import com.example.myapplication.utils.DateTimeUtils;
import com.example.myapplication.utils.ScannerAnimationUtils;
import com.example.myapplication.utils.TableConfigUtils;




import static com.example.myapplication.api.ProductionApi.isTransactionPeriodClosed;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.app.AlertDialog;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.api.ProductionApi;
import com.example.myapplication.model.HistoryItem;
import com.example.myapplication.model.ProductionData;
import com.example.myapplication.utils.CameraUtils;
import com.example.myapplication.utils.CameraXAnalyzer;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;

public class ProsesProduksiS4S extends AppCompatActivity {

    private TableLayout tableLayout;
    private TableLayout headerTableProduksi;
    private PreviewView cameraPreview;
    private Button btnCameraControl;
    private Button btnSimpan;
    private TableRow selectedRow;
    private TextView qrResultText;
    private TextView noProduksiView;
    private TextView tglProduksiView;
    private TextView mesinProduksiView;
    private boolean isShowingResult = false;
    private ConstraintLayout scanLayout;
    private String noProduksi; // Variabel global
    private String tglProduksi; // Variabel global
    private String mesinProduksi; // Variabel global
    private AlertDialog progressDialog;
    private TextView progressText;
    private ProgressBar progressBar;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ProcessCameraProvider cameraProvider;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<String> scannedResults = new ArrayList<>();
    private boolean isCameraActive = false;
    private ProductionData selectedProductionData;
    private EditText kodeLabel;
    private EditText searchMainTable;
    private TextInputLayout textLayoutSearchMainTable;
    private ImageButton btnInputKodeLabel;
    private LinearLayout inputKodeManual;
    private List<String> noS4SList = new ArrayList<>(); // Daftar untuk kode 'R'
    private List<String> noSTList = new ArrayList<>();  // Daftar untuk kode 'E'
    private List<String> noMouldingList = new ArrayList<>();  // Daftar untuk kode 'T'
    private List<String> noFJList = new ArrayList<>();  // Daftar untuk kode 'S'
    private List<String> noCCList = new ArrayList<>();  // Daftar untuk kode 'V'
    private List<String> noReprosesList = new ArrayList<>();  // Daftar untuk kode 'Y'
    private List<String> noLaminatingList = new ArrayList<>();  // Daftar untuk kode 'U'
    private List<String> noSandingList = new ArrayList<>();  // Daftar untuk kode 'W'
    private List<String> noPackingList = new ArrayList<>();  // Daftar untuk kode 'I'
    private List<ProductionData> dataList; // Data asli yang tidak difilter
    private ProgressBar loadingIndicator;
    private TableLayout noS4STableLayout;
    private TableLayout noSTTableLayout;
    private TableLayout noMouldingTableLayout;
    private TableLayout noFJTableLayout;
    private TableLayout noCCTableLayout;
    private TableLayout noReprosesTableLayout;
    private LinearLayout jumlahLabel;
    private LinearLayout jumlahLabelHeader;
    private TextView sumS4SLabel;
    private TextView sumSTLabel;
    private TextView sumMouldingLabel;
    private TextView sumFJLabel;
    private TextView sumCCLabel;
    private TextView sumReprosesLabel;
    private View borderTop;
    private View borderBottom;
    private View borderLeft;
    private View borderRight;
    private View btnHistorySave;
    private AlertDialog dialog;
    private CustomProgressDialog customProgressDialog;
    private View scannerOverlay;
    private final Map<String, Long> recentlyAddedResults = new HashMap<>();
    private static final long RECENTLY_ADDED_INTERVAL = 2000; // 2 detik
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastToastTime = 0; // Untuk throttling toast
    private static final long TOAST_INTERVAL = 2000; // Interval toast (2 detik)
    private ProgressBar loadingIndicatorNoS4S;
    private ProgressBar loadingIndicatorNoST;
    private ProgressBar loadingIndicatorNoMoulding;
    private ProgressBar loadingIndicatorNoFJ;
    private ProgressBar loadingIndicatorNoCC;
    private ProgressBar loadingIndicatorNoReproses;
    int pointerDownY = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proses_produksi_s4_s);

        // Inisialisasi komponen UI
        tableLayout = findViewById(R.id.tableLayout);
        headerTableProduksi = findViewById(R.id.headerTableProduksi);
        cameraPreview = findViewById(R.id.cameraPreview);
        btnCameraControl = findViewById(R.id.btnCameraControl);
        qrResultText = findViewById(R.id.qrResultText);
        scanLayout = findViewById(R.id.scanLayout);
        noProduksiView = findViewById(R.id.noProduksiView);
        tglProduksiView = findViewById(R.id.tglProduksiView);
        mesinProduksiView = findViewById(R.id.mesinProduksiView);
        btnSimpan = findViewById(R.id.btnSimpan);
        kodeLabel = findViewById(R.id.kodeLabel);
        searchMainTable = findViewById(R.id.searchMainTable);
        btnInputKodeLabel = findViewById(R.id.btnInputKodeLabel);
        inputKodeManual = findViewById(R.id.inputKodeManual);
        textLayoutSearchMainTable = findViewById(R.id.textLayoutSearchMainTable);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        noS4STableLayout = findViewById(R.id.noS4STableLayout);
        noSTTableLayout = findViewById(R.id.noSTTableLayout);
        noMouldingTableLayout = findViewById(R.id.noMouldingTableLayout);
        noFJTableLayout = findViewById(R.id.noFJTableLayout);
        noCCTableLayout = findViewById(R.id.noCCTableLayout);
        noReprosesTableLayout = findViewById(R.id.noReprosesTableLayout);
        jumlahLabel = findViewById(R.id.jumlahLabel);
        jumlahLabelHeader = findViewById(R.id.jumlahLabelHeader);
        sumS4SLabel = findViewById(R.id.sumS4SLabel);
        sumSTLabel = findViewById(R.id.sumSTLabel);
        sumMouldingLabel = findViewById(R.id.sumMouldingLabel);
        sumFJLabel = findViewById(R.id.sumFJLabel);
        sumCCLabel = findViewById(R.id.sumCCLabel);
        sumReprosesLabel = findViewById(R.id.sumReprosesLabel);
        borderTop = findViewById(R.id.borderTop);
        borderBottom = findViewById(R.id.borderBottom);
        borderLeft = findViewById(R.id.borderLeft);
        borderRight = findViewById(R.id.borderRight);
        btnHistorySave = findViewById(R.id.btnHistorySave);
        loadingIndicatorNoS4S = findViewById(R.id.loadingIndicatorNoS4S);
        loadingIndicatorNoST = findViewById(R.id.loadingIndicatorNoST);
        loadingIndicatorNoMoulding = findViewById(R.id.loadingIndicatorNoMoulding);
        loadingIndicatorNoFJ = findViewById(R.id.loadingIndicatorNoFJ);
        loadingIndicatorNoCC = findViewById(R.id.loadingIndicatorNoCC);
        loadingIndicatorNoReproses = findViewById(R.id.loadingIndicatorNoReproses);

        loadingIndicator.setVisibility(View.VISIBLE);


        // Inisialisasi View scanner overlay
        scannerOverlay = findViewById(R.id.scannerOverlay);

        // Dapatkan DisplayMetrics untuk tinggi layar
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        // Mulai animasi scanner menggunakan ScannerAnimationUtils
        ScannerAnimationUtils.startScanningAnimation(scannerOverlay, displayMetrics);

        searchMainTable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (dataList == null || dataList.isEmpty()) {
                    Log.e("SearchError", "Data list is null or empty.");
                    return; // Hindari iterasi jika dataList null
                }

                String query = s.toString().toLowerCase();

                final int PAGE_SIZE = 50;
                List<ProductionData> filteredList = new ArrayList<>();
                int count = 0;

                for (ProductionData data : dataList) {
                    if (count >= PAGE_SIZE) break;

                    if ((data.getNoProduksi() != null && data.getNoProduksi().toLowerCase().contains(query)) ||
                            (data.getOperator() != null && data.getOperator().toLowerCase().contains(query)) ||
                            (data.getShift() != null && data.getShift().toLowerCase().contains(query)) ||
                            (data.getTanggal() != null && data.getTanggal().toLowerCase().contains(query)) ||
                            (data.getMesin() != null && data.getMesin().toLowerCase().contains(query))) {
                        filteredList.add(data);
                        count++;
                    }
                }

                populateTable(filteredList);

            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScannedResultsToDatabase();
            }
        });

        btnInputKodeLabel.setOnClickListener(v -> {
            String result = kodeLabel.getText().toString().trim();

            if (!result.isEmpty()) {
                // Panggil metode yang sama dengan hasil scan
                addScanResultToTable(result);
            } else {
                Toast.makeText(this, "Kode tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });


        // Memuat data dari API dan menampilkan ke tabel
        executorService.execute(() -> {
            dataList = ProductionApi.getProductionData("S4SProduksi_h");

            runOnUiThread(() -> {
                populateTable(dataList);
                // Sembunyikan loading indicator
                loadingIndicator.setVisibility(View.GONE);
            });

        });

        // Mendapatkan instance ProcessCameraProvider
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                setupCameraControlButton();
            } catch (Exception e) {
                Log.e("ProsesProduksiS4S", "Error initializing camera provider: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));

        // Inisialisasi requestPermissionLauncher
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission diberikan, aktifkan kamera
                        activateCamera();
                        setupCameraControlButton();
                    } else {
                        // Permission ditolak, beri tahu user
                        Toast.makeText(this, "Permission kamera dibutuhkan untuk mengaktifkan kamera", Toast.LENGTH_SHORT).show();
                    }
                });

        btnHistorySave.setOnClickListener(v -> showHistoryDialog(noProduksi));

    }

    //METHOD S4S

//------------------------------------------------------------------------------------------------------------------------------------------------------//
//----------------------------------METHOD UNTUK PENANGANAN KAMERA DENGAN SCAN QR-----------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------------------------------------//
    /**
     * Menyiapkan tombol kontrol kamera
     */
    private void setupCameraControlButton() {
        updateButtonText(); // Set teks awal tombol
        btnCameraControl.setOnClickListener(v -> {
            if (isCameraActive) {
                deactivateCamera();
            } else {
                String noProduksi = noProduksiView.getText().toString();

                if (!noProduksi.isEmpty()) {
                    executorService.execute(() -> {
                        if (tglProduksi == null) {
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Tanggal Produksi tidak valid!", Toast.LENGTH_SHORT).show()
                            );
                            return;
                        }

                        // Periksa periode transaksi
                        boolean isClosed = isTransactionPeriodClosed(tglProduksi);

                        runOnUiThread(() -> {
                            if (!isClosed) {
                                Toast.makeText(this, "Periode transaksi sudah ditutup!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Jika valid, minta permission kamera
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                        });
                    });
                } else {
                    Toast.makeText(this, "Pilih NoProduksi Terlebih Dahulu!", Toast.LENGTH_SHORT).show();
                }
            }

            updateButtonText(); // Ubah teks tombol sesuai status
        });

    }

    /**
     * Mengaktifkan kamera
     */
    private void activateCamera() {
        if (cameraProvider != null && !isCameraActive) {
            // Periksa permission kamera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission sudah diberikan, setup kamera

                CameraUtils.setupCamera(
                        this,
                        cameraPreview,
                        new CameraXAnalyzer(result -> {
                            runOnUiThread(() -> {
                                if (!isShowingResult) {
                                    // QR code terdeteksi
                                    qrResultText.setText(result);
                                    qrResultText.setVisibility(View.VISIBLE);
                                    isShowingResult = true;

                                    // Kembalikan warna border ke default setelah 3 detik
                                    new android.os.Handler().postDelayed(() -> {
                                        borderTop.setBackgroundResource(R.drawable.border_default);
                                        borderBottom.setBackgroundResource(R.drawable.border_default);
                                        borderLeft.setBackgroundResource(R.drawable.border_default);
                                        borderRight.setBackgroundResource(R.drawable.border_default);
                                    }, 3000);

                                    // Sembunyikan teks hasil setelah 3 detik
                                    new android.os.Handler().postDelayed(() -> {
                                        qrResultText.setVisibility(View.GONE);
                                        isShowingResult = false;
                                    }, 3000);
                                }

                                // Tambahkan hasil ke tabel
                                addScanResultToTable(result);
                            });
                        }),
                        androidx.camera.core.CameraSelector.LENS_FACING_BACK,
                        ProcessCameraProvider.getInstance(this)
                );

                // Atur UI saat kamera aktif
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                ScannerAnimationUtils.startScanningAnimation(scannerOverlay, displayMetrics);
                scanLayout.setVisibility(View.VISIBLE);
                cameraPreview.setVisibility(View.VISIBLE);
                inputKodeManual.setVisibility(View.VISIBLE);
                tableLayout.setVisibility(View.GONE);
                headerTableProduksi.setVisibility(View.GONE);
                searchMainTable.setVisibility(View.INVISIBLE);
                textLayoutSearchMainTable.setVisibility(View.INVISIBLE);
                jumlahLabelHeader.setVisibility(View.VISIBLE);
                jumlahLabel.setVisibility(View.VISIBLE);
                btnSimpan.setEnabled(true);

                isCameraActive = true;
//                Toast.makeText(this, "Kamera diaktifkan", Toast.LENGTH_SHORT).show();
            } else {
                // Permission belum diberikan, minta permission
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        } else {
            Toast.makeText(this, "Kamera sudah aktif", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Menonaktifkan kamera
     */
    private void deactivateCamera() {
        if (cameraProvider != null && isCameraActive) {
            cameraProvider.unbindAll(); // Hentikan semua instance kamera

            ScannerAnimationUtils.stopScanningAnimation();
            scanLayout.setVisibility(View.GONE);
            cameraPreview.setVisibility(View.GONE);
            inputKodeManual.setVisibility(View.GONE);
            jumlahLabelHeader.setVisibility(View.GONE);
            jumlahLabel.setVisibility(View.GONE);
            tableLayout.setVisibility(View.VISIBLE);
            headerTableProduksi.setVisibility(View.VISIBLE);
            searchMainTable.setVisibility(View.VISIBLE);
            textLayoutSearchMainTable.setVisibility(View.VISIBLE);
            btnSimpan.setEnabled(false);



            isCameraActive = false;
//            Toast.makeText(this, "Kamera dinonaktifkan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Kamera sudah tidak aktif", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateButtonText() {
        if (isCameraActive) {
            btnCameraControl.setText("Back");
        } else {
            btnCameraControl.setText("Input");
        }
    }

//------------------------------------------------------------------------------------------------------------------------------------------------------//
//-------------------------------------------METHOD UNTUK DISPLAY KE TABEL------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------------------------------------//
    /**
     * Menampilkan data produksi ke dalam tabel utama
     */
    private void populateTable(List<ProductionData> dataList) {

        tableLayout.removeAllViews();

        if (dataList == null || dataList.isEmpty()) {
            TextView noDataView = new TextView(this);
            noDataView.setText("Data tidak ditemukan");
            noDataView.setGravity(Gravity.CENTER);
            noDataView.setPadding(16, 16, 16, 16);
            tableLayout.addView(noDataView);
            return;
        }

        int rowIndex = 0; // Untuk melacak indeks baris

        for (ProductionData data : dataList) {
            TableRow row = new TableRow(this);

            // Simpan indeks baris di tag
            row.setTag(rowIndex);

            // Tambahkan TextView untuk setiap kolom
            TextView col1 = createTextView(data.getNoProduksi(), 1.0f);
            TextView col2 = createTextView(data.getShift(), 0.5f);
            TextView col3 = createTextView(data.getTanggal(), 1.0f);
            TextView col4 = createTextView(data.getMesin(), 1.0f);
            TextView col5 = createTextView(data.getOperator(), 1.0f);

            setDateToView(data.getTanggal(), col3);

            row.addView(col1);
            row.addView(createDivider());

            row.addView(col2);
            row.addView(createDivider());

            row.addView(col3);
            row.addView(createDivider());

            row.addView(col4);
            row.addView(createDivider());

            row.addView(col5);

            // Tetapkan warna latar belakang berdasarkan indeks baris
            if (rowIndex % 2 == 0) {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.gray)); // Warna untuk baris genap
            } else {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.white)); // Warna untuk baris ganjil
            }

            row.setOnClickListener(v -> {
                // Reset warna baris sebelumnya (jika ada)
                if (selectedRow != null) {
                    int previousRowIndex = (int) selectedRow.getTag();
                    if (previousRowIndex % 2 == 0) {
                        selectedRow.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                    } else {
                        selectedRow.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    }
                    resetTextColor(selectedRow); // Kembalikan warna teks ke hitam
                }

                // Tandai baris yang baru dipilih
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.primary)); // Warna penandaan
                setTextColor(row, R.color.white); // Ubah warna teks menjadi putih
                selectedRow = row;

                // Simpan data yang dipilih
                selectedProductionData = data;

                // Tangani aksi tambahan
                onRowClick(data);
            });


            tableLayout.addView(row);
            rowIndex++; // Tingkatkan indeks
        }
    }

    // Mengambil data tooltip dan menampilkan tooltip
    private void fetchDataAndShowTooltip(View anchorView, String noLabel, String tableH, String tableD, String mainColumn) {
        executorService.execute(() -> {
            // Ambil data tooltip menggunakan ProductionApi
            TooltipData tooltipData = ProductionApi.getTooltipData(noLabel, tableH, tableD, mainColumn);

            runOnUiThread(() -> {
                if (tooltipData != null) {
                    // Pengecekan lebih lanjut jika data dalam tooltipData null
                    if (tooltipData.getNoLabel() != null && tooltipData.getTableData() != null) {

                        // Tampilkan tooltip dengan data yang diperoleh
                        showTooltip(
                                anchorView,
                                tooltipData.getNoLabel(),
                                tooltipData.getFormattedDateTime(),
                                tooltipData.getJenis(),
                                tooltipData.getSpkDetail(),
                                tooltipData.getSpkAsalDetail(),
                                tooltipData.getNamaGrade(),
                                tooltipData.isLembur(),
                                tooltipData.getTableData(),
                                tooltipData.getTotalPcs(),
                                tooltipData.getTotalM3(),
                                tooltipData.getTotalTon(),
                                tooltipData.getNoPlat(),
                                tooltipData.getNoKBSuket(),
                                tableH
                        );



                    } else {
                        // Tampilkan pesan error jika data utama tidak ada
                        Toast.makeText(this, "Data tooltip tidak lengkap", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Tampilkan pesan error jika tooltipData null
                    Toast.makeText(this, "Error fetching tooltip data", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }


    private void showTooltip(View anchorView, String noLabel, String formattedDateTime, String jenis, String spkDetail, String spkAsalDetail, String namaGrade, boolean isLembur, List<String[]> tableData, int totalPcs, double totalM3, double totalTon, String noPlat, String noKBSuket, String tableH) {
        // Inflate layout tooltip
        View tooltipView = LayoutInflater.from(this).inflate(R.layout.tooltip_layout, null);

        // Set data pada TextView
        ((TextView) tooltipView.findViewById(R.id.tvNoLabel)).setText(noLabel);
        ((TextView) tooltipView.findViewById(R.id.tvDateTime)).setText(formattedDateTime);
        ((TextView) tooltipView.findViewById(R.id.tvJenis)).setText(jenis);
        ((TextView) tooltipView.findViewById(R.id.tvNoSPK)).setText(spkDetail);
        ((TextView) tooltipView.findViewById(R.id.tvNoSPKAsal)).setText(spkAsalDetail);
        ((TextView) tooltipView.findViewById(R.id.tvNamaGrade)).setText(namaGrade);
        ((TextView) tooltipView.findViewById(R.id.tvIsLembur)).setText(isLembur ? "Yes" : "No");
        ((TextView) tooltipView.findViewById(R.id.tvNoPlat)).setText(noPlat);
        ((TextView) tooltipView.findViewById(R.id.tvNoKBSuket)).setText(noKBSuket);

        // Referensi TableLayout
        TableLayout tableLayout = tooltipView.findViewById(R.id.tabelDetailTooltip);

        // Membuat Header Tabel Secara Dinamis
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(R.color.hijau));

        String[] headerTexts = {"Tebal", "Lebar", "Panjang", "Pcs"};
        for (String headerText : headerTexts) {
            TextView headerTextView = new TextView(this);
            headerTextView.setText(headerText);
            headerTextView.setGravity(Gravity.CENTER);
            headerTextView.setPadding(8, 8, 8, 8);
            headerTextView.setTextColor(Color.WHITE);
            headerTextView.setTypeface(Typeface.DEFAULT_BOLD);
            headerRow.addView(headerTextView);
        }

        // Tambahkan Header ke TableLayout
        tableLayout.addView(headerRow);

        // Tambahkan Data ke TableLayout
        for (String[] row : tableData) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            tableRow.setBackgroundColor(getResources().getColor(R.color.gray));

            for (String cell : row) {
                TextView textView = new TextView(this);
                textView.setText(cell);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(8, 8, 8, 8);
                textView.setTextColor(Color.BLACK);
                tableRow.addView(textView);
            }
            tableLayout.addView(tableRow);
        }

        // Tambahkan Baris untuk Total Pcs
        TableRow totalRow = new TableRow(this);
        totalRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        totalRow.setBackgroundColor(Color.WHITE);

        // Cell kosong untuk memisahkan total dengan tabel
        for (int i = 0; i < 2; i++) {
            TextView emptyCell = new TextView(this);
            emptyCell.setText(""); // Cell kosong
            totalRow.addView(emptyCell);
        }

        TextView totalLabel = new TextView(this);
        totalLabel.setText("Total :");
        totalLabel.setGravity(Gravity.END);
        totalLabel.setPadding(8, 8, 8, 8);
        totalLabel.setTypeface(Typeface.DEFAULT_BOLD);
        totalRow.addView(totalLabel);

        // Cell untuk Total Pcs
        TextView totalValue = new TextView(this);
        totalValue.setText(String.valueOf(totalPcs));
        totalValue.setGravity(Gravity.CENTER);
        totalValue.setPadding(8, 8, 8, 8);
        totalValue.setTypeface(Typeface.DEFAULT_BOLD);
        totalRow.addView(totalValue);

        // Tambahkan totalRow ke TableLayout
        tableLayout.addView(totalRow);

        // Tambahkan Baris untuk Total M3
        TableRow m3Row = new TableRow(this);
        m3Row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        m3Row.setBackgroundColor(Color.WHITE);

        // Cell kosong untuk memisahkan m3 dengan tabel
        for (int i = 0; i < 2; i++) {
            TextView emptyCell = new TextView(this);
            emptyCell.setText(""); // Cell kosong
            m3Row.addView(emptyCell);
        }

        TextView m3Label = new TextView(this);
        m3Label.setText("M3 :");
        m3Label.setGravity(Gravity.END);
        m3Label.setPadding(8, 8, 8, 8);
        m3Label.setTypeface(Typeface.DEFAULT_BOLD);
        m3Row.addView(m3Label);

        // Cell untuk Total M3
        DecimalFormat df = new DecimalFormat("0.0000");
        TextView m3Value = new TextView(this);
        m3Value.setText(df.format(totalM3));
        m3Value.setGravity(Gravity.CENTER);
        m3Value.setPadding(8, 8, 8, 8);
        m3Value.setTypeface(Typeface.DEFAULT_BOLD);
        m3Row.addView(m3Value);

        // Tambahkan m3Row ke TableLayout
        tableLayout.addView(m3Row);

        //TOOLTIP VIEW PRECONDITION
        if (tableH.equals("ST_h")) {
            tooltipView.findViewById(R.id.fieldNoSPKAsal).setVisibility(View.GONE);
            tooltipView.findViewById(R.id.fieldGrade).setVisibility(View.GONE);

            // Tambahkan Baris untuk Total Ton
            TableRow tonRow = new TableRow(this);
            tonRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            tonRow.setBackgroundColor(Color.WHITE);

            // Cell kosong untuk memisahkan m3 dengan tabel
            for (int i = 0; i < 2; i++) {
                TextView emptyCell = new TextView(this);
                emptyCell.setText(""); // Cell kosong
                tonRow.addView(emptyCell);
            }

            TextView tonLabel = new TextView(this);
            tonLabel.setText("Ton :");
            tonLabel.setGravity(Gravity.END);
            tonLabel.setPadding(8, 8, 8, 8);
            tonLabel.setTypeface(Typeface.DEFAULT_BOLD);
            tonRow.addView(tonLabel);

            // Cell untuk Total Ton
            TextView tonValue = new TextView(this);
            tonValue.setText(df.format(totalTon));
            tonValue.setGravity(Gravity.CENTER);
            tonValue.setPadding(8, 8, 8, 8);
            tonValue.setTypeface(Typeface.DEFAULT_BOLD);
            tonRow.addView(tonValue);

            // Tambahkan m3Row ke TableLayout
            tableLayout.addView(tonRow);
        } else {
            tooltipView.findViewById(R.id.tvNoKBSuket).setVisibility(View.GONE);
            tooltipView.findViewById(R.id.fieldPlatTruk).setVisibility(View.GONE);
        }

        // Buat PopupWindow
        PopupWindow popupWindow = new PopupWindow(
                tooltipView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Ukur ukuran tooltip sebelum menampilkannya
        tooltipView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int tooltipWidth = tooltipView.getMeasuredWidth();
        int tooltipHeight = tooltipView.getMeasuredHeight();

        // Dapatkan posisi anchorView
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);

        // Hitung posisi tooltip
        int x = location[0] - tooltipWidth;
        int y = location[1] + (anchorView.getHeight() / 2) - (tooltipHeight / 2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;
        ImageView trianglePointer = tooltipView.findViewById(R.id.trianglePointer);

        // Menaikkan pointer ketika popup melebihi batas layout
        Log.d("TooltipDebug", "TrianglePointer Y: " + y);
        Log.d("TooltipDebug", "TrianglePointer tooltip : " + (screenHeight - tooltipHeight) );

        if (y < 60) {
            trianglePointer.setY(y - 60);
        }
        else if(y > (screenHeight - tooltipHeight)){
            trianglePointer.setY(y - (screenHeight - tooltipHeight));
        }



        // Tampilkan tooltip
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
    }

    private void populateNoS4STable(List<String> noS4SList) {
        noS4STableLayout.removeAllViews();

        if (noS4SList == null || noS4SList.isEmpty()) {
            TextView noDataView = new TextView(this);
            noDataView.setText("Tidak ada Data");
            noDataView.setGravity(Gravity.CENTER);
            noDataView.setPadding(16, 16, 16, 16);
            noS4STableLayout.addView(noDataView);
            return;
        }

        // Isi tabel
        for (String noS4S : noS4SList) {
            TableRow row = new TableRow(this);

            // Tambahkan TextView ke baris tabel
            TextView textView = createTextView(noS4S, 1.0f);
            row.addView(textView);

            // Tambahkan OnClickListener untuk menampilkan tooltip
            row.setOnClickListener(view -> fetchDataAndShowTooltip(view, noS4S, "S4S_h", "S4S_d", "NoS4S"));

            // Tambahkan baris ke TableLayout
            noS4STableLayout.addView(row);
        }
    }

    private void populateNoSTTable(List<String> noSTList) {
        noSTTableLayout.removeAllViews();

        if (noSTList == null || noSTList.isEmpty()) {
            TextView noDataView = new TextView(this);
            noDataView.setText("Tidak ada Data");
            noDataView.setGravity(Gravity.CENTER);
            noDataView.setPadding(16, 16, 16, 16);
            noSTTableLayout.addView(noDataView);
            return;
        }

        // Data tabel
        for (String noST : noSTList) {
            TableRow row = new TableRow(this);

            // Tambahkan TextView ke baris tabel
            TextView textView = createTextView(noST, 1.0f);
            row.addView(textView);

            // Tambahkan OnClickListener untuk menampilkan tooltip
            row.setOnClickListener(view -> fetchDataAndShowTooltip(view, noST, "ST_h", "ST_d", "NoST"));
            noSTTableLayout.addView(row);
        }
    }

    private void populateNoMouldingTable(List<String> noMouldingList) {
        noMouldingTableLayout.removeAllViews();

        if (noMouldingList == null || noMouldingList.isEmpty()) {
            TextView noDataView = new TextView(this);
            noDataView.setText("Tidak ada Data");
            noDataView.setGravity(Gravity.CENTER);
            noDataView.setPadding(16, 16, 16, 16);
            noMouldingTableLayout.addView(noDataView);
            return;

        }

        // Data tabel
        for (String noMoulding : noMouldingList) {
            TableRow row = new TableRow(this);

            TextView textView = createTextView(noMoulding, 1.0f);
            row.addView(textView);

            row.setOnClickListener(view -> fetchDataAndShowTooltip(view, noMoulding, "Moulding_h", "Moulding_d", "NoMoulding"));

            noMouldingTableLayout.addView(row);
        }
    }

    private void populateNoFJTable(List<String> noFJList) {
        noFJTableLayout.removeAllViews();

        if (noFJList == null || noFJList.isEmpty()) {
            TextView noDataView = new TextView(this);
            noDataView.setText("Tidak ada Data");
            noDataView.setGravity(Gravity.CENTER);
            noDataView.setPadding(16, 16, 16, 16);
            noFJTableLayout.addView(noDataView);
            return;
        }

        // Data tabel
        for (String noFJ : noFJList) {
            TableRow row = new TableRow(this);

            TextView textView = createTextView(noFJ, 1.0f);
            row.addView(textView);
            row.setOnClickListener(view -> fetchDataAndShowTooltip(view, noFJ, "FJ_h", "FJ_d", "NoFJ"));

            noFJTableLayout.addView(row);
        }
    }

    private void populateNoCCTable(List<String> noCCList) {
        noCCTableLayout.removeAllViews();

        if (noCCList == null || noCCList.isEmpty()) {
            TextView noDataView = new TextView(this);
            noDataView.setText("Tidak ada Data");
            noDataView.setGravity(Gravity.CENTER);
            noDataView.setPadding(16, 16, 16, 16);
            noCCTableLayout.addView(noDataView);
            return;
        }

        // Data tabel
        for (String noCC : noCCList) {
            TableRow row = new TableRow(this);

            TextView textView = createTextView(noCC, 1.0f);
            row.addView(textView);
            row.setOnClickListener(view -> fetchDataAndShowTooltip(view, noCC, "CCAkhir_h", "CCAkhir_d", "NoCCAkhir"));

            noCCTableLayout.addView(row);
        }
    }

    private void populateNoReprosesTable(List<String> noReprosesList) {
        noReprosesTableLayout.removeAllViews();

        if (noReprosesList == null || noReprosesList.isEmpty()) {
            TextView noDataView = new TextView(this);
            noDataView.setText("Tidak ada Data");
            noDataView.setGravity(Gravity.CENTER);
            noDataView.setPadding(16, 16, 16, 16);
            noReprosesTableLayout.addView(noDataView);
            return;
        }

        // Data tabel
        for (String noReproses : noReprosesList) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(noReproses, 1.0f));
            noReprosesTableLayout.addView(row);
        }
    }


//------------------------------------------------------------------------------------------------------------------------------------------------------//
//-------------------------------------------METHOD ADD DATA KE TABLE LIST DENGAN PRE-CONDITION---------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------------------------------------//


    private void addRowToTable(TableLayout tableLayout, String result, List<String> list, TextView sumLabel) {
        // Buat TableRow untuk baris baru
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));

        // Buat TextView kosong (placeholder) di sebelah kiri
        TextView emptyView = new TextView(this);
        emptyView.setText(""); // Kosongkan teks
        emptyView.setPadding(0, 0, 0, 0); // Tidak ada padding
        emptyView.setGravity(Gravity.CENTER); // Pusatkan konten (jika ada)
        emptyView.setLayoutParams(new TableRow.LayoutParams(50, 50));

        // Buat TextView untuk menampilkan hasil
        TextView textView = new TextView(this);
        textView.setText(result);
        textView.setPadding(8, 15, 8, 15); // Padding untuk jarak
        textView.setGravity(Gravity.CENTER); // Pusatkan teks di tengah
        textView.setLayoutParams(new TableRow.LayoutParams(
                0, // Lebar proporsional (diatur oleh weight)
                TableRow.LayoutParams.WRAP_CONTENT, // Tinggi mengikuti konten
                1f // Berat untuk membagi lebar secara proporsional
        ));

        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.ic_close);
        deleteButton.setBackground(null);
        deleteButton.setPadding(0, 10, 0, 0);
        deleteButton.setLayoutParams(new TableRow.LayoutParams(50, TableRow.LayoutParams.WRAP_CONTENT));


        deleteButton.setOnClickListener(v -> {
            try {
                // Hapus baris dari tabel
                tableLayout.removeView(row);

                // Hapus data dari daftar
                list.remove(result);
                scannedResults.remove(result);

                sumLabel.setText(Integer.toString(list.size()));

            } catch (Exception e) {
                Log.e("DeleteButton", "Error saat menghapus baris: " + e.getMessage());
            }
        });

        // Tambahkan placeholder, TextView, dan tombol delete ke TableRow
        row.addView(emptyView); // Tambahkan ruang kosong di kolom pertama
        row.addView(textView); // Tambahkan teks di kolom kedua
        row.addView(deleteButton); // Tambahkan tombol delete di kolom ketiga

        // Tambahkan TableRow ke TableLayout
        tableLayout.addView(row, 0);
    }

    private void addScanResultToTable(String result) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            synchronized (scannedResults) {
                // Abaikan jika hasil baru saja ditambahkan
                if (recentlyAddedResults.containsKey(result) &&
                        System.currentTimeMillis() - recentlyAddedResults.get(result) < RECENTLY_ADDED_INTERVAL) {
                    return;
                }

                // Filter prefix: hanya terima awalan R (S4S) dan V (CC Akhir)
                if (!result.startsWith("E") && !result.startsWith("R") && !result.startsWith("T") && !result.startsWith("S") && !result.startsWith("V")) {
                    runOnUiThread(() -> displayErrorState(
                            "Label " + result + " Tidak Sesuai", R.raw.denied_data));
                    return;
                }

                if (!scannedResults.contains(result)) {
                    // Tambahkan hasil baru
                    scannedResults.add(result);
                    recentlyAddedResults.put(result, System.currentTimeMillis());

                    // Jadwalkan penghapusan elemen dari recentlyAddedResults
                    handler.postDelayed(() -> {
                        synchronized (recentlyAddedResults) {
                            recentlyAddedResults.remove(result);
                        }
                    }, RECENTLY_ADDED_INTERVAL);

                    // Ambil konfigurasi tabel berdasarkan prefix hasil
                    Map<String, TableConfig> tableConfigMap = TableConfigUtils.getTableConfigMap(
                            noS4SList, noSTList, noMouldingList, noFJList, noCCList, noReprosesList, noLaminatingList, noSandingList, noPackingList
                    );
                    TableConfig config = tableConfigMap.get(result.substring(0, 1));

                    if (config == null) {
                        displayErrorState("Label " + result + " tidak sesuai", R.raw.denied_data);
                        scannedResults.remove(result);
                        return;
                    }

                    if (ProductionApi.isDataExists(result, config.tableNameH, config.tableNameD, config.columnName)) {
                        if (ProductionApi.isDateUsageNull(result, config.tableNameH, config.columnName)) {
                            handleValidData(result, config);
                        } else {
                            handleDuplicateOrInvalidUsage(result, config);
                        }
                    } else {
                        displayErrorState("Label " + result + " Tidak ditemukan di Database", R.raw.denied_data);
                        scannedResults.remove(result);
                    }
                } else {
                    displayDuplicateScanError(result);
                }
            }
        });
        executor.shutdown();
    }

    private void handleValidData(String result, TableConfig config) {
        if (ProductionApi.isDateValid(noProduksi, "S4SProduksi_h", result, config.tableNameH, config.columnName)) {
            runOnUiThread(() -> {
                TableLayout targetTableLayout = findViewById(config.tableLayoutId);
                TextView targetSumLabel = findViewById(config.sumLabelId);

                if (config.list == null || config.list.isEmpty()) {
                    targetTableLayout.removeAllViews();
                }

                config.list.add(result);
                targetSumLabel.setText(String.valueOf(config.list.size()));
                addRowToTable(targetTableLayout, result, config.list, targetSumLabel);

                // Ubah warna border menjadi hijau
                setBorderState(R.drawable.border_granted);
                kodeLabel.setText("");
                playSound(R.raw.granted_data);
            });
        } else {
            displayErrorState("Tgl Label lebih besar dari Tgl Produksi " + result, R.raw.denied_data);
            scannedResults.remove(result);
        }
    }

    private void handleDuplicateOrInvalidUsage(String result, TableConfig config) {
        setBorderState(R.drawable.border_denied);

        String namaTabel = config.resultChecker.apply(result);

        if (namaTabel == null) {
            displayErrorState("Label " + result + " tidak ada di Proses manapun", R.raw.denied_data);
        } else {
            displayErrorState("Label " + result + " sudah ada pada " + namaTabel, R.raw.denied_data);
        }
        scannedResults.remove(result);
    }

    private void displayDuplicateScanError(String result) {
        setBorderState(R.drawable.border_denied);
        Log.d("DuplicateScan", "Hasil scan sudah ada: " + result);
        showToastAndPlaySound("Label " + result + " Sudah di Masukkan ke dalam Tabel", R.raw.denied_data);
    }

    private void displayErrorState(String message, int soundResId) {
        runOnUiThread(() -> {
            setBorderState(R.drawable.border_denied);
            showToastAndPlaySound(message, soundResId);
        });
    }

    private void setBorderState(int drawableResId) {
        borderTop.setBackgroundResource(drawableResId);
        borderBottom.setBackgroundResource(drawableResId);
        borderLeft.setBackgroundResource(drawableResId);
        borderRight.setBackgroundResource(drawableResId);
    }

    private void showToastAndPlaySound(String message, int soundResId) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToastTime > TOAST_INTERVAL) {
            runOnUiThread(() -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                playSound(soundResId);
            });
            lastToastTime = currentTime;
        }
    }


//------------------------------------------------------------------------------------------------------------------------------------------------------//
//-------------------------------------------METHOD MENYIMPAN DATA KE DALAM DATABASE -------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------------------------------------//

    private void saveScannedResultsToDatabase() {
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.show(); // Tampilkan progress dialog

        String dateTimeSaved = DateTimeUtils.getCurrentDateTime();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Log.d("SaveScannedResults", "Memulai proses penyimpanan hasil scan ke database");

            int totalItems = noS4SList.size() + noSTList.size() + noMouldingList.size() +
                    noFJList.size() + noCCList.size() + noReprosesList.size();
            int savedItems = 0;

            // Proses penyimpanan untuk tabel S4S
            if (!noS4SList.isEmpty()) {
                List<String> existingNoS4S = ProductionApi.getNoS4SByNoProduksi(noProduksi, "S4SProduksiInputS4S");
                List<String> newNoS4S = new ArrayList<>(noS4SList);
                newNoS4S.removeAll(existingNoS4S);
                ProductionApi.saveNoS4S(noProduksi, tglProduksi, newNoS4S, dateTimeSaved, "S4SProduksiInputS4S");
                savedItems += newNoS4S.size();
                int progress = (savedItems * 100) / totalItems;
                runOnUiThread(() -> customProgressDialog.updateProgress(progress));
            }

            // Proses penyimpanan untuk tabel ST
            if (!noSTList.isEmpty()) {
                List<String> existingNoST = ProductionApi.getNoSTByNoProduksi(noProduksi);
                List<String> newNoST = new ArrayList<>(noSTList);
                newNoST.removeAll(existingNoST);
                ProductionApi.saveNoST(noProduksi, tglProduksi, newNoST, dateTimeSaved);
                savedItems += newNoST.size();
                int progress = (savedItems * 100) / totalItems;
                runOnUiThread(() -> customProgressDialog.updateProgress(progress));
            }

            // Proses penyimpanan untuk tabel Moulding
            if (!noMouldingList.isEmpty()) {
                List<String> existingNoMoulding = ProductionApi.getNoMouldingByNoProduksi(noProduksi, "S4SProduksiInputMoulding");
                List<String> newNoMoulding = new ArrayList<>(noMouldingList);
                newNoMoulding.removeAll(existingNoMoulding);
                ProductionApi.saveNoMoulding(noProduksi, tglProduksi, newNoMoulding, dateTimeSaved, "S4SProduksiInputMoulding");
                savedItems += newNoMoulding.size();
                int progress = (savedItems * 100) / totalItems;
                runOnUiThread(() -> customProgressDialog.updateProgress(progress));
            }

            // Proses penyimpanan untuk tabel FJ
            if (!noFJList.isEmpty()) {
                List<String> existingNoFJ = ProductionApi.getNoFJByNoProduksi(noProduksi, "S4SProduksiInputFJ");
                List<String> newNoFJ = new ArrayList<>(noFJList);
                newNoFJ.removeAll(existingNoFJ);
                ProductionApi.saveNoFJ(noProduksi, tglProduksi, newNoFJ, dateTimeSaved, "S4SProduksiInputFJ");
                savedItems += newNoFJ.size();
                int progress = (savedItems * 100) / totalItems;
                runOnUiThread(() -> customProgressDialog.updateProgress(progress));
            }

            // Proses penyimpanan untuk tabel CC
            if (!noCCList.isEmpty()) {
                List<String> existingNoCC = ProductionApi.getNoCCByNoProduksi(noProduksi, "S4SProduksiInputCCAkhir");
                List<String> newNoCC = new ArrayList<>(noCCList);
                newNoCC.removeAll(existingNoCC);
                ProductionApi.saveNoCC(noProduksi, tglProduksi, newNoCC, dateTimeSaved, "S4SProduksiInputCCAkhir");
                savedItems += newNoCC.size();
                int progress = (savedItems * 100) / totalItems;
                runOnUiThread(() -> customProgressDialog.updateProgress(progress));
            }

            // Proses penyimpanan untuk tabel Reproses
            if (!noReprosesList.isEmpty()) {
                List<String> existingNoReproses = ProductionApi.getNoReprosesByNoProduksi(noProduksi);
                List<String> newNoReproses = new ArrayList<>(noReprosesList);
                newNoReproses.removeAll(existingNoReproses);
                ProductionApi.saveNoReproses(noProduksi, tglProduksi, newNoReproses, dateTimeSaved);
                savedItems += newNoReproses.size();
                int progress = (savedItems * 100) / totalItems;
                runOnUiThread(() -> customProgressDialog.updateProgress(progress));
            }

            // Kosongkan semua list setelah penyimpanan berhasil
            noS4SList.clear();
            noSTList.clear();
            noMouldingList.clear();
            noFJList.clear();
            noCCList.clear();
            noReprosesList.clear();
            scannedResults.clear();

            runOnUiThread(() -> {
                customProgressDialog.dismiss(); // Tutup progress dialog
                showHistoryDialog(noProduksi);
                Toast.makeText(this, "Proses penyimpanan selesai.", Toast.LENGTH_SHORT).show();

                // Panggil onRowClick dengan data yang terakhir dipilih
                if (selectedProductionData != null) {
                    onRowClick(selectedProductionData);
                } else {
                    Log.w("SaveScannedResults", "Tidak ada data yang dipilih untuk diperbarui.");
                }
            });

            Log.d("SaveScannedResults", "Proses penyimpanan hasil scan selesai");
        });
    }


//------------------------------------------------------------------------------------------------------------------------------------------------------//
//-------------------------------------------HELPER METHOD-------------------------- -------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------------------------------------------------------//


    private void playSound(int soundResource) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Bebaskan resources setelah selesai
        mediaPlayer.start();
    }


    public void setDateToView(String tglProduksi, TextView tglProduksiView) {
        // Gunakan metode dari DateTimeUtils untuk memformat tanggal
        String formattedDate = DateTimeUtils.formatDate(tglProduksi);

        // Set tanggal terformat ke TextView
        tglProduksiView.setText(formattedDate);
    }

    private void setTextColor(TableRow row, int colorResId) {
        for (int i = 0; i < row.getChildCount(); i++) {
            View child = row.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(ContextCompat.getColor(this, colorResId));
            }
        }
    }

    private void resetTextColor(TableRow row) {
        for (int i = 0; i < row.getChildCount(); i++) {
            View child = row.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(ContextCompat.getColor(this, R.color.black)); // Kembalikan ke hitam
            }
        }
    }

    private void showAllLoadingIndicators(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        loadingIndicatorNoS4S.setVisibility(visibility);
        loadingIndicatorNoST.setVisibility(visibility);
        loadingIndicatorNoMoulding.setVisibility(visibility);
        loadingIndicatorNoFJ.setVisibility(visibility);
        loadingIndicatorNoCC.setVisibility(visibility);
        loadingIndicatorNoReproses.setVisibility(visibility);
    }

    private void setAllTableLayoutsVisibility(int visibility) {
        noS4STableLayout.setVisibility(visibility);
        noSTTableLayout.setVisibility(visibility);
        noMouldingTableLayout.setVisibility(visibility);
        noFJTableLayout.setVisibility(visibility);
        noCCTableLayout.setVisibility(visibility);
        noReprosesTableLayout.setVisibility(visibility);
    }

    private void clearAllDataLists() {
        noS4SList.clear();
        noSTList.clear();
        noMouldingList.clear();
        noFJList.clear();
        noCCList.clear();
        noReprosesList.clear();
        scannedResults.clear();
    }

    /**
     * Membuat TextView untuk digunakan dalam tabel
     */
    private TextView createTextView(String text, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 15, 8, 15); // Padding untuk jarak
        textView.setGravity(Gravity.CENTER); // Pusatkan teks di tengah

        // Atur LayoutParams untuk mengatur lebar kolom berdasarkan weight
        textView.setLayoutParams(new TableRow.LayoutParams(
                0, // Lebar proporsional (diatur oleh weight)
                TableRow.LayoutParams.WRAP_CONTENT, // Tinggi mengikuti konten
                weight // Berat untuk membagi lebar
        ));

        return textView;
    }


    // Tambahkan metode untuk membuat garis pembatas
    private View createDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(Color.GRAY); // Warna garis pemisah

        // Set parameter untuk garis tipis (0.5dp)
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                1,
                TableRow.LayoutParams.MATCH_PARENT // Tinggi penuh
        );
        divider.setLayoutParams(params);

        return divider;
    }

    private void showHistoryDialog(String noProduksi) {
        executorService.execute(() -> {
            String filterQuery =
                    "SELECT 'S4S' AS Label, NoS4S AS KodeLabel, DateTimeSaved FROM S4SProduksiInputS4S WHERE NoProduksi = ? " +
                            "UNION ALL " +
                            "SELECT 'ST' AS Label, NoST AS KodeLabel, DateTimeSaved FROM S4SProduksiInputST WHERE NoProduksi = ? " +
                            "UNION ALL " +
                            "SELECT 'Moulding' AS Label, NoMoulding AS KodeLabel, DateTimeSaved FROM S4SProduksiInputMoulding WHERE NoProduksi = ? " +
                            "UNION ALL " +
                            "SELECT 'FJ' AS Label, NoFJ AS KodeLabel, DateTimeSaved FROM S4SProduksiInputFJ WHERE NoProduksi = ? " +
                            "UNION ALL " +
                            "SELECT 'CrossCut' AS Label, NoCCAkhir AS KodeLabel, DateTimeSaved FROM S4SProduksiInputCCAkhir WHERE NoProduksi = ? " +
                            "UNION ALL " +
                            "SELECT 'Reproses' AS Label, NoReproses AS KodeLabel, DateTimeSaved FROM S4SProduksiInputReproses WHERE NoProduksi = ?";

            // 1. Ambil data history dari API
            List<HistoryItem> historyGroups = ProductionApi.getHistoryItems(noProduksi, filterQuery, 6);

            // 2. Siapkan dan proses data (di latar belakang)
            HistorySummary summary = prepareHistorySummary(historyGroups);

            // 3. Tampilkan dialog di UI thread
            runOnUiThread(() -> showHistoryDialogUI(summary, historyGroups));
        });
    }

    private HistorySummary prepareHistorySummary(List<HistoryItem> historyGroups) {
        int totalS4S = 0;
        int totalST = 0;
        int totalMoulding = 0;
        int totalFJ = 0;
        int totalCCAkhir = 0;
        int totalReproses = 0;

        for (HistoryItem group : historyGroups) {
            totalS4S += group.getTotalS4S();
            totalST += group.getTotalST();
            totalMoulding += group.getTotalMoulding();
            totalFJ += group.getTotalFJ();
            totalCCAkhir += group.getTotalCrossCut();
            totalReproses += group.getTotalReproses();
        }

        // Kembalikan hasil summary
        return new HistorySummary(totalS4S, totalST, totalMoulding, totalFJ, totalCCAkhir, totalReproses);
    }
    public class HistorySummary {
        public int totalS4S, totalST, totalMoulding, totalFJ, totalCCAkhir, totalReproses;

        public HistorySummary(int totalS4S, int totalST, int totalMoulding, int totalFJ, int totalCCAkhir, int totalReproses) {
            this.totalS4S = totalS4S;
            this.totalST = totalST;
            this.totalMoulding = totalMoulding;
            this.totalFJ = totalFJ;
            this.totalCCAkhir = totalCCAkhir;
            this.totalReproses = totalReproses;
        }

        public int getTotalAllLabels() {
            return totalS4S + totalST + totalMoulding + totalFJ + totalCCAkhir + totalReproses;
        }
    }

    private void showHistoryDialogUI(HistorySummary summary, List<HistoryItem> historyGroups) {
        // Siapkan dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_history_save, null);
        builder.setView(dialogView);

        // Inisialisasi elemen UI
        LinearLayout historyContainer = dialogView.findViewById(R.id.historyContainer);
        ImageButton btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);
        TextView tvSumS4S = dialogView.findViewById(R.id.tvSumS4S);
        TextView tvSumST = dialogView.findViewById(R.id.tvSumST);
        TextView tvSumMoulding = dialogView.findViewById(R.id.tvSumMoulding);
        TextView tvSumFJ = dialogView.findViewById(R.id.tvSumFJ);
        TextView tvSumCCAkhir = dialogView.findViewById(R.id.tvSumCCAkhir);
        TextView tvSumLabel = dialogView.findViewById(R.id.tvSumLabel);

        // Tambahkan data ke historyContainer
        populateHistoryItems(historyGroups, historyContainer, inflater);

        // Tampilkan jumlah masing-masing label
        tvSumS4S.setText(String.valueOf(summary.totalS4S));
        tvSumST.setText(String.valueOf(summary.totalST));
        tvSumMoulding.setText(String.valueOf(summary.totalMoulding));
        tvSumFJ.setText(String.valueOf(summary.totalFJ));
        tvSumCCAkhir.setText(String.valueOf(summary.totalCCAkhir));
        tvSumLabel.setText(String.valueOf(summary.getTotalAllLabels()));

        // Tutup dialog
        btnCloseDialog.setOnClickListener(v -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void populateHistoryItems(List<HistoryItem> historyGroups, LinearLayout historyContainer, LayoutInflater inflater) {
        for (HistoryItem group : historyGroups) {
            View itemView = inflater.inflate(R.layout.history_item, null);

            // Header data
            TextView tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            TextView tvTotalCount = itemView.findViewById(R.id.tvTotalCount);
            MaterialCardView dropdownContainer = itemView.findViewById(R.id.dropdownContainer);
            LinearLayout dropdownContent = dropdownContainer.findViewById(R.id.dropdownContent);
            ImageView dropdownIcon = itemView.findViewById(R.id.dropdownIcon);

            // Set data untuk header
            tvTimestamp.setText(DateTimeUtils.formatDateTime(group.getDateTimeSaved()));
            tvTotalCount.setText(String.valueOf(group.getTotalAllLabels()));

            // Tambahkan detail item ke dropdownContent
            for (HistoryItem labelItem : group.getItems()) {
                TextView detailView = new TextView(this);
                detailView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                detailView.setText("x" + labelItem.getLabelCount() + " " + labelItem.getLabel());
                detailView.setTextSize(12);
                detailView.setTextColor(getResources().getColor(R.color.black));
                detailView.setPadding(4, 4, 4, 4);

                dropdownContent.addView(detailView);
            }

            // Klik listener untuk toggle dropdown
            itemView.setOnClickListener(v -> {
                if (dropdownContainer.getVisibility() == View.GONE) {
                    dropdownContainer.setVisibility(View.VISIBLE);
                    dropdownIcon.setRotation(360);
                } else {
                    dropdownContainer.setVisibility(View.GONE);
                    dropdownIcon.setRotation(270);
                }
            });

            historyContainer.addView(itemView);
        }
    }

    private void onRowClick(ProductionData data) {
        // Tampilkan semua indikator loading
        showAllLoadingIndicators(true);

        // Sembunyikan semua tabel
        setAllTableLayoutsVisibility(View.GONE);

        // Bersihkan semua data
        clearAllDataLists();

        executorService.execute(() -> {
            // Ambil data latar belakang
            noProduksi = data.getNoProduksi();
            tglProduksi = data.getTanggal();
            mesinProduksi = data.getMesin();

            // Ambil data untuk setiap tabel
            noS4SList = ProductionApi.getNoS4SByNoProduksi(noProduksi, "S4SProduksiInputS4S");
            noSTList = ProductionApi.getNoSTByNoProduksi(noProduksi);
            noMouldingList = ProductionApi.getNoMouldingByNoProduksi(noProduksi, "S4SProduksiInputMoulding");
            noFJList = ProductionApi.getNoFJByNoProduksi(noProduksi, "S4SProduksiInputFJ");
            noCCList = ProductionApi.getNoCCByNoProduksi(noProduksi, "S4SProduksiInputCCAkhir");
            noReprosesList = ProductionApi.getNoReprosesByNoProduksi(noProduksi);

            // Perbarui UI di thread utama
            runOnUiThread(() -> {
                // Set data ke TextView
                noProduksiView.setText(noProduksi);
                setDateToView(tglProduksi, tglProduksiView);
                mesinProduksiView.setText(mesinProduksi);

                // Populate semua tabel dan sembunyikan loading indikator
                updateTable(noS4SList, sumS4SLabel, loadingIndicatorNoS4S, noS4STableLayout, this::populateNoS4STable);
                updateTable(noSTList, sumSTLabel, loadingIndicatorNoST, noSTTableLayout, this::populateNoSTTable);
                updateTable(noMouldingList, sumMouldingLabel, loadingIndicatorNoMoulding, noMouldingTableLayout, this::populateNoMouldingTable);
                updateTable(noFJList, sumFJLabel, loadingIndicatorNoFJ, noFJTableLayout, this::populateNoFJTable);
                updateTable(noCCList, sumCCLabel, loadingIndicatorNoCC, noCCTableLayout, this::populateNoCCTable);
                updateTable(noReprosesList, sumReprosesLabel, loadingIndicatorNoReproses, noReprosesTableLayout, this::populateNoReprosesTable);
            });
        });
    }

    private <T> void updateTable(
            List<T> dataList,
            TextView sumLabel,
            ProgressBar loadingIndicator,
            View tableLayout,
            Consumer<List<T>> populateTableMethod
    ) {
        populateTableMethod.accept(dataList);
        sumLabel.setText(String.valueOf(dataList.size()));
        loadingIndicator.setVisibility(View.GONE);
        tableLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}