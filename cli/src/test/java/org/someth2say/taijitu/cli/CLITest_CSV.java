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

