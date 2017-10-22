package com.joseth.contas.client.saldos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Saldo;
import com.joseth.contas.client.Home;

public class Saldos extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Saldos> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	NumberFormat nf = NumberFormat.getFormat("###0.00");
	
	@UiField(provided = true) protected ListBox conta = new ListBox();
	@UiField(provided = true) protected ListBox ano = new ListBox();
	@UiField(provided = true) DataGrid<SaldoCalculado> saldosGrid = new DataGrid<SaldoCalculado>();
    ListDataProvider<SaldoCalculado> saldosDataProvider = new ListDataProvider<SaldoCalculado>();
    
//    Map<String, Double> anoMesSoma  = new HashMap<String, Double>();
    Map<String, Conta>  contasMap   = new HashMap<String, Conta>();
	
	public Saldos() 
	{      
	    conta.addChangeHandler(changeHandler);
	    ano.addChangeHandler(changeHandler);
	    
	    colunaMesAno.setCellStyleNames("saldoCelula");
	    colunaInicial.setCellStyleNames("saldoCelula");
	    colunaMovimentos.setCellStyleNames("saldoCelula");
	    colunaMovimentos.setCellStyleNames("saldoCelula");
	    colunaFinal.setCellStyleNames("saldoCelula");
	    colunaDiferenca.setCellStyleNames("saldoCelula");
	    saldosGrid.addColumn(colunaMesAno,     "Periodo");
        saldosGrid.addColumn(colunaInicial,    "Inicial");
        saldosGrid.addColumn(colunaMovimentos, "Movs");
        saldosGrid.addColumn(colunaFinal,      "Final"  );
        saldosGrid.addColumn(colunaDiferenca,  "Diferença"  );
        
        saldosGrid.setRowStyles(new RowStyles<SaldoCalculado>() 
        {
            @Override
            public String getStyleNames(SaldoCalculado row, int rowIndex) 
            {
                return (rowIndex%2)==0?"par":"impar";
            }
        });
        
        saldosDataProvider.addDataDisplay(saldosGrid);
       
        Home.serviceBus.getContas(
            new AsyncCallback<List<Conta>>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List<Conta> result)
                {
                    conta.addItem("Conta", "-1");
                    for(Conta c: result)
                    {
                        contasMap.put(""+c.getId(),c);
                        conta.addItem(c.getUsuario().getNome() + " - " + c.getNumero(), ""+c.getId());
                    }
                }
            }
        );
        
        Home.serviceBus.getAnos(
                new AsyncCallback<List<Integer>>()
                {
                    public void onFailure(Throwable caught){}
                    public void onSuccess(List<Integer> result)
                    {
                        ano.addItem("Ano");
                        for(Integer m:result)
                        {
                            ano.addItem(m.toString());
                        }
                    }
                }
            );
	    
	    initWidget(uiBinder.createAndBindUi(this));    
	}
	
	public void escolheConta()
	{
		final Map<String, Saldo>  anoMesSaldo = new HashMap<String, Saldo>();
	    saldosDataProvider.getList().clear();
	    if( conta.getSelectedIndex() >= 1 && ano.getSelectedIndex() >= 1 )
	    {
	        saldosDataProvider.getList().clear();
            Home.serviceBus.getSaldos( conta.getValue(conta.getSelectedIndex()) , 
                new AsyncCallback<List<Saldo>>()
                {
                    public void onFailure(Throwable caught){}
                    public void onSuccess(List<Saldo> result)
                    {
                        for( Saldo s:result)
                        {
                            String anoMes = s.getAno()+"/"+s.getMes();
                            anoMesSaldo.put(anoMes, s);
                        }
                        Home.serviceBus.getAnoMesSoma( conta.getValue(conta.getSelectedIndex()), ano.getValue(ano.getSelectedIndex()),
                            new AsyncCallback<List>()
                            {
                                public void onFailure(Throwable caught){}
                                public void onSuccess(List result)
                                {
                                    SaldoCalculado sca = null;
                                    for( Map m: (List<Map>)result)
                                    {
                                        String anoMes = m.get("ano")+"/"+m.get("mes");
                                        Saldo sr = anoMesSaldo.get(anoMes);
                                        if( sr == null )
                                        {
                                        	sr = new Saldo();
                                            sr.setId(-1);
                                            sr.setAno((Integer)m.get("ano"));
                                            sr.setMes((Integer)m.get("mes"));
                                            sr.setConta( contasMap.get( conta.getValue( conta.getSelectedIndex() ) ) );
                                        }
                                        
                                        SaldoCalculado sc = new SaldoCalculado(sr,sca,(Double)m.get("soma"));
                                        
                                        sca = sc;
                                        saldosDataProvider.getList().add(sc);
                                    }
                                    saldosGrid.setPageSize(saldosDataProvider.getList().size());
                                }
                            }
                        );
                    }
                }
            );
	    }
	}
	
	private ChangeHandler changeHandler = new ChangeHandler()
	{
        @Override
        public void onChange(ChangeEvent event)
        {
            escolheConta();            
        }
	};

	@UiHandler("refreshSaldosBt")
    void onRefreshSaldosBtClicked(ClickEvent event) 
    {
        escolheConta();            
    };
	
	// Mes Ano
    private Column<SaldoCalculado, String> colunaMesAno = new Column<SaldoCalculado, String>(new TextCell()) 
    {
        public String getValue(SaldoCalculado s) 
        {
           return (s.getSaldoReal().getMes() < 10 ? "0":"") + s.getSaldoReal().getMes() + "/" + s.getSaldoReal().getAno();
        }
    };
    // Inicial
    Column<SaldoCalculado, String> colunaInicial = new Column<SaldoCalculado, String>(new EditTextCell()) 
    {
        {
            setFieldUpdater(
                new FieldUpdater<SaldoCalculado, String>() 
                {
                    @Override
                    public void update(int index, SaldoCalculado s, String v) 
                    {
                        if( v != null && !v.trim().equals("") )
                        {
                            Double val = nf.parse(v);
                            s.setSaldoInicial(val);
                        }                            
                        else
                        {
                            s.setSaldoInicial(null);
                        }
                        ((EditTextCell)getCell()).clearViewData(s);
                        Home.serviceBus.atualizarSaldo(s.getSaldoReal(),
                            new AsyncCallback<Void>()
                            {
                                public void onFailure(Throwable caught){}
                                public void onSuccess(Void result)
                                {
                                    escolheConta();
                                    saldosGrid.redraw();
                                }
                            }
                        );
                    }
                }
            );
        }
        public String getValue(SaldoCalculado s) 
        {
            if( s.getSaldoInicial() == null )
                return "0.0";
            else
                return nf.format(s.getSaldoInicial());
        }
        public String getCellStyleNames(Cell.Context context, SaldoCalculado s)
        {
            String ret = super.getCellStyleNames(context,s);
            
            if( s.getSaldoInicial() == null )
                return ret;

            SaldoCalculado anterior = s.getSaldoAnterior();
            
            if( anterior != null )
            	if( s.getSaldoInicial().compareTo(anterior.getSaldoFinal()) != 0 )
                    ret += " saldoDiferente";
                else
                    ret += " saldoOK";
            
            Saldo sr = s.getSaldoReal();
            if( sr != null && sr.getSaldoInicial() != null )
            	ret += " saldoPreenchido";            	
            
            return ret;
        }
    };
    // Coluna Movimentos
    Column<SaldoCalculado, String> colunaMovimentos = new Column<SaldoCalculado, String>(new TextCell()) 
    {
        public String getValue(SaldoCalculado s) 
        {
            Double soma = s.getMovimentos();
            return nf.format(soma);
        }
    };
    // Coluna Diferenças
    Column<SaldoCalculado, String> colunaDiferenca = new Column<SaldoCalculado, String>(new TextCell()) 
    {
        public String getValue(SaldoCalculado s) 
        {
            return nf.format(s.getMovimentos()-(s.getSaldoFinal()-s.getSaldoInicial()));
        }
        public String getCellStyleNames(Cell.Context context, SaldoCalculado s)
        {
            String ret = super.getCellStyleNames(context,s);
            Double soma = Math.round(s.getMovimentos()*100)/100.0;

            if( soma.compareTo( Math.round( (s.getSaldoFinal()-s.getSaldoInicial()) * 100) / 100.0 ) != 0 )
                ret += " saldoDiferente";
            else
                ret += " saldoOK";
            return ret;
        }

    };
    // Final
    Column<SaldoCalculado, String> colunaFinal = new Column<SaldoCalculado, String>(new EditTextCell()) 
    {
        {
            setFieldUpdater(
                new FieldUpdater<SaldoCalculado, String>() 
                {
                    @Override
                    public void update(int index, SaldoCalculado s, String v) 
                    {
                        if( v != null && !v.trim().equals("") )
                        {
                            Double val = nf.parse(v);
                            s.setSaldoFinal(val);
                        }
                        else
                        {
                            s.setSaldoFinal(null);
                        }
                        ((EditTextCell)getCell()).clearViewData(s);
                        Home.serviceBus.atualizarSaldo(s.getSaldoReal(),
                            new AsyncCallback<Void>()
                            {
                                public void onFailure(Throwable caught){}
                                public void onSuccess(Void result)
                                {
                                    escolheConta();
                                    saldosGrid.redraw();
                                }
                            }
                        );
                    }
                }
            );
        }
        public String getValue(SaldoCalculado s) 
        {
            if( s.getSaldoFinal() == null )
                return "0.0";
            else
                return nf.format(s.getSaldoFinal());
        }
        public String getCellStyleNames(Cell.Context context, SaldoCalculado s)
        {
            String ret = super.getCellStyleNames(context,s);

            if( s.getSaldoFinal() == null )
                return ret;
            
            if( s.getSaldoFinal().compareTo( Math.round( ( s.getSaldoInicial() + s.getMovimentos() ) * 100.0 ) / 100.0 ) != 0 )
                ret += " saldoDiferente";
            else
                ret += " saldoOK";
            
            Saldo sr = s.getSaldoReal();
            if( sr != null && sr.getSaldoFinal() != null )
            	ret += " saldoPreenchido";
            
            return ret;
        }

    };
}

class SaldoCalculado
{
	private SaldoCalculado anterior;
	private Saldo saldoReal;
	private Double movimentos; 
	public SaldoCalculado(Saldo saldoReal, SaldoCalculado anterior, Double movimentos)
	{
		this.saldoReal = saldoReal;
		this.anterior = anterior;
		this.movimentos = movimentos;
	}
	public Saldo getSaldoReal() { return saldoReal; }
	public SaldoCalculado getSaldoAnterior() { return anterior; }
	public Double getMovimentos() { return movimentos; }
	public Double getSaldoInicial()
	{
		if( saldoReal == null || saldoReal.getSaldoInicial() == null )
			if( anterior != null )
				return anterior.getSaldoFinal();
			else
				return 0.0;
		else
			return saldoReal.getSaldoInicial();
	}
	
	public Double getSaldoFinal()
	{
		if( saldoReal == null || saldoReal.getSaldoFinal() == null )
			return Math.round( ( getSaldoInicial() + movimentos ) * 100.0 ) / 100.0;
		else
			return saldoReal.getSaldoFinal();
	}
	public Double getSaldoInicialReal() { if( saldoReal == null ) return null; else return saldoReal.getSaldoInicial(); }
	public Double getSaldoFinalReal() { if( saldoReal == null ) return null; else return saldoReal.getSaldoFinal(); }
	public void setSaldoInicial( Double val ) { saldoReal.setSaldoInicial(val); }
	public void setSaldoFinal( Double val ) { saldoReal.setSaldoFinal(val); }
}
