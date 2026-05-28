package io.app.my_app.service;

import io.app.my_app.exception.DuplicateRecordException;
import io.app.my_app.exception.EntityNotFoundException;
import io.app.my_app.mapper.ProductMapper;
import io.app.my_app.model.Product;
import io.app.my_app.model.dtos.product.ProductFilter;
import io.app.my_app.model.dtos.product.ProductRequest;
import io.app.my_app.model.dtos.product.ProductResponse;
import io.app.my_app.model.enums.DeletionStatus;
import io.app.my_app.repository.ProductRepository;
import io.app.my_app.specification.ProductSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSpec productSpec;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(ProductFilter filter, Pageable pageable) {
        return productRepository.findAll(productSpec.hasFilters(filter), pageable)
                .map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id) {
        return productMapper.toResponse(findActiveProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        String sku = productMapper.normalize(request.getSku());
        if (productRepository.existsBySku(sku)) {
            throw new DuplicateRecordException("exceptions.product.skuAlreadyExists", sku);
        }

        Product product = productMapper.toEntity(request);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findActiveProduct(id);
        String sku = productMapper.normalize(request.getSku());
        if (productRepository.existsBySkuAndIdNot(sku, id)) {
            throw new DuplicateRecordException("exceptions.product.skuAlreadyExists", sku);
        }

        productMapper.updateEntity(request, product);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(UUID id) {
        Product product = findActiveProduct(id);
        product.setDeletionStatus(DeletionStatus.INACTIVE);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    private Product findActiveProduct(UUID id) {
        return productRepository.findByIdAndDeletionStatusAndDeletedAtIsNull(id, DeletionStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.product.notFound", id));
    }
}
