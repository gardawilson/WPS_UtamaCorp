package com.example.myapplication.model;

import java.util.List;

public class TooltipData {

    // Atribut utama dari data tooltip
    private String noLabel; // Nomor Label
    private String formattedDateTime; // Gabungan tanggal dan waktu (sudah diformat)
    private String jenis; // Jenis kayu
    private String spkDetail; // Detail SPK
    private String spkAsalDetail; // Detail SPK asal
    private String namaGrade; // Nama grade kayu
    private boolean isLembur; // Apakah lembur atau tidak

    // Data tabel (detail rows)
    private List<String[]> tableData; // Data tabel sebagai list of rows (array string)
    private int totalPcs; // Total Pcs (jumlah batang)
    private double totalM3; // Total M3 (volume kayu)

    // Constructor kosong (jika diperlukan)
    public TooltipData() {
    }

    // Constructor untuk mengisi semua atribut
    public TooltipData(String noLabel, String formattedDateTime, String jenis, String spkDetail, String spkAsalDetail, String namaGrade, boolean isLembur, List<String[]> tableData, int totalPcs, double totalM3) {
        this.noLabel = noLabel;
        this.formattedDateTime = formattedDateTime;
        this.jenis = jenis;
        this.spkDetail = spkDetail;
        this.spkAsalDetail = spkAsalDetail;
        this.namaGrade = namaGrade;
        this.isLembur = isLembur;
        this.tableData = tableData;
        this.totalPcs = totalPcs;
        this.totalM3 = totalM3;
    }

    // Getter dan Setter untuk semua atribut

    public String getNoLabel() {
        return noLabel;
    }

    public void setNoS4S(String noLabel) {
        this.noLabel = noLabel;
    }

    public String getFormattedDateTime() {
        return formattedDateTime;
    }

    public void setFormattedDateTime(String formattedDateTime) {
        this.formattedDateTime = formattedDateTime;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getSpkDetail() {
        return spkDetail;
    }

    public void setSpkDetail(String spkDetail) {
        this.spkDetail = spkDetail;
    }

    public String getSpkAsalDetail() {
        return spkAsalDetail;
    }

    public void setSpkAsalDetail(String spkAsalDetail) {
        this.spkAsalDetail = spkAsalDetail;
    }

    public String getNamaGrade() {
        return namaGrade;
    }

    public void setNamaGrade(String namaGrade) {
        this.namaGrade = namaGrade;
    }

    public boolean isLembur() {
        return isLembur;
    }

    public void setLembur(boolean lembur) {
        isLembur = lembur;
    }

    public List<String[]> getTableData() {
        return tableData;
    }

    public void setTableData(List<String[]> tableData) {
        this.tableData = tableData;
    }

    public int getTotalPcs() {
        return totalPcs;
    }

    public void setTotalPcs(int totalPcs) {
        this.totalPcs = totalPcs;
    }

    public double getTotalM3() {
        return totalM3;
    }

    public void setTotalM3(double totalM3) {
        this.totalM3 = totalM3;
    }
}