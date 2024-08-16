package com.project.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
public class FilteredCommentsOptions {
    private Optional<String> keyWord;

    public FilteredCommentsOptions(String kewWord) {
        this.keyWord = Optional.ofNullable(kewWord);
    }
}
