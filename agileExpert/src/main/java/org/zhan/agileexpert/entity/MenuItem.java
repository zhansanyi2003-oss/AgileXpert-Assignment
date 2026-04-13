package org.zhan.agileexpert.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zhan.agileexpert.constants.MenuItemType;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuItemType type;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private MenuItem parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "child_order")
    private List<MenuItem> children = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "app_id")
    private App app;

    public void addChild(MenuItem child) {
        child.setParent(this);
        children.add(child);
    }

    @PrePersist
    @PreUpdate
    void prepareForPersistence() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }

        validateState();
    }

    private void validateState() {
        if (type == MenuItemType.APP) {
            if (app == null) {
                throw new IllegalStateException("APP menu items must reference an app.");
            }
            if (!children.isEmpty()) {
                throw new IllegalStateException("APP menu items cannot have children.");
            }
        }

        if (type == MenuItemType.SUBMENU && app != null) {
            throw new IllegalStateException("SUBMENU menu items cannot directly reference an app.");
        }
    }
}
