package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.wicket.web.pages.filter.Filter;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

@Getter
@Setter
@Accessors(chain = true)
public class BoatFilter extends Filter<Boat> {

    String search;

    Unit unit;

    Expirated expire = Expirated.NOT_EXPIRATED;

    @Override
    public Specification<Boat> specification() {
        Specification<Boat> specification = Specification.where(null);
        if (search != null && !Strings.isNullOrEmpty(search)) {
            specification = specification.or((r, q, b) -> b.like(b.lower(r.get("registrationNumber").as(String.class)), "%" + search.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> b.like(b.lower(r.get("tailNumber").as(String.class)), "%" + search.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> b.like(b.lower(r.get("model").as(String.class)), "%" + search.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> b.like(b.lower(r.join("person", JoinType.LEFT).get("lastName").as(String.class)), "%" + search.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> {
                Join<Boat, LegalPerson> lp = r.join("legalPerson", JoinType.LEFT);
                return b.like(b.lower(lp.get("name").as(String.class)), "%" + search.toLowerCase() + "%");
            });
        }
        if (unit != null) {
            specification = specification.or((r, q, b) -> r.join("unit", JoinType.LEFT).in(unit.getAllChildsAndThis()));
        }
        if (expire != null) {
            switch (expire) {
                case NOT_EXPIRATED: {
                    specification = specification.and((r, q, b) -> r.get("expirationDate").isNull());
                    break;
                }
                case EXPIRATED: {
                    specification = specification.and((r, q, b) -> r.get("expirationDate").isNotNull());
                    break;
                }
                default:{
                    break;
                }
            }
        }
        return specification;
    }

    public enum Expirated {
        EXPIRATED, NOT_EXPIRATED
    }
}
