package api.iteration2_senior.requests.skeleton.interfaces;

import api.iteration2_senior.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel baseModel);

    Object post();

    Object get();

    Object put(BaseModel model);

    Object delete(BaseModel model);
}
