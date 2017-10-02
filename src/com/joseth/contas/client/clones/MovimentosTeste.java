package com.joseth.contas.client.clones;

import java.util.Date;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.joseth.contas.beans.Movimento;

public class MovimentosTeste extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, MovimentosTeste> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	ProvidesKey<Movimento> KEY_PROVIDER = new ProvidesKey<Movimento>() { @Override public Object getKey(Movimento item) { return item.getId(); } };

	ListDataProvider<Movimento> movimentosDataProvider = new ListDataProvider<Movimento>();
	
	MultiSelectionModel<Movimento> msm = new MultiSelectionModel<Movimento>();
	
	@UiField(provided = true) CellTable<Movimento> movimentos = new CellTable<Movimento>(KEY_PROVIDER);

	
/* 
 * Construtores
 * 
 */
	public MovimentosTeste()
	{
		//Id
		colunaId.setSortable(true);
		colunaId.setCellStyleNames("mvId mvCelula");
	    movimentos.addColumn(colunaId,"Id");
        movimentos.getHeader(0).setHeaderStyleNames("mvId mvHeader");
//        movimentos.addColumnSortHandler(idSortHandler);
	    
	    // Data
  	    colunaData.setSortable(true);
  	    colunaData.setCellStyleNames("mvData mvCelula");
  	    movimentos.addColumn(colunaData, "Data");
  	    movimentos.getHeader(1).setHeaderStyleNames("mvData mvHeader");
//        movimentos.addColumnSortHandler(dataSortHandler);

	    // Descrição
  	    colunaDescricao.setSortable(true);
  	    colunaDescricao.setCellStyleNames("mvDescricao mvCelula");
  	    movimentos.addColumn(colunaDescricao, "Descrição");
  	    movimentos.getHeader(2).setHeaderStyleNames("mvDescricao mvHeader");
//  	    movimentos.addColumnSortHandler(descricaoSortHandler);
	    
  	    Movimento m = new Movimento();
  	    m.setId(10);
  	    m.setData(new Date());
  	    m.setDescricao("teste");
  	    movimentosDataProvider.getList().add(m);
  	    m = new Movimento();
	    m.setId(11);
	    m.setData(new Date());
	    m.setDescricao("teste2");
	    movimentosDataProvider.getList().add(m);
	    
	    movimentosDataProvider.addDataDisplay(movimentos);
  	  
		initWidget(uiBinder.createAndBindUi(this));
	}


    /*
     * Colunas
     */
   
	// Id
	Column<Movimento, String> colunaId = new Column<Movimento, String>(new TextCell()) 
    {
		public String getValue(Movimento m) {return m.getId()==null?"":m.getId()<0?"":""+m.getId();}
    };

	// Data
	final Column<Movimento, String> colunaData = new Column<Movimento, String>(	new EditTextCell() ) 
    {		
		{
	  	    setFieldUpdater(
	  	    	new FieldUpdater<Movimento, String>() 
	  	    	{
	  	    		@Override
	  	    		public void update(int index, Movimento m, String v) 
	  	    		{
	  	    			DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
	  	    			try
	  	    			{
	  	    				Date d = dtf.parseStrict(v);
	  	    				m.setData(d);
	  	    			}
	  	    			catch( IllegalArgumentException pe)
	  	    			{
	  	    				((EditTextCell) colunaData.getCell()).clearViewData(m.getId()); 
	  	    				movimentos.redraw();
	  	    			}
	  	    		}
	  	    	}
	      	);

		}
		public String getValue(Movimento m) 
		{
			DateTimeFormat dtf = null;
			Context ctx = new Context(0,0,m.getId());
			
			if( getCell().isEditing(ctx, null, null) )
				dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
			else
				dtf = DateTimeFormat.getFormat("E, dd/MM/yyyy");
			return dtf.format(m.getData());
		}
    };

	
	// Descrição
	Column<Movimento, String> colunaDescricao = new Column<Movimento, String>(new EditTextCell()) 
    {
		{
			setFieldUpdater(
			    	new FieldUpdater<Movimento, String>() 
			    	{
			    		@Override
			    		public void update(int index, Movimento m, String v) 
			    		{
			    			m.setDescricao(v);
			    		}
			    	}
		    	);
		}
        public String getValue(Movimento m) {return m.getDescricao()==null?"":""+m.getDescricao();}
    };
}