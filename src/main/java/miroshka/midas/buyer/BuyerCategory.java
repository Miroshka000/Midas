package miroshka.midas.buyer;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BuyerCategory extends OkaeriConfig {
    private String id = "";
    private String name = "";
    private String description = "";
    private String imageType = "path";
    private String image = "";
    private List<BuyerProduct> products = new ArrayList<>();
}
