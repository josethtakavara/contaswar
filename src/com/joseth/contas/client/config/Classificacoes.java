package com.joseth.contas.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.client.Home;

public class Classificacoes extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, Classificacoes> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
    //@UiField(provided=true) ClassificacoesGrid classGrid=new ClassificacoesGrid();
	@UiField(provided=true) CellList<Classificacao> list1 = new CellList<Classificacao>(new EditClassificaoCell());
	@UiField(provided=true) CellList<Classificacao> list2 = new CellList<Classificacao>(new EditClassificaoCell());
	@UiField(provided=true) CellList<Classificacao> list3 = new CellList<Classificacao>(new EditClassificaoCell());
	
	List<Classificacao> cls[] = new List[]{ new ArrayList<Classificacao>(), new ArrayList<Classificacao>(), new ArrayList<Classificacao>() };
	
	
	@UiHandler("classAddBt")
	void onAddBtClicked(ClickEvent event) 
	{
//	    classGrid.novaClassificacao();
	}

	@UiHandler("classRemBt")
	void onRemBtClicked(ClickEvent event) 
	{
//		dataProvider.getList().remove(dataProvider.getList().size()-1);
	}
	

	public Classificacoes() 
	{
	    Home.serviceBus.getClassificacoes(
            new AsyncCallback<List<Classificacao>>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(List<Classificacao> result)
                {
                    for( int x=0 ; x<result.size() ; x++ )
                    {
                        cls[x % 3].add(result.get(x));
                    }
                }
            } 
        );
	    
	    initWidget(uiBinder.createAndBindUi(this));
	}
}

//class EditClassificaoCell implements Cell<Classificacao>
//{
//    private EditTextCell etc = new EditTextCell (); 
//
//    @Override public boolean dependsOnSelection()   { return etc.dependsOnSelection();}
//    @Override public Set<String> getConsumedEvents(){ return etc.getConsumedEvents(); }
//    @Override public boolean handlesSelection()     { return etc.handlesSelection();  }
//    @Override public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, Classificacao value) 
//    { 
//        return etc.isEditing(context, parent, value.getNome()); 
//    }
//
//    @Override
//    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
//            Element parent, Classificacao value, NativeEvent event,
//            ValueUpdater<Classificacao> valueUpdater)
//    {
//        //etc.onBrowserEvent(context, parent, value.getNome(), event, valueUpdater);
//    }
//
//    @Override
//    public void render(com.google.gwt.cell.client.Cell.Context context,
//            Classificacao value, SafeHtmlBuilder sb)
//    {
//        etc.render(context, value.getNome(), sb);
//    }
//
//    @Override
//    public boolean resetFocus(com.google.gwt.cell.client.Cell.Context context,
//            Element parent, Classificacao value)
//    {
//        return etc.resetFocus(context, parent, value.getNome());
//    }
//
//    @Override
//    public void setValue(com.google.gwt.cell.client.Cell.Context context,
//            Element parent, Classificacao value)
//    {
//        etc.setValue(context, parent, value.getNome());
//    }
//    
//}


class EditClassificaoCell implements Cell<Classificacao>
{
  private EditTextCell etc = new EditTextCell (); 

  @Override public boolean dependsOnSelection()   { return etc.dependsOnSelection();}
  @Override public Set<String> getConsumedEvents(){ return etc.getConsumedEvents(); }
  @Override public boolean handlesSelection()     { return etc.handlesSelection();  }
  @Override public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, Classificacao value) 
  { 
      return etc.isEditing(context, parent, value.getNome()); 
  }

  @Override
  public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
          Element parent, Classificacao value, NativeEvent event,
          ValueUpdater<Classificacao> valueUpdater)
  {
      //etc.onBrowserEvent(context, parent, value.getNome(), event, valueUpdater);
  }

  @Override
  public void render(com.google.gwt.cell.client.Cell.Context context,
          Classificacao value, SafeHtmlBuilder sb)
  {
      etc.render(context, value.getNome(), sb);
  }

  @Override
  public boolean resetFocus(com.google.gwt.cell.client.Cell.Context context,
          Element parent, Classificacao value)
  {
      return etc.resetFocus(context, parent, value.getNome());
  }

  @Override
  public void setValue(com.google.gwt.cell.client.Cell.Context context,
          Element parent, Classificacao value)
  {
      etc.setValue(context, parent, value.getNome());
  }
}