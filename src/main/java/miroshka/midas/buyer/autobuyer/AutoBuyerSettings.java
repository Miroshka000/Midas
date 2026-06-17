package miroshka.midas.buyer.autobuyer;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AutoBuyerSettings extends OkaeriConfig {
    private boolean enabled = true;
    private List<AutoBuyerTerminal> terminals = new ArrayList<>();
}
