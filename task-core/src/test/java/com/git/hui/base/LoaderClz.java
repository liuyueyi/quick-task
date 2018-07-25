package com.git.hui.base;

import java.util.Objects;

public class LoaderClz {
    public String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoaderClz loaderClz = (LoaderClz) o;
        return Objects.equals(name, loaderClz.name);
    }
}
