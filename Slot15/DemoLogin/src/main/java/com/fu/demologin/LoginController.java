package com.fu.demologin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Hyperlink registerLink;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean remember = rememberCheckBox.isSelected();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Remember me: " + remember);

        // Xử lý logic đăng nhập tại đây
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        System.out.println("Chuyển tới màn hình Quên mật khẩu");
    }

    @FXML
    void handleRegister(ActionEvent event) {
        System.out.println("Chuyển tới màn hình Đăng ký");
    }
}