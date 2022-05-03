package com.redhat.coolstore.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.redhat.coolstore.client.InventoryClient;
import com.redhat.coolstore.model.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository repository;

    //TODO: Autowire Inventory Client
    @Autowired
    private InventoryClient inventoryClient;

    public Product read(String id, boolean inventory) {
        Product product = repository.findById(id);
        if (product == null) {
            return null;
        }
        if (inventory) {
            JSONArray jsonArray = new JSONArray(inventoryClient.getInventoryStatus(product.getItemId()));
            List<String> quantity = IntStream.range(0, jsonArray.length())
                    .mapToObj(index -> ((JSONObject) jsonArray.get(index))
                            .optString("quantity")).collect(Collectors.toList());
            product.setQuantity(Integer.parseInt(quantity.get(0)));
        }
        return product;
    }

    public List<Product> readAll(boolean inventory) {
        List<Product> productList = repository.readAll();
        if (inventory) {
            productList.forEach(p -> {
                JSONArray jsonArray = new JSONArray(this.inventoryClient.getInventoryStatus(p.getItemId()));
                List<String> quantity = IntStream.range(0, jsonArray.length())
                        .mapToObj(index -> ((JSONObject) jsonArray.get(index))
                                .optString("quantity")).collect(Collectors.toList());
                p.setQuantity(Integer.parseInt(quantity.get(0)));
            });
        }
        return productList;
    }

    public Page<Product> readAll(Pageable page, boolean inventory) {
        Page<Product> productPage = repository.readAll(page);
        if (inventory) {
            productPage.forEach(p -> {
                JSONArray jsonArray = new JSONArray(this.inventoryClient.getInventoryStatus(p.getItemId()));
                List<String> quantity = IntStream.range(0, jsonArray.length())
                        .mapToObj(index -> ((JSONObject) jsonArray.get(index))
                                .optString("quantity")).collect(Collectors.toList());
                p.setQuantity(Integer.parseInt(quantity.get(0)));
            });
        }
        return productPage;
    }

    public List<Product> readByIds(List<String> ids, boolean inventory) {
        List<Product> productList = repository.findByIdList(ids);
        if (inventory) {
            productList.forEach(p -> {
                JSONArray jsonArray = new JSONArray(this.inventoryClient.getInventoryStatus(p.getItemId()));
                List<String> quantity = IntStream.range(0, jsonArray.length())
                        .mapToObj(index -> ((JSONObject) jsonArray.get(index))
                                .optString("quantity")).collect(Collectors.toList());
                p.setQuantity(Integer.parseInt(quantity.get(0)));
            });;
        }
        return productList;
    }

    public List<Product> readFeaturedProductsByCategory(String category, boolean inventory) {
        List<Product> productList = repository.findFeaturedProducts(category);
        if (inventory) {
            productList.forEach(p -> {
                JSONArray jsonArray = new JSONArray(this.inventoryClient.getInventoryStatus(p.getItemId()));
                List<String> quantity = IntStream.range(0, jsonArray.length())
                        .mapToObj(index -> ((JSONObject) jsonArray.get(index))
                                .optString("quantity")).collect(Collectors.toList());
                p.setQuantity(Integer.parseInt(quantity.get(0)));
            });
        }
        return productList;
    }
}