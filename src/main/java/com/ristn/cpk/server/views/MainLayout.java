package com.ristn.cpk.server.views;

import com.ristn.cpk.server.views.about.AboutView;
import com.ristn.cpk.server.views.admin.AdminView;
import com.ristn.cpk.server.views.packages.PackagesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL,
                    TextColor.BODY);
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

    }

    private Dialog contactAdminDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Contact Admin");

        Select<String> select = new Select<>();
        select.setLabel("Reason");
        select.setItems("Remove package", "Question", "Other");
        select.setValue("Remove package");

        TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.setHeightFull();
        textArea.setLabel("Message");
        textArea.setPlaceholder("Enter message here:");

        dialog.add(select, textArea);

        Button uploadButton = new Button("Send", e -> dialog.close());
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(uploadButton);

        return dialog;
    }

    public Button createLoginButton() {
        Button button = new Button();
        button.setIcon(LineAwesomeIcon.COG_SOLID.create());

        button.addClassNames("fixed");
        button.getElement().getStyle().set("right", "10px");
        button.getElement().getStyle().set("bottom", "5px");

        button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.navigate(AdminView.class)));

        return button;
    }

    public MainLayout() {
        addToNavbar(createHeaderContent());
        setDrawerOpened(false);
    }

    private Component createHeaderContent() {
        Header header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        Div layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN, Padding.Horizontal.LARGE);

        H1 appName = new H1("C Package Manager");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);
        layout.add(appName);

        Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL);

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems(appName)) {
            list.add(menuItem);
        }

        Dialog adminContact = contactAdminDialog();

        Button contactButton = new Button("Contact Admin");
        contactButton.addClickListener(e -> adminContact.open());

        layout.add(contactButton);

        header.add(layout, nav, createLoginButton());
        return header;
    }

    private MenuItemInfo[] createMenuItems(Component pageName) {
        return new MenuItemInfo[]{
                new MenuItemInfo("Home", LineAwesomeIcon.QUESTION_SOLID.create(), AboutView.class),
                new MenuItemInfo("Packages", LineAwesomeIcon.ARCHIVE_SOLID.create(), PackagesView.class)
        };
    }

}
