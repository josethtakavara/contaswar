package com.joseth.contas.client.movimentos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.client.Home;

public class ClassListCell extends AbstractCell<Movimento>
{
    ListDataProvider<Movimento> dp;
    String cidRemover;
    boolean ctrl;
    MultiSelectionModel<Movimento> msm;
    public ClassListCell(ListDataProvider<Movimento> dp, MultiSelectionModel<Movimento> msm)
    {
        super("drop","dragover","dragstart","dragend","keydown","keyup" );
        this.dp = dp;
        this.msm = msm;
    }
    
	@Override
	public void render(Cell.Context context, Movimento value, SafeHtmlBuilder sb) 
	{
		String start = "<ul class=\"mvClassList\">";
		String end = "</ul>";
		String istart1 = "<li draggable=\"true\" ondragstart=\"event.dataTransfer.setData('text/plain',";
		String istart2 = ")\" ondragend=\"this.dispatchEvent(new Event('change'));\">";
		String iend = "</li>";
		sb.append(SafeHtmlUtils.fromTrustedString(start));
		for(Classificacao c: value.getClassificacoes() )
		{
			sb.append(SafeHtmlUtils.fromTrustedString(istart1));
			sb.append(SafeHtmlUtils.fromTrustedString(""+c.getId()));
			sb.append(SafeHtmlUtils.fromTrustedString(istart2));
			sb.append(SafeHtmlUtils.fromTrustedString(c.getNome()));
			sb.append(SafeHtmlUtils.fromTrustedString(iend));
		}
		
		sb.append(SafeHtmlUtils.fromTrustedString(end));
	}
	
	@Override
	public void onBrowserEvent(Cell.Context context, Element parent, Movimento value, NativeEvent event, ValueUpdater<Movimento> valueUpdater)
	{
	      if (value == null) return;
	      super.onBrowserEvent(context, parent, value, event, valueUpdater);
	      
	      if ("drop".equals(event.getType())) 
	      {
	    	  Set<Movimento> msSelecionados = new TreeSet<Movimento>();
	    	  msSelecionados.add(value);
	    	  Home.consoleLog(msm);
	    	  msSelecionados.addAll(msm.getSelectedSet());
	          Home.serviceBus.getClassificacoes( 
	        		  new ClassListCellCallBack(event.getDataTransfer().getData("text/plain"), msSelecionados, dp,ClassListCellCallBack.OP_ADD)
    		  );
	      }
	      else if ("dragend".equals(event.getType()))
          {
        	  Set<Movimento> msSelecionados = new TreeSet<Movimento>();
	    	  msSelecionados.add(value);
	    	  Home.consoleLog(msm);
	    	  msSelecionados.addAll(msm.getSelectedSet());
	          Home.serviceBus.getClassificacoes( 
	        		  new ClassListCellCallBack(cidRemover, msSelecionados, dp,ClassListCellCallBack.OP_REM)
    		  );
          }
	      else if ("dragstart".equals(event.getType()))
          {
              cidRemover = event.getDataTransfer().getData("text/plain"); 
          }
	      else if ("keydown".equals(event.getType()))
          {
	          if( event.getCtrlKey() )
	              ctrl=true;
          }
	      else if ("keyup".equals(event.getType()))
          {
              if( event.getCtrlKey() )
                  ctrl=false;
          }
	}
}

class ClassListCellCallBack implements AsyncCallback<List<Classificacao>>
{
	public static final int OP_ADD=1;
	public static final int OP_REM=2;
    String cid;
    Set<Movimento> ms;
    ListDataProvider<Movimento> dp;
    int op;
    public ClassListCellCallBack(String cid, Set<Movimento> ms, ListDataProvider<Movimento> dp, int op)
    {
        this.cid = cid;
        this.ms=ms;
        this.dp=dp;
        this.op=op;
    }
    public void onFailure(Throwable caught){}
    public void onSuccess(List<Classificacao> result)
    {
    	List<Movimento> msAtualizados = new ArrayList<Movimento>();
        for( Classificacao c: result)
        {
            if( c.getId().toString().equals(cid))
            {
            	for( Movimento m: ms )
            	{
            		if( op == OP_ADD )
            		{
		                if( !m.getClassificacoes().contains(c) )
		                {
		                    m.getClassificacoes().add(c); 
		                    msAtualizados.add(m);
		                }
            		}
	                else if( op == OP_REM )
	                {
	                	if( m.getClassificacoes().contains(c) )
		                {
	                		m.getClassificacoes().remove(c);
	                		msAtualizados.add(m);
		                }
	                }
            	}
                break;
            }
        }
        for(Movimento m: msAtualizados)
        {
        	final Movimento fm = m;
	        Home.serviceBus.atualizarMovimento(fm,new AsyncCallback<Movimento>()
	            {
	                public void onFailure(Throwable caught){}
	                public void onSuccess(Movimento m2)
	                {
	                    if( fm.getId() < 0 )
	                        fm.setId(m2.getId());
                        dp.refresh();
	                }
	            }
	        );
        }
    }
}
