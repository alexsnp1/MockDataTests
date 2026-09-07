package api.iteration2_senior.requests.skeleton.requesters;

import api.iteration2_senior.models.BaseModel;
import api.iteration2_senior.requests.skeleton.HttpRequest;
import api.iteration2_senior.requests.skeleton.interfaces.CrudEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ValidatedCrudRequester<T> extends HttpRequest implements CrudEndpointInterface {
    private CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }


    @Override
    public T post(BaseModel model) {
        return (T) crudRequester.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T post() {
        return (T) crudRequester.post().extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T) crudRequester.get().extract().as(endpoint.getResponseModel());
    }

    @Override
    public T put(BaseModel model) {
        return (T) crudRequester.put(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public Object delete(BaseModel model) {
        return null;
    }
}
