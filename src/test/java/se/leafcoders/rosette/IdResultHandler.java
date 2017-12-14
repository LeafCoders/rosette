package se.leafcoders.rosette;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import com.jayway.jsonpath.JsonPath;

public class IdResultHandler implements ResultHandler {

    private final JsonPath jsonPath;
    private final IdResult idResult;

    public static ResultHandler assignTo(String jsonPath, IdResult idResult) {
        return new IdResultHandler(JsonPath.compile(jsonPath), idResult);
    }

    protected IdResultHandler(JsonPath jsonPath, IdResult idResult) {
        this.jsonPath = jsonPath;
        this.idResult = idResult;
    }

    @Override
    public void handle(MvcResult result) throws Exception {
        String resultString = result.getResponse().getContentAsString();
        idResult.id = ((Integer) jsonPath.read(resultString)).longValue();
    }
}
