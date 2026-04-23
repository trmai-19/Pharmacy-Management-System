package com.pharmacy.controller.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;

public class RoleManagerController {

    @FXML private TextField txtSearchEmp;
    @FXML private TableView<EmpRoleModel> tableRoles;
    @FXML private TableColumn<EmpRoleModel, String> colEmpId;
    @FXML private TableColumn<EmpRoleModel, String> colEmpName;
    @FXML private TableColumn<EmpRoleModel, String> colDept;
    @FXML private TableColumn<EmpRoleModel, String> colRole;
    @FXML private TableColumn<EmpRoleModel, String> colLastUpdate;

    @FXML private VBox paneDetail;
    @FXML private Label lblSelectedEmp;
    
    // Checkboxes (Mock up 1 vài quyền)
    @FXML private CheckBox chkKhoView, chkKhoAdd, chkKhoEdit, chkKhoDel;
    @FXML private CheckBox chkSaleView, chkSaleAdd, chkSaleEdit, chkSaleDel;
    @FXML private CheckBox chkCustView, chkCustAdd, chkCustEdit, chkCustDel;
    
    @FXML private Button btnResetPerm;
    @FXML private Button btnSavePerm;

    // Danh sách các nhóm quyền
    private final ObservableList<String> roleList = FXCollections.observableArrayList(
            "Admin Tổng", "Quản Lý", "Dược Sĩ Bán Hàng", "Thủ Kho", "Nhân Sự"
    );

    @FXML
    public void initialize() {
        // Cho phép bảng được chỉnh sửa trực tiếp
        tableRoles.setEditable(true);

        setupColumns();
        loadData();
        setupInteractions();
    }

    private void setupColumns() {
        colEmpId.setCellValueFactory(cell -> cell.getValue().empIdProperty());
        colEmpName.setCellValueFactory(cell -> cell.getValue().empNameProperty());
        colDept.setCellValueFactory(cell -> cell.getValue().deptProperty());
        colLastUpdate.setCellValueFactory(cell -> cell.getValue().lastUpdateProperty());

        // Cột Quyền (Role) dùng ComboBox để chọn trực tiếp trên bảng
        colRole.setCellValueFactory(cell -> cell.getValue().roleProperty());
        colRole.setCellFactory(ComboBoxTableCell.forTableColumn(roleList));
        
        // Sự kiện khi chọn quyền mới trong ComboBox
        colRole.setOnEditCommit(event -> {
            EmpRoleModel emp = event.getRowValue();
            emp.setRole(event.getNewValue());
            emp.setLastUpdate("Vừa xong");
            System.out.println("Đã đổi quyền của " + emp.getEmpName() + " thành: " + event.getNewValue());
            updateCheckBoxesBasedOnRole(event.getNewValue()); // Tự động check các ô bên phải
            tableRoles.refresh();
        });
    }

    private void loadData() {
        ObservableList<EmpRoleModel> list = FXCollections.observableArrayList(
                new EmpRoleModel("NV001", "Nguyễn Văn Phát", "Ban Giám Đốc", "Admin Tổng", "22/04/2026"),
                new EmpRoleModel("NV002", "Trần Thị Lan", "Bán Hàng", "Dược Sĩ Bán Hàng", "18/04/2026"),
                new EmpRoleModel("NV003", "Phạm Hoàng Sơn", "Quản Lý Kho", "Thủ Kho", "20/04/2026"),
                new EmpRoleModel("NV004", "Lê Minh Tuấn", "Bán Hàng", "Quản Lý", "21/04/2026")
        );
        tableRoles.setItems(list);
    }

    private void setupInteractions() {
        // Khi bấm vào 1 nhân viên trên bảng -> Mở bảng chi tiết bên phải
        tableRoles.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                paneDetail.setDisable(false);
                lblSelectedEmp.setText(newSel.getEmpName() + " - " + newSel.getRole());
                updateCheckBoxesBasedOnRole(newSel.getRole());
            }
        });

        btnSavePerm.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Thành công");
            alert.setContentText("Đã lưu thiết lập quyền chi tiết cho nhân sự: " + lblSelectedEmp.getText());
            alert.showAndWait();
        });
    }

    // Logic giả lập: Chọn chức danh nào thì tự động tích Checkbox tương ứng
    private void updateCheckBoxesBasedOnRole(String role) {
        boolean isAdmin = role.equals("Admin Tổng");
        boolean isQuanLy = role.equals("Quản Lý");
        boolean isThuKho = role.equals("Thủ Kho");
        boolean isBanHang = role.equals("Dược Sĩ Bán Hàng");

        // Kho
        chkKhoView.setSelected(isAdmin || isQuanLy || isThuKho || isBanHang);
        chkKhoAdd.setSelected(isAdmin || isThuKho);
        chkKhoEdit.setSelected(isAdmin || isThuKho);
        chkKhoDel.setSelected(isAdmin);

        // Bán hàng
        chkSaleView.setSelected(isAdmin || isQuanLy || isBanHang);
        chkSaleAdd.setSelected(isAdmin || isQuanLy || isBanHang);
        chkSaleEdit.setSelected(isAdmin || isQuanLy);
        chkSaleDel.setSelected(isAdmin);

        // Khách hàng
        chkCustView.setSelected(isAdmin || isQuanLy || isBanHang);
        chkCustAdd.setSelected(isAdmin || isQuanLy || isBanHang);
        chkCustEdit.setSelected(isAdmin || isQuanLy);
        chkCustDel.setSelected(isAdmin);
    }

    // ==========================================
    // CLASS MODEL NỘI BỘ (Để code chạy ngay)
    // ==========================================
    public static class EmpRoleModel {
        private final SimpleStringProperty empId, empName, dept, role, lastUpdate;

        public EmpRoleModel(String id, String name, String dept, String role, String lastUpdate) {
            this.empId = new SimpleStringProperty(id);
            this.empName = new SimpleStringProperty(name);
            this.dept = new SimpleStringProperty(dept);
            this.role = new SimpleStringProperty(role);
            this.lastUpdate = new SimpleStringProperty(lastUpdate);
        }

        public String getEmpName() { return empName.get(); }
        public String getRole() { return role.get(); }
        public void setRole(String value) { role.set(value); }
        public void setLastUpdate(String value) { lastUpdate.set(value); }

        public SimpleStringProperty empIdProperty() { return empId; }
        public SimpleStringProperty empNameProperty() { return empName; }
        public SimpleStringProperty deptProperty() { return dept; }
        public SimpleStringProperty roleProperty() { return role; }
        public SimpleStringProperty lastUpdateProperty() { return lastUpdate; }
    }
}