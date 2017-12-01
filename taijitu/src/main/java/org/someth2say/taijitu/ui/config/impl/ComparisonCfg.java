package org.someth2say.taijitu.ui.config.impl;

import org.someth2say.taijitu.ui.config.impl.defaults.IComparisonCfgDefaults;
import org.someth2say.taijitu.ui.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.ui.config.interfaces.ISourceCfg;
import java.util.List;


public class ComparisonCfg
        extends NamedCfg<IComparisonCfg>
        implements IComparisonCfgDefaults<IComparisonCfg> {

    public ComparisonCfg(IComparisonCfg delegate, IComparisonCfgDefaults parent) {
        super(delegate, parent);
    }

    private List<ISourceCfg> sourceConfigs = null;

    @Override
    public List<ISourceCfg> getSourceConfigs() {
        if (sourceConfigs == null) {
            sourceConfigs = IComparisonCfgDefaults.super.getSourceConfigs();
        }
        return sourceConfigs;
    }

}
