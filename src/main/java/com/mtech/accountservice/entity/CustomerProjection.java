package com.mtech.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Entity
@Table(name = "customer_projection")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProjection implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "customer_id", nullable = false)
    @JsonProperty("customer_id")
    private Long customerId;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;
}
