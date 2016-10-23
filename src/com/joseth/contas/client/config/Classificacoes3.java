package com.joseth.contas.client.config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Classificacoes3 extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Classificacoes3> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
    @UiField(provided=true) ClassificacoesGrid classGrid=new ClassificacoesGrid();
	
	
	@UiHandler("classAddBt")
	void onAddBtClicked(ClickEvent event) 
	{
	    classGrid.novaClassificacao();
	}

	@UiHandler("classRemBt")
	void onRemBtClicked(ClickEvent event) 
	{
//		dataProvider.getList().remove(dataProvider.getList().size()-1);
	}
	

	public Classificacoes3() 
	{
	    initWidget(uiBinder.createAndBindUi(this));
	}
}



