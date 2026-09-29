package dev.hexoforge.betterfc.config.model;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public interface RawJsonHolder {
    void setRawJson(JsonObject rawJson);
    @Nullable JsonObject getRawJson();
}
