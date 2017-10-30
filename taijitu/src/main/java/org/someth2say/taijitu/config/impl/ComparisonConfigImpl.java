package org.someth2say.taijitu.config.impl;

import org.someth2say.taijitu.config.delegate.ComparisonConfigDelegate;
import org.someth2say.taijitu.config.delegate.SourceConfigDelegate;
import org.someth2say.taijitu.config.delegating.DelegatingConfigIface;
import org.someth2say.taijitu.config.delegating.EqualityConfigIface;

import java.util.List;
import java.util.Properties;


public class ComparisonConfigImpl
        extends DelegatingNamedConfigImpl<ComparisonConfigImpl, ComparisonConfigDelegate>
        implements ComparisonConfigDelegate {


    private StrategyConfigImpl strategyConfig = null;

    public ComparisonConfigImpl(ComparisonConfigImpl parent, ComparisonConfigDelegate delegate) {
        super(delegate);
    }

    @Override
    public StrategyConfigImpl getStrategyConfig() {
        if (strategyConfig == null) {
            strategyConfig = getStrategyConfigInner();
        }
        return strategyConfig;
    }

    public StrategyConfigImpl getStrategyConfigInner() {
        StrategyConfigImpl strategyConfig = getDelegate().getStrategyConfig();
        return strategyConfig != null ? strategyConfig : getParent() != null ? getParent().getStrategyConfig() : null;
    }


    private List<SourceConfigImpl<SourceConfigImpl, SourceConfigDelegate>> sourceConfigs = null;

    @Override
    public List<SourceConfigImpl<SourceConfigImpl, SourceConfigDelegate>> getSourceConfigs() {
        if (sourceConfigs == null) {
            sourceConfigs = getSourceConfigsInner();
        }
        return sourceConfigs;
    }

    public List<SourceConfigImpl<SourceConfigImpl, SourceConfigDelegate>> getSourceConfigsInner() {
        List<SourceConfigImpl<SourceConfigImpl, SourceConfigDelegate>> sourceConfigs = getDelegate().getSourceConfigs();
        return sourceConfigs != null ? sourceConfigs : getParent() != null ? getParent().getSourceConfigs() : null;
    }

    public String getMatchingStrategyName() {
        String matchingStrategyName = getDelegate().getMatchingStrategyName();
        return matchingStrategyName != null ? matchingStrategyName : getParent() != null ? getParent().getMatchingStrategyName() : null;
    }

    // Warning: equalityConfigs are additive, not failback-ing
    public List<EqualityConfigIface> getEqualityConfigs() {
        List<EqualityConfigIface> equalityConfigs = getDelegate().getEqualityConfigs();

        if (equalityConfigs != null) {
            if (getParent() != null) {
                equalityConfigs.addAll(getParent().getEqualityConfigs());
            }
        } else {
            if (getParent() != null) {
                equalityConfigs = getParent().getEqualityConfigs();
            }
        }
        return equalityConfigs;
    }

    @Override
    public String getType() {
        return getDelegate().getType();
    }

    @Override
    public List<String> getKeyFields() {
        return getDelegate().getKeyFields();
    }

    @Override
    public Properties getSourceProperties() {
        return getDelegate().getSourceProperties();
    }
}
