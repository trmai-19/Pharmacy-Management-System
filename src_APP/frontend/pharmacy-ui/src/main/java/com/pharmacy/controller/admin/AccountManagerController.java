package com.pharmacy.controller.admin;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JavaType;
import com.pharmacy.dto.ApiResponse;
import com.pharmacy.model.Account;
import com.pharmacy.util.ApiService;

import javafx.application.Platform;
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
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML private TableView<Account> tableCustomerAcc;

    @FXML private Label lblEmpAccounts;
    @FXML private Label lblWebAccounts;
    @FXML private Label lblLockedAccounts;
    
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
        
        setupEmployeeActions();
        setupCustomerActions();
    }

    private void setupColumns() {
        colEmpUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmpName.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        colEmpRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmpEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmpStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colCustPhone.setCellValueFactory(new PropertyValueFactory<>("username"));
        colCustEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        colCustStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                    JavaType listType = ApiService.mapper.getTypeFactory().constructCollectionType(List.class, Account.class);
                    JavaType apiResponseType = ApiService.mapper.getTypeFactory().constructParametricType(ApiResponse.class, listType);
                    
                    ApiResponse<List<Account>> apiRes = ApiService.mapper.readValue(jsonResponseBody, apiResponseType);

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
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể tải danh sách tài khoản!"));
                return null;
            });
    }

    private void setupSearchFilters() {
        FilteredList<Account> empData = new FilteredList<>(allAccounts, acc -> "EMPLOYEE".equalsIgnoreCase(acc.getAccountType()));
        FilteredList<Account> custData = new FilteredList<>(allAccounts, acc -> "CUSTOMER".equalsIgnoreCase(acc.getAccountType()));

        FilteredList<Account> searchEmpData = new FilteredList<>(empData, b -> true);
        txtSearchEmp.textProperty().addListener((observable, oldValue, newValue) -> {
            searchEmpData.setPredicate(acc -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return (acc.getUsername() != null && acc.getUsername().toLowerCase().contains(lower)) ||
                       (acc.getOwnerName() != null && acc.getOwnerName().toLowerCase().contains(lower));
            });
        });
        SortedList<Account> sortedEmpData = new SortedList<>(searchEmpData);
        sortedEmpData.comparatorProperty().bind(tableEmployeeAcc.comparatorProperty());
        tableEmployeeAcc.setItems(sortedEmpData);

        FilteredList<Account> searchCustData = new FilteredList<>(custData, b -> true);
        txtSearchCust.textProperty().addListener((observable, oldValue, newValue) -> {
            searchCustData.setPredicate(acc -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return (acc.getUsername() != null && acc.getUsername().toLowerCase().contains(lower)) ||
                       (acc.getOwnerName() != null && acc.getOwnerName().toLowerCase().contains(lower));
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
                showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn nhân viên!");
                return;
            }

            String email = selectedAcc.getEmail();
            if (email == null || email.trim().isEmpty() || "-".equals(email.trim())) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Email", "Tài khoản chưa cập nhật Email!");
                return;
            }

            if (showConfirmationDialog("Xác nhận", "Cấp lại mật khẩu cho " + selectedAcc.getOwnerName() + "?")) {
                String endpoint = "/api/admin/accounts/" + selectedAcc.getUsername() + "/reset-password";
                btnResetPwdEmp.setDisable(true);

                ApiService.postText(endpoint, email)
                    .thenAccept(res -> Platform.runLater(() -> {
                        btnResetPwdEmp.setDisable(false);
                        try {
                            ApiResponse<?> apiRes = ApiService.mapper.readValue(res.body(), ApiResponse.class);
                            if (res.statusCode() == 200 && apiRes.getStatus() == 200) {
                                showAlert(Alert.AlertType.INFORMATION, "Thành công", apiRes.getMessage());
                            } else {
                                showAlert(Alert.AlertType.WARNING, "Thất bại", apiRes.getMessage());
                            }
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể gửi yêu cầu! Mã lỗi: " + res.statusCode());
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            btnResetPwdEmp.setDisable(false);
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể kết nối đến máy chủ!");
                        });
                        return null;
                    });
            }
        });

        btnLockEmp.setOnAction(event -> {
            Account selectedAcc = tableEmployeeAcc.getSelectionModel().getSelectedItem();
            if (selectedAcc == null) {
                showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn nhân viên!");
                return;
            }

            String currentStatus = selectedAcc.getStatus();
            boolean isLocking = currentStatus == null || 
                !(currentStatus.toLowerCase().contains("khóa") || 
                currentStatus.toLowerCase().contains("lock") || 
                currentStatus.toLowerCase().contains("inactive"));
            
            String title = isLocking ? "Xác nhận khóa" : "Xác nhận mở khóa";
            if (showConfirmationDialog(title, "Thao tác với tài khoản " + selectedAcc.getOwnerName() + "?")) {
                String endpoint = "/api/admin/accounts/" + selectedAcc.getUsername() + "/status";
                btnLockEmp.setDisable(true);

                ApiService.put(endpoint, "")
                    .thenAccept(res -> Platform.runLater(() -> {
                        btnLockEmp.setDisable(false);
                        try {
                            ApiResponse<?> apiRes = ApiService.mapper.readValue(res.body(), ApiResponse.class);
                            
                            if (res.statusCode() == 200 && apiRes.getStatus() == 200) {
                                selectedAcc.setStatus(isLocking ? "LOCKED" : "ACTIVE");
                                tableEmployeeAcc.refresh(); 
                                updateAccountSummaryCounts();
                                showAlert(Alert.AlertType.INFORMATION, "Thành công", apiRes.getMessage());
                            } else {
                                String errorBody = res.body() != null ? res.body() : "";
                                String errorMessage = "Backend từ chối thao tác (Mã lỗi " + res.statusCode() + ").\n\n";
                                
                                if (errorBody.contains("RESIGNED") || errorBody.contains("đã nghỉ")) {
                                    errorMessage += "Lý do: Nhân sự này đang làm việc.\n=> Bạn phải qua trang Nhân Sự, chuyển trạng thái sang ĐÃ NGHỈ VIỆC trước khi khóa tài khoản.";
                                } else if (errorBody.contains("WORKING") || errorBody.contains("đang làm")) {
                                    errorMessage += "Lý do: Nhân sự này chưa chuyển sang ĐANG LÀM VIỆC.";
                                } else if (errorBody.contains("Không tìm thấy")) {
                                    errorMessage += "Lý do: Không tìm thấy tài khoản trong DB.";
                                } else {
                                    errorMessage += "Chi tiết lỗi từ Server: " + errorBody;
                                }
                                
                                showAlert(Alert.AlertType.ERROR, "Không thể cập nhật", errorMessage);
                            }
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đã có lỗi xảy ra! Server trả về code: " + res.statusCode());
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            btnLockEmp.setDisable(false);
                            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể cập nhật trạng thái!");
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
        if (lblEmpAccounts == null || lblWebAccounts == null || lblLockedAccounts == null) return;

        long empCount = allAccounts.stream().filter(a -> "EMPLOYEE".equalsIgnoreCase(a.getAccountType())).count();
        long custCount = allAccounts.stream().filter(a -> "CUSTOMER".equalsIgnoreCase(a.getAccountType())).count();

        lblEmpAccounts.setText(String.valueOf(empCount));
        lblWebAccounts.setText(String.valueOf(custCount));
        
        // Đã dọn dẹp sạch sẽ cái "Khóa: 1" đỏ choét đi rồi nhé =))
        lblLockedAccounts.setText(""); 
    }

    private void setupCustomerActions() {
        btnResetPwdCust.setOnAction(event -> {
            Account selectedAcc = tableCustomerAcc.getSelectionModel().getSelectedItem();
            if (selectedAcc == null) {
                showAlert(Alert.AlertType.WARNING, "Chưa chọn tài khoản", "Vui lòng chọn khách hàng!");
                return;
            }

            String email = selectedAcc.getEmail(); 
            if (email == null || email.trim().isEmpty() || "-".equals(email.trim())) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Email", "Khách hàng chưa có Email!");
                return;
            }

            if (showConfirmationDialog("Xác nhận", "Cấp lại mật khẩu cho " + selectedAcc.getOwnerName() + "?")) {
                String endpoint = "/api/admin/accounts/" + selectedAcc.getUsername() + "/reset-password";
                btnResetPwdCust.setDisable(true);
                ApiService.postText(endpoint, email)
                    .thenAccept(res -> Platform.runLater(() -> {
                        btnResetPwdCust.setDisable(false);
                        try {
                            ApiResponse<?> apiRes = ApiService.mapper.readValue(res.body(), ApiResponse.class);
                            if (res.statusCode() == 200 && apiRes.getStatus() == 200) {
                                showAlert(Alert.AlertType.INFORMATION, "Thành công", apiRes.getMessage());
                            } else {
                                showAlert(Alert.AlertType.WARNING, "Thất bại", apiRes.getMessage());
                            }
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Lỗi gửi yêu cầu! Code: " + res.statusCode());
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            btnResetPwdCust.setDisable(false);
                            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể gửi yêu cầu!");
                        });
                        return null;
                    });
            }
        });
    }
}