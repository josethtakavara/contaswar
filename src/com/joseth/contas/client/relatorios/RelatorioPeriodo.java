package com.joseth.contas.client.relatorios;

import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.joseth.contas.client.filtro.FiltroMovimentos;

public class RelatorioPeriodo extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, RelatorioPeriodo> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField(provided=true) FiltroMovimentos filtroMovimentos = new FiltroMovimentosRelatorio(this);
	@UiField(provided=true) VerticalPanel painelResultado = new VerticalPanel();
	
    ListDataProvider movimentosDataProvider = new ListDataProvider();
	
	public RelatorioPeriodo() 
	{
	    initWidget(uiBinder.createAndBindUi(this));
	    
	}
	   ProvidesKey<Map> KEY_PROVIDER = new ProvidesKey<Map>() { @Override public Object getKey(Map item) { return item.get("nome"); } };

	public void resultado(List res)
	{
        DataGrid<Map> resultado = new DataGrid<Map>(KEY_PROVIDER);
        resultado.addStyleName("relatorioPeriodoDataGrid");
        resultado.setRowStyles(new RowStyles<Map>() 
        {
            @Override
            public String getStyleNames(Map row, int rowIndex) 
            {
                return (rowIndex%2)==0?"par":"impar";
            }
        });
        
        List<String> mesAnoTabela = (List<String>)res.get(0);
        res.remove(0);

	    Column<Map, String> colunaClass = new Column<Map, String>(new TextCell()) 
	    {       
	        public String getValue(Map m) 
	        {
	            return (String)m.get("nome");
	        }
	    };
	    colunaClass.setCellStyleNames("relClass");
	    resultado.addColumn(colunaClass, "Classificação");
	    resultado.getHeader(0).setHeaderStyleNames("relClass");
	    
	    int i=1;
	    for(String h: mesAnoTabela )
	    {
	        if( !h.equals("nome") && !h.equals("Total") )
	        {
    	        ColunaData dt = new ColunaData(new TextCell(),h);
                resultado.addColumn(dt, h);
                resultado.getHeader(i).setHeaderStyleNames("relValor");
                i++;
	        }
	    }

       Column<Map, String> colunaTotal = new Column<Map, String>(new TextCell()) 
       {  
           NumberFormat nf = NumberFormat.getFormat("#,##0.00");
           public String getValue(Map m) 
           {
               return nf.format((Double)m.get("Total"));
           }
       };
       colunaTotal.setCellStyleNames("relTotal");
       resultado.addColumn(colunaTotal, "Total");
       resultado.getHeader(i).setHeaderStyleNames("relTotal");

	    
        movimentosDataProvider.getList().clear();
        movimentosDataProvider.getList().addAll(res);
        movimentosDataProvider.addDataDisplay(resultado);
        
        painelResultado.clear();
        painelResultado.add(resultado);
	}
}

class ColunaData extends Column<Map, String> 
{   
    String nomeColuna;
    NumberFormat nf = NumberFormat.getFormat("#,##0.00");
    public ColunaData(Cell<String> cell,String nomeColuna)
    {
        super(cell);
        this.nomeColuna = nomeColuna;
        setCellStyleNames("relValor");
    }
    public String getValue(Map m) 
    {
        Double d = (Double)m.get(nomeColuna);
        String ret="";
        if( d != null )
            ret = nf.format(d);
        return ret;

    }
};