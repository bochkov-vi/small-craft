package com.bochkov.smallcraft.jpa.entity;

import com.bochkov.hierarchical.IHierarchical;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"createDate",
        "allChildsAndThis",
        "allChilds",
        "canHaveChilds",
        "new",
        "allParentsAndThis",
        "firstParent",
        "allParents",
        "canHaveParents"})
public class Unit extends AbstractEntity<Long> implements IHierarchical<Long, Unit> {

    @Id
    @Column(name = "id_unit")
    @GeneratedValue(generator = "unit_seq")
    Long id;

    @Column(unique = true, nullable = false)
    String name;

    String phone;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "unit_p", joinColumns = @JoinColumn(name = "id_unit", referencedColumnName = "id_unit"),
            inverseJoinColumns = @JoinColumn(name = "id_unit_parent", referencedColumnName = "id_unit"))
    List<Unit> parents;

    @JsonIgnore
    @ManyToMany(mappedBy = "parents", cascade = CascadeType.ALL)
    List<Unit> childs;

    public Unit(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
