package com.loopers.interfaces.api.order;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderV1Dto {
    public record OrderRequest(@NotNull List<ProductOrderRequest> items, @NotNull Long totalPrice) {

    }

    public record ProductOrderRequest(@NotNull Long id, @NotNull Long quantity) {

    }
}
