package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.Query;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;
import play.libs.Json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "kardex")
public class Kardex extends Model {
    public static final Finder<Integer, Kardex> find = new Finder<>(Kardex.class);

    @Id
    @JsonProperty
    public Integer id;

    @Column(name="date_")
    @JsonProperty
    public ZonedDateTime date;

    @Column(name="product_id")
    @JsonProperty
    public String productId;

    @Column(name="quantity")
    @JsonProperty
    public Integer quantity;

    @Column(name="price")
    @JsonProperty
    public Double price;

    @Column(name="weighted_price")
    @JsonProperty
    public Double weightedPrice;

    @JsonIgnore
    public void updatePrice() {
// SELECT SUM(quantity*price) AS pp, SUM(quantity) sq FROM kardex.kardex WHERE product_id = 'P3' GROUP BY product_id
        String sql
                = " SELECT SUM(k.quantity * k.price) AS pp, SUM(k.quantity) sq "
                + " FROM kardex k"
                + " WHERE k.product_id = '" + this.productId + "' "
                + " GROUP BY k.product_id ";

        RawSql rawSql = RawSqlBuilder
                // let ebean parse the SQL so that it can
                // add expressions to the WHERE and HAVING
                // clauses
                .parse(sql)
                // map resultSet columns to bean properties
                .columnMapping("pp", "partial_price")
                .columnMapping("sq", "quantity")
                .create();


        Query<KardexResult> query = db().find(KardexResult.class);
        query.setRawSql(rawSql);
                // add expressions to the WHERE and HAVING clauses
//                .where().gt("order.id", 0)
//                .having().gt("totalAmount", 20);

        List<KardexResult> list = query.findList();
        if (!list.isEmpty()) {
            this.weightedPrice = list.get(0).partial_price / list.get(0).quantity;
        }
    }

    @JsonIgnore
    public String toString() {
        JsonNode content =  Json.toJson(this);

        return content.toString();
    }

    @JsonIgnore
    public void fromString(String msg) throws IOException {
        JsonNode json = Json.mapper().readTree(msg);
        Kardex kardex = Json.mapper().treeToValue(json, Kardex.class);
        this.id = kardex.id;
        this.date = kardex.date;
        this.productId = kardex.productId;
        this.quantity = kardex.quantity;
        this.price = kardex.price;
    }
}
