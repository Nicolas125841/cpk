package com.ristn.cpk.server.views.usage;

import com.ristn.cpk.server.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

@PageTitle("Usage")
@Route(value = "usage", layout = MainLayout.class)
@Uses(Icon.class)
public class UsageView extends Composite<VerticalLayout> {

    public UsageView() {
        H2 h2 = new H2();
        Paragraph textMedium = new Paragraph();
        H3 h3 = new H3();
        Paragraph textMedium2 = new Paragraph();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Icon icon = new Icon();
        Paragraph textMedium3 = new Paragraph();
        H3 h32 = new H3();
        Paragraph textMedium4 = new Paragraph();
        Paragraph textMedium5 = new Paragraph();
        Paragraph textMedium6 = new Paragraph();
        Paragraph textMedium7 = new Paragraph();
        Paragraph textMedium8 = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        h2.setText("How to use the CPK CLI");
        h2.setWidth("max-content");
        textMedium.setText("Once you have a CPK CLI binary/JAR installed, using it takes two steps.");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        h3.setText("Add imports into your desired C files with @import");
        h3.setWidth("max-content");
        textMedium2.setText(
                "Much like #include preprocessor directives, CPK uses a special keyword to designate CPK packages to include.");
        textMedium2.setWidth("100%");
        textMedium2.getStyle().set("font-size", "var(--lumo-font-size-m)");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        icon.getElement().setAttribute("icon", "lumo:user");
//        icon.setHeight("100%");
        textMedium3.setText(
                "To add a CPK dependency, simply type @import(name1,name2,name3) in the first line of the C file, replacing the names with the packages you need to import.");
        textMedium3.setWidth("100%");
        textMedium3.getStyle().set("font-size", "var(--lumo-font-size-m)");
        h32.setText("Compile your program with the CPK CLI");
        h32.setWidth("max-content");
        textMedium4.setText("Run your compilation command as normal but replace your compiler with cpk");
        textMedium4.setWidth("100%");
        textMedium4.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textMedium5.setText("For instance gcc hello.c -o hello becomes cpk hello.c -o hello");
        textMedium5.setWidth("100%");
        textMedium5.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textMedium6.setText(
                "CPK will use the compiler specified by the CC environment variable, and all flags will be passed to that compiler.");
        textMedium6.setWidth("100%");
        textMedium6.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textMedium7.setText(
                "During preprocessing, you will see the following messages as cpk resolves import dependencies. Every dependency will be stored and built in a.cpk/{package name} folder within your project.");
        textMedium7.setWidth("100%");
        textMedium7.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textMedium8.setText(
                "If dependency resolution succeeds, compilation will occur as normal, otherwise an error message will be displayed indicating what went wrong. If everything succeeds, the C file will be compiled with the requirements injected.");
        textMedium8.setWidth("100%");
        textMedium8.getStyle().set("font-size", "var(--lumo-font-size-m)");
        getContent().add(h2);
        getContent().add(textMedium);
        getContent().add(h3);
        getContent().add(textMedium2);
        getContent().add(layoutRow);
        layoutRow.add(icon);
        layoutRow.add(textMedium3);
        getContent().add(h32);
        getContent().add(textMedium4);
        getContent().add(textMedium5);
        getContent().add(textMedium6);
        getContent().add(textMedium7);
        getContent().add(textMedium8);
    }
}
