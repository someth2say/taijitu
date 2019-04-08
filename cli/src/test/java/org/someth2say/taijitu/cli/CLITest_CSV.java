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
       final List<Difference> firstComparisonResult = differenceStream.collect(Collectors.toList());
       Assert.assertEquals(1, firstComparisonResult.size());

       Difference difference = firstComparisonResult.get(0);
       String[] lhs = (String[]) difference.getEntries().get(0);
       String[] rhs = (String[]) difference.getEntries().get(1);
       System.out.println("From: "+ Arrays.toString(lhs));
       System.out.println("  To: "+ Arrays.toString(rhs));
       for (int i = 0; i < lhs.length; i++) {
            if (i==4) {
                Assert.assertNotEquals(lhs[i],rhs[i]);
            } else {
                Assert.assertEquals(lhs[i], rhs[i]);
            }
       }

       // Underlying differences not yet implemented...
       /*Stream<Difference> underlyingDifferences = difference.getUnderlyingDifferences();
       List<Difference> udiffs = underlyingDifferences.collect(Collectors.toList());
       System.out.printf(""+udiffs.size());
       udiffs.forEach(System.out::println);*/

    }

}

