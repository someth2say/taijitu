package org.someth2say.taijitu.util;

import org.junit.Test;
import org.someth2say.taijitu.query.properties.HProperties;
import org.someth2say.taijitu.config.TaijituConfig;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jordi Sola on 14/02/2017.
 */
public class TaijituConfigTest {

    /**** PARAMETERS *********************************************************************/

    @Test
    public void testParameters() {
        HProperties props = new HProperties();
        // Global parameters
        props.putInSections("globalParam", "setup", "parameters", "globalParam");
        props.putInSections("overwrittenParam", "setup", "parameters", "overwrittenParam");
        // Test params
        props.putInSections("test1", "comparison", "1", "name");
        props.putInSections("overwrittenParamInTest", "comparison", "1", "setup", "parameters", "overwrittenParam");
        props.putInSections("test1Param", "comparison", "1", "setup", "parameters", "test1Param");
        props.putInSections("overwrittenParamInTest", "comparison", "1", "setup", "parameters", "overwrittenParam");
        props.putInSections("test1Param2", "comparison", "1", "parameters", "test1Param2");

        props.putInSections("test2", "comparison", "2", "name");
        props.putInSections("test2Param", "comparison", "2", "setup", "parameters", "test2Param");
        props.putInSections("test1ParamInTest2", "comparison", "2", "setup", "parameters", "test1Param");

        TaijituConfig.setProperties(props);

        //final Properties test1Params = TaijituConfig.getAllParameters("test1");
        final Properties test1Params = TaijituConfig.getAllParameters("1");
        assertEquals("GetAllParameters should provide both global and local params, considering overwrites: " + test1Params, 5, test1Params.size());
        assertEquals("Explicit setup parameter must be set", "test1Param", TaijituConfig.getParameter("1", "test1Param"));
        assertEquals("Explicit non-setup parameter must be set", "test1Param2", TaijituConfig.getParameter("1", "test1Param2"));
        assertEquals("Overwritten parameter must be set", "overwrittenParamInTest", TaijituConfig.getParameter("1", "overwrittenParam"));
        assertEquals("Global parameter must be set", "globalParam", TaijituConfig.getParameter("1", "globalParam"));
        assertEquals("Non-defined param should not be set ", null, TaijituConfig.getParameter("1", "undefined"));

        //final Properties test2Params = TaijituConfig.getAllParameters("test1");
        final Properties test2Params = TaijituConfig.getAllParameters("2");
        assertEquals("GetAllParameters should provide both global and local params, considering overwrites:" + test2Params, 4, test2Params.size());
        assertEquals("Parameters set in one test should not spread to other tests", "test2Param", TaijituConfig.getParameter("2", "test2Param"));
        assertEquals("Parameters set in one test should not be available in other test", null, TaijituConfig.getParameter("2", "test1Param2"));

    }
}