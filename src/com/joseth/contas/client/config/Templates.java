package com.joseth.contas.client.config;

import java.util.List;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.joseth.contas.beans.BlackDescricao;
import com.joseth.contas.beans.BlackString;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Template;
import com.joseth.contas.client.Home;

public class Templates extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Templates> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField(provided = true) Label mensagem = new Label();
	@UiField(provided = true) Label parsedData = new Label();
	@UiField(provided = true) TextArea testData = new TextArea();
	@UiField(provided = true) TextArea parseData = new TextArea();
    @UiField(provided = true) DataGrid<Template>       templatesGrid = new DataGrid<Template>();
    @UiField(provided = true) DataGrid<BlackString>    blackStringGrid = new DataGrid<BlackString>();
    @UiField(provided = true) DataGrid<BlackDescricao> blackDescricaoGrid = new DataGrid<BlackDescricao>();
    @UiField(provided = true) DataGrid<Movimento>      movimentosGrid = new DataGrid<Movimento>();
    @UiField(provided = true) DataGrid<String>         descartadosGrid = new DataGrid<String>();
    ListDataProvider<Template>        templatesDataProvider      = new ListDataProvider<Template>();
    ListDataProvider<BlackString>     blackStringDataProvider    = new ListDataProvider<BlackString>();
    ListDataProvider<BlackDescricao>  blackDescricaoDataProvider = new ListDataProvider<BlackDescricao>();
    ListDataProvider<Movimento>       movimentosDataProvider     = new ListDataProvider<Movimento>();
    ListDataProvider<String>          descartadosDataProvider    = new ListDataProvider<String>();
    MultiSelectionModel<Template>       msmt  = new MultiSelectionModel<Template>();
    MultiSelectionModel<BlackString>    msmbs = new MultiSelectionModel<BlackString>();
    MultiSelectionModel<BlackDescricao> msmbd = new MultiSelectionModel<BlackDescricao>();
	
	public Templates() 
	{
	    templatesGrid.addColumn(colunaNomeT,       "Nome");
	    templatesGrid.addColumn(colunaPadraoT,     "Padrão");
	    templatesGrid.getHeader(0).setHeaderStyleNames("mvHeader mvNome");
	    templatesGrid.getHeader(1).setHeaderStyleNames("mvHeader mvPadrao");
	    templatesDataProvider.addDataDisplay(templatesGrid);
	    templatesGrid.setSelectionModel(msmt);
	       
        Home.serviceBus.getTemplates(
            new AsyncCallback<List<Template>>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List<Template> result)
                {
                    templatesDataProvider.getList().addAll(result);
                }
            }
        );
        
        blackStringGrid.addColumn(colunaPadraoBS,     "BlackString");
        blackStringGrid.getHeader(0).setHeaderStyleNames("mvHeader");
        blackStringDataProvider.addDataDisplay(blackStringGrid);
        blackStringGrid.setSelectionModel(msmbs);
           
        Home.serviceBus.getBlackStrings(
            new AsyncCallback<List<BlackString>>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List<BlackString> result)
                {
                    blackStringDataProvider.getList().addAll(result);
                }
            }
        );
        
        blackDescricaoGrid.addColumn(colunaPadraoBD,     "BlackDescricao");
        blackDescricaoGrid.getHeader(0).setHeaderStyleNames("mvHeader");
        blackDescricaoDataProvider.addDataDisplay(blackDescricaoGrid);
        blackDescricaoGrid.setSelectionModel(msmbd);
           
        Home.serviceBus.getBlackDescricoes(
            new AsyncCallback<List<BlackDescricao>>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List<BlackDescricao> result)
                {
                    blackDescricaoDataProvider.getList().addAll(result);
                }
            }
        );
        
        movimentosGrid.addColumn(colunaData,"Data");
        movimentosGrid.addColumn(colunaDescricao,"Descrição");
        movimentosGrid.addColumn(colunaDocumento,"Documento");
        movimentosGrid.addColumn(colunaValor,"Valor");
        movimentosDataProvider.addDataDisplay(movimentosGrid);
        
        descartadosGrid.addColumn(colunaDescartados,"Descrição");
        descartadosDataProvider.addDataDisplay(descartadosGrid);

	    
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("templateAddBt") 
    public void onAdicionarTemplate(ClickEvent event) 
    {
        Template t = new Template();
        t.setId(-1);
        t.setPadrao("");
        t.setNome("");
        atualizarTemplate(t);
        
    }
	
    @UiHandler("templateRemBt") 
    public void onRemoverTemplate(ClickEvent event) 
    {
       for( Template t: msmt.getSelectedSet() )
       {
           Home.serviceBus.removerTemplate(t,nullCallBack);
           templatesDataProvider.getList().remove(t);
       }

    }
	
    @UiHandler("blackStringAddBt") 
    public void onAdicionarBlackString(ClickEvent event) 
    {
        BlackString t = new BlackString();
        t.setId(-1);
        t.setPadrao("");
        atualizarBlackString(t);
        
    }
    @UiHandler("blackStringRemBt") 
    public void onRemoverBlackString(ClickEvent event) 
    {
       for( BlackString t: msmbs.getSelectedSet() )
       {
           Home.serviceBus.removerBlackString(t,nullCallBack);
           blackStringDataProvider.getList().remove(t);
       }

    }	    
    @UiHandler("blackDescricaoAddBt") 
    public void onAdicionarBlackDescricao(ClickEvent event) 
    {
        BlackDescricao t = new BlackDescricao();
        t.setId(-1);
        t.setPadrao("");
        atualizarBlackDescricao(t);
        
    }
    @UiHandler("blackDescricaoRemBt") 
    public void onRemoverBlackDescricao(ClickEvent event) 
    {
       for( BlackDescricao t: msmbd.getSelectedSet() )
       {
           Home.serviceBus.removerBlackDescricao(t,nullCallBack);
           blackDescricaoDataProvider.getList().remove(t);
       }

    }

	@UiHandler("testBt") 
    public void onTestBotao(ClickEvent event) 
    {
	    if( msmt.getSelectedSet().size() != 1 )
	    {
	        mensagem.setText("Selecione um padrão");
	        return;
	    }
	    
	    Template t = (Template)msmt.getSelectedSet().toArray()[0];
	    
	    Home.serviceBus.getTextoMovimentosTemplate(t.getPadrao(),testData.getText(), false, null, null, 
            new AsyncCallback<List[]>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List[] result)
                {
                    movimentosDataProvider.getList().clear();
                    movimentosDataProvider.getList().addAll(result[0]);
                    descartadosDataProvider.getList().clear();
                    descartadosDataProvider.getList().addAll(result[1]);
                }
            }
        );
	    
	    mensagem.setText("");
    }
	
	@UiHandler("parseData") 
    public void onParseDataChange(KeyUpEvent event) 
    {
        //parsedData.setText(parseData.getText());
	    String buf = parseData.getText().replaceAll("\\t","\\\\t");
	    parsedData.setText(buf);
    }
	
    // Template
    private Column<Template, String> colunaPadraoT = new Column<Template, String>(new EditTextCell()) 
    {
        {
            setCellStyleNames("mvPadrao");
            setFieldUpdater
            (
                new FieldUpdater<Template, String>() 
                {
                    @Override
                    public void update(int index, Template t, String v) 
                    {
                        t.setPadrao(v);
                        atualizarTemplate(t);
                    }
                }
            );
        }
        public String getValue(Template t) 
        {
           return t.getPadrao();
        }
    };
    // Nome
    private Column<Template, String> colunaNomeT = new Column<Template, String>(new EditTextCell()) 
    {
        {
            setCellStyleNames("mvNome");
            setFieldUpdater
            (
                new FieldUpdater<Template, String>() 
                {
                    @Override
                    public void update(int index, Template t, String v) 
                    {
                        t.setNome(v);
                        atualizarTemplate(t);
                    }
                }
            );
        }
        public String getValue(Template t) 
        {
           return t.getNome();
        }
    };
    
    
    // BlackString
    private Column<BlackString, String> colunaPadraoBS = new Column<BlackString, String>(new EditTextCell()) 
    {
        {
            setCellStyleNames("mvBlackString");
            setFieldUpdater
            (
                new FieldUpdater<BlackString, String>() 
                {
                    @Override
                    public void update(int index, BlackString t, String v) 
                    {
                        t.setPadrao(v);
                        atualizarBlackString(t);
                    }
                }
            );
        }
        public String getValue(BlackString t) 
        {
           return t.getPadrao();
        }
    };
    
    // BlackDescricao
    private Column<BlackDescricao, String> colunaPadraoBD = new Column<BlackDescricao, String>(new EditTextCell()) 
    {
        {
            setCellStyleNames("mvBlackDescricao");
            setFieldUpdater
            (
                new FieldUpdater<BlackDescricao, String>() 
                {
                    @Override
                    public void update(int index, BlackDescricao t, String v) 
                    {
                        t.setPadrao(v);
                        atualizarBlackDescricao(t);
                    }
                }
            );
        }
        public String getValue(BlackDescricao t) 
        {
           return t.getPadrao();
        }
    };
    
    // Descartados String
    private Column<String, String> colunaDescartados = new Column<String, String>(new TextCell()) 
    {
        public String getValue(String s) 
        {
           return s;
        }
    };
    // Movimento Data
    private Column<Movimento, String> colunaData = new Column<Movimento, String>(new TextCell()) 
    {
        public String getValue(Movimento m) 
        {
            DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
            return dtf.format(m.getData());
        }
    };
    // Movimento Descricao
    private Column<Movimento, String> colunaDescricao = new Column<Movimento, String>(new TextCell()) 
    {
        public String getValue(Movimento m) 
        {
           return m.getDescricao();
        }
    };
    // Movimento Documento
    private Column<Movimento, String> colunaDocumento = new Column<Movimento, String>(new TextCell()) 
    {
        public String getValue(Movimento m) 
        {
           return m.getDocumento();
        }
    };
    // Movimento Valor
    private Column<Movimento, String> colunaValor = new Column<Movimento, String>(new TextCell()) 
    {
        public String getValue(Movimento m) 
        {
           NumberFormat nf = NumberFormat.getFormat("###0.00");
           return nf.format(m.getValor());
        }
    };

    
    private AsyncCallback<Void> nullCallBack = new AsyncCallback<Void>()
    {
        public void onFailure(Throwable caught){}
        public void onSuccess(Void a){}
    };
    
    private void atualizarTemplate(final Template t)
    {
        Home.serviceBus.atualizarTemplate(t,new AsyncCallback<Template>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(Template t2)
                {
                    if( t.getId() < 0 )
                    {
                        t.setId(t2.getId());
                        templatesDataProvider.getList().add(t);
                        templatesDataProvider.refresh();
                    }
                }
            }
        );
    }
    
    private void atualizarBlackString(final BlackString t)
    {
        Home.serviceBus.atualizarBlackString(t,new AsyncCallback<BlackString>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(BlackString t2)
                {
                    if( t.getId() < 0 )
                    {
                        t.setId(t2.getId());
                        blackStringDataProvider.getList().add(t);
                        blackStringDataProvider.refresh();
                    }
                }
            }
        );
    }
    
    private void atualizarBlackDescricao(final BlackDescricao t)
    {
        Home.serviceBus.atualizarBlackDescricao(t,new AsyncCallback<BlackDescricao>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(BlackDescricao t2)
                {
                    if( t.getId() < 0 )
                    {
                        t.setId(t2.getId());
                        blackDescricaoDataProvider.getList().add(t);
                        blackDescricaoDataProvider.refresh();
                    }
                }
            }
        );
    }
	
}