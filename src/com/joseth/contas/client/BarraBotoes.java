package com.joseth.contas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.joseth.contas.client.backrest.BackupRestorePainel;
import com.joseth.contas.client.config.ConfigPainel;
import com.joseth.contas.client.movimentos.MovimentosPainel;
import com.joseth.contas.client.relatorios.RelatoriosPainel;
import com.joseth.contas.client.resumo.ResumoPainel;

public class BarraBotoes extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, BarraBotoes> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public BarraBotoes() 
	{
	    initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("homeBt")
	void onHomeBtClicked(ClickEvent event) 
	{
		ResumoPainel resumoPainel = new ResumoPainel(); 
		RootPanel.get("principal").clear();
		RootPanel.get("principal").add(resumoPainel);
	}
	
	@UiHandler("movimentosBt")
	void onMovimentosBtClicked(ClickEvent event) 
	{
		MovimentosPainel mvspnl = new MovimentosPainel(); 
		RootPanel.get("principal").clear();
		RootPanel.get("principal").add(mvspnl);
	}
	
	@UiHandler("configBt")
	void onConfigBtClicked(ClickEvent event) 
	{
		ConfigPainel configPainel = new ConfigPainel(); 
		RootPanel.get("principal").clear();
		RootPanel.get("principal").add(configPainel);
	}
	
	@UiHandler("bkprstBt")
	void onBkpRstBtClicked(ClickEvent event) 
	{
		BackupRestorePainel painel = new BackupRestorePainel(); 
		RootPanel.get("principal").clear();
		RootPanel.get("principal").add(painel);
	}
	
	@UiHandler("relatoriosBt")
    void onRelatorioBtClicked(ClickEvent event) 
    {
        RelatoriosPainel relatoriosPainel = new RelatoriosPainel(); 
        RootPanel.get("principal").clear();
        RootPanel.get("principal").add(relatoriosPainel);
    }
}
