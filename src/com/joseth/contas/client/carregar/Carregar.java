package com.joseth.contas.client.carregar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Template;
import com.joseth.contas.client.Home;

public class Carregar extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Carregar> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField(provided=true) RadioButton tpArquivo = new RadioButton("tipo","Arquivo");
	@UiField(provided=true) RadioButton tpDados = new RadioButton("tipo","Dados");;
	@UiField DivElement divConta;
	@UiField ListBox    conta;
	@UiField ListBox    template;
	@UiField CheckBox   inverter;
	
	@UiField DivElement arquivoPainel;
    @UiField FormPanel  arquivoFormPainel;
    @UiField Hidden     arquivoHiddenId;
    
    @UiField DivElement textoPainel;
    @UiField TextArea   ta;
    @UiField Button     visualizarBotao;
	
	@UiField DivElement processadosPainel;
	@UiField DivElement descartadosPainel;
	
	Movimento pai;
	PopupPanel painelPai;
	
	Map<String, Conta> cacheConta = new HashMap<String, Conta>();
	Map<String, Template> cacheTemplate = new HashMap<String, Template>();

	ProvidesKey<Movimento> KEY_PROVIDER = new ProvidesKey<Movimento>() { @Override public Object getKey(Movimento item) { return item.getId(); } };
	
    @UiField(provided=true) DataGrid<Movimento> visualizarMovimentos = new DataGrid<Movimento>(KEY_PROVIDER);   
	ListDataProvider<Movimento> movimentosDataProvider = new ListDataProvider<Movimento>();
	@UiField Button gravarBotao;
	
	@UiField(provided=true) DataGrid<String> descartarMovimentos = new DataGrid<String>();
    ListDataProvider<String> descartadosDataProvider = new ListDataProvider<String>();
	
	public Carregar(PopupPanel painelPai, Movimento pai) 
	{
	    this.pai = pai;
	    this.painelPai = painelPai;
		Home.serviceBus.getContas(
			new AsyncCallback<List<Conta>>()
			{
				public void onFailure(Throwable caught){}
			    public void onSuccess(List<Conta> result)
			    {
					for(Conta c:result)
					{
						conta.addItem(c.getNumero(),""+c.getId());
						cacheConta.put(""+c.getId(), c);
					}
			    }
			}
		);
		
		Home.serviceBus.getTemplates(
		      new AsyncCallback<List<Template>>()
              {
                  public void onFailure(Throwable caught){}
                  public void onSuccess(List<Template> result)
                  {
                      for(Template c:result)
                      {
                          template.addItem(c.getNome() + " - " + c.getPadrao(),""+c.getId());
                          cacheTemplate.put(""+c.getId(), c);
                      }
                  }
              }
          );
		
	      visualizarMovimentos.setRowStyles(new RowStyles<Movimento>() 
          {
              @Override
              public String getStyleNames(Movimento row, int rowIndex) 
              {
                  return (rowIndex%2)==0?"par":"impar";
              }
          });
          
        // Data
        colunaData.setSortable(true);
        visualizarMovimentos.addColumn(colunaData, "Data");
        colunaData.setCellStyleNames("mvData");
          
        // Descrição
        colunaDescricao.setSortable(true);
        visualizarMovimentos.addColumn(colunaDescricao, "Descrição");
        colunaDescricao.setCellStyleNames("mvDescricao");
          
        // Documento
        colunaDocumento.setSortable(true);
        visualizarMovimentos.addColumn(colunaDocumento, "Documento");
        colunaDocumento.setCellStyleNames("mvDocumento");
          
        // Valor
        colunaValor.setSortable(true);
        visualizarMovimentos.addColumn(colunaValor, "Valor");
        colunaValor.setCellStyleNames("mvValor");
          
        movimentosDataProvider.addDataDisplay(visualizarMovimentos);
       
        descartarMovimentos.addColumn(colunaString, "Descartados");
        descartadosDataProvider.addDataDisplay(descartarMovimentos);

		initWidget(uiBinder.createAndBindUi(this));
		if( pai != null )
		    divConta.setClassName("visiblefalse");
	}
	
	// Ao clicar radio arquivo mostra painel respectivo
	@UiHandler("tpArquivo")
	public void onClickArquivo(ClickEvent event)
	{
		final int rndid = Random.nextInt();
		arquivoHiddenId.setValue("rndid"+rndid);
		arquivoPainel.removeClassName("visiblefalse");
        textoPainel.addClassName("visiblefalse");
        processadosPainel.addClassName("visiblefalse");
        descartadosPainel.addClassName("visiblefalse");
	}
    // Ao clicar radio dados mostra painel respectivo
	@UiHandler("tpDados")
    public void onClickDados(ClickEvent event)
    {
        textoPainel.removeClassName("visiblefalse");
        arquivoPainel.addClassName("visiblefalse");
        processadosPainel.addClassName("visiblefalse");
        descartadosPainel.addClassName("visiblefalse");
    }
	
    // Processa movimentos do arquivo e chama rotina comum para visualização
	@UiHandler("arquivoFormPainel")
    public void onSubmitComplete(SubmitCompleteEvent event) 
    {
        Home.serviceBus.getArquivoMovimentos(arquivoHiddenId.getValue(), inverter.getValue(), (Conta)cacheConta.get(conta.getValue(conta.getSelectedIndex())), pai, 
            new AsyncCallback<List[]>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List[] result)
                {
                    visualizar(result);
                }
            }
        );
    }
	
	// Processa movimentos da TextArea e chama rotina comum para visualização
	@UiHandler("visualizarBotao")
	public void onVisualizar(ClickEvent event) 
	{
	    Home.serviceBus.getTextoMovimentosTemplate(
              ((Template)cacheTemplate.get(template.getValue(template.getSelectedIndex()))).getPadrao(),
              ta.getText(), inverter.getValue(), (Conta)cacheConta.get(conta.getValue(conta.getSelectedIndex())), pai, 
          new AsyncCallback<List[]>()
          {
              public void onFailure(Throwable caught){}
              public void onSuccess(List[] result)
              {
                  visualizar(result);
              }
          }
        );
    }
	
	// Visualizar movimentos processados e descartados
	public void visualizar( List[] newmovs )
	{	
  	    movimentosDataProvider.getList().clear();
  	    movimentosDataProvider.getList().addAll(newmovs[0]);
        descartadosDataProvider.getList().clear();
        descartadosDataProvider.getList().addAll(newmovs[1]);
  	    
        if( newmovs[0].size() > 0  )
            processadosPainel.removeClassName("visiblefalse");
        else
            processadosPainel.addClassName("visiblefalse");
        
        if( newmovs[1].size() > 0  )
            descartadosPainel.removeClassName("visiblefalse");
        else
            descartadosPainel.addClassName("visiblefalse");

	}
	
	// Grava movimentos processados e limpa paineis
	@UiHandler("gravarBotao")
    public void onGravarMovimentos(ClickEvent event) 
    {
        Home.serviceBus.atualizar(new ArrayList(movimentosDataProvider.getList()), 
            new AsyncCallback()
            {
                public void onFailure(Throwable caught)
                {
                    Window.alert(caught.getMessage());
                }
                public void onSuccess( Object v )
                {
                    if( painelPai != null )
                        painelPai.hide();
                    else
                    {
                        movimentosDataProvider.getList().clear();
                        descartadosDataProvider.getList().clear();
                        processadosPainel.addClassName("visiblefalse");
                        descartadosPainel.addClassName("visiblefalse");
                    }
                }
            }
        );
    };

	
    // Coluna String
    final Column<String, String> colunaString = new Column<String, String>(new TextCell()) 
    {       
        public String getValue(String s) 
        {
            return s;
        }
    };
    
	// Coluna Data
	final Column<Movimento, String> colunaData = new Column<Movimento, String>(new TextCell()) 
    {		
		public String getValue(Movimento m) 
		{
			DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
			return dtf.format(m.getData());
		}
    };
	
	// Coluna Descrição
	Column<Movimento, String> colunaDescricao = new Column<Movimento, String>(new TextCell()) 
    {
		public String getValue(Movimento m) {return m.getDescricao();}
    };
	
	// Coluna Documento
	Column<Movimento, String> colunaDocumento = new Column<Movimento, String>(new TextCell()) 
    {
		public String getValue(Movimento m) {return m.getDocumento();}
    };
	
	// Coluna Valor
	Column<Movimento, Number> colunaValor = new Column<Movimento, Number>(new NumberCell(NumberFormat.getFormat("###0.00"))) 
    {
		public Number getValue(Movimento m) {return m.getValor();}
    };
}

//Window.alert(newmovs[0].size() + "-" + newmovs[1].size());
//if( newmovs[1].size() > 0 )
//{
//  String lns = "<b>Linhas descartadas</b><hr/><div style='background-color: white; text-align: left'><pre>";
//  for( String s: (List<String>)newmovs[1])
//  {
//      lns+=s;
//      lns+="<br/>";           
//  }
//  lns+="</pre></div>";
//  descartar.setInnerHTML(lns);
//}
