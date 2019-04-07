package org.someth2say.taijitu.cli;

import org.junit.Assert;
import org.junit.Test;
import org.someth2say.taijitu.cli.config.TaijituConfig;
import org.someth2say.taijitu.cli.config.interfaces.ITaijituCfg;
import org.someth2say.taijitu.equality.explain.Difference;

import java.util.Arrays;
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
       Stream<Difference> differenceStream = comparisonResults.get(0);
       final List<Difference> firstResult = differenceStream.collect(Collectors.toList());
        //TODO: Introduce expected differences
       Difference difference = firstResult.get(0);
       Object lhs = difference.getEntries().get(0);
       Object rhs = difference.getEntries().get(1);
       System.out.println("From: "+ Arrays.toString((String[]) lhs));
       System.out.println("  To: "+ Arrays.toString((String[]) rhs));
        Assert.assertEquals(1, firstResult.size());
    }

}

