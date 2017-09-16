package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.util.Pair;

import java.util.Collection;

/**
 * @author Jordi Sola
 */
public class ComparisonResult {

    private Collection<Pair<ComparableObjectArray, ComparableObjectArray>> different;

    private Collection<ComparableObjectArray> sourceOnly;
    private Collection<ComparableObjectArray> targetOnly;
    private ComparisonResultStatus status = ComparisonResultStatus.PENDING;

    private String[] sourceColumnDescriptions;
    private String[] targetColumnDescriptions;

    public Collection<ComparableObjectArray> getSourceOnly() {
        return sourceOnly;
    }

    public void setSourceOnly(Collection<ComparableObjectArray> sourceOnly) {
        this.sourceOnly = sourceOnly;
    }

    public Collection<ComparableObjectArray> getTargetOnly() {
        return targetOnly;
    }

    public void setTargetOnly(Collection<ComparableObjectArray> targetOnly) {
        this.targetOnly = targetOnly;
    }

    /**
     * @return the different pair of entries
     */
    public Collection<Pair<ComparableObjectArray, ComparableObjectArray>> getDifferent() {
        return different;
    }

    public void setDifferent(Collection<Pair<ComparableObjectArray, ComparableObjectArray>> different) {
        this.different = different;
    }

    public ComparisonResultStatus getStatus() {
        return status;
    }

    public void setStatus(ComparisonResultStatus _status) {
        this.status = _status;
    }

    public String[] getSourceColumnDescriptions() {
        return sourceColumnDescriptions;
    }

    public void setSourceColumnDescriptions(String[] sourceColumnDescriptions) {
        this.sourceColumnDescriptions = sourceColumnDescriptions;
    }

    public String[] getTargetColumnDescriptions() {
        return targetColumnDescriptions;
    }

    public void setTargetColumnDescriptions(String[] targetColumnDescriptions) {
        this.targetColumnDescriptions = targetColumnDescriptions;
    }

    public enum ComparisonResultStatus {
        PENDING(),
        RUNNING(),
        SUCCESS(),
        ERROR()
    }


}
