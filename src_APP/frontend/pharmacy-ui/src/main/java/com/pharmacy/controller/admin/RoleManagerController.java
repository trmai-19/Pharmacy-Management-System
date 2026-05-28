package com.pharmacy.controller.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;

public class RoleManagerController {

    @FXML private TableView<EmpRoleModel> tableRoles;
    @FXML private TableColumn<EmpRoleModel, String> colEmpId;
    @FXML private TableColumn<EmpRoleModel, String> colEmpName;
    @FXML private TableColumn<EmpRoleModel, String> colRole;

    @FXML private VBox paneDetail;
    @FXML private Label lblSelectedEmp;
    
    // Checkboxes (Quyền thao tác)
    @FXML private CheckBox chkKhoView, chkKhoAdd, chkKhoEdit, chkKhoDel;
    @FXML private CheckBox chkSaleView, chkSaleAdd, chkSaleEdit, chkSaleDel;
    @FXML private CheckBox chkCustView, chkCustAdd, chkCustEdit, chkCustDel;
    
    @FXML private Button btnResetPerm;
    @FXML private Button btnSavePerm;

    // Danh sách các nhóm quyền mặc định
    private final ObservableList<String> roleList = FXCollections.observableArrayList(
            "ADMIN", "MANAGER", "PHARMACIST", "STAFF"
    );

    @FXML
    public void initialize() {
        tableRoles.setEditable(true); // Cho phép sửa quyền trực tiếp trên bảng
        setupColumns();
        loadDataFromAPI();
        setupInteractions();
    }

    private void setupColumns() {
        colEmpId.setCellValueFactory(cell -> cell.getValue().empIdProperty());
        colEmpName.setCellValueFactory(cell -> cell.getValue().empNameProperty());

        // Cột Quyền (Role) dùng ComboBox
        colRole.setCellValueFactory(cell -> cell.getValue().roleProperty());
        colRole.setCellFactory(ComboBoxTableCell.forTableColumn(roleList));
        
        // Sự kiện khi bạn chọn quyền mới ngay trên dòng của bảng
        colRole.setOnEditCommit(event -> {
            EmpRoleModel emp = event.getRowValue();
            emp.setRole(event.getNewValue());
            System.out.println("Đã đổi quyền của " + emp.getEmpName() + " thành: " + event.getNewValue());
            
            // Nếu dòng bị sửa đang được select thì update luôn checkbox bên phải
            if (tableRoles.getSelectionModel().getSelectedItem() == emp) {
                lblSelectedEmp.setText(emp.getEmpName() + " (" + emp.getRole() + ")");
                updateCheckBoxesBasedOnRole(event.getNewValue());
            }
            tableRoles.refresh();
        });
    }

    private void loadDataFromAPI() {
        // Gọi API thật để lấy danh sách nhân viên
        ApiService.get("/api/admin/employees").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode rootNode = ApiService.mapper.readTree(response.body());
                        // Đề phòng API trả thẳng array thay vì { "data": [...] }
                        JsonNode dataNode = rootNode.has("data") ? rootNode.get("data") : rootNode;
                        
                        if (dataNode != null && dataNode.isArray() && dataNode.size() > 0) {
                            ObservableList<EmpRoleModel> list = FXCollections.observableArrayList();
                            
                            for (JsonNode node : dataNode) {
                                // Bắt cả key viết thường và camelCase
                                String id = node.has("manv") ? node.get("manv").asText() : (node.has("maNv") ? node.get("maNv").asText() : "NV???");
                                String name = node.has("tennv") ? node.get("tennv").asText() : (node.has("tenNv") ? node.get("tenNv").asText() : "Chưa có tên");
                                String role = node.has("chucvu") ? node.get("chucvu").asText().toUpperCase() : (node.has("chucVu") ? node.get("chucVu").asText().toUpperCase() : "STAFF");
                                
                                // Map dữ liệu chức vụ sang mảng Role chuẩn của hệ thống
                                if (!roleList.contains(role)) {
                                    if (role.contains("QUẢN LÝ") || role.contains("MANAGER")) role = "MANAGER";
                                    else if (role.contains("BÁN HÀNG") || role.contains("PHARMACIST")) role = "PHARMACIST";
                                    else role = "STAFF";
                                }
                                
                                list.add(new EmpRoleModel(id, name, role));
                            }
                            tableRoles.setItems(list);
                        } else {
                            System.out.println("⚠️ API rỗng, chuyển sang dùng dữ liệu ảo.");
                            loadMockData();
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi parse JSON, chuyển sang dùng dữ liệu ảo.");
                        e.printStackTrace();
                        loadMockData();
                    }
                } else {
                    System.err.println("❌ Lỗi gọi API nhân viên (Status: " + response.statusCode() + "). Dùng dữ liệu ảo.");
                    loadMockData();
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                System.err.println("❌ Không kết nối được tới Backend. Đang dùng dữ liệu ảo.");
                loadMockData();
            });
            return null;
        });
    }

    // Cơ chế Fallback: Đổ dữ liệu giả vào nếu API chết, giúp màn hình không bao giờ bị trắng
    private void loadMockData() {
        ObservableList<EmpRoleModel> list = FXCollections.observableArrayList(
                new EmpRoleModel("NV001", "Nguyễn Văn Phát", "ADMIN"),
                new EmpRoleModel("NV002", "Trần Thị Lan", "PHARMACIST"),
                new EmpRoleModel("NV003", "Phạm Hoàng Sơn", "STAFF"),
                new EmpRoleModel("NV004", "Lê Minh Tuấn", "MANAGER")
        );
        tableRoles.setItems(list);
    }

    private void setupInteractions() {
        // Khi bấm vào 1 nhân viên trên bảng trái -> Mở bảng chi tiết bên phải
        tableRoles.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                paneDetail.setDisable(false); // Mở khóa ma trận phân quyền
                lblSelectedEmp.setText(newSel.getEmpName() + " (" + newSel.getRole() + ")");
                updateCheckBoxesBasedOnRole(newSel.getRole());
            } else {
                paneDetail.setDisable(true);
                lblSelectedEmp.setText("Chưa chọn nhân sự...");
            }
        });

        // Nút Lưu thay đổi (Giả lập)
        btnSavePerm.setOnAction(e -> {
            if (tableRoles.getSelectionModel().getSelectedItem() == null) return;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Thành công");
            alert.setContentText("Hệ thống đã cập nhật phân quyền bảo mật cho nhân sự:\n" + tableRoles.getSelectionModel().getSelectedItem().getEmpName());
            alert.showAndWait();
        });
        
        // Nút Reset Mặc định
        btnResetPerm.setOnAction(e -> {
            EmpRoleModel sel = tableRoles.getSelectionModel().getSelectedItem();
            if (sel != null) {
                updateCheckBoxesBasedOnRole(sel.getRole());
            }
        });
    }

    // Logic giả lập: Chọn chức danh nào thì tự động tích Checkbox tương ứng bên phải
    private void updateCheckBoxesBasedOnRole(String role) {
        boolean isAdmin = role.equals("ADMIN");
        boolean isManager = role.equals("MANAGER");
        boolean isPharmacist = role.equals("PHARMACIST");

        // Kho
        chkKhoView.setSelected(isAdmin || isManager || isPharmacist);
        chkKhoAdd.setSelected(isAdmin || isManager);
        chkKhoEdit.setSelected(isAdmin || isManager);
        chkKhoDel.setSelected(isAdmin);

        // Bán hàng
        chkSaleView.setSelected(isAdmin || isManager || isPharmacist);
        chkSaleAdd.setSelected(isAdmin || isManager || isPharmacist);
        chkSaleEdit.setSelected(isAdmin || isManager);
        chkSaleDel.setSelected(isAdmin);

        // Khách hàng
        chkCustView.setSelected(isAdmin || isManager || isPharmacist);
        chkCustAdd.setSelected(isAdmin || isManager || isPharmacist);
        chkCustEdit.setSelected(isAdmin || isManager);
        chkCustDel.setSelected(isAdmin);
    }

    // ==========================================
    // CLASS MODEL NỘI BỘ (Chứa dữ liệu dòng)
    // ==========================================
    public static class EmpRoleModel {
        private final SimpleStringProperty empId, empName, role;

        public EmpRoleModel(String id, String name, String role) {
            this.empId = new SimpleStringProperty(id);
            this.empName = new SimpleStringProperty(name);
            this.role = new SimpleStringProperty(role);
        }

        public String getEmpId() { return empId.get(); }
        public String getEmpName() { return empName.get(); }
        public String getRole() { return role.get(); }
        public void setRole(String value) { role.set(value); }

        public SimpleStringProperty empIdProperty() { return empId; }
        public SimpleStringProperty empNameProperty() { return empName; }
        public SimpleStringProperty roleProperty() { return role; }
    }
}