package org.someth2say.taijitu.cli.config;

import org.junit.Test;
import org.someth2say.taijitu.cli.TaijituCliException;
import org.someth2say.taijitu.cli.config.impl.TaijituGfg;
import org.someth2say.taijitu.cli.config.interfaces.ITaijituCfg;

public class TaijituGfgConfigTest {

    @Test
    public void fromFile() throws TaijituCliException {
        ITaijituCfg iTaijituCfg = TaijituConfig.fromPropertiesFile("junit.properties");
        System.out.println(TaijituConfig.toYaml((TaijituGfg) iTaijituCfg));
    }

    @Test
    public void fromYaml() throws TaijituCliException {
        ITaijituCfg iTaijituCfg = TaijituConfig.fromYamlFile("test_db.yaml");
        System.out.println(TaijituConfig.toYaml((TaijituGfg) iTaijituCfg));
    }

    @Test
    public void toYaml() {
    }
}