package com.joseth.contas.client.movimentos;


public class Colinator
{		
/*	private void initTableColumns() 
	{
	    
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
	

	// Id
	Column<Movimento, String> getColunaId(final Boolean sortable, final String styleNames)
	{
	    return 
            new Column<Movimento, String>(new TextCell())
            {
	            {
	                setSortable(sortable);
	                setCellStyleNames(styleNames);
	            }
        		public String getValue(Movimento m) 
        		{
    		        return m.getId()==null?"":m.getId()<0?"":""+m.getId();
		        }
            };
	}

	// Data
	Column<Movimento, String> getColunaDataEditavel()
	{
	    return 
            new Column<Movimento, String>(new EditTextCell()) 
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
        	  	    				((EditTextCell)getCell()).clearViewData(m.getId()); 
        	  	    				movimentos.redraw();
        	  	    			}
                                atualizaMovimento(m);
        	  	    		}
        	  	    	}
        	      	);
        
        		}
        		public String getValue(Movimento m) 
        		{
        			DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
        			return dtf.format(m.getData());
        		}
            };
	}
	
	// Descrição
	Column<Movimento, String> getColunaDescricao()
	{
	    return new Column<Movimento, String>(new EditTextCell()) 
        {
    		{
    			setFieldUpdater(
    			    	new FieldUpdater<Movimento, String>() 
    			    	{
    			    		@Override
    			    		public void update(int index, Movimento m, String v) 
    			    		{
    			    			m.setDescricao(v);
    			    			atualizaMovimento(m);
    			    		}
    			    	}
    		    	);
    		}
            public String getValue(Movimento m) {return m.getDescricao()==null?"":""+m.getDescricao();}
        };
	}
	
	// Documento
	Column<Movimento, String> getColunaDocumento()
	{
	    return new Column<Movimento, String>(new EditTextCell()) 
  	    {
  			{
  				setFieldUpdater(
  				    	new FieldUpdater<Movimento, String>() 
  				    	{
  				    		@Override
  				    		public void update(int index, Movimento m, String v) 
  				    		{
  				    			m.setDocumento(v);
                                atualizaMovimento(m);
  				    		}
  				    	}
  			    	);
  			}
  	        public String getValue(Movimento m) {return m.getDocumento()==null?"":""+m.getDocumento();}
  	    };
	}
	
	// Valor
	Column<Movimento, String> getColunaValor()
	{
	    return new Column<Movimento, String>(new EditTextCell()) 
        {
    		{
    			setFieldUpdater(
    			    	new FieldUpdater<Movimento, String>() 
    			    	{
    			    		@Override
    			    		public void update(int index, Movimento m, String v) 
    			    		{
    			    		    NumberFormat nf = NumberFormat.getFormat("###0.00");
    			    			m.setValor(nf.parse(v));
    			    			atualizarTotais();
                                atualizaMovimento(m);
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
	}
	
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
			    			m.setComentario(v);
                            atualizaMovimento(m);
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
    
    private AsyncCallback<Void> nullCallBack = new AsyncCallback<Void>()
    {
        public void onFailure(Throwable caught){}
        public void onSuccess(Void a){}
    };
    
    private AsyncCallback<Movimento> movimentoCallBack = new AsyncCallback<Movimento>()
    {
        public void onFailure(Throwable caught){}
        public void onSuccess(Movimento m2)
        {
            
        }
    };*/
}