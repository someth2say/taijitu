package org.someth2say.taijitu.cli;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicComparisonCfg;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicSourceCfg;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicTaijituCfg;
import org.someth2say.taijitu.cli.config.impl.TaijituCfg;
import org.someth2say.taijitu.cli.source.csv.CSVResourceSource;
import org.someth2say.taijitu.equality.explain.Difference;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Jordi Sola
 */
@RunWith(Parameterized.class)
public class CLITest_CSV {


    private List<String> compare;
    private List<String> key;
    private List<String> sort;

    public CLITest_CSV(String compare, String key, String sort) {
        this.compare = compare == "" ? null : Arrays.asList(compare.split(","));
        this.key = key == "" ? null : Arrays.asList(key.split(","));
        this.sort = sort == "" ? null : Arrays.asList(sort.split(","));
    }

    //FIELDS, in the form of [COMPARE, KEY, SORT]
    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<String[]> fields() {
        //return Arrays.asList(
        return Collections.singletonList(
                new String[]{"", "street,price,latitude,longitude", ""}
        );
    }


    @Test
    public void CSVTest() throws TaijituCliException {
        // Create the tables and test data
        final TaijituCfg configuration = buildCSVConfiguration(
                "http://samplecsvs.s3.amazonaws.com/Sacramentorealestatetransactions.csv",
                "/csv/Sacramentorealestatetransactions.csv"
        );

        final List<Stream<Difference>> comparisonResults = TaijituCli.compare(configuration);

        Assert.assertEquals(1, comparisonResults.size());
        final List<Difference> firstResult = comparisonResults.get(0).collect(Collectors.toList());
        Assert.assertEquals(0, firstResult.size());
    }

    private TaijituCfg buildCSVConfiguration(String resource1, String resource2) {
        BasicTaijituCfg basicTaijituCfg = new BasicTaijituCfg("");

        //URL Scheme
        Properties s1buildProperties = new Properties();
        s1buildProperties.setProperty(ConfigurationLabels.RESOURCE, resource1);

        // File scheme: File source (should be in classpath)
        Properties s2buildProperties = new Properties();
        s2buildProperties.setProperty(ConfigurationLabels.RESOURCE, resource2);
        // File scheme (must be absolute)
        // s2buildProperties.setProperty(ConfigurationLabels.Comparison.RESOURCE, "file:///"+ ClassLoader.getSystemResource(".").getPath() +"/csv/Sacramentorealestatetransactions.csv");

        // We don't actually need a mapper here, we can compare directly the strings provided by the source.
        BasicSourceCfg sourceSrc = new BasicSourceCfg("source", CSVResourceSource.class.getSimpleName(), null, s1buildProperties, null);
        BasicSourceCfg targetSrc = new BasicSourceCfg("target", CSVResourceSource.class.getSimpleName(), null, s2buildProperties, null);

        BasicComparisonCfg comp1 = new BasicComparisonCfg("csv", compare, key, sort, Arrays.asList(sourceSrc, targetSrc));
        basicTaijituCfg.setComparisons(Collections.singletonList(comp1));

        return new TaijituCfg(basicTaijituCfg);
    }

}

