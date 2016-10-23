package com.joseth.contas.client.movimentos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.client.Home;
import com.joseth.contas.client.carregar.SubCarregar;

public class MovimentosSubMenu extends PopupPanel
{
    VerticalPanel movimentoContextMenuInnerPanel = new VerticalPanel();
    Label labelSubmovimentos = new Label("Submovimentos");
    Label labelCarregar = new Label("Carregar");
    Label labelAdicionar = new Label("Adicionar Sub");
    Label labelDividir = new Label("Dividir");
    Label labelInverter = new Label("Inverter");
    ClickHandlerSubMovimentos chsm = new ClickHandlerSubMovimentos(this);
    ClickHandlerCarregar chc = new ClickHandlerCarregar(this);
    ClickHandlerDividir chd;
    ClickHandlerInverter chi;

    Movimento pai;
    final Movimentos ms;

    public MovimentosSubMenu(Movimentos mss)
    {
        super(true);
        final MovimentosSubMenu thisMovimentoContextMenu = this;
        this.ms = mss;
        setWidth("100px");     
        add(movimentoContextMenuInnerPanel);

        labelSubmovimentos.addStyleName("movPopItem");
        labelCarregar.addStyleName("movPopItem");
        labelAdicionar.addStyleName("movPopItem");
        labelDividir.addStyleName("movPopItem");
        labelInverter.addStyleName("movPopItem");
        
        labelSubmovimentos.addClickHandler(chsm);
        labelCarregar.addClickHandler(chc);
        Home.serviceBus.getClassificacoes(
            new AsyncCallback<List<Classificacao>>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List<Classificacao> result)
                {
                    Map <String,Classificacao> cls = new HashMap<String,Classificacao>();
                    for( Classificacao c: result)
                        cls.put(c.getNome(), c);
                    chd = new ClickHandlerDividir(thisMovimentoContextMenu,ms,cls);
                    chi = new ClickHandlerInverter(thisMovimentoContextMenu,ms,cls,ms.contas);
                    labelDividir.addClickHandler(chd);
                    labelInverter.addClickHandler(chi);
                }
            } 
        );

        movimentoContextMenuInnerPanel.add(labelSubmovimentos);
        movimentoContextMenuInnerPanel.add(labelCarregar);
        movimentoContextMenuInnerPanel.add( new HTML("<hr/>") );
        movimentoContextMenuInnerPanel.add(labelAdicionar);
        movimentoContextMenuInnerPanel.add( new HTML("<hr/>") );
        movimentoContextMenuInnerPanel.add(labelDividir);
        movimentoContextMenuInnerPanel.add( new HTML("<hr/>") );
        movimentoContextMenuInnerPanel.add(labelInverter);
        addStyleName("movPopMenu");
    }
    public void setPai(Movimento pai)
    {
        this.pai=pai;
        chsm.setPai(pai);
        chc.setPai(pai);
        chd.setPai(pai);
        chi.setPai(pai);
        if( pai.getMovimentos().size() == 0 )
        {
            labelSubmovimentos.setVisible(false);
            labelCarregar.setVisible(true);
        }
        else
        {
        	labelSubmovimentos.setVisible(true);
        	labelCarregar.setVisible(false);
        }
    }

}

//ClickHandler para abrir painel de submovimentos
class ClickHandlerSubMovimentos implements ClickHandler
{
	Movimento pai;
	PopupPanel movimentoContextMenu;
    public ClickHandlerSubMovimentos( PopupPanel movimentoContextMenu )
    {
    	this.movimentoContextMenu = movimentoContextMenu;
    }
     
    @Override
    public void onClick(ClickEvent event) 
    {
        PopupPanel pm = new PopupPanel();
        int d = getProfundidade(pai);
        pm.setPopupPosition(200+d*10, 100+d*10);
        pm.setSize("1100px", "700px");
        pm.setModal(true);
        pm.add(new SubMovimentos( pm, pai ));
    
        pm.addStyleName("subcontas");
        pm.show();
        movimentoContextMenu.hide();
    }
    public void setPai(Movimento pai){this.pai=pai;}
    public static int getProfundidade( Movimento pai )
    {
    	int d = 0;
        while( pai != null )
        {
            d++;
            pai = pai.getMovimento();
        }
        return d;
    }
};


// ClickHandler para abrir tela de carregamento
class ClickHandlerCarregar implements ClickHandler
{
  Movimento pai;
  PopupPanel movimentoContextMenu;
  public ClickHandlerCarregar( PopupPanel movimentoContextMenu )
  {
      this.movimentoContextMenu = movimentoContextMenu;
  }
   
  @Override
  public void onClick(ClickEvent event) 
  {
      PopupPanel pm = new PopupPanel();
      pm.setPopupPosition(200, 100);
      pm.setSize("800px", "500px");
      pm.setModal(true);
      pm.add(new SubCarregar(pm, pai ));
  
      pm.addStyleName("subcarregar");
      pm.show();
      movimentoContextMenu.hide();
  }
  public void setPai(Movimento pai){this.pai=pai;}
};

//ClickHandler para Dividir um movimento
class ClickHandlerDividir implements ClickHandler
{
    Movimento pai;
    Movimentos ms;
    Map<String,Classificacao> cls;
    PopupPanel movimentoContextMenu;

    public ClickHandlerDividir( PopupPanel movimentoContextMenu, Movimentos ms, Map<String,Classificacao> cls )
    {
       this.ms = ms;
       this.cls = cls;
       this.movimentoContextMenu = movimentoContextMenu;

    }
    @Override
    public void onClick(ClickEvent event) 
    {
        Movimento m = new Movimento();
        Movimento m2 = new Movimento();
        m.setId(-2);
        m2.setId(-3);
        m.setDescricao(pai.getDescricao());
        m2.setDescricao(pai.getDescricao());
        m.setComentario(pai.getComentario());
        m2.setComentario(pai.getComentario());
        m.setDocumento(pai.getDocumento());
        m2.setDocumento(pai.getDocumento());
        m.setData(pai.getData());
        m2.setData(pai.getData());
        m.setValor(pai.getValor());
        m2.setValor(0.0);
        m.setMovimento(pai);
        m2.setMovimento(pai);
        if( pai.getMovimentos() == null )
        {
            List<Movimento> mmm = new ArrayList<Movimento>();
            mmm.add(m);
            mmm.add(m2);
            pai.setMovimentos(mmm);
        }
        else
        {
            pai.getMovimentos().add(m);
            pai.getMovimentos().add(m2);
        }
        
        m.setClassificacoes( new ArrayList<Classificacao>( pai.getClassificacoes() ) );
        m2.setClassificacoes( new ArrayList<Classificacao>( pai.getClassificacoes() ) );
        pai.getClassificacoes().clear();
        pai.getClassificacoes().add( cls.get("Subdividido") );
        ms.atualizaMovimento(m);
        ms.atualizaMovimento(m2);
        ms.atualizaMovimento(pai);
        movimentoContextMenu.hide();
    }
    public void setPai(Movimento pai){this.pai=pai;}
};

//ClickHandler para Dividir um movimento
class ClickHandlerInverter implements ClickHandler
{
  Movimento pai;
  Movimentos ms;
  Map<String,Classificacao> cls;
  Map<String,Conta> cs;
  PopupPanel movimentoContextMenu;

  public ClickHandlerInverter( PopupPanel movimentoContextMenu, Movimentos ms, Map<String,Classificacao> cls, Map<String,Conta> cs )
  {
     this.ms = ms;
     this.cls = cls;
     this.cs = cs;
     this.movimentoContextMenu = movimentoContextMenu;

  }
  @Override
  public void onClick(ClickEvent event) 
  {
      Conta contaInverterFrancis = cs.get("Grana Francis");
      Conta contaInverterLeticia = cs.get("Grana Le");
      Classificacao classAcerto = cls.get("Acertos");
      Conta contaCredito;
      Conta contaDebito;
      if(pai.getUsuarioRaiz().getNome().equals("Francis"))
      {
          contaCredito = contaInverterFrancis;
          contaDebito  = contaInverterLeticia;
      }
      else
      {
          contaDebito  = contaInverterFrancis;
          contaCredito = contaInverterLeticia;
      }

      
    Movimento movDebito=new Movimento();
    movDebito.setId(-2);
    movDebito.setData(pai.getData());
    movDebito.setDocumento(pai.getDocumento());
    movDebito.setDescricao(pai.getDescricao());     
    movDebito.setComentario(pai.getComentario());
    movDebito.setValor(pai.getValor());
    movDebito.setConta(contaDebito);
    movDebito.getClassificacoes().addAll(pai.getClassificacoes());
    
    Movimento movCredito=new Movimento();
    movCredito.setId(-3);
    movCredito.setData(pai.getData());
    movCredito.setDocumento(pai.getDocumento());
    movCredito.setDescricao(pai.getDescricao());
    movCredito.setComentario("Invertido - Não modificar");
    movCredito.setValor(-pai.getValor());
    movCredito.setConta(contaCredito);
    movCredito.getClassificacoes().add(classAcerto);
    
    pai.getClassificacoes().clear();
    pai.getClassificacoes().add(classAcerto);
    pai.setComentario("Invertido - Não modificar");
    
    ms.atualizaMovimento(movDebito);
    ms.atualizaMovimento(movCredito);
    ms.atualizaMovimento(pai);       
    movimentoContextMenu.hide();
  }
  public void setPai(Movimento pai){this.pai=pai;}
}