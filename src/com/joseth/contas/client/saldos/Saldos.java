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
	@UiField(provided = true) DataGrid<Saldo> saldosGrid = new DataGrid<Saldo>();
    ListDataProvider<Saldo> saldosDataProvider = new ListDataProvider<Saldo>();
    
    Map<String, Double> anoMesSoma  = new HashMap<String, Double>();
    Map<String, Saldo>  anoMesSaldo = new HashMap<String, Saldo>();
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
        
        saldosGrid.setRowStyles(new RowStyles<Saldo>() 
        {
            @Override
            public String getStyleNames(Saldo row, int rowIndex) 
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
	    saldosDataProvider.getList().clear();
	    if( conta.getSelectedIndex() >= 1 && ano.getSelectedIndex() >= 1 )
	    {
	        saldosDataProvider.getList().clear();
	        anoMesSaldo.clear();
	        anoMesSoma.clear();
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
                                    Saldo sva = null;
                                    for( Map m: (List<Map>)result)
                                    {
                                        String anoMes = m.get("ano")+"/"+m.get("mes");
                                        anoMesSoma.put(anoMes, (Double)m.get("soma"));
                                        Saldo sr = anoMesSaldo.get(anoMes);
                                        
                                        Saldo sv = new Saldo();
                                        sv.setAno((Integer)m.get("ano"));
                                        sv.setMes((Integer)m.get("mes"));
                                        sv.setConta( contasMap.get( conta.getValue( conta.getSelectedIndex() ) ) );
                                        
                                        if( sr != null )
                                        {
                                            if( sr.getSaldoInicial() != null )
                                                sv.setSaldoInicial( sr.getSaldoInicial() );
                                            else if( sva != null )
                                                sv.setSaldoInicial( sva.getSaldoFinal() );
                                            else
                                                sv.setSaldoInicial( 0.0 );
                                            
                                            if( sr.getSaldoFinal() != null )
                                                sv.setSaldoFinal( sr.getSaldoFinal() );
                                            else
                                                sv.setSaldoFinal( sv.getSaldoInicial() + (Double)m.get("soma") );
                                        }
                                        else if( sva != null )
                                        {
                                            sv.setSaldoInicial(sva.getSaldoFinal());
                                            sv.setSaldoFinal(sva.getSaldoFinal()+(Double)m.get("soma"));
                                        }
                                        else
                                        {
                                            sv.setSaldoInicial(0.0);
                                            sv.setSaldoFinal((Double)m.get("soma"));
                                        }
                                        sva=sv;
                                        saldosDataProvider.getList().add(sv);
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
    private Column<Saldo, String> colunaMesAno = new Column<Saldo, String>(new TextCell()) 
    {
        public String getValue(Saldo s) 
        {
           return (s.getMes() < 10 ? "0":"") + s.getMes() + "/" + s.getAno();
        }
    };
    
    // Inicial
    Column<Saldo, String> colunaInicial = new Column<Saldo, String>(new EditTextCell()) 
    {
        {
            setFieldUpdater(
                new FieldUpdater<Saldo, String>() 
                {
                    @Override
                    public void update(int index, Saldo s, String v) 
                    {
                        String anoMes = s.getAno()+"/"+s.getMes();
                        Saldo sr = anoMesSaldo.get(anoMes);
//                        Double soma = anoMesSoma.get(anoMes);

                        if( v != null && !v.trim().equals("") )
                        {
                            Double val = nf.parse(v);
                            s.setSaldoInicial(val);
                            if( sr == null )
                            {
                                sr = new Saldo();
                                sr.setId(-1);
                                sr.setAno(s.getAno());
                                sr.setMes(s.getMes());
                                sr.setConta(s.getConta());
                                anoMesSaldo.put(anoMes,sr);
                            }
                            sr.setSaldoInicial(s.getSaldoInicial());
                        }                            
                        else
                        {
                            int idx = saldosDataProvider.getList().indexOf(s);
                            if( idx > 0 && saldosDataProvider.getList().size() > 0 )
                            {
                                Saldo sa = saldosDataProvider.getList().get(idx-1);
                                s.setSaldoInicial(sa.getSaldoFinal());
                            }
                            else
                                s.setSaldoInicial(0.0);
                            
                            if( sr != null )
                                sr.setSaldoInicial(null);
                        }
                        ((EditTextCell)getCell()).clearViewData(s);
                        Home.serviceBus.atualizarSaldo(sr,
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
        public String getValue(Saldo s) 
        {
            if( s.getSaldoInicial() == null )
                return "0.0";
            else
                return nf.format(s.getSaldoInicial());
        }
        public String getCellStyleNames(Cell.Context context, Saldo s)
        {
            String ret = super.getCellStyleNames(context,s);
            String anoMes = s.getAno()+"/"+s.getMes();
            Saldo sr = anoMesSaldo.get(anoMes);
            
            if( sr == null || sr.getSaldoInicial() == null )
                return ret;

            int idx = saldosDataProvider.getList().indexOf(s);
            if( idx > 0 && saldosDataProvider.getList().size() > 0 )
            {
                Saldo sa = saldosDataProvider.getList().get(idx-1);
                if( sa.getSaldoFinal().compareTo(s.getSaldoInicial()) != 0 )
                    ret += " saldoDiferente";
                else
                    ret += " saldoOK";
            }
            return ret;
        }
    };
    // Coluna Movimentos
    Column<Saldo, String> colunaMovimentos = new Column<Saldo, String>(new TextCell()) 
    {
        public String getValue(Saldo s) 
        {
            String anoMes = s.getAno()+"/"+s.getMes();
            Double soma = anoMesSoma.get(anoMes);
            return nf.format(soma);
        }
    };
    // Coluna Diferenças
    Column<Saldo, String> colunaDiferenca = new Column<Saldo, String>(new TextCell()) 
    {
        public String getValue(Saldo s) 
        {
            String anoMes = s.getAno()+"/"+s.getMes();
            Double soma = anoMesSoma.get(anoMes);
            return nf.format(soma-(s.getSaldoFinal()-s.getSaldoInicial()));
        }
        public String getCellStyleNames(Cell.Context context, Saldo s)
        {
            String ret = super.getCellStyleNames(context,s);
            String anoMes = s.getAno()+"/"+s.getMes();
            Double soma = Math.round(anoMesSoma.get(anoMes)*100)/100.0;

            if( soma.compareTo( Math.round( (s.getSaldoFinal()-s.getSaldoInicial()) * 100) / 100.0 ) != 0 )
                ret += " saldoDiferente";
            else
                ret += " saldoOK";
            return ret;
        }

    };

    
    // Final
    Column<Saldo, String> colunaFinal = new Column<Saldo, String>(new EditTextCell()) 
    {
        {
            setFieldUpdater(
                new FieldUpdater<Saldo, String>() 
                {
                    @Override
                    public void update(int index, Saldo s, String v) 
                    {
                        String anoMes = s.getAno()+"/"+s.getMes();
                        Saldo sr = anoMesSaldo.get(anoMes);
                        Double soma = anoMesSoma.get(anoMes);
                        
                        if( v != null && !v.trim().equals("") )
                        {
                            Double val = nf.parse(v);
                            s.setSaldoFinal(val);
                            if( sr == null )
                            {
                                sr = new Saldo();
                                sr.setId(-1);
                                sr.setAno(s.getAno());
                                sr.setMes(s.getMes());
                                sr.setConta(s.getConta());
                                anoMesSaldo.put(anoMes,sr);
                            }
                            sr.setSaldoFinal(s.getSaldoFinal());
                        }
                        else
                        {
                            s.setSaldoFinal(s.getSaldoInicial()+soma);
                            if( sr != null )
                                sr.setSaldoFinal(null);
                        }
                        ((EditTextCell)getCell()).clearViewData(s);
                        Home.serviceBus.atualizarSaldo(sr,
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
        public String getValue(Saldo s) 
        {
            if( s.getSaldoFinal() == null )
                return "0.0";
            else
                return nf.format(s.getSaldoFinal());
        }
        public String getCellStyleNames(Cell.Context context, Saldo s)
        {
            String ret = super.getCellStyleNames(context,s);
            String anoMes = s.getAno()+"/"+s.getMes();
            Double soma = anoMesSoma.get(anoMes);
            
            Saldo sr = anoMesSaldo.get(anoMes);
            if( sr == null || sr.getSaldoFinal() == null )
                return ret;

            Double sf = Math.round((s.getSaldoInicial() +  soma)*100)/100.0;
            
            if( s.getSaldoFinal().compareTo( sf ) != 0 )
                ret += " saldoDiferente";
            else
                ret += " saldoOK";

            return ret;
        }

    };
}