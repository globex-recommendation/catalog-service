package com.redhat.coolstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.redhat.coolstore.model.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Product> rowMapper = (rs, rowNum) -> new Product(
            rs.getString("item_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getString("category"),
            rs.getString("image"));

    public List<Product> readAll() {
        return this.jdbcTemplate.query("SELECT * FROM catalog", rowMapper);
    }

    public Page<Product> readAll(Pageable page) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM catalog", Integer.class);
        List<Product> products = this.jdbcTemplate.query("SELECT * FROM catalog LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset(), rowMapper );
        return new PageImpl<>(products, page, count);
    }

    public Product findById(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM catalog WHERE item_id = ?", new Object[]{id}, rowMapper);
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
        return jdbcTemplate.query("SELECT * FROM catalog WHERE item_id in (" + sb.substring(0,sb.length() -1) + ")", rowMapper);
    }

    public List<Product> findFeaturedProducts(String category) {
        List<Map<String, Object>> featuredProducts = jdbcTemplate.query("SELECT * FROM featured_products WHERE category = '" + category + "'", new ColumnMapRowMapper());
        if (featuredProducts.isEmpty()) {
            return new ArrayList<Product>();
        }
        JSONArray array = new JSONArray((String)featuredProducts.get(0).get("items"));
        return IntStream.range(0, array.length()).mapToObj(array::get).map(o -> (JSONObject)o)
                .map(j -> j.getString("productId"))
                .map(this::findById)
                .collect(Collectors.toList());
    }
}