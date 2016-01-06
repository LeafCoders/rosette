package se.leafcoders.rosette.util;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ManyQuery {

    private final String START_INDEX = "startIndex";
    private final String MAX_ITEMS = "maxItems";
    private final String SORT_BY = "sortBy";
    
    private Query query;
    private final int startIndex;
    private final int maxItems;

    public ManyQuery() {
        this.query = null;
        this.startIndex = 0;
        this.maxItems = Integer.MAX_VALUE;
    }

    public ManyQuery(HttpServletRequest request) {
        this(request, null);
    }

    public ManyQuery(HttpServletRequest request, String defaultSortBy) {
        this.startIndex = parseInt(request.getParameter(START_INDEX), 0);
        this.maxItems = parseInt(request.getParameter(MAX_ITEMS), Integer.MAX_VALUE);

        Query q = new Query();
        String sortBy = request.getParameter(SORT_BY);
        if (sortBy == null) {
            sortBy = defaultSortBy;
        }
        if (sortBy != null) {
            List<Sort.Order> orders = new LinkedList<Sort.Order>(); 
            for (String part : sortBy.split(",")) {
                part = part.trim();
                switch (part.charAt(0)) {
                    case '+': orders.add(new Sort.Order(Sort.Direction.ASC, part.substring(1))); break;
                    case '-': orders.add(new Sort.Order(Sort.Direction.DESC, part.substring(1))); break;
                    default:  orders.add(new Sort.Order(Sort.Direction.ASC, part)); break;
                }
            }
            q.with(new Sort(orders));
        }
        this.query = q;
    }

    public Query addCriteria(Criteria criteria) {
        query.addCriteria(criteria);
        return query;
    }
    
    public <T> List<T> filter(final List<T> items) {
        if (startIndex >= items.size()) {
            return new LinkedList<T>();
        } else if (items.isEmpty()) {
            return items;
        } else {
            return items.subList(startIndex, getEndIndex(items.size()) + 1);
        }
    }
    
    private int parseInt(String value, int defaultValue) {
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private int getEndIndex(int maxSize) {
        if (maxItems == Integer.MAX_VALUE) {
            return maxSize - 1;
        } else {
            return Math.min(startIndex + maxItems, maxSize - 1);
        }
    }

    public Query getQuery() {
        return query;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getMaxItems() {
        return maxItems;
    }
}
