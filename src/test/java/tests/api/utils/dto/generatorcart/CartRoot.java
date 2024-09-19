package tests.api.utils.dto.generatorcart;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class CartRoot {

    @JsonProperty()
    private String date;

    @JsonProperty("__v")
    private int v;

    @JsonProperty("id")
    private int id;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("products")
    private List<CartInfo> products;
}