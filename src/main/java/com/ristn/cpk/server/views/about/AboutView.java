package com.ristn.cpk.server.views.about;

import com.ristn.cpk.server.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {
        UI.getCurrent().getPage().addJsModule("https://unpkg.com/typed.js@2.1.0/dist/typed.umd.js");

        setSpacing(true);
        getThemeList().add("spacing-xl");
        setId("dyna-bkg");

        H1 title = new H1("CPK (C Package Manger) is a platform to share, find, and use C libraries for free!");
        title.getStyle().setTextAlign(Style.TextAlign.CENTER);
        title.setWidthFull();
        add(title);

        H2 cedit = new H2("");
        cedit.getStyle().setTextAlign(Style.TextAlign.CENTER);
        cedit.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        cedit.getStyle().setBackground("var(--lumo-primary-color-10pct)");
        cedit.getStyle().setPadding("1em");
        cedit.getStyle().setBorderRadius("10px");
        cedit.setId("require-animation");
        add(cedit);

        UnorderedList features = new UnorderedList();
        Span notation = new Span("@import(package-1,package-2,package-3...)");
        notation.getElement().getThemeList().add("badge");
        notation.getStyle().setMarginInlineStart("0.5em");
        notation.getStyle().setMarginInlineEnd("0.5em");

        features.add(
                new ListItem(inlineH2("Find packages on the \"Packages\" list that suit your project needs")),
                new ListItem(inlineH2("View package usages and details on its \"Description\" page")),
                new ListItem(inlineH2("Donwload package source code to modify packages for your needs")),
                new ListItem(inlineH2("Share and store your own C libraries to help others and put your code in an " +
                            "easy-to-access place")),
                new ListItem(
                        inlineH2("Simply specify package dependencies with a "),
                        notation,
                        inlineH2("declaration in your C program (requires CPK client).")
                )
        );

        features.getStyle().setTextAlign(Style.TextAlign.LEFT);

        add(features);

        String js = """
                new Typed('#require-animation', {
                    strings: ['@import(fft)', '@import(fft,socket)', '@import(dependencies)', '@import(cpk)'],
                    typeSpeed: 80,
                    backSpeed: 30,
                    smartBackspace: true,
                    showCursor: false
                });
                """;

        UI.getCurrent().getPage().executeJs(js);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.START);
        getStyle().set("text-align", "center");
    }

    private H2 inlineH2(String text) {
        H2 h2 = new H2(text);
        h2.getStyle().set("display", "inline-block");
        h2.getStyle().setMarginBottom("1em");

        return h2;
    }

}
