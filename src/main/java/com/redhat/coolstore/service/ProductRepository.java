package com.redhat.coolstore.service;

import com.redhat.coolstore.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Product> rowMapper = (rs, rowNum) -> new Product(
            rs.getString("item_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getDouble("price"));

    public List<Product> readAll() {
        return this.jdbcTemplate.query("SELECT item_id, name, description, price, category.category FROM catalog " +
                "JOIN category ON catalog.category = category_id", rowMapper);
    }

    public Page<Product> readAll(Pageable page) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM catalog", Integer.class);
        List<Product> products = this.jdbcTemplate.query("SELECT item_id, name, description, price, category.category FROM catalog " +
                "JOIN category ON catalog.category = category_id LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset(), rowMapper );
        return new PageImpl<>(products, page, count);
    }

    public Product findById(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT item_id, name, description, price, category.category FROM catalog " +
                    "JOIN category ON catalog.category = category_id WHERE item_id = ?", rowMapper, id);
        } catch (DataAccessException dae) {
            return null;
        }
    }

    public List<Product> findByIdList(List<String> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        ids.forEach(id -> sb.append("'").append(id).append("'").append(","));
        return jdbcTemplate.query("SELECT item_id, name, description, price, category.category FROM catalog " +
                "JOIN category ON catalog.category = category_id WHERE item_id in (" + sb.substring(0,sb.length() -1) + ")", rowMapper);
    }

    public List<Product> findByCategoryList(List<String> categories) {
        if (categories.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        categories.forEach(cat -> sb.append("'").append(cat.toLowerCase()).append("'").append(","));
        return jdbcTemplate.query("SELECT item_id, name, description, price, category.category FROM catalog " +
                        "JOIN category ON catalog.category = category_id " +
                        "WHERE category.category IN (" + sb.substring(0,sb.length() -1) + ");", rowMapper)
                .stream().distinct().collect(Collectors.toList());
    }

    public List<Product> findByTagList(List<String> tags) {
        if (tags.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        tags.forEach(cat -> sb.append("'").append(cat.toLowerCase()).append("'").append(","));
        return jdbcTemplate.query("SELECT catalog.item_id, name, description, price, category.category FROM catalog " +
                        "JOIN category ON catalog.category = category_id " +
                        "JOIN catalog_tag ON catalog.item_id = catalog_tag.item_id " +
                        "JOIN tag ON catalog_tag.tag_id = tag.tag_id " +
                        "WHERE tag.tag IN (" + sb.substring(0,sb.length() -1) + ");", rowMapper)
                .stream().distinct().collect(Collectors.toList());
    }

}