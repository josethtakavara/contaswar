package com.joseth.contas.client.backrest;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;

public class BackupRestorePainel extends Composite
{
	interface MyUiBinder extends UiBinder<Widget, BackupRestorePainel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public BackupRestorePainel() 
	{
	    initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiField
	FileUpload arquivo;
	
	@UiField
	FormPanel form;
	
	@UiHandler("backupBt")
	void onBackupBtClicked(ClickEvent event) 
	{
		String url = GWT.getModuleBaseURL() + "../restrito/backup";
		Window.open( url, "", "");
	}
	
	@UiHandler("restoreBt")
	void onRestoreBtClicked(ClickEvent event) 
	{
		form.submit();
	}
}