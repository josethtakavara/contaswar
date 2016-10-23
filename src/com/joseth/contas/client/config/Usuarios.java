package com.joseth.contas.client.config;

import java.util.List;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.client.Home;

public class Usuarios extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Usuarios> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField(provided=true)
	HorizontalPanel pnUsuarios;
	
	public Usuarios() 
	{
		pnUsuarios = new HorizontalPanel();	
		Home.serviceBus.getUsuarios(
			new AsyncCallback<List<Usuario>>()
			{
				public void onFailure(Throwable caught){}
			    public void onSuccess(List<Usuario> result)
			    {
			    	for(Usuario u:result)
			    	{
				    	ListDataProvider<Conta> dataProvider = new ListDataProvider<Conta>();
				    	CellTable<Conta> cellTable = new CellTable<Conta>();
					    initTableColumns(cellTable,u);
					    dataProvider.getList().addAll(u.getContas());
					    dataProvider.addDataDisplay(cellTable);				    
					    pnUsuarios.add(cellTable);
			    	}
			    }
			}
		);
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private void initTableColumns(CellTable<Conta> cellTable, Usuario u) 
	{
		Column<Conta, String> nomeColumn = 
		new Column<Conta, String>(new EditTextCell()) 
		{
			@Override
			public String getValue(Conta object) {return object.getNumero();}
		};
		nomeColumn.setHorizontalAlignment(Column.ALIGN_CENTER);
		cellTable.addColumn(nomeColumn, u.getNome() );
		nomeColumn.setFieldUpdater(
			new FieldUpdater<Conta, String>() 
			{
				@Override
				public void update(int index, Conta object, String value) 
				{
					object.setNumero(value);
				}
			}
		);
	}
}

//ListDataProvider<Conta> dataProvider = new ListDataProvider<Conta>();
//List<Usuario> usuarios = new ArrayList<Usuario>();
//Conta c = new Conta();
//c.setId(1);
//c.setNumero("123");
//Usuario u = new Usuario();
//u.setId(1);
//u.setNome("a");
//u.getContas().add(c);
//c = new Conta();
//c.setId(2);
//c.setNumero("123456");
//u.getContas().add(c);

//CellTable<Conta> cellTable = new CellTable<Conta>();
////cellTable.setWidth("10%", true);
//cellTable.setAutoHeaderRefreshDisabled(true);
//cellTable.setAutoFooterRefreshDisabled(true);
//initTableColumns(cellTable,u);
//dataProvider.getList().addAll(u.getContas());
//dataProvider.addDataDisplay(cellTable);
//
//pnUsuarios.add(cellTable);
//
//dataProvider = new ListDataProvider<Conta>();
//c = new Conta();
//c.setId(3);
//c.setNumero("456");
//u = new Usuario();
//u.setId(2);
//u.setNome("b");
//u.getContas().add(c);
//c = new Conta();
//c.setId(4);
//c.setNumero("456789");
//u.getContas().add(c);
//
//cellTable = new CellTable<Conta>();
////cellTable.setWidth("10%", true);
//cellTable.setAutoHeaderRefreshDisabled(true);
//cellTable.setAutoFooterRefreshDisabled(true);
//initTableColumns(cellTable,u);
//dataProvider.getList().addAll(u.getContas());


//dataProvider.addDataDisplay(cellTable);
//
//pnUsuarios.add(cellTable);