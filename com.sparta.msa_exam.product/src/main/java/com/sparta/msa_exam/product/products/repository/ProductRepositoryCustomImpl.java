package com.sparta.msa_exam.product.products.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.product.core.ProductEntity;
import com.sparta.msa_exam.product.products.dto.ProductSearchDto;
import com.sparta.msa_exam.product.products.dto.ProductsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sparta.msa_exam.product.core.QProductEntity.productEntity;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductsResponseDto> searchProducts(ProductSearchDto searchDto, Pageable pageable) {

        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

        List<ProductEntity> results = queryFactory
                .selectFrom(productEntity)
                .where(
                        productIdCheck(searchDto.getProductId()),
                        nameContains(searchDto.getName()),
                        descriptionContains(searchDto.getDescription()),
                        priceBetween(searchDto.getMinPrice(), searchDto.getMaxPrice()),
                        quantityBetween(searchDto.getMinQuantity(), searchDto.getMaxQuantity())
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(queryFactory
                .select(productEntity.count())
                .from(productEntity)
                .where(
                        productIdCheck(searchDto.getProductId()),
                        nameContains(searchDto.getName()),
                        descriptionContains(searchDto.getDescription()),
                        priceBetween(searchDto.getMinPrice(), searchDto.getMaxPrice()),
                        quantityBetween(searchDto.getMinQuantity(), searchDto.getMaxQuantity())
                )
                .fetchOne()).orElse(0L);

        List<ProductsResponseDto> content = results.stream()
                .map(ProductEntity::toResponseDto)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    // productId가 제공되지 않으면 이 조건은 효과가 없고, 제공되면 productId와 일치하는 productEntity.id를 찾는 조건을 쿼리에 추가
    // 추후에 in 조건 써봐도될듯!
    private BooleanExpression productIdCheck(Long productId) {
        return productId != null ? productEntity.id.in(productId) : null;
    }

    private BooleanExpression nameContains(String name) {
        return name != null ? productEntity.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return description != null ? productEntity.description.containsIgnoreCase(description) : null;
    }

    private BooleanExpression priceBetween(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return productEntity.supply_price.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return productEntity.supply_price.goe(minPrice);
        } else if (maxPrice != null) {
            return productEntity.supply_price.loe(maxPrice);
        } else {
            return null;
        }
    }

    private BooleanExpression quantityBetween(Integer minQuantity, Integer maxQuantity) {
        if (minQuantity != null && maxQuantity != null) {
            return productEntity.quantity.between(minQuantity, maxQuantity);
        } else if (minQuantity != null) {
            return productEntity.quantity.goe(minQuantity);
        } else if (maxQuantity != null) {
            return productEntity.quantity.loe(maxQuantity);
        } else {
            return null;
        }
    }

    // 정렬 컬럼 선택
    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort() != null) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
                switch (sortOrder.getProperty()) {
                    case "createdAt":
                        orders.add(new OrderSpecifier<>(direction, productEntity.createdAt));
                        break;
                    case "price":
                        orders.add(new OrderSpecifier<>(direction, productEntity.supply_price));
                        break;
                    case "quantity":
                        orders.add(new OrderSpecifier<>(direction, productEntity.quantity));
                        break;
                    default:
                        break;
                }
            }
        }

        return orders;
    }
}
