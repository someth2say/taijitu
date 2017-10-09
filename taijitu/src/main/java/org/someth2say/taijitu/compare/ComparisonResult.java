package org.someth2say.taijitu.compare;

import org.someth2say.taijitu.config.ComparisonConfig;
import org.someth2say.taijitu.util.Pair;

import java.util.Collection;

/**
 * @author Jordi Sola
 */
public class ComparisonResult {

	private final ComparisonConfig comparisonConfig;
	
	public ComparisonResult(final ComparisonConfig comparisonConfig) {
		this.comparisonConfig = comparisonConfig;
	}

	
    private Collection<Pair<ComparableTuple, ComparableTuple>> different;

    private Collection<ComparableTuple> sourceOnly;
    private Collection<ComparableTuple> targetOnly;
    private ComparisonResultStatus status = ComparisonResultStatus.PENDING;

    private String[] sourceColumnDescriptions;
    private String[] targetColumnDescriptions;

    public Collection<ComparableTuple> getSourceOnly() {
        return sourceOnly;
    }

    public void setSourceOnly(Collection<ComparableTuple> sourceOnly) {
        this.sourceOnly = sourceOnly;
    }

    public Collection<ComparableTuple> getTargetOnly() {
        return targetOnly;
    }

    public void setTargetOnly(Collection<ComparableTuple> targetOnly) {
        this.targetOnly = targetOnly;
    }

    public Collection<Pair<ComparableTuple, ComparableTuple>> getDifferent() {
        return different;
    }

    public void setDifferent(Collection<Pair<ComparableTuple, ComparableTuple>> different) {
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

    public ComparisonConfig getComparisonConfig() {
		return comparisonConfig;
	}

	public enum ComparisonResultStatus {
        PENDING(),
        RUNNING(),
        SUCCESS(),
        ERROR()
    }


}
