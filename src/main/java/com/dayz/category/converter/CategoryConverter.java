package com.dayz.category.converter;

import com.dayz.category.domain.Category;
import com.dayz.category.dto.ReadAllCategoriesResponse;
import com.dayz.category.dto.ReadAllCategoriesResponse.CategoryResult;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public ReadAllCategoriesResponse convertToReadAllCategoryResponse(List<Category> categories) {
        return ReadAllCategoriesResponse.of(
                categories.stream()
                        .map(category -> convertToReadAllCategoryResult(category))
                        .collect(Collectors.toList())
        );
    }

    public ReadAllCategoriesResponse.CategoryResult convertToReadAllCategoryResult(Category category) {
        return ReadAllCategoriesResponse.CategoryResult.of(category.getId(), category.getName());
    }

}
