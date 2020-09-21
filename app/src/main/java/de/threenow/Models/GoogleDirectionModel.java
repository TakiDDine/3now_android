package de.threenow.Models;

import com.akexorcist.googledirection.model.Direction;

public class GoogleDirectionModel {

    Direction direction;
    String rawBody;


    public GoogleDirectionModel(Direction direction, String rawBody) {
        this.direction = direction;
        this.rawBody = rawBody;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getRawBody() {
        return rawBody;
    }
}
