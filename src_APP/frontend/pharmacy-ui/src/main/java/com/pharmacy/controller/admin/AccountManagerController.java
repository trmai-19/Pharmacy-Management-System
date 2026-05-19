package com.pharmacy.controller.admin;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.dto.ApiResponse;
import com.pharmacy.model.Account;
import com.pharmacy.util.ApiService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AccountManagerController {

    // --- TAB 1: NHÂN VIÊN ---
    @FXML private TextField txtSearchEmp;
    @FXML private Button btnResetPwdEmp;
    @FXML private Button btnLockEmp;
    @FXML private TableView<Account> tableEmployeeAcc;
    @FXML private TableColumn<Account, String> colEmpUser;
    @FXML private TableColumn<Account, String> colEmpName;
    @FXML private TableColumn<Account, String> colEmpRole;
    @FXML private TableColumn<Account, String> colEmpEmail;
    @FXML private TableColumn<Account, String> colEmpStatus;

    // --- TAB 2: KHÁCH HÀNG WEB ---
    @FXML private TextField txtSearchCust;
    @FXML private Button btnResetPwdCust;
    // Đã bỏ nút btnLockCust (Đình chỉ truy cập)
    @FXML private TableView<Account> tableCustomerAcc;

    @FXML private Label lblEmpAccounts;
    @FXML private Label lblWebAccounts;
    @FXML private Label lblLockedAccounts;
    // Tách thành 2 cột hiển thị
    @FXML private TableColumn<Account, String> colCustPhone;
    @FXML private TableColumn<Account, String> colCustEmail;
    @FXML private TableColumn<Account, String> colCustName;
    @FXML private TableColumn<Account, String> colCustStatus;

    private ObservableList<Account> allAccounts;

    @FXML
    public void initialize() {
        System.out.println("🔐 AccountManagerController đang tải...");

        setupColumns();
        loadAccountDataFromServer();
        setupSearchFilters();
        
        // Cài đặt sự kiện cho các nút bấm
        setupEmployeeActions();
        setupCustomerActions();
    }

        private void setupColumns() {
        colEmpUser.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colEmpName.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        colEmpRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        colEmpStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        colEmpEmail.setCellValueFactory(cellData -> {
            String empEmail = cellData.getValue().getEmail();
            if (empEmail == null || empEmail.trim().isEmpty()) {
                return new SimpleStringProperty("-");
            }
            return cellData.getValue().emailProperty();
        });

        colCustPhone.setCellValueFactory(cellData -> {
            String phone = cellData.getValue().getUsername(); 
            if (phone == null || phone.trim().isEmpty()) {
                return new SimpleStringProperty("-");
            }
            return cellData.getValue().usernameProperty();
        });

        colCustEmail.setCellValueFactory(cellData -> {
            String custEmail = cellData.getValue().getEmail(); 
            if (custEmail == null || custEmail.trim().isEmpty()) {
                return new SimpleStringProperty("-");
            }
            return cellData.getValue().emailProperty(); 
        });

        colCustName.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());

        colCustStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    private void loadAccountDataFromServer() {
    if (allAccounts == null) {
        allAccounts = FXCollections.observableArrayList();
    }
    allAccounts.clear();

    ApiService.get("/api/admin/accounts") 
        .thenApply(HttpResponse::body) 
        .thenAccept(jsonResponseBody -> {
            try {
                ApiResponse<List<Account>> apiRes = ApiService.mapper.readValue(
                    jsonResponseBody,
                    new TypeReference<ApiResponse<List<Account>>>() {}
                );

                if (apiRes != null && apiRes.getStatus() == 200 && apiRes.getData() != null) {
                    List<Account> serverAccounts = apiRes.getData();

                    Platform.runLater(() -> {
                        allAccounts.setAll(serverAccounts);
                        
                        updateAccountSummaryCounts();
                        
                        setupSearchFilters(); 
                    });
                } else {
                    String msg = (apiRes != null) ? apiRes.getMessage() : "Lỗi không xác định";
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi nghiệp vụ", msg));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi xử lý", "Không thể bóc tách cấu trúc JSON tài khoản!"));
            }
        })
        .exceptionally(ex -> {
            ex.printStackTrace();
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể kết nối đến Backend để tải danh sách tài khoản!"));
            return null;
        });
}

    

    private void setupSearchFilters() {
        FilteredList<Account> empData = new FilteredList<>(allAccounts, acc -> "EMPLOYEE".equals(acc.getAccountType()));
        FilteredList<Account> custData = new FilteredList<>(allAccounts, acc -> "CUSTOMER".equals(acc.getAccountType()));

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

            String email = selectedAcc.getEmail();

            if (email == null || email.trim().isEmpty() || "-".equals(email.trim())) {
                showAlert(Alert.AlertType.ERROR, "Thiếu thông tin Email", 
                    "Tài khoản của nhân viên [" + selectedAcc.getOwnerName() + "] chưa cập nhật Email trên hệ thống.\nKhông thể thực hiện cấp lại mật khẩu!");
                return;
            }

            boolean isConfirm = showConfirmationDialog("Xác nhận", "Bạn đồng ý cấp lại mật khẩu cho nhân viên [" + selectedAcc.getOwnerName() + "] chứ?");
            if (isConfirm) {
                String sdt = selectedAcc.getUsername(); // Lấy số điện thoại (username) làm path variable
                String plainEmailBody = email;
                
                String endpoint = "/api/admin/accounts/" + sdt + "/reset-password";
                
                btnResetPwdEmp.setDisable(true);

                ApiService.postText(endpoint, plainEmailBody)
                    .thenApply(HttpResponse::body)
                    .thenAccept(responseString -> {
                        Platform.runLater(() -> {
                            btnResetPwdEmp.setDisable(false);
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                                "Mật khẩu tạm thời đã được gửi thành công về hòm thư nhân viên: " + email);
                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            btnResetPwdEmp.setDisable(false);
                            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể gửi yêu cầu tới máy chủ!");
                        });
                        return null;
                    });
            }
        });

        btnLockEmp.setOnAction(event -> {
            Account selectedAcc = tableEmployeeAcc.getSelectionModel().getSelectedItem();
            if (selectedAcc == null) {
                showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn một nhân viên trong bảng để thay đổi trạng thái!");
                return;
            }

            String currentStatus = selectedAcc.getStatus();
            boolean isLocking = !"Đã khóa".equals(currentStatus); // Nếu trạng thái khác "Đã khóa" thì nghĩa là Admin muốn khóa
            
            String dialogTitle = isLocking ? "Xác nhận khóa" : "Xác nhận mở khóa";
            String dialogContent = isLocking 
                ? "Bạn có chắc chắn muốn KHÓA tài khoản nhân viên [" + selectedAcc.getOwnerName() + "] không?"
                : "Bạn có chắc chắn muốn MỞ KHÓA tài khoản nhân viên [" + selectedAcc.getOwnerName() + "] không?";

            boolean isConfirm = showConfirmationDialog(dialogTitle, dialogContent);
            if (isConfirm) {
                String accountId = selectedAcc.getUsername(); 
                
                String endpoint = "/api/admin/accounts/" + accountId + "/status";
                btnLockEmp.setDisable(true);

                ApiService.put(endpoint, "")
                    .thenApply(HttpResponse::body)
                    .thenAccept(responseString -> {
                        Platform.runLater(() -> {
                            btnLockEmp.setDisable(false);
                            
                            if (isLocking) {
                                selectedAcc.setStatus("Đã khóa");
                                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã khóa tài khoản nhân viên thành công!");
                            } else {
                                selectedAcc.setStatus("Đang hoạt động"); // Hoặc trạng thái mặc định của hệ thống bạn
                                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã mở khóa tài khoản nhân viên thành công!");
                            }
                            
                            tableEmployeeAcc.refresh(); // Làm mới lại bảng nhân viên hiển thị dữ liệu mới
                            updateAccountSummaryCounts();
                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            btnLockEmp.setDisable(false);
                            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể cập nhật trạng thái tài khoản lên máy chủ!");
                        });
                        return null;
                    });
            }
        });
    }


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

    private void updateAccountSummaryCounts() {
        if (lblEmpAccounts == null || lblWebAccounts == null || lblLockedAccounts == null) {
            return;
        }

        long employeeCount = allAccounts.stream()
            .filter(acc -> {
                boolean isEmployee = "EMPLOYEE".equalsIgnoreCase(acc.getAccountType());
                System.out.println("🔍 DEBUG - Account: " + acc.getUsername() + ", accountType: [" + acc.getAccountType() + "], isEmployee: " + isEmployee);
                return isEmployee;
            })
            .count();
        
        long customerCount = allAccounts.stream()
            .filter(acc -> "CUSTOMER".equalsIgnoreCase(acc.getAccountType()))
            .count();
        
        long lockedCount = allAccounts.stream()
            .filter(acc -> {
                String status = acc.getStatus();
                return status != null && (status.toLowerCase().contains("khóa") || status.toLowerCase().contains("lock"));
            })
            .count();

        System.out.println("📊 TỔNG CỘNG - Employee: " + employeeCount + ", Customer: " + customerCount + ", Locked: " + lockedCount);
        
        lblEmpAccounts.setText(String.valueOf(employeeCount));
        lblWebAccounts.setText(String.valueOf(customerCount));
        lblLockedAccounts.setText(String.valueOf(lockedCount));
    }

    private void setupCustomerActions() {
    // Nút Cấp lại mật khẩu - KHÁCH HÀNG
    btnResetPwdCust.setOnAction(event -> {
        Account selectedAcc = tableCustomerAcc.getSelectionModel().getSelectedItem();
        
        if (selectedAcc == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn một khách hàng trong bảng để cấp lại mật khẩu!");
            return;
        }

        String email = selectedAcc.getEmail(); 

        if (email == null || email.trim().isEmpty() || "-".equals(email.trim())) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin Email", 
                "Tài khoản của khách hàng [" + selectedAcc.getOwnerName() + "] chưa cập nhật Email trên hệ thống.\nKhông thể thực hiện cấp lại mật khẩu!");
            return;
        }

        boolean isConfirm = showConfirmationDialog("Xác nhận", "Bạn đồng ý cấp lại mật khẩu cho khách hàng [" + selectedAcc.getOwnerName() + "] chứ?");
        
        if (isConfirm) {
            String sdt = selectedAcc.getUsername(); // Lấy số điện thoại (username)
            String plainEmailBody = email;
            String endpoint = "/api/admin/accounts/" + sdt + "/reset-password";
            btnResetPwdCust.setDisable(true);
            ApiService.postText(endpoint, plainEmailBody)
                .thenApply(HttpResponse::body)
                .thenAccept(responseString -> {
                    Platform.runLater(() -> {
                        btnResetPwdCust.setDisable(false);
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                            "Mật khẩu tạm thời đã được gửi thành công về hòm thư: " + email);
                    });
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        btnResetPwdCust.setDisable(false);
                        showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể gửi yêu cầu tới máy chủ!");
                    });
                    return null;
                });
        }
    });
}
}