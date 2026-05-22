package com.pharmacy.dto;

import java.util.List;

public class InventoryReportDTO {
    private String totalMedicines;
    private String lowStockCount;
    private String nearExpiryCount;
    private String expiredCount;
    private String inventoryValue;
    private List<MedicineRow> tableData;
    private List<ChartData> categoryDistribution;
    private List<ChartData> slowestMoving;

    // Getters and Setters
    public String getTotalMedicines() { return totalMedicines; }
    public void setTotalMedicines(String totalMedicines) { this.totalMedicines = totalMedicines; }

    public String getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(String lowStockCount) { this.lowStockCount = lowStockCount; }

    public String getNearExpiryCount() { return nearExpiryCount; }
    public void setNearExpiryCount(String nearExpiryCount) { this.nearExpiryCount = nearExpiryCount; }

    public String getExpiredCount() { return expiredCount; }
    public void setExpiredCount(String expiredCount) { this.expiredCount = expiredCount; }

    public String getInventoryValue() { return inventoryValue; }
    public void setInventoryValue(String inventoryValue) { this.inventoryValue = inventoryValue; }

    public List<MedicineRow> getTableData() { return tableData; }
    public void setTableData(List<MedicineRow> tableData) { this.tableData = tableData; }

    public List<ChartData> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(List<ChartData> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public List<ChartData> getSlowestMoving() { return slowestMoving; }
    public void setSlowestMoving(List<ChartData> slowestMoving) { this.slowestMoving = slowestMoving; }

    public static class MedicineRow {
        private String maThuoc;
        private String tenThuoc;
        private String phanLoai;
        private Long tonKho;
        private String hanSuDung;
        private String tinhTrang;

        // Getters and Setters
        public String getMaThuoc() { return maThuoc; }
        public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

        public String getTenThuoc() { return tenThuoc; }
        public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

        public String getPhanLoai() { return phanLoai; }
        public void setPhanLoai(String phanLoai) { this.phanLoai = phanLoai; }

        public Long getTonKho() { return tonKho; }
        public void setTonKho(Long tonKho) { this.tonKho = tonKho; }

        public String getHanSuDung() { return hanSuDung; }
        public void setHanSuDung(String hanSuDung) { this.hanSuDung = hanSuDung; }

        public String getTinhTrang() { return tinhTrang; }
        public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
    }

    public static class ChartData {
        private String label;
        private Long value;

        // Getters and Setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Long getValue() { return value; }
        public void setValue(Long value) { this.value = value; }
    }
}