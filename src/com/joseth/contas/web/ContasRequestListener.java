package com.joseth.contas.web;

import javax.persistence.EntityManager;
import javax.servlet.ServletRequestEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContasRequestListener implements javax.servlet.ServletRequestListener
{
    static ThreadLocal<EntityManager> emtl = new ThreadLocal<EntityManager>();
    
    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
        EntityManager em = ContasBootstrap.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        emtl.set(em);
        sre.getServletContext().log("Request in: " + em + ", thread " + Thread.currentThread());
    }
    
    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        EntityManager em = emtl.get();
        em.getTransaction().commit();
        em.close();
        sre.getServletContext().log("Request out: " + em  + ", thread " + Thread.currentThread());
    }
    
    public static EntityManager getEntityManager()
    {
        System.out.println("em: "+emtl.get());
        return emtl.get();
    }

}
