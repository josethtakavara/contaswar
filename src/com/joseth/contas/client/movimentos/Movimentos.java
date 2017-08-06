package com.joseth.contas.client.movimentos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.client.Home;
import com.joseth.contas.client.clones.ContasEditTextCell;
import com.joseth.contas.client.filtro.FiltroMovimentos;

public class Movimentos extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Movimentos> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	ProvidesKey<Movimento> KEY_PROVIDER = new ProvidesKey<Movimento>() { @Override public Object getKey(Movimento item) { return item.getId(); } };
	 
	@UiField(provided=true) ClassificacoesGridMovimentos classGrid=new ClassificacoesGridMovimentos();
	
	@UiField(provided = true) FiltroMovimentos filtroMovimentos = new FiltroMovimentosLista(this);
	@UiField(provided = true) CellTable<Movimento> movimentos = new CellTable<Movimento>(KEY_PROVIDER);
	@UiField(provided = true) SimplePager movimentosPager;
	@UiField(provided = true) Label sumPagina = new Label("");
	@UiField(provided = true) Label sumTabela = new Label("");
	@UiField(provided = true) Label sumSelecao = new Label("");
	ListDataProvider<Movimento> movimentosDataProvider = new ListDataProvider<Movimento>();

	MovimentosSubMenu movimentoContextMenu = new MovimentosSubMenu(this);
	
	MultiSelectionModel<Movimento> msm = new MultiSelectionModel<Movimento>();
	
	Map<String,Conta> contas = new HashMap<String,Conta>();
	
	int iNewCnt = -1;
	boolean sub;
	
	Movimento pai;
	
/* 
 * Construtores
 * 
 */
	public Movimentos()
	{
	    this(null,false);
	}
	// Se for visao de submovimentos, não mostra coluna de contas
	public Movimentos(Movimento pai, boolean sub) 
	{
	    this.sub = sub;
	    this.pai = pai;
		movimentos.setWidth("100%", true);
		movimentos.setAutoHeaderRefreshDisabled(true);
		movimentos.setAutoFooterRefreshDisabled(true);
		movimentos.setRowStyles(new RowStyles<Movimento>() 
		{
			@Override
			public String getStyleNames(Movimento row, int rowIndex) 
			{
			    String sts = "";
				sts += (rowIndex%2)==0?"par mvlinha":"impar mvlinha";
				if( row.getMovimentos().size() > 0 )
				    sts += " movimentoComSubs";
				if( msm.getSelectedSet().contains(row) )
				    sts += " mvlinhaselecionada";
				return sts;
			}
		});

		movimentos.setSelectionModel(msm);
		
		msm.addSelectionChangeHandler(
	        new SelectionChangeEvent.Handler()
	        {
	            @Override
	            public void onSelectionChange(SelectionChangeEvent event) 
	            {
	                atualizarTotais();        
	            }
	        }
		);
		
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		movimentosPager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true)
		{
		    protected void onRangeOrRowCountChanged()
		    {
		        super.onRangeOrRowCountChanged();
		        atualizarTotais();
		    }
		};
		movimentosPager.setDisplay(movimentos);
		movimentosDataProvider.addDataDisplay(movimentos);
		
		atualizarTotais();
	    
        Home.serviceBus.getContas(
                new AsyncCallback<List<Conta>>()
                {
                    public void onFailure(Throwable caught){}
                    public void onSuccess(List<Conta> result)
                    {
                        for( Conta c:result )
                            contas.put(c.getNumero(),c);

                        colunaConta = new Column<Movimento, String>(new SelectionCell(new ArrayList(contas.keySet()))) 
                        {
                            {
                                setFieldUpdater(
                                    new FieldUpdater<Movimento, String>() 
                                    {
                                        @Override
                                        public void update(int index, Movimento m, String v) 
                                        {
                                            for( Conta c: contas.values() )
                                            {
                                                if( c.getNumero().equals(v) )
                                                {
                                                    m.setConta(c);
                                                    atualizaMovimento(m);
                                                    
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                );

                            }
                            public String getValue(Movimento m) 
                            {
                                if( m.getConta() != null )
                                    return m.getConta().getNumero();
                                else
                                    return "";
                            }
                        };
                        
                        initTableColumns();
                    }
                }
            );
		    
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private void initTableColumns() 
	{
		//Id
		colunaId.setSortable(true);
		colunaId.setCellStyleNames("mvId mvCelula");
	    movimentos.addColumn(colunaId,"Id");
        movimentos.getHeader(0).setHeaderStyleNames("mvId mvHeader");
        movimentos.addColumnSortHandler(idSortHandler);
	    
	    // Data
  	    colunaData.setSortable(true);
  	    colunaData.setCellStyleNames("mvData mvCelula");
  	    movimentos.addColumn(colunaData, "Data");
  	    movimentos.getHeader(1).setHeaderStyleNames("mvData mvHeader");
        movimentos.addColumnSortHandler(dataSortHandler);
	    
	    // Descrição
  	    colunaDescricao.setSortable(true);
  	    colunaDescricao.setCellStyleNames("mvDescricao mvCelula");
  	    movimentos.addColumn(colunaDescricao, "Descrição");
  	    movimentos.getHeader(2).setHeaderStyleNames("mvDescricao mvHeader");
  	    movimentos.addColumnSortHandler(descricaoSortHandler);
	    
	    // Documento
	    colunaDocumento.setSortable(true);
	    colunaDocumento.setCellStyleNames("mvDocumento mvCelula");
  	    movimentos.addColumn(colunaDocumento, "Documento");
  	    movimentos.getHeader(3).setHeaderStyleNames("mvDocumento mvHeader");
  	    movimentos.addColumnSortHandler(documentoSortHandler);
	    
	    // Valor
  	    colunaValor.setSortable(true);
  	    colunaValor.setCellStyleNames("mvValor mvCelula");
  	    movimentos.addColumn(colunaValor, "Valor");
  	    movimentos.getHeader(4).setHeaderStyleNames("mvValor mvHeader");
  	    movimentos.addColumnSortHandler(valorSortHandler);
 	    
	    // Comentario
  	    colunaComentario.setSortable(true);
  	    colunaComentario.setCellStyleNames("mvComentario mvCelula");
  	    movimentos.addColumn(colunaComentario, "Comentario");
  	    movimentos.getHeader(5).setHeaderStyleNames("mvComentario mvHeader");
  	    movimentos.addColumnSortHandler(comentarioSortHandler);

        // Conta
  	    if( !sub )
  	    {
      	    movimentos.addColumn(colunaConta, "Conta");
      	    colunaConta.setCellStyleNames("mvConta mvCelula");
      	    movimentos.getHeader(6).setHeaderStyleNames("mvConta mvHeader");
  	    }

  	    // Classificações
  	    movimentos.addColumn(colunaClassificacoes, "Classificações");
  	    colunaClassificacoes.setCellStyleNames("mvClassificacoes mvCelula");
  	    movimentos.getHeader(movimentos.getColumnIndex(colunaClassificacoes)).setHeaderStyleNames("mvClassificacoes mvHeader");
	}
	
	/*
	 * Handlers
	 */
	private void atualizarTotais()
    {
        NumberFormat nf = NumberFormat.getFormat("###0.00");
        Double sump=0.0, sumt=0.0, sums=0.0;
        int i=0;
        for(Movimento m: movimentosDataProvider.getList())
        {
            if( i >= movimentosPager.getPageStart() && i <= (movimentosPager.getPageStart() + movimentosPager.getPageSize()) )
                sump += m.getValor();
            if( msm.isSelected(m) )
                sums += m.getValor();
            sumt += m.getValor();
            i++;
        }
        sumSelecao.setText("Seleção: "+nf.format(sums));
        sumPagina.setText("Página: "+nf.format(sump));
        sumTabela.setText("Tabela: "+nf.format(sumt));
    }

	@UiHandler("movAdc") 
    public void onAdicionarMovimento(ClickEvent event) 
    {
	    Movimento m = new Movimento();
	    m.setId(-1);
	    m.setDescricao("");
	    m.setComentario("");
	    m.setDocumento("");
	    m.setData(new Date());
	    m.setValor(0.0);
	    if( pai != null )
	        m.setMovimento(pai);
	    atualizaMovimento(m);
	    
    }
	
	@UiHandler("movRem") 
    public void onRemoverMovimento(ClickEvent event) 
    {
       for( Movimento m: msm.getSelectedSet() )
       {
           if( m.getId() >= 0 )
               Home.serviceBus.removerMovimento(m,nullCallBack);
           movimentosDataProvider.getList().remove(m);
       }

    }
   private AsyncCallback<Void> nullCallBack = new AsyncCallback<Void>()
   {
       public void onFailure(Throwable caught){}
       public void onSuccess(Void a){}
   };
   
   void atualizaMovimento(final Movimento m)
   {
       Home.serviceBus.atualizarMovimento(m,new AsyncCallback<Movimento>()
           {
               public void onFailure(Throwable caught){}
               public void onSuccess(Movimento m2)
               {
                   if( m.getId() == -1 )
                   {
                       m.setId(m2.getId());
                       movimentosDataProvider.getList().add(m);
                   }
                   movimentosDataProvider.refresh();
               }
           }
       );
   }


    /*
     * Colunas
     */
   
	// Id
	Column<Movimento, String> colunaId = new Column<Movimento, String>(new TextCell()
	{ 
		@Override
		public Set<String> getConsumedEvents() {
            HashSet<String> events = new HashSet<String>();
            events.add(BrowserEvents.CONTEXTMENU);
            return events;
        }
	}) 
    {
		public String getValue(Movimento m) {return m.getId()==null?"":m.getId()<0?"":""+m.getId();}

		// Handler para abrir o contextmenu de ações da linha atual
		@Override
        public void onBrowserEvent(Cell.Context context, Element elem, Movimento object, NativeEvent event)
        {
        	event.preventDefault();
        	movimentoContextMenu.setPai(object);
        	movimentoContextMenu.setPopupPosition(event.getClientX()+5, event.getClientY());
        	movimentoContextMenu.show();
        	
        }
    };

	// Data
	final Column<Movimento, String> colunaData = new Column<Movimento, String>(	new DataEditTextCell() ) 
    {		
		{
	  	    setFieldUpdater(
	  	    	new FieldUpdater<Movimento, String>() 
	  	    	{
	  	    		@Override
	  	    		public void update(int index, Movimento m, String v) 
	  	    		{
	  	    			Date old = m.getData();
	  	    			DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
	  	    			try
	  	    			{
	  	    				Date d = dtf.parseStrict(v);
	  	    				m.setData(d);
	  	    			}
	  	    			catch( IllegalArgumentException pe)
	  	    			{
	  	    				((DataEditTextCell) colunaData.getCell()).clearViewData(m.getId()); 
//	  	    				movimentos.redraw();
	  	    			}
	  	    			if( !old.equals(m.getData()))
	  	    				atualizaMovimento(m);
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
			    			if( (v == null && m.getDescricao() != null) || !v.equals(m.getDescricao() ) )
	    					{
			    				m.setDescricao(v);
			    				atualizaMovimento(m);
	    					}
			    		}
			    	}
		    	);
		}
        public String getValue(Movimento m) {return m.getDescricao()==null?"":""+m.getDescricao();}
    };
	
	// Documento
	Column<Movimento, String> colunaDocumento = new Column<Movimento, String>(new EditTextCell()) 
	  	    {
	  			{
	  				setFieldUpdater(
	  				    	new FieldUpdater<Movimento, String>() 
	  				    	{
	  				    		@Override
	  				    		public void update(int index, Movimento m, String v) 
	  				    		{
	  				    			if( (v == null && m.getDocumento() != null) || !v.equals(m.getDocumento() ) )
	  		    					{
	  				    				m.setDocumento(v);
	  				    				atualizaMovimento(m);
	  		    					}
	  				    		}
	  				    	}
	  			    	);
	  			}
	  	        public String getValue(Movimento m) {return m.getDocumento()==null?"":""+m.getDocumento();}
	  	    };

	
	// Valor
	Column<Movimento, String> colunaValor = new Column<Movimento, String>(new EditTextCell()) 
    {
		{
			setFieldUpdater(
			    	new FieldUpdater<Movimento, String>() 
			    	{
			    		@Override
			    		public void update(int index, Movimento m, String v) 
			    		{
			    		    NumberFormat nf = NumberFormat.getFormat("###0.00");
			    		    double nv = nf.parse(v); 
			    		    if( nv != m.getValor().doubleValue() )
			    		    {
				    			m.setValor(nv);
				    			atualizarTotais();
	                            atualizaMovimento(m);
			    		    }
			    		}
			    	}
		    	);
		}
		public String getValue(Movimento m) 
		{
		    NumberFormat nf = NumberFormat.getFormat("###0.00");
		    return nf.format(m.getValor());
	    }
		public String getCellStyleNames(Cell.Context context, Movimento m)
        {
            String ret = super.getCellStyleNames(context,m);
            
            if( m.getMovimentos().size() > 0 )
            {
                Double soma=0.0;
                for( Movimento subm:m.getMovimentos() )
                    soma += subm.getValor();
                soma = Math.round( soma * 100 )/100.0;
                if( soma.compareTo(m.getValor()) != 0 )
                    ret += " movsubdivergente ";
            }

            return ret;
        }
    };
	
    // Comentario
	Column<Movimento, String> colunaComentario = new Column<Movimento, String>(new EditTextCell()) 
    {
		{
			setFieldUpdater(
			    	new FieldUpdater<Movimento, String>() 
			    	{
			    		@Override
			    		public void update(int index, Movimento m, String v) 
			    		{
			    			if( (v == null && m.getComentario() != null) || !v.equals(m.getComentario() ) )
	    					{
			    				m.setComentario(v);
	                            atualizaMovimento(m);	
	    					}			    			
			    		}
			    	}
		    	);
		}
        public String getValue(Movimento m) {return m.getComentario()==null?"":""+m.getComentario();}
    };
	    
    // Conta
    Column<Movimento, String> colunaConta;
	
    // Classificações
	Column<Movimento, Movimento> colunaClassificacoes = new Column<Movimento, Movimento>(new ClassListCell(movimentosDataProvider, msm)) 
    {
		public Movimento getValue(Movimento m) 
		{
			return m;
		}
    };
    
/*
 * Sort Handlers
 */
    
    ListHandler<Movimento> idSortHandler = new ListHandler<Movimento>(movimentosDataProvider.getList())
    {   
        {
            setComparator(colunaId,
            new Comparator<Movimento>() {
              public int compare(Movimento o1, Movimento o2) {
                if (o1 == o2)return 0;
                if (o1 != null) 
                  return (o2 != null) ? o1.getId().compareTo(o2.getId()) : 1;
                return -1;
              }
            });
        }
    };
    ListHandler<Movimento> descricaoSortHandler = new ListHandler<Movimento>(movimentosDataProvider.getList())
    {   
        {
            setComparator(colunaDescricao,
            new Comparator<Movimento>() {
              public int compare(Movimento o1, Movimento o2) {
                if (o1 == o2)return 0;
                if (o1 != null) 
                  return (o2 != null) ? o1.getDescricao().compareTo(o2.getDescricao()) : 1;
                return -1;
              }
            });
        }
    };
    
    ListHandler<Movimento> documentoSortHandler = new ListHandler<Movimento>(movimentosDataProvider.getList())
    {   
        {
            setComparator(colunaDocumento,
            new Comparator<Movimento>() {
              public int compare(Movimento o1, Movimento o2) 
              {
                if (o1 == o2)return 0;
                String s1 = o1.getDocumento();
                String s2 = o2.getDocumento();
                if (s1 != null) 
                  return (s2 != null) ? s1.compareTo(s2) : 1;
                return -1;
              }
            });
        }
    };
    ListHandler<Movimento> comentarioSortHandler = new ListHandler<Movimento>(movimentosDataProvider.getList())
    {   
        {
            setComparator(colunaComentario,
            new Comparator<Movimento>() {
              public int compare(Movimento o1, Movimento o2) 
              {
                if (o1 == o2)return 0;
                String s1 = o1.getComentario();
                String s2 = o2.getComentario();
                if (s1 != null) 
                  return (s2 != null) ? s1.compareTo(s2) : 1;
                return -1;
              }
            });
        }
    };
    
    
    ListHandler<Movimento> dataSortHandler = new ListHandler<Movimento>(movimentosDataProvider.getList())
    {   
        {
            setComparator(colunaData,
            new Comparator<Movimento>() {
              public int compare(Movimento o1, Movimento o2) {
                if (o1 == o2)return 0;
                if (o1 != null) 
                  return (o2 != null) ? o1.getData().compareTo(o2.getData()) : 1;
                return -1;
              }
            });
        }
    };
    ListHandler<Movimento> valorSortHandler = new ListHandler<Movimento>(movimentosDataProvider.getList())
    {   
        {
            setComparator(colunaValor,
            new Comparator<Movimento>() {
              public int compare(Movimento o1, Movimento o2) {
                if (o1 == o2)return 0;
                if (o1 != null) 
                  return (o2 != null) ? o1.getValor().compareTo(o2.getValor()) : 1;
                return -1;
              }
            });
        }
    };
}

class DataEditTextCell extends ContasEditTextCell
{
	@Override
	protected void edit(Cell.Context context,
            Element parent,
            java.lang.String value)
	{
		super.edit(context,parent,value.substring(value.indexOf(", ")+2));
	}
	
	public boolean isEditing(Movimento m)
	{
		Context ctx = new Context(0,0,m.getId());
		return isEditing(ctx, null, null);
	}

}