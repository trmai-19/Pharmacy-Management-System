package com.pharmacy.controller.admin;

import com.pharmacy.model.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AccountManagerController {

    // --- TAB 1: NHÂN VIÊN ---
    @FXML private TextField txtSearchEmp;
    @FXML private TableView<Account> tableEmployeeAcc;
    @FXML private TableColumn<Account, String> colEmpUser;
    @FXML private TableColumn<Account, String> colEmpName;
    @FXML private TableColumn<Account, String> colEmpRole;
    @FXML private TableColumn<Account, String> colEmpStatus;
    @FXML private TableColumn<Account, String> colEmpLastLogin;

    // --- TAB 2: KHÁCH HÀNG WEB ---
    @FXML private TextField txtSearchCust;
    @FXML private TableView<Account> tableCustomerAcc;
    @FXML private TableColumn<Account, String> colCustUser;
    @FXML private TableColumn<Account, String> colCustName;
    @FXML private TableColumn<Account, String> colCustRole;
    @FXML private TableColumn<Account, String> colCustStatus;
    @FXML private TableColumn<Account, String> colCustLastLogin;

    // Danh sách dữ liệu chung (hoặc riêng lẻ tùy bạn query từ DB)
    private ObservableList<Account> allAccounts;

    @FXML
    public void initialize() {
        System.out.println("🔐 AccountManagerController đang tải...");

        setupColumns();
        loadMockData();
        setupSearchFilters();
    }

    private void setupColumns() {
        // Cột cho bảng Nhân Viên
        colEmpUser.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colEmpName.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        colEmpRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        colEmpStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colEmpLastLogin.setCellValueFactory(cellData -> cellData.getValue().lastLoginProperty());

        // Cột cho bảng Khách Hàng Web
        colCustUser.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colCustName.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        colCustRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        colCustStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colCustLastLogin.setCellValueFactory(cellData -> cellData.getValue().lastLoginProperty());
    }

    private void loadMockData() {
        allAccounts = FXCollections.observableArrayList(
            // --- DỮ LIỆU NHÂN VIÊN ---
            new Account("admin_phat", "Nguyễn Văn Phát", "Admin Hệ Thống", "Đang hoạt động", "18/04/2026 08:30:00", "EMPLOYEE"),
            new Account("sales_lan", "Trần Thị Lan", "Dược Sĩ Bán Hàng", "Đang hoạt động", "18/04/2026 07:15:22", "EMPLOYEE"),
            new Account("stock_son", "Phạm Hoàng Sơn", "Quản Lý Kho", "Đang hoạt động", "17/04/2026 18:00:00", "EMPLOYEE"),
            new Account("sales_tuan", "Lê Minh Tuấn", "Dược Sĩ", "Đã nghỉ việc / Khóa", "10/01/2026 12:00:00", "EMPLOYEE"),
            new Account("sales_hoa", "Nguyễn Hoa", "Thu Ngân", "Tạm khóa (Sai Pass)", "15/04/2026 09:12:00", "EMPLOYEE"),

            // --- DỮ LIỆU KHÁCH HÀNG (CHỈ TRÊN WEB) ---
            new Account("0988123456", "Nguyễn Thu Hà", "Thành viên Web", "Đã xác thực OTP", "18/04/2026 10:20:00", "CUSTOMER"),
            new Account("luan.tran@gmail.com", "Trần Văn Luân", "Thành viên Web", "Chưa xác thực Email", "02/04/2026 14:15:00", "CUSTOMER"),
            new Account("0912333444", "Lê Thị Lan Anh", "VIP Web", "Đã xác thực OTP", "16/04/2026 20:45:11", "CUSTOMER"),
            new Account("0977888111", "Hoàng Kim Liên", "Thành viên Web", "Bị khóa (Spam Order)", "12/04/2026 09:00:00", "CUSTOMER")
        );
    }

    private void setupSearchFilters() {
        // 1. Tách danh sách thành 2 luồng: Nhân viên và Khách hàng
        FilteredList<Account> empData = new FilteredList<>(allAccounts, acc -> acc.getAccountType().equals("EMPLOYEE"));
        FilteredList<Account> custData = new FilteredList<>(allAccounts, acc -> acc.getAccountType().equals("CUSTOMER"));

        // 2. Logic tìm kiếm cho Tab Nhân viên
        FilteredList<Account> searchEmpData = new FilteredList<>(empData, b -> true);
        txtSearchEmp.textProperty().addListener((observable, oldValue, newValue) -> {
            searchEmpData.setPredicate(acc -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return acc.getUsername().toLowerCase().contains(lowerCaseFilter) ||
                       acc.getOwnerName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<Account> sortedEmpData = new SortedList<>(searchEmpData);
        sortedEmpData.comparatorProperty().bind(tableEmployeeAcc.comparatorProperty());
        tableEmployeeAcc.setItems(sortedEmpData);

        // 3. Logic tìm kiếm cho Tab Khách hàng
        FilteredList<Account> searchCustData = new FilteredList<>(custData, b -> true);
        txtSearchCust.textProperty().addListener((observable, oldValue, newValue) -> {
            searchCustData.setPredicate(acc -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return acc.getUsername().toLowerCase().contains(lowerCaseFilter) ||
                       acc.getOwnerName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<Account> sortedCustData = new SortedList<>(searchCustData);
        sortedCustData.comparatorProperty().bind(tableCustomerAcc.comparatorProperty());
        tableCustomerAcc.setItems(sortedCustData);
    }
}