package org.smg.carlisting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smg.carlisting.common.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Represents a car listing in the application.
 * <p>
 * This class is a domain model for car listings, mapped to an Elasticsearch index.
 * It includes various attributes of a car listing, such as make, model, year, pricing,
 * and color.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "car_listings")
public class CarListing {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = Constants.MAKE)
    private String make;

    @Field(type = FieldType.Text, name = Constants.MODEL)
    private String model;

    @Field(type = FieldType.Integer, name = Constants.YEAR)
    private int year;

    @Field(type = FieldType.Double, name = Constants.MIN_PRICE)
    private double minPrice;

    @Field(type = FieldType.Double, name = Constants.MAX_PRICE)
    private double maxPrice;

    @Field(type = FieldType.Text, name = Constants.COLOR)
    private String color;
}
