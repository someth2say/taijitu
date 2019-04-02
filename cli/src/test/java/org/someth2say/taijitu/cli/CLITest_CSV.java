package org.someth2say.taijitu.cli;

import org.junit.Assert;
import org.junit.Test;
import org.someth2say.taijitu.cli.config.TaijituConfig;
import org.someth2say.taijitu.cli.config.interfaces.ITaijituCfg;
import org.someth2say.taijitu.equality.explain.Difference;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Jordi Sola
 */
public class CLITest_CSV {

/*
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
    }*/


   @Test
    public void CSVTest() throws TaijituCliException {

        final ITaijituCfg configuration = TaijituConfig.fromYamlFile("test_csv.yaml");

        final List<Stream<Difference>> comparisonResults = TaijituCli.compare(configuration);

        Assert.assertEquals(1, comparisonResults.size());
        final List<Difference> firstResult = comparisonResults.get(0).collect(Collectors.toList());
        //TODO: Introduce expected differences
        Assert.assertEquals(0, firstResult.size());
    }

}

