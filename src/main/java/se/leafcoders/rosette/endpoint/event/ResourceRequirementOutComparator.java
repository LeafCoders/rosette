package se.leafcoders.rosette.endpoint.event;

import java.util.Comparator;

public class ResourceRequirementOutComparator implements Comparator<ResourceRequirementOut> {

    @Override
    public int compare(ResourceRequirementOut a, ResourceRequirementOut b) {
        return a.getResourceType().getDisplayOrder().compareTo(b.getResourceType().getDisplayOrder());
    }

    public static Comparator<ResourceRequirementOut> comparator() {
        return (ResourceRequirementOut a, ResourceRequirementOut b) -> a.getResourceType().getDisplayOrder().compareTo(b.getResourceType().getDisplayOrder());
    }
}
