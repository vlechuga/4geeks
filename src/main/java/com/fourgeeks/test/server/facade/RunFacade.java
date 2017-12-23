package com.fourgeeks.test.server.facade;

import com.fourgeeks.test.server.domain.entities.Run;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class RunFacade extends AbstractFacade<Run> {

    @PersistenceContext(unitName = "JPA_TEST")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public RunFacade() {
        super(Run.class);
    }

}
