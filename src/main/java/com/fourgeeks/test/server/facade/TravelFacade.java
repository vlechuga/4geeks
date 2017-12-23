package com.fourgeeks.test.server.facade;

import com.fourgeeks.test.server.domain.entities.Travel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class TravelFacade extends AbstractFacade<Travel> {

    @PersistenceContext(unitName = "JPA_TEST")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public TravelFacade() {
        super(Travel.class);
    }

}
