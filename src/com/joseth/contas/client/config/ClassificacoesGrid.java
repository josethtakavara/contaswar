package com.joseth.contas.client.config;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.client.Home;

public class ClassificacoesGrid extends Grid
{
    private int cellCnt = 0;
    public ClassificacoesGrid()
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
                        ClassificacaoTextBox ct = new ClassificacaoTextBox(c);                      
                        setWidget(y, x, ct);

                        x++;
                        cellCnt ++;
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
    
    public void novaClassificacao()
    {       
        final Classificacao c = new Classificacao();
        c.setNome("Nova Classificação");
        
        Home.serviceBus.atualizar(c,new AsyncCallback<Classificacao>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(Classificacao c2)
                {
                    c.setId(c2.getId());
                    c.setVersion(c2.getVersion());
                }
            }
        );

        ClassificacaoTextBox ct = new ClassificacaoTextBox(c);
        cellCnt++;
        
        resize((int)Math.ceil( cellCnt / 4.0 ), 4);
        setWidget((int)Math.ceil( cellCnt / 4.0 )-1, (int)Math.ceil( (cellCnt-1) % 4 ), ct);
    }
    
    public void removerClassificacao( ClassificacaoTextBox ctb )
    {
        if( ctb.getClassificacao().getMovimentosCount() > 0 )
        {
            Window.alert("Não é possível remover Classificação associada a Movimentos");
        }
        else
        {
            Home.serviceBus.apagar( ctb.getClassificacao(), new AsyncCallback<Void>()
                {
                    public void onFailure(Throwable caught){}
                    public void onSuccess(Void arg0){}
                }
            );
            remove(ctb);
        }
    }
}

class ClassificacaoTextBox extends TextBox implements DoubleClickHandler, FocusHandler, ChangeHandler, KeyPressHandler, DragEndHandler
{
    private Classificacao c;
    private String valorAnterior;
    public ClassificacaoTextBox(Classificacao c)
    {
        this.c = c;
        setStyleName("classPainelCelula");
        setReadOnly(true);
        setText(c.getNome());
        getElement().setDraggable(Element.DRAGGABLE_TRUE);
        addDoubleClickHandler(this);
        addFocusHandler(this);
        addChangeHandler(this);
        addKeyPressHandler(this);
        addDragEndHandler(this);
    }
    public Classificacao getClassificacao(){return c;}
    public void onDoubleClick(DoubleClickEvent dce)
    {
        setReadOnly(false);
        valorAnterior = getText();
    }        
    public void onFocus(FocusEvent fe)
    {
        setReadOnly(true);
    }
    public void onChange(ChangeEvent ce)
    {
        c.setNome(getText());
        Home.serviceBus.atualizar(c,new AsyncCallback<Classificacao>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(Classificacao c){}
            }
        );
    }
    @Override
    public void onKeyPress(KeyPressEvent kpe)
    {
        if( kpe.getCharCode() == KeyCodes.KEY_ESCAPE )
        {
            setText(valorAnterior);
            setReadOnly(true);
        } 
        else if( kpe.getCharCode() == KeyCodes.KEY_ENTER )
        {
            setReadOnly(true);
        }
        
    }
    @Override
    public void onDragEnd(DragEndEvent dee)
    {
        ((ClassificacoesGrid)getParent()).removerClassificacao(this);       
    }   
}