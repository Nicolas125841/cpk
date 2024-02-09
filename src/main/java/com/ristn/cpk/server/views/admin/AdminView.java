package com.ristn.cpk.server.views.admin;

import com.ristn.cpk.server.data.AdminData;
import com.ristn.cpk.server.services.AdminService;
import com.ristn.cpk.server.services.PackageService;
import com.ristn.cpk.server.services.SecurityService;
import com.ristn.cpk.server.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@Uses(Icon.class)
@RolesAllowed("ADMIN")
public class AdminView extends Composite<VerticalLayout> {
    private final PackageService packageManager;

    public AdminView(@Autowired SecurityService securityService, @Autowired AdminService adminService,
            @Autowired PackageService packageService) {
        this.packageManager = packageService;

        Binder<AdminData> binder = new Binder<>(AdminData.class);
        AdminData buffer = new AdminData();

        buffer.load(adminService.getDataHook());

        PasswordField passwordField = new PasswordField();
        Button setCredentials = new Button();

        passwordField.setLabel("Admin Password");
        passwordField.setWidth("min-content");
        setCredentials.setText("Update");
        setCredentials.setWidth("min-content");
        setCredentials.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.forField(passwordField)
              .withValidator(value -> !value.isBlank(), "Password cannot be blank")
              .bind(AdminData::getPassword, AdminData::setPassword);

        setCredentials.addClickListener(e -> {
            if(binder.validate().getValidationErrors().stream().noneMatch(result -> result.isError() && result.getErrorMessage().equals("Password cannot be blank"))) {
                adminService.setCredentials(buffer);
                success("Success!");
            }
        });

        TextField removeName = new TextField();
        Button removeButton = new Button();

        removeName.setLabel("Remove Package");
        removeName.setPlaceholder("package-name");
        removeName.setWidth("min-content");
        removeButton.setText("Remove");
        removeButton.setWidth("min-content");
        removeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        removeButton.addClickListener(e -> confirmDialog(removeName.getValue()));

        TextField serverUrl = new TextField();
        Button serverUpdate = new Button();

        serverUrl.setLabel("File Server Address");
        serverUrl.setWidth("min-content");
        serverUpdate.setText("Set");
        serverUpdate.setWidth("min-content");
        serverUpdate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.forField(serverUrl)
              .withValidator(value -> !value.isBlank(), "URL cannot be blank")
              .bind(AdminData::getServerUrl, AdminData::setServerUrl);

        serverUpdate.addClickListener(e -> {
            if(binder.validate().getValidationErrors().stream().noneMatch(result -> result.isError() && result.getErrorMessage().equals("URL cannot be blank"))) {
                if(packageService.reindexPackages(buffer.getServerUrl())) {
                    success("Success!");
                } else {
                    fail("Could not reindex!");
                }

                buffer.setServerUrl(adminService.getDataHook().getServerUrl());
                binder.refreshFields();
            }
        });

        EmailField emailField = new EmailField();
        Button setEmail = new Button("Update");

        emailField.setLabel("Contact email");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        setEmail.setWidth("min-content");
        setEmail.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.forField(emailField)
              .withValidator(new EmailValidator("Email is invalid"))
              .bind(AdminData::getContactEmail, AdminData::setContactEmail);

        setEmail.addClickListener(e -> {
            if(binder.validate().getValidationErrors().stream().noneMatch(result -> result.isError() && result.getErrorMessage().equals("Email is invalid"))) {
                adminService.setEmail(buffer);
                success("Success!");
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setWidth("min-content");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logoutButton.addClickListener(e -> securityService.logout());

        HorizontalLayout layoutRow = new HorizontalLayout();

        layoutRow.setWidthFull();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow.setAlignItems(Alignment.CENTER);
        layoutRow.setJustifyContentMode(JustifyContentMode.START);

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.START);
        getContent().setFlexGrow(1.0, layoutRow);
        getContent().add(passwordField);
        getContent().add(setCredentials);
        getContent().add(new Hr());
        getContent().add(removeName);
        getContent().add(removeButton);
        getContent().add(new Hr());
        getContent().add(emailField);
        getContent().add(setEmail);
        getContent().add(new Hr());
        getContent().add(layoutRow);
        layoutRow.add(serverUrl);
        getContent().add(serverUpdate);
        getContent().add(new Hr());
        getContent().add(logoutButton);

        binder.setBean(buffer);
    }

    public static void success(String msg) {
        Notification notification = Notification.show(msg, 2000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void fail(String msg) {
        Notification notification = Notification.show(msg, 2000,
                Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void confirmDialog(String pName) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete " + pName + "?");
        dialog.setText("Are you sure you want to permanently delete this package?");

        dialog.setCancelable(true);

        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            if(packageManager.removePackage(pName)) {
                success("Removed " + pName + "!");
            } else {
                fail("Could not find or remove package " + pName + "!");
            }
        });

        dialog.open();
    }
}
