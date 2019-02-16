package org.someth2say.taijitu.cli.util;

import org.junit.Test;
import org.someth2say.taijitu.cli.TaijituCli;
import org.someth2say.taijitu.cli.TaijituCliException;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicComparisonCfg;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicSourceCfg;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicTaijituCfg;
import org.someth2say.taijitu.cli.config.impl.TaijituCfg;
import org.someth2say.taijitu.cli.config.interfaces.ISourceCfg;
import org.someth2say.taijitu.cli.source.StreamSource;
import org.someth2say.taijitu.equality.explain.Difference;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassScanTest {

    @Test
    public void testClassScan() throws TaijituCliException {
        BasicTaijituCfg config = new BasicTaijituCfg("");
        
        config.setUseScanClassPath(true);
        
        Properties buildProperties = new Properties();
        buildProperties.setProperty(ConfigurationLabels.SOURCE_BUILD_PROPERTIES,"1,2,3,4,5");
        ISourceCfg source1 = new BasicSourceCfg("1",StreamSource.class.getSimpleName(), null, buildProperties,null);
        ISourceCfg source2 = new BasicSourceCfg("2",StreamSource.class.getSimpleName(), null, buildProperties,null);
        BasicComparisonCfg comparison = new BasicComparisonCfg("testClassScan", null, null, null, Arrays.asList(source1, source2));
        config.setComparisons(Collections.singletonList(comparison));
        List<Stream<Difference>> results = TaijituCli.compare(new TaijituCfg(config));
        assertEquals(1,results.size());
        List<Difference> collect = results.get(0).collect(Collectors.toList());
        assertTrue(collect.isEmpty());
    }

}