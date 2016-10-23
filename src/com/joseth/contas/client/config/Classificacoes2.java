package com.joseth.contas.client.config;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.client.Home;

public class Classificacoes2 extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Classificacoes2> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField(provided = true)
	CellTable<Classificacao> cellTable;
	@UiField(provided = true)
	SimplePager pager;
	
	private ListDataProvider<Classificacao> dataProvider = new ListDataProvider<Classificacao>();
	
//	private HandlerRegistration registration;
	
	
	@UiHandler("classAddBt")
	void onAddBtClicked(ClickEvent event) 
	{
		Classificacao c = new Classificacao();
	    c.setId(1);
	    c.setNome("a");
	    dataProvider.getList().add(c);
	}

	@UiHandler("classRemBt")
	void onRemBtClicked(ClickEvent event) 
	{
		dataProvider.getList().remove(dataProvider.getList().size()-1);
	}
	
//	@Override
//    protected void onLoad() {
//        super.onLoad();
//        registration = addDomHandler
//		(
//			new KeyDownHandler() 
//	        {
//	            public void onKeyDown(final KeyDownEvent event) 
//	            {
//	                if( event.getNativeKeyCode() == KeyCodes.KEY_DELETE )
//	                	dataProvider.getList().remove(dataProvider.getList().size()-1);
//	            }
//	        }, 
//	        KeyDownEvent.getType()
//        );
//    }
	
//    @Override
//    protected void onUnload() {
//        super.onUnload();
//
////        registration.removeHandler();
//        registration = null;
//    }

	
	public Classificacoes2() 
	{
		cellTable = new CellTable<Classificacao>();
		cellTable.setWidth("100%", true);

		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(cellTable);
		
	    initTableColumns();
	    
	    dataProvider.addDataDisplay(cellTable);
	    
	    Home.serviceBus.getClassificacoes(new ListCallBack<Classificacao>(dataProvider.getList()));

	    initWidget(uiBinder.createAndBindUi(this));
	}
	
	private void initTableColumns() 
	{
		Column<Classificacao, String> nomeColumn = new Column<Classificacao, String>(new EditTextCell()) 
	    {
			@Override
			public String getValue(Classificacao object) {return object.getNome();}
	    };
	    nomeColumn.setSortable(true);
	    cellTable.addColumn(nomeColumn, "Nome");
//	    cellTable.getHeader(0).setHeaderStyleNames("{style.cellHeader}");
	    nomeColumn.setFieldUpdater(
	    	new FieldUpdater<Classificacao, String>() 
	    	{
	    		@Override
	    		public void update(int index, Classificacao object, String value) 
	    		{
	    			object.setNome(value);
//	    			Home.serviceBus.update(object,null);
	    		}
	    	}
    	);
	    cellTable.setColumnWidth(nomeColumn, 20, Unit.PCT);
	}
}

//Classificacao c = new Classificacao();
//c.setId(1);
//c.setNome("a");
//dataProvider.getList().add(c);
//c = new Classificacao();
//c.setId(2);
//c.setNome("b");
//dataProvider.getList().add(c);