package com.zoopick.server.image;

import java.nio.file.Path;

public enum ImagePurpose {
    ITEM;

    public Path getPath(Path root) {
        return root.resolve(this.name().toLowerCase());
    }

    public String getUrl() {
        return "/images/" + this.name().toLowerCase() + "/";
    }
}
