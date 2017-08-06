package com.joseth.contas.client.movimentos;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.joseth.contas.client.carregar.Carregar;
import com.joseth.contas.client.clones.Movimentos2;
import com.joseth.contas.client.saldos.Saldos;

public class MovimentosPainel extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, MovimentosPainel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public MovimentosPainel() 
	{
	    initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiField
	Panel movimentosPrincipal;
	
	@UiHandler("movsBt")
	void onClassBtClicked(ClickEvent event) 
	{
		Movimentos mvs = new Movimentos();
		movimentosPrincipal.clear();
		movimentosPrincipal.add(mvs);
	}
	
	@UiHandler("carregarBt")
	void onCarregarBtClicked(ClickEvent event) 
	{
		Carregar carregar = new Carregar(null,null);
		movimentosPrincipal.clear();
		movimentosPrincipal.add(carregar);
	}
	
   @UiHandler("saldosBt")
    void onSaldosBtClicked(ClickEvent event) 
    {
        Saldos saldos = new Saldos();
        movimentosPrincipal.clear();
        movimentosPrincipal.add(saldos);
    }
}