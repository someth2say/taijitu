package org.someth2say.taijitu.config.delegate;

import org.someth2say.taijitu.config.delegating.EqualityConfigIface;
import org.someth2say.taijitu.config.impl.SourceConfigImpl;
import org.someth2say.taijitu.config.impl.StrategyConfigImpl;

import java.util.List;

//TODO:Find a way for ComparisonConfig to provide SourceConfig info
public interface ComparisonConfigDelegate extends SourceConfigDelegate { //, FileSourceConfigDelegate, QuerySourceConfigDelegate {

    StrategyConfigImpl getStrategyConfig();

    String getMatchingStrategyName();

    List<EqualityConfigIface> getEqualityConfigs();

    List<SourceConfigImpl<SourceConfigImpl, SourceConfigDelegate>> getSourceConfigs();

}
