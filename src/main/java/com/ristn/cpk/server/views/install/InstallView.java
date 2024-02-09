package com.ristn.cpk.server.views.install;

import com.ristn.cpk.server.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

@PageTitle("Install")
@Route(value = "install", layout = MainLayout.class)
@Uses(Icon.class)
public class InstallView extends Composite<VerticalLayout> {

    public InstallView() {
        H2 h2 = new H2();
        Paragraph textMedium = new Paragraph();
        H3 h3 = new H3();
        Paragraph textMedium2 = new Paragraph();
        Button buttonPrimary = new Button();
        Paragraph textMedium3 = new Paragraph();
        H3 h32 = new H3();
        HorizontalLayout layoutRow = new HorizontalLayout();
        H4 h4 = new H4();
        Paragraph textMedium4 = new Paragraph();
        Button buttonPrimary2 = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        h2.setText("How to install the CPK CLI");
        h2.setWidth("max-content");
        textMedium.setText(
                "There are three ways to install the CPK CLI: Download a prebuilt binary, build your own binary, or download the JAR bundle.");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        h3.setText("Download a prebuilt binary");
        h3.setWidth("max-content");
        textMedium2.setText("Go to the CPK Github releases page and download a binary for your system.");
        textMedium2.setWidth("100%");
        textMedium2.getStyle().set("font-size", "var(--lumo-font-size-m)");
        buttonPrimary.setText("Releases");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        textMedium3.setText("Then, move the binary to a desired directory and add it to your PATH.");
        textMedium3.setWidth("100%");
        textMedium3.getStyle().set("font-size", "var(--lumo-font-size-m)");
        h32.setText("Build your own binary");
        h32.setWidth("max-content");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        h4.setText("Requires a GraalVM JDK Java 21");
        h4.setWidth("max-content");
        textMedium4.setText("Instructions are given in BUILD.md on the CPK CLI Github");
        textMedium4.setWidth("100%");
        textMedium4.getStyle().set("font-size", "var(--lumo-font-size-m)");
        buttonPrimary2.setText("Github");
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(h2);
        getContent().add(textMedium);
        getContent().add(h3);
        getContent().add(textMedium2);
        getContent().add(buttonPrimary);
        getContent().add(textMedium3);
        getContent().add(h32);
        getContent().add(layoutRow);
        layoutRow.add(h4);
        getContent().add(textMedium4);
        getContent().add(buttonPrimary2);
    }
}
