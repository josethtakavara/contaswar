package com.joseth.contas.client.config;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

class ListCallBack<T> implements AsyncCallback<List<T>> 
{
	private List<T> l;
	public ListCallBack( List<T> l )
	{
		this.l=l;
	}
	
	public void onFailure(Throwable caught){}
    public void onSuccess(List<T> result)
    {
    	l.clear();
        l.addAll(result);
    }
}
