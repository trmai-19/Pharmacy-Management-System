package com.pharmacy.controller.admin;

import com.pharmacy.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductManagerController {

    // Thống kê
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStock;
    @FXML private Label lblExpiring;

    // Thanh công cụ
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;

    // Bảng và Cột
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, String> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colActiveIngredient;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colUnit;
    @FXML private TableColumn<Product, String> colQuantity;
    @FXML private TableColumn<Product, String> colExpiryDate;
    @FXML private TableColumn<Product, String> colStatus;

    private ObservableList<Product> productList;
    private FilteredList<Product> filteredData;

    @FXML
    public void initialize() {
        System.out.println("📦 ProductManagerController đang tải...");

        // 1. Ánh xạ các cột với Model Product
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colActiveIngredient.setCellValueFactory(cellData -> cellData.getValue().activeIngredientProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        colExpiryDate.setCellValueFactory(cellData -> cellData.getValue().expiryDateProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // 2. Khởi tạo dữ liệu Dropdown Danh mục
        cbCategory.setItems(FXCollections.observableArrayList(
                "Tất cả danh mục", "Kháng sinh", "Giảm đau - Hạ sốt", "Vitamin - Khoáng chất", "Thực phẩm chức năng", "Vật tư y tế"
        ));
        cbCategory.getSelectionModel().selectFirst();

        // 3. Tải dữ liệu giả lập (Mock Data chuẩn ngành Dược)
        loadMockData();

        // 4. Thiết lập tính năng Tìm kiếm & Lọc thời gian thực
        setupSearchAndFilter();
    }

    private void loadMockData() {
        productList = FXCollections.observableArrayList(
                new Product("SP001", "Avastin 400mg Injection", "Bevacizumab (400mg)", "Cancer of colon and rectum Non-small cell lung cancer Kidney cancer Brain tumor Ovarian cancer Cervical cancer", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP002", "Augmentin 625 Duo Tablet", "Amoxycillin  (500mg) +  Clavulanic Acid (125mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP003", "Azithral 500 Tablet", "Azithromycin (500mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP004", "Ascoril LS Syrup", "Ambroxol (30mg/5ml) + Levosalbutamol (1mg/5ml) + Guaifenesin (50mg/5ml)", "Cough with mucus", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP005", "Aciloc 150 Tablet", "Ranitidine (150mg)", "Peptic ulcer disease", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP006", "Allegra 120mg Tablet", "Fexofenadine (120mg)", "Allergic conditions", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP007", "Avil 25 Tablet", "Pheniramine (25mg)", "Skin conditions with inflammation & itchingTreatment and prevention of Meniere's disease", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP008", "Aricep 5 Tablet", "Donepezil (5mg)", "Alzheimer's disease", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP009", "Amoxyclav 625 Tablet", "Amoxycillin  (500mg) +  Clavulanic Acid (125mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP010", "Atarax 25mg Tablet", "Hydroxyzine (25mg)", "Skin conditions with inflammation & itching", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP011", "Azee 500 Tablet", "Azithromycin (500mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP012", "Anovate Cream", "Phenylephrine (0.10% w/w) + Beclometasone (0.025% w/w) + Lidocaine (2.50% w/w)", "Piles", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP013", "Allegra-M Tablet", "Montelukast (10mg) + Fexofenadine (120mg)", "Sneezing and runny nose due to allergies", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP014", "Ascoril D Plus Syrup Sugar Free", "Phenylephrine (5mg) + Chlorpheniramine Maleate (2mg) + Dextromethorphan Hydrobromide (10mg)", "Dry cough", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP015", "Alex Syrup", "Phenylephrine (5mg/5ml) + Chlorpheniramine Maleate (2mg/5ml) + Dextromethorphan Hydrobromide (10mg/5ml)", "Dry cough", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP016", "Armotraz Tablet", "Anastrozole (1mg)", "Breast cancer", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP017", "Augmentin Duo Oral Suspension", "Amoxycillin  (200mg) +  Clavulanic Acid (28.5mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP018", "Albendazole 400mg Tablet", "Albendazole (400mg)", "Parasitic infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP019", "Arkamin Tablet", "Clonidine (100mcg)", "Hypertension (high blood pressure)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP020", "Allegra 180mg Tablet", "Fexofenadine (180mg)", "Allergic conditions", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP021", "Asthalin 100mcg Inhaler", "Salbutamol (100mcg)", "Asthma Chronic obstructive pulmonary disease (COPD)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP022", "Azee 250 Tablet", "Azithromycin (250mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP023", "Amlokind-AT Tablet", "Amlodipine (5mg) + Atenolol (50mg)", "Hypertension (high blood pressure)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP024", "Atarax 10mg Tablet", "Hydroxyzine (10mg)", "Anxiety Skin conditions with inflammation & itching", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP025", "Amoxyclav 625 Tablet", "Amoxycillin  (500mg) +  Clavulanic Acid (125mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP026", "Amlong Tablet", "Amlodipine (5mg)", "Hypertension (high blood pressure)Prevention of Angina (heart-related chest pain)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP027", "Azee 500 Tablet", "Azithromycin (500mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP028", "Aciloc 300 Tablet", "Ranitidine (300mg)", "Gastroesophageal reflux disease (Acid reflux)Treatment of Peptic ulcer disease", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP029", "Amaryl 1mg Tablet", "Glimepiride (1mg)", "Type 2 diabetes mellitus", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP030", "Alkasol Oral Solution", "Disodium Hydrogen Citrate (1.37gm/5ml)", "GoutTreatment of Kidney stone", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP031", "Ativan 1mg Tablet", "Lorazepam (1mg)", "Short term anxiety", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP032", "Amaryl 2mg Tablet", "Glimepiride (2mg)", "Type 2 diabetes mellitus", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP033", "Amlokind 5 Tablet", "Amlodipine (5mg)", "Hypertension (high blood pressure)Prevention of Angina (heart-related chest pain)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP034", "Anafortan Tablet", "Camylofin (25mg) + Paracetamol (300mg)", "Abdominal pain", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP035", "Azithral 200 Liquid", "Azithromycin (200mg/5ml)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP036", "Ativan 2mg Tablet", "Lorazepam (2mg)", "Short term anxiety", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP037", "Aquasol A Capsule", "Vitamin A (50000IU)", "Vitamin A deficiency", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP038", "Ascoril LS Drops", "Ambroxol (7.5mg/ml) + Levosalbutamol (0.25mg/ml) + Guaifenesin (12.5mg/ml)", "Cough with mucus", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP039", "Azopt Eye Drop", "Brinzolamide (1% w/v)", "Glaucoma", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP040", "Aldactone Tablet", "Spironolactone (25mg)", "Hypertension (high blood pressure)Treatment of EdemaTreatment of Low potassium Heart failure", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP041", "Actrapid HM Penfill", "Human Insulin/Soluble Insulin (100IU/ml)", "Diabetes mellitus", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP042", "Asthalin 4 Tablet", "Salbutamol (4mg)", "Asthma Chronic obstructive pulmonary disease (COPD)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP043", "Axtar 1.5gm Injection", "Ceftriaxone (1000mg) + Sulbactam (500mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP044", "Abzorb Dusting Powder", "Clotrimazole (1% w/w)", "Fungal skin infections", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP045", "App UP Tablet", "Cyproheptadine (4mg)", "Loss of appetite", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP046", "Aldigesic-SP Tablet", "Aceclofenac (100mg) + Paracetamol (325mg) + Serratiopeptidase (15mg)", "Pain relief", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP047", "Amodep AT Tablet", "Amlodipine (5mg) + Atenolol (50mg)", "Hypertension (high blood pressure)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP048", "Asthalin Respirator Solution", "Salbutamol (5mg/ml)", "Asthma Chronic obstructive pulmonary disease (COPD)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP049", "Angispan - TR 2.5mg Capsule", "Nitroglycerin (2.5mg)", "Angina (heart-related chest pain)", "Hộp", "100", "31/12/2025", "Tốt"),
new Product("SP050", "AF - 150 Tablet", "Fluconazole (150mg)", "Fungal infections", "Hộp", "100", "31/12/2025", "Tốt"),
                new Product("SP051", "Amlokind 10 Tablet", "Amlodipine (10mg)", "Hypertension (high blood pressure)Prevention of Angina (heart-related chest pain)", "Hộp", "100", "31/12/2025", "Tốt"),
                new Product("SP052", "Azee 250 Tablet", "Azithromycin (250mg)", "Bacterial infections", "Hộp", "100", "31/12/2025", "Tốt"),
                new Product("SP053", "Amlokind 5 Tablet", "Amlodipine (5mg)", "Hypertension (high blood pressure)Prevention of Angina (heart-related chest pain)", "Hộp", "100", "31/12/2025", "Tốt")
                
        );
        tableProduct.setItems(productList);
    }

    private void setupSearchAndFilter() {
        filteredData = new FilteredList<>(productList, b -> true);

        // Lắng nghe sự thay đổi của Text Search
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // Lắng nghe sự thay đổi của ComboBox Danh mục
        cbCategory.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedData);
    }

    // Hàm xử lý lọc kép: Phải thỏa mãn cả từ khóa tìm kiếm VÀ danh mục đã chọn
    private void updateFilter() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedCategory = cbCategory.getValue();

        filteredData.setPredicate(product -> {
            // 1. Kiểm tra lọc theo Danh mục trước
            boolean matchesCategory = true;
            if (selectedCategory != null && !selectedCategory.equals("Tất cả danh mục")) {
                matchesCategory = product.getCategory().equals(selectedCategory);
            }

            // 2. Kiểm tra lọc theo Text
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = product.getName().toLowerCase().contains(searchText) ||
                                product.getId().toLowerCase().contains(searchText) ||
                                product.getActiveIngredient().toLowerCase().contains(searchText);
            }

            // Phải thỏa mãn cả 2 điều kiện
            return matchesCategory && matchesSearch;
        });
    }

    @FXML
    void handleAddProduct(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thêm thuốc mới");
        alert.setHeaderText("Mở Form Nhập Kho");
        alert.setContentText("Tại đây sẽ bật lên một Dialog để nhập thông tin thuốc mới, mã vạch, số lô, v.v.");
        alert.showAndWait();
    }
}