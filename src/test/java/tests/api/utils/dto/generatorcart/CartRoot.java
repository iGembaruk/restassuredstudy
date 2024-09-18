package tests.api.utils.dto.generatorcart;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRoot {

    @JsonProperty("date")
    private String date;

    @JsonProperty("__v")
    private int V;

    @JsonProperty("id")
    private int id;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("products")
    private List<CartInfo> products;
}