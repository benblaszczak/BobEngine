package com.bobbyloujo.bobengine.components;

import com.bobbyloujo.bobengine.entities.Entity;

/**
 * Allows a component to react to being assigned to an entity.
 * Created by Benjamin on 4/11/2016.
 */
public interface ParentAssignmentHandler extends Component {
    /**
     * Event that will occur when this component is added to an entity with
     * addComponent().
     * @param parent The parent entity that this component was added to.
     */
    void onParentAssigned(Entity parent);
}
