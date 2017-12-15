package org.someth2say.taijitu.cli.config.interfaces;

import org.someth2say.taijitu.cli.util.Named;

import java.util.List;

public interface IComparisonCfg extends ISourceCfg, IStrategyCfg, IEqualityCfg, Named, ICfg {


    List<String> getKeyFields();

    List<String> getSortFields();

    List<String> getCompareFields();

    List<IEqualityCfg> getEqualityConfigs();

    List<ISourceCfg> getSourceConfigs();
    
}
