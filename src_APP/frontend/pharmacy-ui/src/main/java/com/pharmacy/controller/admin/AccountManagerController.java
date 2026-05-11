package com.pharmacy.controller.admin;

import com.pharmacy.model.Account;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Optional;

public class AccountManagerController {

    // --- TAB 1: NHÂN VIÊN ---
    @FXML private TextField txtSearchEmp;
    @FXML private Button btnResetPwdEmp;
    @FXML private Button btnLockEmp;
    @FXML private TableView<Account> tableEmployeeAcc;
    @FXML private TableColumn<Account, String> colEmpUser;
    @FXML private TableColumn<Account, String> colEmpName;
    @FXML private TableColumn<Account, String> colEmpRole;
    @FXML private TableColumn<Account, String> colEmpStatus;
    @FXML private TableColumn<Account, String> colEmpLastLogin;

    // --- TAB 2: KHÁCH HÀNG WEB ---
    @FXML private TextField txtSearchCust;
    @FXML private Button btnResetPwdCust;
    // Đã bỏ nút btnLockCust (Đình chỉ truy cập)
    @FXML private TableView<Account> tableCustomerAcc;
    // Tách thành 2 cột hiển thị
    @FXML private TableColumn<Account, String> colCustPhone;
    @FXML private TableColumn<Account, String> colCustEmail;
    @FXML private TableColumn<Account, String> colCustName;
    @FXML private TableColumn<Account, String> colCustRole;
    @FXML private TableColumn<Account, String> colCustStatus;
    @FXML private TableColumn<Account, String> colCustLastLogin;

    // Danh sách dữ liệu chung
    private ObservableList<Account> allAccounts;

    @FXML
    public void initialize() {
        System.out.println("🔐 AccountManagerController đang tải...");

        setupColumns();
        loadMockData();
        setupSearchFilters();
        
        // Cài đặt sự kiện cho các nút bấm
        setupEmployeeActions();
        setupCustomerActions();
    }

    private void setupColumns() {
        // Cột cho bảng Nhân Viên
        colEmpUser.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colEmpName.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        colEmpRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        colEmpStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colEmpLastLogin.setCellValueFactory(cellData -> cellData.getValue().lastLoginProperty());

        // Cột cho bảng Khách Hàng Web (Tự động phân loại SĐT và Email từ Username)
        colCustPhone.setCellValueFactory(cellData -> {
            String user = cellData.getValue().getUsername();
            // Nếu không có '@' thì đưa vào cột Số điện thoại
            if (user != null && !user.contains("@")) {
                return new SimpleStringProperty(user);
            }
            return new SimpleStringProperty("-");
        });

        colCustEmail.setCellValueFactory(cellData -> {
            String user = cellData.getValue().getUsername();
            // Nếu có '@' thì đưa vào cột Email
            if (user != null && user.contains("@")) {
                return new SimpleStringProperty(user);
            }
            return new SimpleStringProperty("-");
        });

        colCustName.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        colCustRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        colCustStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colCustLastLogin.setCellValueFactory(cellData -> cellData.getValue().lastLoginProperty());
    }

    private void loadMockData() {
        // GIỮ NGUYÊN CONSTRUCTOR CŨ CỦA BẠN - KHÔNG CẦN SỬA MODEL ACCOUNT
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
        FilteredList<Account> empData = new FilteredList<>(allAccounts, acc -> "EMPLOYEE".equals(acc.getAccountType()));
        FilteredList<Account> custData = new FilteredList<>(allAccounts, acc -> "CUSTOMER".equals(acc.getAccountType()));

        // Logic tìm kiếm cho Tab Nhân viên
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

        // Logic tìm kiếm cho Tab Khách hàng
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

    private void setupEmployeeActions() {
        btnResetPwdEmp.setOnAction(event -> {
            Account selectedAcc = tableEmployeeAcc.getSelectionModel().getSelectedItem();
            if (selectedAcc == null) {
                showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn một nhân viên trong bảng để cấp lại mật khẩu!");
                return;
            }
            boolean isConfirm = showConfirmationDialog("Xác nhận", "Bạn đồng ý cấp lại mật khẩu cho nhân viên [" + selectedAcc.getOwnerName() + "] chứ?");
            if (isConfirm) {
                System.out.println("Đã cấp lại mật khẩu cho NV: " + selectedAcc.getUsername());
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cấp lại mật khẩu thành công!");
            }
        });

        btnLockEmp.setOnAction(event -> {
            Account selectedAcc = tableEmployeeAcc.getSelectionModel().getSelectedItem();
            if (selectedAcc == null) {
                showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn một nhân viên trong bảng để khóa tài khoản!");
                return;
            }
            boolean isConfirm = showConfirmationDialog("Xác nhận khóa", "Bạn có chắc chắn muốn khóa tài khoản nhân viên [" + selectedAcc.getOwnerName() + "] không?");
            if (isConfirm) {
                System.out.println("Đã khóa tài khoản NV: " + selectedAcc.getUsername());
                selectedAcc.statusProperty().set("Đã khóa");                
                tableEmployeeAcc.refresh(); 
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã khóa tài khoản nhân viên thành công!");
            }
        });
    }

    private void setupCustomerActions() {
        // Nút Cấp lại mật khẩu - KHÁCH HÀNG
        btnResetPwdCust.setOnAction(event -> {
            Account selectedAcc = tableCustomerAcc.getSelectionModel().getSelectedItem();
            if (selectedAcc == null) {
                showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn một khách hàng trong bảng để cấp lại mật khẩu!");
                return;
            }
            boolean isConfirm = showConfirmationDialog("Xác nhận", "Bạn đồng ý cấp lại mật khẩu cho khách hàng [" + selectedAcc.getOwnerName() + "] chứ?");
            if (isConfirm) {
                System.out.println("Đã cấp lại mật khẩu cho KH: " + selectedAcc.getUsername());
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cấp lại mật khẩu thành công và gửi thông báo cho khách hàng!");
            }
        });
    }

    // --- CÁC HÀM TIỆN ÍCH DÙNG CHUNG ---
    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}