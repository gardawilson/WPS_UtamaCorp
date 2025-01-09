package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.graphics.Typeface;
import android.app.AlertDialog;
import android.Manifest;

import android.graphics.drawable.ColorDrawable;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.api.ProductionApi;
import com.example.myapplication.model.ProductionData;
import com.example.myapplication.utils.CameraUtils;
import com.example.myapplication.utils.CameraXAnalyzer;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;



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
    private Button btnInputKodeLabel;
    private LinearLayout inputKodeManual;
    private List<String> noS4SList = new ArrayList<>(); // Daftar untuk kode 'R'
    private List<String> noSTList = new ArrayList<>();  // Daftar untuk kode 'E'
    private List<String> noMouldingList = new ArrayList<>();  // Daftar untuk kode 'T'
    private List<String> noFJList = new ArrayList<>();  // Daftar untuk kode 'S'
    private List<String> noCCList = new ArrayList<>();  // Daftar untuk kode 'V'
    private List<String> noReprosesList = new ArrayList<>();  // Daftar untuk kode 'Y'
    private List<ProductionData> dataList; // Data asli yang tidak difilter
    private ProgressBar loadingIndicator;
    private TableLayout noS4STableLayout;
    private TableLayout noSTTableLayout;
    private TableLayout noMouldingTableLayout;
    private TableLayout noFJTableLayout;
    private TableLayout noCCTableLayout;
    private TableLayout noReprosesTableLayout;

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


        loadingIndicator.setVisibility(View.VISIBLE);

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
            dataList = ProductionApi.getProductionData();

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
    }

    //METHOD S4S

    private void playSound(int soundResource) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Bebaskan resources setelah selesai
        mediaPlayer.start();
    }


    public void setDateToView(String tglProduksi, TextView tglProduksiView) {
        // Variabel untuk menyimpan tanggal asli dan tanggal tampilan
        String originalDate = tglProduksi; // Format asli dari database
        String formattedDate;

        try {
            // Format input dan output
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");

            // Parsing tanggal dan format ulang
            Date date = inputFormat.parse(originalDate);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            Log.e("DateFormatError", "Gagal memformat tanggal: " + e.getMessage());
            // Jika gagal, gunakan tanggal asli untuk tampilan
            formattedDate = originalDate;
        }

        // Set tanggal ke TextView
        tglProduksiView.setText(formattedDate);
    }

    private void showCustomProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // Dialog tidak bisa ditutup dengan back button atau di luar
        View dialogView = getLayoutInflater().inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);

        progressDialog = builder.create();
        progressDialog.show();

        // Inisialisasi elemen dari layout
        progressText = dialogView.findViewById(R.id.progress_text);
        progressBar = dialogView.findViewById(R.id.progress_bar);

        // Set nilai awal
        progressText.setText("0%");
        progressBar.setProgress(0);
    }

    private void updateProgressDialog(int progress) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressText.setText(progress + "%");
            progressBar.setProgress(progress);
        }
    }

    private void dismissCustomProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void saveScannedResultsToDatabase() {
        showCustomProgressDialog(); // Tampilkan dialog loading

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Log.d("SaveScannedResults", "Memulai proses penyimpanan hasil scan ke database");

            // Pisahkan hasil scan berdasarkan prefix
            List<String> noS4SList = new ArrayList<>();
            List<String> noSTList = new ArrayList<>();
            List<String> noMouldingList = new ArrayList<>();
            List<String> noFJList = new ArrayList<>();
            List<String> noCCList = new ArrayList<>();
            List<String> noReprosesList = new ArrayList<>();

            for (String result : scannedResults) {
                if (result.startsWith("R")) {
                    noS4SList.add(result);
                } else if (result.startsWith("E")) {
                    noSTList.add(result);
                } else if (result.startsWith("T")) {
                    noMouldingList.add(result);
                } else if (result.startsWith("S")) {
                    noFJList.add(result);
                } else if (result.startsWith("V")) {
                    noCCList.add(result);
                } else if (result.startsWith("Y")) {
                    noReprosesList.add(result);
                }
            }

            int totalItems = noS4SList.size() + noSTList.size() + noMouldingList.size() +
                    noFJList.size() + noCCList.size() + noReprosesList.size();
            int savedItems = 0;

            // Proses penyimpanan untuk tabel S4S
            if (!noS4SList.isEmpty()) {
                List<String> existingNoS4S = ProductionApi.getNoS4SByNoProduksi(noProduksi);
                List<String> newNoS4S = new ArrayList<>(noS4SList);
                newNoS4S.removeAll(existingNoS4S);
                ProductionApi.saveNoS4S(noProduksi, tglProduksi, newNoS4S);
                savedItems += newNoS4S.size();
                int progress = (savedItems * 100) / totalItems;
                updateProgressDialog(progress); // Perbarui dialog progress
            }

            // Proses penyimpanan untuk tabel ST
            if (!noSTList.isEmpty()) {
                List<String> existingNoST = ProductionApi.getNoSTByNoProduksi(noProduksi);
                List<String> newNoST = new ArrayList<>(noSTList);
                newNoST.removeAll(existingNoST);
                ProductionApi.saveNoST(noProduksi, tglProduksi, newNoST);
                savedItems += newNoST.size();
                int progress = (savedItems * 100) / totalItems;
                updateProgressDialog(progress); // Perbarui dialog progress
            }

            // Proses penyimpanan untuk tabel Moulding
            if (!noMouldingList.isEmpty()) {
                List<String> existingNoMoulding = ProductionApi.getNoMouldingByNoProduksi(noProduksi);
                List<String> newNoMoulding = new ArrayList<>(noMouldingList);
                newNoMoulding.removeAll(existingNoMoulding);
                ProductionApi.saveNoMoulding(noProduksi, tglProduksi, newNoMoulding);
                savedItems += newNoMoulding.size();
                int progress = (savedItems * 100) / totalItems;
                updateProgressDialog(progress); // Perbarui dialog progress
            }

            // Proses penyimpanan untuk tabel FJ
            if (!noFJList.isEmpty()) {
                List<String> existingNoFJ = ProductionApi.getNoFJByNoProduksi(noProduksi);
                List<String> newNoFJ = new ArrayList<>(noFJList);
                newNoFJ.removeAll(existingNoFJ);
                ProductionApi.saveNoFJ(noProduksi, tglProduksi, newNoFJ);
                savedItems += newNoFJ.size();
                int progress = (savedItems * 100) / totalItems;
                updateProgressDialog(progress); // Perbarui dialog progress
            }

            // Proses penyimpanan untuk tabel CC
            if (!noCCList.isEmpty()) {
                List<String> existingNoCC = ProductionApi.getNoCCByNoProduksi(noProduksi);
                List<String> newNoCC = new ArrayList<>(noCCList);
                newNoCC.removeAll(existingNoCC);
                ProductionApi.saveNoCC(noProduksi, tglProduksi, newNoCC);
                savedItems += newNoCC.size();
                int progress = (savedItems * 100) / totalItems;
                updateProgressDialog(progress); // Perbarui dialog progress
            }

            // Proses penyimpanan untuk tabel Reproses
            if (!noReprosesList.isEmpty()) {
                List<String> existingNoReproses = ProductionApi.getNoReprosesByNoProduksi(noProduksi);
                List<String> newNoReproses = new ArrayList<>(noReprosesList);
                newNoReproses.removeAll(existingNoReproses);
                ProductionApi.saveNoReproses(noProduksi, tglProduksi, newNoReproses);
                savedItems += newNoReproses.size();
                int progress = (savedItems * 100) / totalItems;
                updateProgressDialog(progress); // Perbarui dialog progress
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
                dismissCustomProgressDialog(); // Tutup dialog loading
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

    private void startScanningAnimation(View scannerOverlay) {
        // Dapatkan tinggi layar
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenHeight = displayMetrics.heightPixels;

        // Buat animator untuk memindahkan overlay naik turun
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                scannerOverlay,
                "translationY",
                0f,
                screenHeight
        );

        // Konfigurasi animasi
        animator.setDuration(1500); // Durasi animasi (ms)
        animator.setRepeatMode(ObjectAnimator.REVERSE); // Animasi bolak-balik
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Ulang terus

        // Mulai animasi
        animator.start();
    }


    /**
     * Menyiapkan tombol kontrol kamera
     */
    private void setupCameraControlButton() {
        updateButtonText(); // Set teks awal tombol
        btnCameraControl.setOnClickListener(v -> {
            if (isCameraActive) {
                deactivateCamera();
            } else {
                if(!noProduksiView.getText().toString().isEmpty()){
                    // Minta permission kamera langsung saat halaman dibuka
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
                else{
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
                View borderTop = findViewById(R.id.borderTop);
                View borderBottom = findViewById(R.id.borderBottom);
                View borderLeft = findViewById(R.id.borderLeft);
                View borderRight = findViewById(R.id.borderRight);

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

                                    // Ubah warna border menjadi kuning
                                    borderTop.setBackgroundResource(R.drawable.border_focus);
                                    borderBottom.setBackgroundResource(R.drawable.border_focus);
                                    borderLeft.setBackgroundResource(R.drawable.border_focus);
                                    borderRight.setBackgroundResource(R.drawable.border_focus);

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
                View scannerOverlay = findViewById(R.id.scannerOverlay);
                startScanningAnimation(scannerOverlay);
                scanLayout.setVisibility(View.VISIBLE);
                cameraPreview.setVisibility(View.VISIBLE);
                inputKodeManual.setVisibility(View.VISIBLE);
                tableLayout.setVisibility(View.GONE);
                headerTableProduksi.setVisibility(View.GONE);
                searchMainTable.setVisibility(View.INVISIBLE);
                textLayoutSearchMainTable.setVisibility(View.INVISIBLE);

                isCameraActive = true;
                Toast.makeText(this, "Kamera diaktifkan", Toast.LENGTH_SHORT).show();
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

            scanLayout.setVisibility(View.GONE);
            cameraPreview.setVisibility(View.GONE);
            inputKodeManual.setVisibility(View.GONE);
            tableLayout.setVisibility(View.VISIBLE);
            headerTableProduksi.setVisibility(View.VISIBLE);
            searchMainTable.setVisibility(View.VISIBLE);
            textLayoutSearchMainTable.setVisibility(View.VISIBLE);

            isCameraActive = false;
            Toast.makeText(this, "Kamera dinonaktifkan", Toast.LENGTH_SHORT).show();
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

    /**
     * Menambahkan hasil QR Code ke dalam tabel hasil scan
     */

    private final Map<String, Long> recentlyAddedResults = new HashMap<>();
    private static final long RECENTLY_ADDED_INTERVAL = 2000; // 2 detik
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastToastTime = 0; // Untuk throttling toast
    private static final long TOAST_INTERVAL = 2000; // Interval toast (2 detik)



    private void addScanResultToTable(String result) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            synchronized (scannedResults) {
                // Abaikan jika hasil baru saja ditambahkan
                if (recentlyAddedResults.containsKey(result) && System.currentTimeMillis() - recentlyAddedResults.get(result) < RECENTLY_ADDED_INTERVAL) {
                    return;
                }

                if (!scannedResults.contains(result)) {
                    scannedResults.add(result);
                    recentlyAddedResults.put(result, System.currentTimeMillis());

                    // Jadwalkan penghapusan elemen dari recentlyAddedResults
                    handler.postDelayed(() -> {
                        synchronized (recentlyAddedResults) {
                            recentlyAddedResults.remove(result);
                        }
                    }, RECENTLY_ADDED_INTERVAL);

                    String tableProduksi = "S4SProduksi_h";

                    Map<String, TableConfig> tableConfigMap = new HashMap<>();
                    tableConfigMap.put("R", new TableConfig("S4S_h", "S4S_d", "NoS4S", R.id.noS4STableLayout, noS4SList, ProductionApi::findS4SResultTable));
                    tableConfigMap.put("E", new TableConfig("ST_h", "ST_d", "NoST", R.id.noSTTableLayout, noSTList, ProductionApi::findSTResultTable));
                    tableConfigMap.put("T", new TableConfig("Moulding_h", "Moulding_d", "NoMoulding", R.id.noMouldingTableLayout, noMouldingList, ProductionApi::findMouldingResultTable));
                    tableConfigMap.put("S", new TableConfig("FJ_h", "FJ_d", "NoFJ", R.id.noFJTableLayout, noFJList, ProductionApi::findFJResultTable));
                    tableConfigMap.put("V", new TableConfig("CCAkhir_h", "CCAkhir_d", "NoCCAkhir", R.id.noCCTableLayout, noCCList, ProductionApi::findCCAkhirResultTable));
                    tableConfigMap.put("Y", new TableConfig("Reproses_h", "Reproses_d", "NoReproses", R.id.noReprosesTableLayout, noReprosesList, ProductionApi::findReprosesResultTable));

                    TableConfig config = tableConfigMap.get(result.substring(0, 1));

                    if (config == null) {
                        runOnUiThread(() -> {
                            showToastAndPlaySound("Label " + result + " tidak sesuai", R.raw.denied_data);
                        });
                        scannedResults.remove(result);
                        return;
                    }

                    if (ProductionApi.isDataExists(result, config.tableNameH, config.tableNameD, config.columnName)) {
                        if (ProductionApi.isDateUsageNull(result, config.tableNameH, config.columnName)) {
                            if (ProductionApi.isDateValid(noProduksi, tableProduksi, result, config.tableNameH, config.columnName)) {
                                runOnUiThread(() -> {
                                    TableLayout targetTableLayout = findViewById(config.tableLayoutId);
                                    Log.d("Configlist", "configlist: " + config.list);

                                    if (config.list == null || config.list.isEmpty()) {
                                        targetTableLayout.removeAllViews();
                                    }
                                    config.list.add(result);
                                    addRowToTable(targetTableLayout, result, config.list);

                                    playSound(R.raw.granted_data);
                                });

                            } else {
                                showToastAndPlaySound("Tgl Label lebih besar dari Tgl Produksi " + result, R.raw.denied_data);
                                scannedResults.remove(result);
                            }
                        } else {
                            String namaTabel = config.resultChecker.apply(result);

                            if (namaTabel == null) {
                                showToastAndPlaySound("Label " + result + " tidak ada di Proses manapun", R.raw.denied_data);
                            } else {
                                showToastAndPlaySound("Label " + result + " sudah ada pada " + namaTabel, R.raw.denied_data);
                                scannedResults.remove(result);
                            }
                        }
                    } else {
                        showToastAndPlaySound("Label " + result + " Tidak ditemukan di Database", R.raw.denied_data);
                        scannedResults.remove(result);
                    }

                } else {
                    Log.d("DuplicateScan", "Hasil scan sudah ada: " + result);
                    showToastAndPlaySound("Label " + result + " Sudah di Masukkan ke dalam Tabel", R.raw.denied_data);
                }
            }
        });
        executor.shutdown();
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


    public class TableConfig {
        public final String tableNameH;
        public final String tableNameD;
        public final String columnName;
        public final int tableLayoutId;
        public final List<String> list;
        public final Function<String, String> resultChecker;

        public TableConfig(String tableNameH, String tableNameD, String columnName, int tableLayoutId, List<String> list, Function<String, String> resultChecker) {
            this.tableNameH = tableNameH;
            this.tableNameD = tableNameD;
            this.columnName = columnName;
            this.tableLayoutId = tableLayoutId;
            this.list = list;
            this.resultChecker = resultChecker;
        }
    }




    private void addRowToTable(TableLayout tableLayout, String result, List<String> list) {
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

        // Buat tombol delete
        Button deleteButton = new Button(this);
        deleteButton.setText("-");
        deleteButton.setPadding(0, 0, 0, 0);
        deleteButton.setMinWidth(0);
        deleteButton.setMinimumHeight(0);
        deleteButton.setLayoutParams(new TableRow.LayoutParams(50, 50));

        deleteButton.setOnClickListener(v -> {
            try {
                // Hapus baris dari tabel
                tableLayout.removeView(row);

                // Hapus data dari daftar
                list.remove(result);
                scannedResults.remove(result);

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

    private void onRowClick(ProductionData data) {

        ProgressBar loadingIndicatorNoS4S = findViewById(R.id.loadingIndicatorNoS4S);
        ProgressBar loadingIndicatorNoST = findViewById(R.id.loadingIndicatorNoST);
        ProgressBar loadingIndicatorNoMoulding = findViewById(R.id.loadingIndicatorNoMoulding);
        ProgressBar loadingIndicatorNoFJ = findViewById(R.id.loadingIndicatorNoFJ);
        ProgressBar loadingIndicatorNoCC = findViewById(R.id.loadingIndicatorNoCC);
        ProgressBar loadingIndicatorNoReproses = findViewById(R.id.loadingIndicatorNoReproses);

        loadingIndicatorNoS4S.setVisibility(View.VISIBLE);
        loadingIndicatorNoST.setVisibility(View.VISIBLE);
        loadingIndicatorNoMoulding.setVisibility(View.VISIBLE);
        loadingIndicatorNoFJ.setVisibility(View.VISIBLE);
        loadingIndicatorNoCC.setVisibility(View.VISIBLE);
        loadingIndicatorNoReproses.setVisibility(View.VISIBLE);

        noS4STableLayout.setVisibility(View.GONE);
        noSTTableLayout.setVisibility(View.GONE);
        noMouldingTableLayout.setVisibility(View.GONE);
        noFJTableLayout.setVisibility(View.GONE);
        noCCTableLayout.setVisibility(View.GONE);
        noReprosesTableLayout.setVisibility(View.GONE);

        noS4SList.clear();
        noSTList.clear();
        noMouldingList.clear();
        noFJList.clear();
        noCCList.clear();
        noReprosesList.clear();
        scannedResults.clear();

        executorService.execute(() -> {
            // Ambil data latar belakang
            noProduksi = data.getNoProduksi();
            tglProduksi = data.getTanggal();
            mesinProduksi = data.getMesin();

            noS4SList = ProductionApi.getNoS4SByNoProduksi(noProduksi);
            noSTList = ProductionApi.getNoSTByNoProduksi(noProduksi);
            noMouldingList = ProductionApi.getNoMouldingByNoProduksi(noProduksi);
            noFJList = ProductionApi.getNoFJByNoProduksi(noProduksi);
            noCCList = ProductionApi.getNoCCByNoProduksi(noProduksi);
            noReprosesList = ProductionApi.getNoReprosesByNoProduksi(noProduksi);

            // Perbarui UI di thread utama
            runOnUiThread(() -> {
                // Set data ke TextView
                noProduksiView.setText(noProduksi);
                setDateToView(tglProduksi, tglProduksiView);
                mesinProduksiView.setText(mesinProduksi);

                // Populate tabel
                populateNoS4STable(noS4SList);
                loadingIndicatorNoS4S.setVisibility(View.GONE);
                noS4STableLayout.setVisibility(View.VISIBLE);

                populateNoSTTable(noSTList);
                loadingIndicatorNoST.setVisibility(View.GONE);
                noSTTableLayout.setVisibility(View.VISIBLE);

                populateNoMouldingTable(noMouldingList);
                loadingIndicatorNoMoulding.setVisibility(View.GONE);
                noMouldingTableLayout.setVisibility(View.VISIBLE);

                populateNoFJTable(noFJList);
                loadingIndicatorNoFJ.setVisibility(View.GONE);
                noFJTableLayout.setVisibility(View.VISIBLE);

                populateNoCCTable(noCCList);
                loadingIndicatorNoCC.setVisibility(View.GONE);
                noCCTableLayout.setVisibility(View.VISIBLE);

                populateNoReprosesTable(noReprosesList);
                loadingIndicatorNoReproses.setVisibility(View.GONE);
                noReprosesTableLayout.setVisibility(View.VISIBLE);

            });
        });
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
            row.addView(createTextView(noS4S, 1.0f));
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
            row.addView(createTextView(noST, 1.0f));
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
            row.addView(createTextView(noMoulding, 1.0f));
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
            row.addView(createTextView(noFJ, 1.0f));
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
            row.addView(createTextView(noCC, 1.0f));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}