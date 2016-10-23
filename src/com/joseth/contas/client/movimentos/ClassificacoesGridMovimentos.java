package com.joseth.contas.client.movimentos;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.client.Home;

public class ClassificacoesGridMovimentos extends Grid
{
    public ClassificacoesGridMovimentos()
    {
        super(1,1);
        Home.serviceBus.getClassificacoes(
                new AsyncCallback<List<Classificacao>>()
                {
                    public void onFailure(Throwable caught){}
                    public void onSuccess(List<Classificacao> result)
                    {
                        resize((int)Math.ceil(result.size()/4.0), 4);
                        int x=0,y=0;
                        for( Classificacao c: result)
                        {
                            Label l = new Label(c.getNome());
                            l.getElement().setDraggable(Element.DRAGGABLE_TRUE);
                            l.addDragStartHandler( new ClassificacaoDragStartHandler(c.getId() ) );
                            l.setStyleName("classPainelCelula");
                            setWidget(y, x, l);
                            
                            x++;
                            if( x>3)
                            {
                                x=0;
                                y++;
                            }
                        }
                    }
                } 
            );
    }

}

class ClassificacaoDragStartHandler implements DragStartHandler
{
  int cid;
  public ClassificacaoDragStartHandler(int cid)
  {
      this.cid=cid;
  }

  @Override
  public void onDragStart(DragStartEvent event)
  {
      event.getDataTransfer().setData("text/plain", ""+cid);
      
  }
  
}
