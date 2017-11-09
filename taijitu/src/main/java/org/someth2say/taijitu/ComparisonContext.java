package org.someth2say.taijitu;

import org.someth2say.taijitu.config.interfaces.IComparisonCfg;
import org.someth2say.taijitu.tuple.FieldDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jordi Sola
 * This class will keep all data that may be pre-computed before comparison: canonical fields, indexes, equality configurations...
 */
@Deprecated
public class ComparisonContext {

    private final IComparisonCfg comparisonConfigIface;
    private Map<String, List<FieldDescription>> providedFieldsMap = new HashMap<>();

    public ComparisonContext(final IComparisonCfg comparisonConfigIface) {
        this.comparisonConfigIface = comparisonConfigIface;
    }

    public List<FieldDescription> getProvidedFields(String name) {
        return providedFieldsMap.get(name);
    }

}
