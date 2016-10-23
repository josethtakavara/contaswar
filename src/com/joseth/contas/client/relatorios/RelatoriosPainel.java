package com.joseth.contas.client.relatorios;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class RelatoriosPainel extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, RelatoriosPainel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public RelatoriosPainel() 
	{
	    initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiField
	Panel relatoriosPrincipal;
	
	@UiHandler("relatorioPeriodoBt")
	void onClassBtClicked(ClickEvent event) 
	{
		RelatorioPeriodo mvs = new RelatorioPeriodo();
		relatoriosPrincipal.clear();
		relatoriosPrincipal.add(mvs);
	}
}