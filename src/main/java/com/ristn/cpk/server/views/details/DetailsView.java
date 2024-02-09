package com.ristn.cpk.server.views.details;

import com.ristn.cpk.server.services.PackageService;
import com.ristn.cpk.server.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.firitin.components.RichText;

import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Package Details")
@Route(value = "details", layout = MainLayout.class)
@AnonymousAllowed
public class DetailsView extends VerticalLayout implements HasUrlParameter<String> {
	private final PackageService packageService;
	public DetailsView(@Autowired PackageService packageService) {
		this.packageService = packageService;
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		add(new RichText().withMarkDown(packageService.getPackageDetails(parameter)));
	}
}
