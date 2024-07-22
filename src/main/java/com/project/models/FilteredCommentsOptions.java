package com.project.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
public class FilteredCommentsOptions {
    private Optional<String> kewWord;

    public FilteredCommentsOptions(String kewWord) {
        this.kewWord = Optional.ofNullable(kewWord);
    }
}
