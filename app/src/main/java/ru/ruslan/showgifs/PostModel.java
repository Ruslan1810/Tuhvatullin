package ru.ruslan.showgifs;

import com.google.gson.annotations.SerializedName;

public class PostModel {
    @SerializedName("gifURL")
    String gifUrl;

    @SerializedName("description")
    String description;

}

