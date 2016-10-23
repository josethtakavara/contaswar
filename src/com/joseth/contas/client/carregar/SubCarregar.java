package com.joseth.contas.client.carregar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.joseth.contas.beans.Movimento;

public class SubCarregar extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, SubCarregar> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");

	PopupPanel painelPai;
	Movimento movimentoPai;
	 
	@UiField(provided=true)
	Label paiLabel = new Label();
	
	@UiField(provided=true)
	Carregar carregar;
	 
	@UiHandler("fecharBt")
	void onFecharBtClicked(ClickEvent event) 
	{
		painelPai.hide();
	}
	
	public SubCarregar(PopupPanel painelPai, Movimento movimentoPai) 
	{
		this.painelPai = painelPai;
		this.movimentoPai = movimentoPai;
		paiLabel.setText(" - " + dtf.format(movimentoPai.getData())+" - "+movimentoPai.getDescricao());
		carregar = new Carregar(painelPai,movimentoPai);
		initWidget(uiBinder.createAndBindUi(this));
	}
}