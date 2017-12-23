package com.fourgeeks.test.server.facade;

import com.fourgeeks.test.server.domain.entities.Category;
import com.fourgeeks.test.server.providers.exceptions.EntityAlreadyExistsException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;


@Stateless
public class CategoryFacade extends AbstractFacade<Category> {

    @PersistenceContext(unitName = "JPA_TEST")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public CategoryFacade() {
        super(Category.class);
    }

    public Category create(Category entity) {
        List<Category> categories = findByName(entity.getName());
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            throw new EntityAlreadyExistsException("Category already exists");
        }
        getEntityManager().persist(entity);
        getEntityManager().flush();
        return entity;
    }

    public List<Category> findByName(String name) {
        return em.createNamedQuery("Category.findByName")
                .setParameter("name", name)
                .getResultList();
    }

    public void edit(Category entity) {
        List<Category> categories = findByName(entity.getName());
        if (Objects.nonNull(categories) && !categories.isEmpty() &&
                categories.stream()
                        .filter(c -> !c.getId().equals(entity.getId()))
                        .findFirst()
                        .isPresent()) {
            throw new EntityAlreadyExistsException("This category already exists");
        }
        getEntityManager().merge(entity);
    }

}
