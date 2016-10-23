package com.joseth.contas.client.movimentos;

import java.util.List;

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
//	private List<Classificacao> cls = new ArrayList<Classificacao>();

    ListDataProvider dp;
    int cidRemover;
    boolean ctrl;
    MultiSelectionModel<Movimento> msm;
    public ClassListCell(ListDataProvider dp, MultiSelectionModel<Movimento> msm)
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
//            Window.setTitle("$"+event.getDataTransfer().getData("text/plain"));
	          Home.serviceBus.getClassificacoes( new ClassListCellCallBack(event.getDataTransfer().getData("text/plain"), value, dp));
	          for( Movimento m: msm.getSelectedSet() )
	              Home.serviceBus.getClassificacoes( new ClassListCellCallBack(event.getDataTransfer().getData("text/plain"), m, dp));
	      }
	      else if ("dragleave".equals(event.getType()))
	      {
	          //Window.alert(event.getDataTransfer().getData("text/plain"));
	          //Home.serviceBus.getClassificacoes( new ClassListCellCallBack(event.getDataTransfer().getData("text/plain"), value, dp));
//	          Window.setTitle("*"+event.getDataTransfer().getData("text/plain"));
	      }
	      else if ("dragend".equals(event.getType()))
          {
//              Window.setTitle("!"+event.getDataTransfer().getData("text/plain"));
	          if( !ctrl )
	          {
                  for(int i=0; i < value.getClassificacoes().size(); i++)
                  {
                      if( value.getClassificacoes().get(i).getId() == cidRemover )
                      {
                          value.getClassificacoes().remove(i);
//                          Home.serviceBus.atualizarMovimento(value,nullCallBack);
                          final Movimento v2 = value;
                          Home.serviceBus.atualizarMovimento(value,new AsyncCallback<Movimento>()
                                  {
                                      public void onFailure(Throwable caught){}
                                      public void onSuccess(Movimento m2)
                                      {
                                          if( v2.getId() < 0 )
                                          {
                                              v2.setId(m2.getId());
                                              dp.refresh();
                                          }
                                      }
                                  }
                              );
                          break;
                      }
                  }
	          }
              dp.refresh();
          }
	      else if ("dragstart".equals(event.getType()))
          {
//              Window.setTitle("%"+event.getDataTransfer().getData("text/plain"));
              cidRemover = (int)NumberFormat.getFormat("0").parse(event.getDataTransfer().getData("text/plain")); 
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
	private AsyncCallback<Void> nullCallBack = new AsyncCallback<Void>()
    {
        public void onFailure(Throwable caught){}
        public void onSuccess(Void a){}
    };
}

class ClassListCellCallBack implements AsyncCallback<List<Classificacao>>
{
    String cid;
    Movimento m;
    ListDataProvider dp;
    public ClassListCellCallBack(String cid, Movimento m, ListDataProvider dp)
    {
        this.cid = cid;
        this.m=m;
        this.dp=dp;
    }
    public void onFailure(Throwable caught){}
    public void onSuccess(List<Classificacao> result)
    {
        for( Classificacao c: result)
        {
            if( c.getId().toString().equals(cid))
            {
                if( !m.getClassificacoes().contains(c) )
                {
                    m.getClassificacoes().add(c);
                    dp.refresh();
                }
            }
        }
//        Home.serviceBus.atualizarMovimento(m,nullCallBack);
        Home.serviceBus.atualizarMovimento(m,new AsyncCallback<Movimento>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(Movimento m2)
                {
                    if( m.getId() < 0 )
                    {
                        m.setId(m2.getId());
                        dp.refresh();
                    }
                }
            }
        );
    }
    private AsyncCallback<Void> nullCallBack = new AsyncCallback<Void>()
    {
        public void onFailure(Throwable caught){}
        public void onSuccess(Void a){}
    };
}
