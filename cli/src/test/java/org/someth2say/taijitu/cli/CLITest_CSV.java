package org.someth2say.taijitu.cli;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.someth2say.taijitu.cli.config.ConfigurationLabels;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicComparisonCfg;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicEqualityCfg;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicSourceCfg;
import org.someth2say.taijitu.cli.config.delegates.simple.BasicTaijituCfg;
import org.someth2say.taijitu.cli.config.impl.TaijituCfg;
import org.someth2say.taijitu.cli.config.interfaces.IEqualityCfg;
import org.someth2say.taijitu.cli.source.csv.CSVResourceSource;
import org.someth2say.taijitu.cli.source.mapper.CSVTupleMapper;
import org.someth2say.taijitu.compare.equality.impl.value.DateThreshold;
import org.someth2say.taijitu.compare.equality.impl.value.NumberThreshold;
import org.someth2say.taijitu.compare.equality.impl.value.StringCaseInsensitive;
import org.someth2say.taijitu.compare.result.Difference;

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
        final TaijituCfg configuration = getCSVConfiguration();

        final List<Stream<Difference<?>>> comparisonResults = TaijituCli.compare(configuration);

        Assert.assertEquals(1, comparisonResults.size());
        final List<Difference<?>> firstResult = comparisonResults.get(0).collect(Collectors.toList());
        Assert.assertEquals(0, firstResult.size());
    }

    private TaijituCfg getCSVConfiguration() {
        BasicTaijituCfg basicTaijituCfg = new BasicTaijituCfg("");

        // Comparisons

        Properties s1buildProperties = new Properties();
        //URL Scheme
        s1buildProperties.setProperty(ConfigurationLabels.RESOURCE, "http://samplecsvs.s3.amazonaws.com/Sacramentorealestatetransactions.csv");

        Properties s2buildProperties = new Properties();
        // No scheme: File source (should be in classpath)
        s2buildProperties.setProperty(ConfigurationLabels.RESOURCE, "/csv/Sacramentorealestatetransactions.csv");
        // File scheme (must be absolute)
//        s2buildProperties.setProperty(ConfigurationLabels.Comparison.RESOUCE, "file:///"+ ClassLoader.getSystemResource(".").getPath() +"/csv/Sacramentorealestatetransactions.csv");

        // We don't actually need a mapper here, we can compare directly the strings provided by the source.
        BasicSourceCfg sourceSrc = new BasicSourceCfg("source", CSVResourceSource.NAME, null, s1buildProperties, null);//CSVTupleMapper.NAME);
        BasicSourceCfg targetSrc = new BasicSourceCfg("target", CSVResourceSource.NAME, null, s2buildProperties, CSVTupleMapper.NAME);


        BasicComparisonCfg comp1 = new BasicComparisonCfg("csv", compare, key, sort, Arrays.asList(sourceSrc, targetSrc));
        basicTaijituCfg.setComparisons(Collections.singletonList(comp1));

        // Equalizer
        BasicEqualityCfg stringEq = new BasicEqualityCfg(StringCaseInsensitive.class.getSimpleName(), String.class.getName(), null);
        BasicEqualityCfg numberEq = new BasicEqualityCfg(NumberThreshold.class.getSimpleName(), Number.class.getName(), null, "2");
        IEqualityCfg timestampEq = new BasicEqualityCfg(DateThreshold.class.getSimpleName(), Date.class.getName(), null, "100");
        basicTaijituCfg.setEqualityConfigs(Arrays.asList(stringEq, numberEq, timestampEq));

        return new TaijituCfg(basicTaijituCfg);
    }

}

