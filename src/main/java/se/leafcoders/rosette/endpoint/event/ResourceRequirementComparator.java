package se.leafcoders.rosette.comparator;

import java.util.Comparator;
import se.leafcoders.rosette.persistence.model.ResourceRequirement;

public class ResourceRequirementComparator implements Comparator<ResourceRequirement> {

    @Override
    public int compare(ResourceRequirement a, ResourceRequirement b) {
        return a.getResourceType().getDisplayOrder().compareTo(b.getResourceType().getDisplayOrder());
    }

    public static Comparator<ResourceRequirement> comparator() {
        return (ResourceRequirement a, ResourceRequirement b) -> a.getResourceType().getDisplayOrder().compareTo(b.getResourceType().getDisplayOrder());
    }
}
