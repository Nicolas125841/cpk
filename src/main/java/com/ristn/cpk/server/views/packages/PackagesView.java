package com.ristn.cpk.server.views.packages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import com.ristn.cpk.server.data.CPackage;
import com.ristn.cpk.server.data.UPackage;
import com.ristn.cpk.server.data.UploadFile;
import com.ristn.cpk.server.services.PackageService;
import com.ristn.cpk.server.views.MainLayout;
import com.ristn.cpk.server.views.admin.AdminView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.java.Log;
import org.vaadin.firitin.components.DynamicFileDownloader;

@PageTitle("Packages")
@Route(value = "packages", layout = MainLayout.class)
@Uses(Icon.class)
@Log
@AnonymousAllowed
public class PackagesView extends Div {
    private final ListDataProvider<CPackage> packages;
	private Grid<CPackage> grid;
	private final Filters filters;

    private final Dialog uploadPrompt;
    private final PackageService packageService;

    public PackagesView(PackageService packageService) {
        this.packageService = packageService;

        setSizeFull();
        addClassNames("packages-view");

        uploadPrompt = createAddPackageDialog();

        packages = DataProvider.ofCollection(packageService.getPackages());
        packages.setSortOrder(CPackage::getPackageName, SortDirection.ASCENDING);

        filters = new Filters(this::refreshGrid);

        Button addBtn = new Button("Add Package", e -> uploadPrompt.open());
        addBtn.addClassName("packages-view-button-1");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout pos = new VerticalLayout(addBtn);
        pos.setHeightFull();
        pos.setPadding(true);
        pos.setSpacing(false);
        pos.setAlignItems(FlexComponent.Alignment.END);
        pos.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        HorizontalLayout controlBar = new HorizontalLayout(createMobileFilters(), filters, pos);
        controlBar.setWidthFull();
        controlBar.setPadding(false);
        controlBar.setSpacing(false);
        controlBar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        VerticalLayout layout = new VerticalLayout(controlBar, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private Dialog createAddPackageDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("New package");

        SynchronousTextField packageName = new SynchronousTextField("Package Name:");
        packageName.setPlaceholder("package-name");

        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
        Span uploadStatus = new Span();
        uploadStatus.getStyle().set("color", "red");
        Upload upload = new Upload(multiFileMemoryBuffer);
        upload.setAcceptedFileTypes(".c", ".h", ".md");
        upload.setMaxFiles(3);
        upload.setMaxFileSize(256_000);

        VerticalLayout dialogLayout = new VerticalLayout(packageName, upload, uploadStatus);
        dialogLayout.setSizeFull();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        dialog.add(dialogLayout);

        Button uploadButton = new Button("Upload");
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(uploadButton);

        Binder<UPackage> binder = new Binder<>(UPackage.class);
        List<String> fileList = new ArrayList<>();

        binder.forField(packageName)
                .withValidator(name -> name.length() > 0, "")
                .withValidator(name -> name.matches("([a-z]+-)*[a-z]+"), "Package name must be lowercase and " +
                        "separated with -'s, ending with character")
                .withValidator(name -> !packageService.getPackages().contains(new CPackage(name)), "Package name " +
                        "already exists!")
                .bind(UPackage::getName, UPackage::setName);

        binder.forField(packageName)
                .withValidator(name -> name.matches("([a-z]+-)*[a-z]+") && fileList.size() != 0, "")
                .withValidator(name -> {
                    List<String> fileReq = Stream.of(".md", ".c", ".h").map(ext -> name + ext).toList();

                    return fileList.size() == 3 && fileList.containsAll(fileReq);
                }, "Must have one .md, .c, and .h file each with matching package name.")
                .withStatusLabel(uploadStatus)
                .bind(UPackage::getName, UPackage::setName);

        upload.addFinishedListener(event -> binder.validate());
        upload.addSucceededListener(event -> fileList.add(event.getFileName()));
        upload.getElement().addEventListener("file-remove", event -> fileList.remove(event.getEventData().getString("event.detail.file.name"))).addEventData("event.detail.file.name");

        binder.addStatusChangeListener(event -> uploadButton.setEnabled(event.getBinder().isValid()));

        uploadButton.addClickListener(e -> {
            if(binder.isValid() && packageService.uploadPackage(packageName.getValue(),
                    fileList.stream().map(name -> new UploadFile(name, multiFileMemoryBuffer.getInputStream(name))))) {
                dialog.close();
                AdminView.success("Package uploaded");
                refreshGrid();
            } else {
                AdminView.fail("Package failed to upload");
            }
        });

        dialog.addOpenedChangeListener(event -> {
            if(event.isOpened()) {
                packageName.clear();
                fileList.clear();
                binder.validate();
            }
        });

        return dialog;
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class SynchronousTextField extends TextField {

        public SynchronousTextField() {super();}
        public SynchronousTextField(String name) {super(name);}

        @Override
        @Synchronize("input")
        public String getValue() {
            return getElement().getProperty("value");
        }
    }

    public static class Filters extends Div {

        private final SynchronousTextField name = new SynchronousTextField();

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            name.setPlaceholder("Package name");
            name.setLabel("Find Package:");
            name.addInputListener(e -> onSearch.run());

            add(name);
        }

        private SerializablePredicate<CPackage> getPredicate() {
            if(!name.getValue().isBlank()) {
                return p -> p.getPackageName().contains(name.getValue().toLowerCase(Locale.ROOT).replaceAll("\s+",
                        "-"));
            }

            return p -> true;
        }
    }

    private Component createGrid() {
        grid = new Grid<>(CPackage.class, false);
        grid.addColumn("packageName").setAutoWidth(true);
        grid.addColumn(LitRenderer
                    .<CPackage>of("<a href='/details/${item.name}'>Details</a>")
                    .withProperty("name",
                            CPackage::getPackageName))
            .setAutoWidth(true)
            .setHeader("Package Details");
        grid.addColumn(new ComponentRenderer<>(pkg -> new DynamicFileDownloader("Download", pkg.getPackageName() + ".zip",
                out -> packageService.downloadPackage(pkg.getPackageName(), out))))
            .setAutoWidth(true)
            .setHeader("Source Code");
        grid.setDataProvider(packages);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        return grid;
    }

    private void refreshGrid() {
        packages.clearFilters();
        packages.addFilter(filters.getPredicate());
        packages.refreshAll();
    }
}
