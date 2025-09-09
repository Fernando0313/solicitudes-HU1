package com.first.challenge.r2dbc.helper;
import com.first.challenge.model.criteria.SearchCriteria;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReactiveCriteriaHelper {

    private ReactiveCriteriaHelper() {}

    /* Devuelve el SQL WHERE + los valores ya en orden para bind() */
    public static WhereClause buildWhere(Map<String, String> filters) {
        if (filters.isEmpty()) {
            return new WhereClause("", Map.of());
        }
        StringBuilder sql = new StringBuilder(" WHERE ");
        Map<String, Object> params = new LinkedHashMap<>();
        int idx = 0;
        for (var e : filters.entrySet()) {
            String[] parts = e.getValue().split(":", 2);
            String op  = parts.length == 2 ? parts[0] : "eq";
            String val = parts.length == 2 ? parts[1] : parts[0];

            String col = toSnake(e.getKey());          // camel -> snake_case
            String bindKey = "p" + idx++;
            if (idx > 1) sql.append(" AND ");
            switch (op) {
                case "like" -> sql.append(col).append(" ILIKE :").append(bindKey);
                case "gte"  -> sql.append(col).append(" >= :").append(bindKey);
                case "lte"  -> sql.append(col).append(" <= :").append(bindKey);
                default     -> sql.append(col).append(" = :").append(bindKey);
            }
            params.put(bindKey, convertType(val, col));
        }
        return new WhereClause(sql.toString(), params);
    }

    /* value-object interno */
    public record WhereClause(String sql, Map<String, Object> params) {}

    /* helpers */
    private static String toSnake(String camel) {
        return camel.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

   /* private static Object convertType(String val, String col) {

        return "amount".equals(col) || "interest_rate".equals(col)
                ||"base_salary".equals(col) ? new BigDecimal(val)
                : val;
    }*/
   private static Object convertType(String val, String col) {
       return switch (col) {
           case "amount", "interest_rate", "base_salary", "monthly_amount" -> new BigDecimal(val);
           case "term" -> {
               try {
                   yield Integer.valueOf(val);
               } catch (NumberFormatException ex) {
                   throw new IllegalArgumentException("Filter value for 'term' must be an integer");
               }
           }
           default -> val; // String
       };
   }



}