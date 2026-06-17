package miroshka.midas.buyer.autobuyer;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoBuyerTerminal extends OkaeriConfig {
    private String world = "";
    private int dimensionId = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;
}
