package com.ristn.cpk.server.views.login;

import com.ristn.cpk.server.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Login")
@Route(value = "login", layout = MainLayout.class)
@Uses(Icon.class)
@AnonymousAllowed
public class LoginView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    LoginForm loginForm = new LoginForm();

    public LoginView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, loginForm);
        getContent().add(loginForm);

        loginForm.setAction("login");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                           .getQueryParameters()
                           .getParameters()
                           .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
